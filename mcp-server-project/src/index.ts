import { randomUUID } from 'node:crypto';
import 'dotenv/config'; // Load environment variables
import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import { StreamableHTTPServerTransport } from '@modelcontextprotocol/sdk/server/streamableHttp.js';
import * as z from 'zod'; // Adjusted from 'zod/v4'
import { isInitializeRequest } from '@modelcontextprotocol/sdk/types.js';
import { createMcpExpressApp } from '@modelcontextprotocol/sdk/server/express.js';

import * as fs from 'node:fs';
import * as path from 'node:path';
import { Request, Response } from 'express';

// Create Express application
const app = createMcpExpressApp({
	host: '0.0.0.0',
	allowedHosts: ['mcp-server', 'localhost', '127.0.0.1'] // Explicitly allow internal Docker hosts
});
const port = process.env.MCP_PORT ? parseInt(process.env.MCP_PORT) : 3000;

interface Component {
	name: string;
	description?: string;
	keywords?: string;
	data?: Array<{ [k: string]: unknown }>;
}

let componentData: Record<string, Component[]> = {
	triggers: [],
	targets: [],
	conditions: [],
	mechanics: []
} as Record<string, Component[]>;

try {
	const jsonPath = path.join(process.cwd(), 'components.json'); // Adjusted path for Docker
	const jsonData = fs.readFileSync(jsonPath, 'utf-8');
	componentData = JSON.parse(jsonData) as Record<string, Component[]>;
	console.log('Successfully loaded components.json');
} catch (error) {
	console.warn(
		'Could not read or parse components.json. Initializing with empty component data. Error:',
		error
	);
}

// Fabled Tools implementation
class FabledTools {
	list_components(input: { component_type: 'trigger' | 'target' | 'condition' | 'mechanic'; keywords?: string }) {
		const { component_type, keywords } = input;
		let allComponents: Component[] = [];
		const componentTypes = [component_type + 's']; // component_type is now mandatory

		for (const type of componentTypes) {
			if (componentData[type]) {
				allComponents = allComponents.concat(componentData[type]);
			}
		}

		let componentsWithDescription = allComponents.map((comp: Component) => ({
			name: comp.name,
			description: comp.description || 'No description provided.',
			keywords: comp.keywords
		}));

		if (keywords) {
			const lowerCaseKeywords = keywords.toLowerCase().split(/\s*,\s*/).filter(Boolean);
			componentsWithDescription = componentsWithDescription.filter(comp =>
				lowerCaseKeywords.some(keyword =>
					comp.name.toLowerCase().includes(keyword) ||
					(comp.description && comp.description.toLowerCase().includes(keyword)) ||
					(comp.keywords && comp.keywords.toLowerCase().includes(keyword))
				)
			);
		}

		if (!componentsWithDescription.length && component_type) {
			throw new Error(`No components found for type: ${component_type}${keywords ? ` with keywords: ${keywords}` : ''}`);
		}

		const componentsForOutput = componentsWithDescription.map(({ name, description }) => ({
			name,
			description
		}));

		const returnValue = {
			content: [
				{
					type: 'text' as const,
					text: JSON.stringify(componentsForOutput)
				}
			],
			structuredContent: { items: componentsForOutput }
		};
		console.log('list_components returning:', JSON.stringify(returnValue, null, 2));
		return returnValue;
	}

	get_component_details(input: {
		component_name: string;
		component_type?: 'trigger' | 'target' | 'condition' | 'mechanic';
	}) {
		const { component_name, component_type } = input;
		const componentTypesToSearch = component_type
			? [component_type + 's']
			: ['triggers', 'targets', 'conditions', 'mechanics'];

		for (const type of componentTypesToSearch) {
			const list = componentData[type];
			const component = list?.find(
				(comp: Component) => comp.name.toLowerCase() === component_name.toLowerCase()
			);
			if (component) {
				const structuredContent = {
					details: {
						name: component.name,
						description: component.description,
						parameters: component.data
					}
				};
				const returnValue = {
					content: [{ type: 'text' as const, text: JSON.stringify(structuredContent) }],
					structuredContent
				};
				console.log('get_component_details returning:', JSON.stringify(returnValue, null, 2));
				return returnValue;
			}
		}
		throw new Error(`Component '${component_name}' not found.`);
	}

	generate_random_number(input: { min?: number; max?: number; integer_only?: boolean }) {
		const { min = 0, max = 1, integer_only = false } = input;
		if (min >= max) {
			throw new Error('Min value must be less than max value.');
		}
		let randomNumber = Math.random() * (max - min) + min;
		if (integer_only) {
			randomNumber = Math.floor(randomNumber);
		}
		const returnValue = {
			content: [
				{
					type: 'text' as const,
					text: `Generated random number between ${min} and ${max} (integer_only: ${integer_only}): ${randomNumber}`
				}
			],
			structuredContent: { randomNumber, min, max, integer_only }
		};
		console.log('generate_random_number returning:', JSON.stringify(returnValue, null, 2));
		return returnValue;
	}
}

const toolsInstance = new FabledTools();

const getMcpServer = () => {
	const mcpServer = new McpServer(
		{
			name: 'fabled-editor-mcp-server', // Your server name
			version: '1.0.0'
		},
		{
			capabilities: {
				logging: {}
			}
		}
	);

	// Register your Fabled tools
	mcpServer.registerTool(
		'list_components',
		{
			title: 'List Components',
			description:
				'Lists available Fabled components of a specified type (trigger, target, condition, mechanic), optionally filtered by keywords. Use this tool to discover components that might deal with specific functionalities, like "teleporting", "movement", "damage", or "permissions". For example, you can ask "What fabled components deal with teleporting?" or "List components related to user permissions." Keywords can be used to further narrow down the search, e.g., "list_components(component_type=\'mechanic\', keywords=\'damage, area of effect\')".',
			inputSchema: z.object({
				component_type: z
					.enum(['trigger', 'target', 'condition', 'mechanic'])
					.describe(
						'The type of component to list (e.g., "trigger", "target", "condition", "mechanic"). This parameter is now mandatory.'
					),
				keywords: z
					.string()
					.optional()
					.describe(
						'Optional: Comma-separated keywords to filter component names and descriptions (e.g., "damage, healing").'
					),
			}),
			outputSchema: z.object({
				items: z.array(z.object({ name: z.string(), description: z.string().optional() }))
			})
		},
		toolsInstance.list_components
	);

	mcpServer.registerTool(
		'get_component_details',
		{
			title: 'Get Component Details',
			description: `Retrieves detailed information about a specific Fabled component, including its purpose and parameters. This is useful for understanding how a component works, what inputs it requires, and how to use it to implement specific behaviors or check conditions. For example, "How can I check if a user has a permission?" or "What are the parameters for the 'Teleport' mechanic?"`,
			inputSchema: z.object({
				component_name: z.string().describe('The name of the component.'),
				component_type: z
					.enum(['trigger', 'target', 'condition', 'mechanic'])
					.describe(
						'Optional: The type of component to retrieve (e.g., "trigger", "mechanic"). Specify this when the component name is ambiguous (e.g., "Heal" can be a trigger or a mechanic).'
					)
					.optional()
			}),
			outputSchema: z.object({
				details: z.object({
					name: z.string().describe('The name of the component.'),
					description: z.string().describe('The description of the component.'),
					parameters: z.array(z.any()).describe('The list of parameters for the component.')
				})
			})
		},
		toolsInstance.get_component_details
	);

	mcpServer.registerTool(
		'generate_random_number',
		{
			title: 'Generate Random Number',
			description:
				'Generates a random number. Accepts optional `min` and `max` parameters to specify the range, and an optional `integer_only` parameter to return an integer.',
			inputSchema: z.object({
				min: z
					.number()
					.optional()
					.describe(
						'Optional: The minimum value for the random number (inclusive). Defaults to 0.'
					),
				max: z
					.number()
					.optional()
					.describe(
						'Optional: The maximum value for the random number (exclusive). Defaults to 1.'
					),
				integer_only: z
					.boolean()
					.optional()
					.describe(
						'Optional: If true, the generated number will be an integer. Defaults to false.'
					)
			}),
			outputSchema: z.object({
				randomNumber: z.number().describe('A random number within the specified range.'),
				min: z.number().optional().describe('The minimum value used for generation.'),
				max: z.number().optional().describe('The maximum value used for generation.'),
				integer_only: z
					.boolean()
					.optional()
					.describe('Whether the generated number was an integer.')
			})
		},
		toolsInstance.generate_random_number
	);
	return mcpServer;
};

// Store transports by session ID
const transports: Record<string, StreamableHTTPServerTransport> = {};

//=============================================================================
// STREAMABLE HTTP TRANSPORT (PROTOCOL VERSION 2025-03-26)
//=============================================================================

app.get('/health', (req: Request, res: Response) => {
	res.status(200).json({ status: 'healthy', timestamp: new Date().toISOString() });
});

// Handle all MCP Streamable HTTP requests (GET, POST, DELETE) on a single endpoint
app.all('/mcp', async (req: Request, res: Response) => {
	console.log(`Received ${req.method} request to /mcp`);

	try {
		const sessionId = req.headers['mcp-session-id'] as string | undefined;
		let transport: StreamableHTTPServerTransport;

		if (sessionId && transports[sessionId]) {
			const existingTransport = transports[sessionId];
			if (existingTransport instanceof StreamableHTTPServerTransport) {
				transport = existingTransport;
			} else {
				res.status(400).json({
					jsonrpc: '2.0',
					error: {
						code: -32000,
						message: 'Bad Request: Session exists but uses a different transport protocol'
					},
					id: null
				});
				return;
			}
		} else if (!sessionId && req.method === 'POST' && isInitializeRequest(req.body)) {
			transport = new StreamableHTTPServerTransport({
				sessionIdGenerator: () => randomUUID(),
				onsessioninitialized: (newSessionId: string) => {
					console.log(`StreamableHTTP session initialized with ID: ${newSessionId}`);
					transports[newSessionId] = transport;
				},
				enableJsonResponse: true
			});

			transport.onclose = () => {
				const sid = transport.sessionId;
				if (sid && transports[sid]) {
					console.log(`Transport closed for session ${sid}, removing from transports map`);
					delete transports[sid];
				}
			};

			const mcpServerInstance = getMcpServer();
			await mcpServerInstance.connect(transport);
		} else {
			res.status(400).json({
				jsonrpc: '2.0',
				error: {
					code: -32000,
					message: 'Bad Request: No valid session ID provided or not an initialization request'
				},
				id: null
			});
			return;
		}

		await transport.handleRequest(req, res, req.body);
	} catch (error) {
		console.error('Error handling MCP request:', error);
		if (!res.headersSent) {
			res.status(500).json({
				jsonrpc: '2.0',
				error: {
					code: -32603,
					message: 'Internal server error'
				},
				id: null
			});
		}
	}
});

//=============================================================================
// DEPRECATED HTTP+SSE TRANSPORT (PROTOCOL VERSION 2024-11-05)
//=============================================================================

app.get('/sse', async (req: Request, res: Response) => {
	// Unsupported
	console.log('Received GET request to /sse (deprecated SSE transport)');
	// Return 405
	res.status(405).json({
		jsonrpc: '2.0',
		error: {
			code: -32000,
			message:
				'Method Not Allowed: SSE transport is deprecated. Please use Streamable HTTP transport at /mcp endpoint.'
		},
		id: null
	});
});

// Start the server
const httpServer = app.listen(port, '0.0.0.0', (error) => {
	if (error) {
		console.error('Failed to start server:', error);
		process.exit(1);
	}
	console.log(`Backwards compatible MCP server listening on port ${port}`);
	console.log(`
==============================================
SUPPORTED TRANSPORT OPTIONS:

1. Streamable Http(Protocol version: 2025-03-26)
   Endpoint: /mcp
   Methods: GET, POST, DELETE
   Usage: 
     - Initialize with POST to /mcp
     - Establish SSE stream with GET to /mcp
     - Send requests with POST to /mcp
     - Terminate session with DELETE to /mcp

2. Http + SSE (Protocol version: 2024-11-05)
   Endpoints: /sse (GET) and /messages (POST)
   Usage:
     - Establish SSE stream with GET to /sse
     - Send requests with POST to /messages?sessionId=<id>
==============================================
`);
});

// Implement graceful shutdown
const gracefulShutdown = () => {
	console.log('Shutting down MCP Server...');

	// Close all active transports
	const closePromises = Object.values(transports).map(async (transport) => {
		try {
			if (transport) {
				console.log(`Closing transport for session ${transport.sessionId}`);
				await transport.close();
			}
		} catch (error) {
			console.error(`Error closing transport for session ${transport?.sessionId}:`, error);
		}
	});

	Promise.all(closePromises).finally(() => {
		httpServer.close(() => {
			console.log('MCP server gracefully terminated.');
			process.exit(0);
		});
	});
};

// Enable graceful shutdown on 'q' key press (development)
if (process.stdin.isTTY) {
	console.log("Press 'q' or 'ctrl+c' to shut down the server.");
	process.stdin.setRawMode(true);
	process.stdin.resume();
	process.stdin.on('data', (data) => {
		const input = data.toString().trim().toLowerCase();
		if (input === 'q' || input === '\u0003') {
			// \u0003 is Ctrl+C
			gracefulShutdown();
		}
	});
}

// Enable graceful shutdown on SIGTERM (production/container)
process.on('SIGTERM', gracefulShutdown);
