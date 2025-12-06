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
import { chatWithGemini } from './llm-service.js';
import * as sqlite3 from 'sqlite3';

const DISCORD_MSG_MAX_LENGTH = 1900; // Leave some room for markdown and ellipsis
const DB_PATH = process.env.SQLITE_DB_PATH || './active_channels.db';

async function chunkContent(content: string): Promise<string[]> {
	const chunks: string[] = [];
	let currentChunk = '';
	const lines = content.split('\n');

	for (const line of lines) {
		if (currentChunk.length + line.length + 1 > DISCORD_MSG_MAX_LENGTH) {
			// +1 for newline
			chunks.push(currentChunk);
			currentChunk = '';
		}
		currentChunk += (currentChunk ? '\n' : '') + line;
	}
	if (currentChunk) {
		chunks.push(currentChunk);
	}
	return chunks;
}

async function sendMessageInChunks(targetChannel: Message['channel'], content: string) {
	const chunks = await chunkContent(content);

	// Ensure the channel is text-based before sending messages
	if (
		!targetChannel ||
		!('send' in targetChannel) ||
		!(targetChannel instanceof TextChannel || targetChannel instanceof DMChannel)
	) {
		console.error(
			`[❌ ERROR] Attempted to send message to a non-text-based or null channel. Message content not sent.`
		);
		return;
	}

	for (const chunk of chunks) {
		await targetChannel.send(chunk);
	}
}

async function replyMessageInChunks(originalMessage: Message, content: string) {
	const chunks = await chunkContent(content);

	// Reply to the original message with the first chunk
	const firstReply = await originalMessage.reply(chunks[0]);

	// Send subsequent chunks in the same channel
	for (let i = 1; i < chunks.length; i++) {
		if (firstReply.channel instanceof TextChannel || firstReply.channel instanceof DMChannel) {
			await firstReply.channel.send(chunks[i]);
		}
	}
}

const TOKEN = process.env.DISCORD_TOKEN;
const CLIENT_ID = process.env.DISCORD_CLIENT_ID;

const activeChannels: Set<string> = new Set(); // Stores channel IDs where the bot is active without mentions

if (!TOKEN || !CLIENT_ID) {
	console.error(
		'❌ ERROR: Missing critical environment variables! Please ensure DISCORD_TOKEN and DISCORD_CLIENT_ID are set.'
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

	// Set the bot's presence
	client.user?.setPresence({
		activities: [{ name: '🐒 Swinging through code!', type: 0 }], // Type 0 is PLAYING
		status: 'online',
	});

	// Register slash commands
	const commands = [
		{
			name: 'ping',
			description: 'Replies with Pong!'
		},
		// Removed listcomponents and componentdetails
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
		}
	];

	const rest = new REST({ version: '10' }).setToken(TOKEN);

	try {
		await rest.put(
			Routes.applicationCommands(CLIENT_ID), // Register globally
			{ body: commands }
		);
		console.log('✅ Successfully reloaded application (/) commands globally.');
	} catch (error) {
		console.error('❌ ERROR: Failed to register slash commands:', error);
	}

	// No longer need to initialize MCP client directly here
	// await initializeMcpClient();
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
			await sendMessageInChunks(
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
			activeChannels.add(chatInteraction.channel.id);
			db.run(`INSERT OR IGNORE INTO active_channels (channel_id) VALUES (?)`, [chatInteraction.channel.id], (err) => {
				if (err) {
					console.error('❌ ERROR: Failed to save active channel to database:', err.message);
					chatInteraction.editReply('❌ Failed to activate channel persistently. Please try again.');
				} else {
					chatInteraction.reply({
						content: '✅ This channel has been activated for direct bot messaging. I will respond to all messages here without needing to be tagged.',
						ephemeral: true,
					});
					console.log(`Channel ${chatInteraction.channel.id} activated and saved.`);
				}
			});
		} else {
			await chatInteraction.reply({
				content: '❌ Could not activate this channel. Make sure this is a valid channel.',
				ephemeral: true,
			});
		}
	} else if (commandName === 'deactivatechannel') {
		if (chatInteraction.channel && chatInteraction.channel.id) {
			activeChannels.delete(chatInteraction.channel.id);
			db.run(`DELETE FROM active_channels WHERE channel_id = ?`, [chatInteraction.channel.id], (err) => {
				if (err) {
					console.error('❌ ERROR: Failed to remove active channel from database:', err.message);
					chatInteraction.editReply('❌ Failed to deactivate channel persistently. Please try again.');
				} else {
					chatInteraction.reply({
						content: '✅ This channel has been deactivated for direct bot messaging. I will only respond if tagged.',
						ephemeral: true,
					});
					console.log(`Channel ${chatInteraction.channel.id} deactivated and removed from persistence.`);
				}
			});
		} else {
			await chatInteraction.reply({
				content: '❌ Could not deactivate this channel. Make sure this is a valid channel.',
				ephemeral: true,
			});
		}
	}
});

// Function to determine if the bot should respond to a message
function shouldRespondToMessage(message: Message): boolean {
    // Ignore messages from bots or empty content
    if (message.author.bot || !message.content) {
        return false;
    }

    // Only process messages that mention the bot, or if it's a DM, or if the channel is active
    const botMention = message.mentions.users.find((user) => user.id === client.user?.id);
    const isDM = message.channel.type === 1; // 1 is DM channel type
    const isActiveChannel = activeChannels.has(message.channel.id);

    if (botMention || isDM || isActiveChannel) {
        return true;
    }

    return false;
}


client.on('messageCreate', async (message: Message) => {
	if (!shouldRespondToMessage(message)) {
		return;
	}

	// Re-evaluate botMention and isDM for cleanMessageContent
	const botMention = message.mentions.users.find((user) => user.id === client.user?.id);
	const isDM = message.channel.type === 1; // 1 is DM channel type

	// Remove bot mention from message content if present
	const cleanMessageContent = botMention
		? message.content.replace(`<@${client.user?.id}>`, '').trim()
		: message.content.trim();

	if (!cleanMessageContent) {
		if (isDM)
			await sendMessageInChunks(
				message.channel,
				'Please provide a message for the Fabled chat bot.'
			);
		return;
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

		const llmResponse = await chatWithGemini(cleanMessageContent);

		if (isDM) {
			await sendMessageInChunks(message.channel, llmResponse);
		} else {
			await replyMessageInChunks(message, llmResponse);
		}
	} catch (error: unknown) {
		console.error('❌ LLM Communication Error:', error);
		if (isDM) {
			await sendMessageInChunks(message.channel, 'Failed to get response from LLM.');
		} else {
			await replyMessageInChunks(message, 'Failed to get response from LLM.');
		}
	} finally {
		if (typingInterval) {
			clearInterval(typingInterval);
		}
	}
});

client.login(TOKEN);
