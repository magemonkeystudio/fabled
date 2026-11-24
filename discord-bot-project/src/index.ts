// discord-bot-project/src/index.ts
import {
	Client,
	GatewayIntentBits,
	Interaction,
	REST,
	Routes,
	ChatInputCommandInteraction,
	Message,
	TextChannel,
	DMChannel
} from 'discord.js';
import 'dotenv/config'; // Loads .env file
import { chatWithGemini, reloadSystemPrompt, shouldAnswer, hasActiveSession, endSession } from './llm-service.js';
import {
	activeChannels,
	initializeDatabase,
	loadActiveChannels,
	saveActiveChannel,
	deleteActiveChannel
} from './persistence.js';
import { messageUtils } from './message-utils.js';

const TOKEN = process.env.DISCORD_TOKEN;
const CLIENT_ID = process.env.DISCORD_CLIENT_ID;
const OWNER_ID = process.env.OWNER_ID;

if (!TOKEN || !CLIENT_ID || !OWNER_ID) {
	const missing = [];
	if (!TOKEN) missing.push('DISCORD_TOKEN');
	if (!CLIENT_ID) missing.push('DISCORD_CLIENT_ID');
	if (!OWNER_ID) missing.push('OWNER_ID');

	console.error(
		`❌ ERROR: Missing critical environment variables: ${missing.join(
			', '
		)}. Please ensure these are set in your .env file.`
	);
	process.exit(1);
}

const client = new Client({
	intents: [
		GatewayIntentBits.Guilds,
		GatewayIntentBits.GuildMessages,
		GatewayIntentBits.MessageContent,
		GatewayIntentBits.DirectMessages
	]
});

client.once('clientReady', async () => {
	console.log(`🚀 Bot is online! Logged in as ${client.user?.tag}`);

	try {
		await initializeDatabase(); // Initialize DB from persistence module
		await loadActiveChannels(); // Load active channels from persistence module
	} catch (error) {
		console.error('❌ ERROR: Failed to initialize database or load active channels:', error);
		process.exit(1);
	}

	// Set the bot's presence
	client.user?.setPresence({
		activities: [{ name: '🐒 Swinging through code!', type: 0 }], // Type 0 is PLAYING
		status: 'online'
	});

	// Register slash commands
	const commands = [
		{
			name: 'ping',
			description: 'Replies with Pong!'
		},
		{
			name: 'startdm',
			description: 'Initiates a direct message session with the bot.'
		},
		{
			name: 'activatechannel',
			description: 'Activates the current channel for direct bot messaging without mentions.'
		},
		{
			name: 'deactivatechannel',
			description: 'Deactivates the current channel from direct bot messaging.'
		},
		{
			name: 'reload-system-prompt',
			description: 'Reloads the system prompt for the LLM.'
		},
		{
			name: 'endsession',
			description: 'Ends the current chat session with the bot, clearing context.'
		}
	];

	const rest = new REST({ version: '10' }).setToken(TOKEN);

	try {
		await rest.put(Routes.applicationCommands(CLIENT_ID), { body: commands });
		console.log('✅ Successfully reloaded application (/) commands globally.');
	} catch (error) {
		console.error('❌ ERROR: Failed to register slash commands:', error);
	}
});

client.on('interactionCreate', async (interaction: Interaction) => {
	if (!interaction.isChatInputCommand()) return;

	const chatInteraction = interaction as ChatInputCommandInteraction;
	const { commandName } = chatInteraction;

	if (commandName === 'ping') {
		await chatInteraction.reply('Pong!');
	} else if (commandName === 'startdm') {
		await chatInteraction.deferReply({ ephemeral: true }); // Defer with ephemeral so only user sees
		try {
			const user = chatInteraction.user;
			const dmChannel = await user.createDM();
			await messageUtils.sendMessageInChunks(
				dmChannel,
				`Hello! We can chat here directly now. You don't need to ping me in DMs.`
			);
			await chatInteraction.editReply('DM session initiated! Check your private messages.');
		} catch (error: unknown) {
			console.error(
				`[❌ ERROR] Failed to initiate DM session with ${chatInteraction.user.tag}:`,
				error
			);
			await chatInteraction.editReply(
				'Failed to initiate DM session. Please check your privacy settings or try again.'
			);
		}
	} else if (commandName === 'activatechannel') {
		if (chatInteraction.channel && chatInteraction.channel.id) {
			const err = await saveActiveChannel(chatInteraction.channel.id);
			if (err) {
				console.error('❌ ERROR: Failed to save active channel to database:', err.message);
				await chatInteraction.editReply(
					'❌ Failed to activate channel persistently. Please try again.'
				);
			} else {
				activeChannels.add(chatInteraction.channel.id); // Add to local set after successful DB save
				await chatInteraction.reply({
					content:
						'✅ This channel has been activated for direct bot messaging. I will respond to all messages here without needing to be tagged.',
					ephemeral: true
				});
				console.log(`Channel ${chatInteraction.channel?.id} activated and saved.`);
			}
		} else {
			await chatInteraction.reply({
				content: '❌ Could not activate this channel. Make sure this is a valid channel.',
				ephemeral: true
			});
		}
	} else if (commandName === 'deactivatechannel') {
		if (chatInteraction.channel && chatInteraction.channel.id) {
			const err = await deleteActiveChannel(chatInteraction.channel.id);
			if (err) {
				console.error('❌ ERROR: Failed to remove active channel from database:', err.message);
				await chatInteraction.editReply(
					'❌ Failed to deactivate channel persistently. Please try again.'
				);
			} else {
				activeChannels.delete(chatInteraction.channel.id); // Remove from local set after successful DB delete
				await chatInteraction.reply({
					content:
						'✅ This channel has been deactivated for direct bot messaging. I will only respond if tagged.',
					ephemeral: true
				});
				console.log(
					`Channel ${chatInteraction.channel?.id} deactivated and removed from persistence.`
				);
			}
		} else {
			await chatInteraction.reply({
				content: '❌ Could not deactivate this channel. Make sure this is a valid channel.',
				ephemeral: true
			});
		}
	} else if (commandName === 'reload-system-prompt') {
		if (chatInteraction.user.id !== OWNER_ID) {
			await chatInteraction.reply({
				content: '❌ You are not authorized to use this command.',
				ephemeral: true
			});
			return;
		}

		await chatInteraction.deferReply({ ephemeral: true });
		try {
			await reloadSystemPrompt();
			await chatInteraction.editReply('✅ System prompt reloaded successfully.');
		} catch (error) {
			console.error('❌ ERROR: Failed to reload system prompt:', error);
			await chatInteraction.editReply('❌ Failed to reload system prompt.');
		}
	} else if (commandName === 'endsession') {
		if (chatInteraction.channel && chatInteraction.channel.id) {
			const cleared = endSession(chatInteraction.channel.id);
			if (cleared) {
				await chatInteraction.reply({
					content: '✅ Chat session ended. I have forgotten our recent conversation context.',
					ephemeral: false
				});
			} else {
				await chatInteraction.reply({
					content: 'ℹ️ No active chat session found for this channel.',
					ephemeral: true
				});
			}
		} else {
			await chatInteraction.reply({
				content: '❌ Could not determine channel to end session.',
				ephemeral: true
			});
		}
	}
});

// Function to determine if the bot should respond to a message
async function shouldRespondToMessage(message: Message): Promise<boolean> {
	// Ignore messages from bots or empty content
	if (message.author.bot || !message.content) {
		return false;
	}

	// Only process messages that mention the bot, or if it's a DM, or if the channel is active
	const botMention = message.mentions.users.find((user) => user.id === client.user?.id);
	const isDM = message.channel.type === 1; // 1 is DM channel type
	const isActiveChannel = activeChannels.has(message.channel.id);

	// If the bot is mentioned, we always want to consider responding.
	if (botMention) {
		return true;
	}

	// If it's a DM, we always respond (since botMention is not required in DMs).
	if (isDM) {
		return true;
	}

	// If it's an active channel, we might respond, but we need to check for replies.
	if (isActiveChannel) {
		// If this is a reply to another user (and not the bot), we ignore it unless the bot was mentioned.
		// (We already know botMention is false at this point).
		if (message.reference && message.reference.messageId) {
			try {
				const referencedMessage = await message.fetchReference();
				if (referencedMessage.author.id !== client.user?.id) {
					// It's a reply to someone else.
					return false;
				}
			} catch (error) {
				console.warn(`[⚠️ WARN] Could not fetch referenced message for reply check:`, error);
				// If we can't fetch it, we'll proceed cautiously. 
				// In an active channel, we might still want to classify it.
			}
		}
		return true;
	}

	return false;
}

client.on('messageCreate', async (message: Message) => {
	if (!(await shouldRespondToMessage(message))) {
		return;
	}

	// Re-evaluate botMention and isDM for cleanMessageContent
	const botMention = message.mentions.users.find((user) => user.id === client.user?.id);
	const isDM = message.channel.type === 1; // 1 is DM channel type
	const isActiveChannel = activeChannels.has(message.channel.id);

	// Remove bot mention from message content if present
	const cleanMessageContent = botMention
		? message.content.replace(`<@${client.user?.id}>`, '').trim()
		: message.content.trim();

	if (!cleanMessageContent) {
		await messageUtils.sendMessageInChunks(
			message.channel,
			'Please provide a message for the Fabled chat bot.'
		);
		return;
	}

	if (isActiveChannel && !botMention && !hasActiveSession(message.channel.id)) {
		// If we're in an active channel, we don't necessarily want to respond to _all_ messages.
		// Only ones that have intent for the bot to respond to.
		// BUT: If we already have an active session, assume the user is talking to us.
		const respondToMessage = await shouldAnswer(cleanMessageContent);
		if (!respondToMessage) {
			return;
		}
	}

	let typingInterval: NodeJS.Timeout | undefined;
	try {
		// Start typing indicator
		if (message.channel instanceof TextChannel || message.channel instanceof DMChannel) {
			message.channel.sendTyping();
			typingInterval = setInterval(() => {
				if (message.channel instanceof TextChannel || message.channel instanceof DMChannel) {
					message.channel.sendTyping();
				}
			}, 8000);
		}

		const llmResponse = await chatWithGemini(message.channel.id, cleanMessageContent);

		if (isDM) {
			await messageUtils.sendMessageInChunks(message.channel, llmResponse);
		} else {
			await messageUtils.replyMessageInChunks(message, llmResponse);
		}
	} catch (error: unknown) {
		console.error('❌ LLM Communication Error:', error);
		if (isDM) {
			await messageUtils.sendMessageInChunks(message.channel, 'Failed to get response from LLM.');
		} else {
			await messageUtils.replyMessageInChunks(message, 'Failed to get response from LLM.');
		}
	} finally {
		if (typingInterval) {
			clearInterval(typingInterval);
		}
	}
});

client.login(TOKEN);
