// discord-bot-project/src/llm-service.ts
import { promises as fs } from 'fs';
import * as path from 'path';
import { fileURLToPath } from 'url';
import 'dotenv/config';
import { GoogleGenAI, mcpToTool } from '@google/genai';
import { Client } from '@modelcontextprotocol/sdk/client';
import { StreamableHTTPClientTransport } from '@modelcontextprotocol/sdk/client/streamableHttp.js';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const GEMINI_API_KEY = process.env.GEMINI_API_KEY;
const GEMINI_MODEL_NAME = process.env.GEMINI_MODEL_NAME || 'gemini-pro'; // Default to gemini-pro
const MCP_SERVER_URL = process.env.MCP_SERVER_URL || 'http://localhost:3000/mcp';

let systemPrompt: string;

async function loadSystemPrompt() {
	try {
		const promptPath = path.join(__dirname, '../src', 'system-prompt.md');
		systemPrompt = await fs.readFile(promptPath, 'utf-8');
		console.log('✅ System prompt loaded successfully.');
	} catch (error) {
		console.error('❌ ERROR: Failed to load system prompt!', error);
		// Fallback to a default prompt if the file is missing or unreadable
		systemPrompt =
			"You are a helpful assistant. Please respond to the user's request.";
	}
}

export async function reloadSystemPrompt() {
	await loadSystemPrompt();
}

// Load the system prompt on startup
loadSystemPrompt();

if (!GEMINI_API_KEY) {
	console.error(
		'❌ ERROR: Missing GEMINI_API_KEY! Gemini integration will not function without it.'
	);
	// In a real application, you might want a more graceful handling or throw an error immediately.
}

const ai = GEMINI_API_KEY ? new GoogleGenAI({ apiKey: GEMINI_API_KEY }) : undefined;

const client = new Client({
	name: 'fabled-client',
	version: '1.0.0'
});
await client.connect(new StreamableHTTPClientTransport(new URL(MCP_SERVER_URL)));

export async function chatWithGemini(message: string): Promise<string> {
	if (!ai) {
		throw new Error('Google Generative AI not initialized. GEMINI_API_KEY is missing.');
	}

	try {
		const tools = [mcpToTool(client)];
		console.log('--- START OF GENERATED TOOL SCHEMA ---');
		console.log(JSON.stringify(tools, null, 2));
		console.log('--- END OF GENERATED TOOL SCHEMA ---');

		const result = await ai.models.generateContent({
			model: GEMINI_MODEL_NAME,
			contents: message,
			config: {
				systemInstruction: systemPrompt,
				tools: tools
			}
		});

		if (result.usageMetadata) {
			console.log('💸 Gemini Usage Metadata:', result.usageMetadata);
		}

		const text = result.text || 'Unknown response from Gemini model.';
		return text;
	} catch (error) {
		console.error('❌ Gemini API Communication Error:', error);
		throw new Error(`Failed to get response from Gemini LLM: ${(error as Error).message}`);
	}
}

// Function to classify message intent (Fabled, Divinity, Codex vs. banter)
export async function shouldAnswer(message: string): Promise<boolean> {
	// Let's do some quick string checks to short-circuit obvious cases.
	// This helps reduce costs and latency by avoiding unnecessary LLM calls.
	const lowerMessage = message.toLowerCase();
	const keywords = ['fabled', 'divinity', 'codex', 'skill', 'component'];
	if (keywords.some((keyword) => lowerMessage.includes(keyword))) {
		console.log(`✅ Quick keyword match found for message: "${message}"`);
		return true;
	}

	if (!ai) {
		console.warn(
			'Google Generative AI not initialized. GEMINI_API_KEY is missing. Defaulting to true for shouldAnswer.'
		);
		return true; // If AI is not available, assume we should answer to avoid blocking
	}

	const classificationPrompt = `
    Analyze the following user message and determine if it is related to the Spigot/Paper plugins Fabled, Divinity, or Codex.
    Respond with "YES" if it is related to these topics or requests information that could be answered by tools related to these topics (e.g., listing components, getting component details).
    Respond with "NO" if it appears to be general banter, off-topic, or something the Fabled bot should not respond to as a specialized helper.

    Message: "${message}"
    Response (YES/NO):`;

	try {
		const result = await ai.models.generateContent({
			model: GEMINI_MODEL_NAME,
			contents: classificationPrompt
		});
		const responseText = result.text?.trim().toUpperCase();

		if (responseText === 'YES') {
			console.log(`✅ Gemini classified message as domain-specific: "${message}"`);
			return true;
		} else if (responseText === 'NO') {
			console.log(`🚫 Gemini classified message as general banter: "${message}"`);
			return false;
		} else {
			console.warn(
				`⚠️ Gemini returned unexpected response for intent classification: "${responseText}". Defaulting to true.`
			);
			return true; // Default to true if classification is ambiguous
		}
	} catch (error) {
		console.error('❌ Error during intent classification with Gemini:', error);
		return true; // Default to true if classification fails, to avoid silence
	}
}
