# MCP Server

This project is a simple backend server that implements the [Model-Context-Protocol (MCP)](https://github.com/model-context-protocol/mcp-spec). Its purpose is to provide a live, tool-callable backend for an AI model, in this case, the Discord Bot.

## Features

-   **Express Server:** Built with [Express](https://expressjs.com/) for a lightweight and robust API.
-   **MCP Implementation:** Implements the MCP spec, allowing an AI to discover and call tools.
-   **Fabled Component Data:** Serves data from `components.json`, which contains detailed information about Fabled skill components. This allows the AI to answer questions accurately.

## Getting Started

### Prerequisites

-   [Node.js](https://nodejs.org/) (v20 or later)

### Local Development

1.  **Install Dependencies:**
    ```bash
    npm install
    ```
2.  **Build the Project:**
    ```bash
    npm run build
    ```
3.  **Run the Server:**
    ```bash
    npm start
    ```
    The server will start on port 3000 by default.

## Docker

This server is intended to be run as part of a `docker-compose` stack defined in the root of the repository. It provides the necessary backend for the Discord Bot to function correctly.

See the root `README.md` for instructions on running the entire application stack.
