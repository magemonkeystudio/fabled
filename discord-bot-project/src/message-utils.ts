import { Message, TextChannel, DMChannel } from 'discord.js';

const DISCORD_MSG_MAX_LENGTH = 1900; // Leave some room for markdown and ellipsis

export class MessageUtils {
    async chunkContent(content: string): Promise<string[]> {
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

    async sendMessageInChunks(targetChannel: Message['channel'], content: string) {
        const chunks = await this.chunkContent(content);

        // Ensure the channel is text-based before sending messages
        if (
            !targetChannel ||
            !('send' in targetChannel) ||
            !(targetChannel instanceof TextChannel || targetChannel instanceof DMChannel)
        ) {
            console.error(
                `[❌ ERROR] Attempted to send message to a non-text-based or null channel.`
            );
            return;
        }

        for (const chunk of chunks) {
            await targetChannel.send(chunk);
        }
    }

    async replyMessageInChunks(originalMessage: Message, content: string) {
        const chunks = await this.chunkContent(content);

        // Reply to the original message with the first chunk
        const firstReply = await originalMessage.reply(chunks[0]);

        // Send subsequent chunks in the same channel
        for (let i = 1; i < chunks.length; i++) {
            if (firstReply.channel instanceof TextChannel || firstReply.channel instanceof DMChannel) {
                await firstReply.channel.send(chunks[i]);
            }
        }
    }
}

export const messageUtils = new MessageUtils();
