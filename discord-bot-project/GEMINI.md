# Gemini Context: Discord Bot Project

## Project Overview
This project is a Discord bot designed to assist users with the Fabled, Divinity, and Codex Minecraft plugins. It utilizes Google's Gemini AI for natural language understanding and generation, and integrates with a backend Model-Context-Protocol (MCP) Server to access real-time data and tools related to the plugins.

## Architecture & Key Components

### Tech Stack
*   **Runtime:** Node.js
*   **Language:** TypeScript
*   **Framework:** Discord.js
*   **AI:** Google Gemini (`@google/genai`)
*   **MCP:** `@modelcontextprotocol/sdk` for tool integration
*   **Database:** `better-sqlite3` (SQLite) for local persistence

### Core Files
*   **`src/index.ts`**: The main entry point. It sets up the Discord client, registers slash commands (`ping`, `startdm`, `activatechannel`, `deactivatechannel`, `reload-system-prompt`, `endsession`), and handles message events. It includes logic to decide when to respond (mentions, DMs, or active channels).
*   **`src/llm-service.ts`**: Manages the connection to Gemini and the MCP server. It includes `chatWithGemini` for generating responses and `shouldAnswer` for filtering off-topic messages. It now manages **active conversation sessions** per channel, maintaining context history with a 7-minute inactivity timeout.
*   **`src/persistence.ts`**: Handles the storage of "active channels" (channels where the bot listens to all messages) using a local SQLite database (`active_channels.db`).
*   **`src/message-utils.ts`**: Provides utilities for splitting large AI responses into chunks that fit within Discord's message character limits.
*   **`src/system-prompt.md`**: Defines the bot's persona (a helpful monkey) and operational constraints (e.g., not outputting raw YAML, redirecting to the online editor).

## Build & Run Instructions

### Prerequisites
*   Node.js (v20+)
*   A `.env` file populated with credentials (see `.env.example`).

### Commands
*   **Install Dependencies:**
    ```bash
    npm install
    ```
*   **Build Project:**
    ```bash
    npm run build
    ```
    (Runs `tsc` and outputs to `dist/`)
*   **Start Bot:**
    ```bash
    npm start
    ```
    (Runs `node dist/index.js`)

## Development Conventions

*   **Environment Variables:** Critical configuration (tokens, IDs, API keys) is managed via `dotenv`. Ensure `.env` is present locally.
*   **TypeScript:** Strict mode is enabled. Use explicit types.
*   **Error Handling:** Use `try/catch` blocks for async operations, especially Discord API and LLM calls. Log errors with descriptive messages and emojis (e.g., `❌ ERROR: ...`).
*   **Bot Logic:**
    *   **Intent Filtering:** The bot uses a two-step process to decide whether to answer: logic checks (mentions/channels/replies) -> keyword/LLM classification (`shouldAnswer`).
        *   **Mentions/DMs:** Always processed.
        *   **Active Channels:** Processed unless the message is a **reply to another user** and the bot is not specifically mentioned.
        *   **Existing Sessions:** If an active chat session already exists for the channel, the `shouldAnswer` intent check is **skipped**, maintaining conversation flow for all non-reply messages (or replies to the bot).
    *   **Persistence:** `better-sqlite3` is used synchronously for most operations, wrapped in promises for async consistency in the codebase.
    *   **Message Chunking:** Always use `messageUtils.sendMessageInChunks` or `messageUtils.replyMessageInChunks` when sending AI responses to handle potential length issues.
