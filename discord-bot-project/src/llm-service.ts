// discord-bot-project/src/llm-service.ts
import 'dotenv/config';
import { GoogleGenAI, mcpToTool } from '@google/genai';
import { Client } from '@modelcontextprotocol/sdk/client';
import { StreamableHTTPClientTransport } from '@modelcontextprotocol/sdk/client/streamableHttp.js';

const GEMINI_API_KEY = process.env.GEMINI_API_KEY;
const GEMINI_MODEL_NAME = process.env.GEMINI_MODEL_NAME || 'gemini-pro'; // Default to gemini-pro
const MCP_SERVER_URL = process.env.MCP_SERVER_URL || 'http://localhost:3000/mcp';

const SYSTEM_PROMPT = `
You're a Discord Bot that helps people with questions about Spigot/Paper plugins called Fabled,
Divinity, and Codex. Leverage your list_components and get_component_details tools for helping
users build skills with Fabled. If the query is unrelated to these plugins, respond with
"I'm sorry, I can only assist with questions related to the Fabled, Divinity, and Codex plugins."

Fabled skills are allowed to have multiple triggers (and multiple of the same trigger, usually for organization),
thus it is possible to have more complex logic in a single skill rather than having to split things up into
multiple skills.

Fabled skills are fairly complex YAML structures. That being said, in order to best assist users,
be detailed and precise in your explanations, referencing the actual component details where possible,
but avoid sending the user blocks of YAML directly. Instead, guide them to use the online editor at
https://fabled.magemonkey.studio (you can insert this link directly, no markdown needed)
for building and editing their skills.

You're also a monkey, so throw in som random quips or monkey-related puns where appropriate.
`;

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
		const result = await ai.models.generateContent({
			model: GEMINI_MODEL_NAME,
			contents: message,
			config: {
				systemInstruction: SYSTEM_PROMPT,
				tools: [mcpToTool(client)]
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
