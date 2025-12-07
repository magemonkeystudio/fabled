# Discord Bot

This Discord bot uses Google's Gemini AI to answer user questions about the Fabled, Divinity, and Codex Minecraft plugins. It connects to a backend MCP Server to get detailed, real-time information about the plugin's components and tools.

## Features

-   **AI-Powered Assistance:** Uses Gemini to understand and answer natural language questions.
-   **Fabled Integration:** Leverages the Model-Context-Protocol (MCP) to interact with live data about Fabled components.
-   **Configurable System Prompt:** The bot's core instructions are defined in `system-prompt.md` and can be reloaded on the fly.
-   **Containerized:** Designed to be run with Docker as part of the larger project ecosystem.

## Getting Started

### Prerequisites

-   [Node.js](https://nodejs.org/) (v20 or later)
-   An environment file with the necessary credentials.

### Environment Setup

1.  Create a `.env` file in this directory (`discord-bot-project`).
2.  Copy the contents from `.env.example` and fill in your credentials.

    ```bash
    cp .env.example .env
    ```
    You will need to provide:
    -   `DISCORD_TOKEN`: Your Discord bot token.
    -   `DISCORD_CLIENT_ID`: Your Discord application/client ID.
    -   `OWNER_ID`: Your Discord user ID (for admin commands).
    -   `GEMINI_API_KEY`: Your Google AI Studio API key.

### Local Development

1.  **Install Dependencies:**
    ```bash
    npm install
    ```
2.  **Build the Project:**
    ```bash
    npm run build
    ```
3.  **Run the Bot:**
    ```bash
    npm start
    ```
    **Note:** For the bot to be fully functional, it needs to connect to the MCP Server. Ensure the `MCP_SERVER_URL` in your `.env` file is pointing to a running MCP Server instance.

## Docker

This bot is intended to be run as part_of a `docker-compose` stack defined in the root of the repository. See the root `README.md` for instructions on running the entire application stack.

### Overriding the System Prompt

The system prompt used by the bot can be overridden at runtime by mounting a custom `system-prompt.md` file to `/app/src/system-prompt.md` in the container.

You can do this by modifying the `docker-compose.yml` file in the project root:

```yaml
services:
  discord-bot:
    # ...
    volumes:
      - ./path/to/your/custom-prompt.md:/app/src/system-prompt.md
      - discord_bot_data:/data # Keep this line
```
