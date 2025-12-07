FROM node:20-alpine

WORKDIR /app

COPY . .
RUN npm ci
RUN npm run build

EXPOSE 3000

# Healthcheck for the MCP server
HEALTHCHECK --interval=30s --timeout=10s --retries=3 CMD curl --fail http://localhost:3000/health || exit 1

CMD ["npm", "start"]