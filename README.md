# MageMonkey Studio Editor & Tools

This repository contains the ecosystem for MageMonkey Studio's plugin tools, including the Fabled Skill Editor, a Discord assistance bot, and the backend services that power them.

## Components

This project is a monorepo that consists of three main components:

-   **Fabled Editor (`./`)**: A web-based application built with [SvelteKit](https://kit.svelte.dev/) for creating and editing Fabled skills.
-   **Discord Bot (`./discord-bot-project`)**: A Discord bot that uses Google's Gemini AI to answer user questions about the Fabled, Divinity, and Codex plugins.
-   **MCP Server (`./mcp-server-project`)**: A backend server that implements the Model-Context-Protocol, providing the Discord bot with detailed information and tools for interacting with the Fabled plugin's components.

## Getting Started

The easiest way to get the entire ecosystem running is with Docker and Docker Compose.

### Prerequisites

-   [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/) are installed.
-   You have created a `.env` file in the root of the project with the necessary environment variables for the Discord Bot. You can use `discord-bot-project/.env.example` as a template.

### Running the Stack

1.  **Set up your environment:**
    Create a `.env` file in the root of this project. Copy the contents from `discord-bot-project/.env.example` and fill in your credentials:
    ```bash
    cp discord-bot-project/.env.example .env
    ```
2.  **Run Docker Compose:**
    From the root of the project, run the following command to build and start the services:
    ```bash
    docker-compose up -d --build
    ```
    This will start the MCP Server and the Discord Bot in the background.

3.  **To stop the services:**
    ```bash
    docker-compose down
    ```

## Development

For detailed instructions on how to develop each component individually, please refer to the `README.md` file within each sub-project directory:

-   [Fabled Editor (`./README.md`)](./README.md) *(You are here)*
-   [Discord Bot (`./discord-bot-project/README.md`)](./discord-bot-project/README.md)
-   [MCP Server (`./mcp-server-project/README.md`)](./mcp-server-project/README.md)

---

*The following sections contain information specific to the Fabled Editor.*

### Fabled Editor: Developing

After cloning the repository, run `npm install` to fetch dependencies for the editor.

To run the project locally, run `npm run dev` and navigate to [localhost:5173](http://localhost:5173). Alternatively run `npm run dev -- --open` to open the project in your default browser automatically.

### Fabled Editor: Deploying

This app is set up to have a pretty automatic pipeline. You should just need to
create a pull request and merge it into `editor` to deploy the app. The app will
be deployed to [fabled.magemonkey.studio](https://fabled.magemonkey.studio) where you can edit to your hearts content.
