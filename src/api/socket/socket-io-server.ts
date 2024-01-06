import { Server, Socket }     from 'socket.io';
import type { ViteDevServer } from 'vite';

interface SocketProps {
	serverId?: string;
	clientId?: string;
}

type IDSocket = Socket & SocketProps;

export const webSocketServer = {
	name: 'webSocketServer',
	configureServer(server: ViteDevServer) {
		if (!server.httpServer) return;

		const io = new Server(server.httpServer);

		io
			.use((socket: IDSocket, next) => {
				const auth = socket.handshake.auth;
				if (auth.serverId) {
					console.log('🖥️ Server connected', auth.serverId);
					socket.serverId = auth.serverId;
					if (socket.serverId) socket.join(socket.serverId);
				} else if (auth.clientId) {
					console.log('📱 Client connected', auth.clientId);
					socket.clientId = auth.clientId;
					if (socket.clientId) socket.join(socket.clientId);
				}

				next();
			})
			.on('connection', (socket: IDSocket) => {
				socket.emit('message', { message: '👋 Welcome, ' + (socket.serverId || socket.clientId), from: 'server' });

				socket
					.on('disconnect', () => console.log(socket.serverId || socket.clientId, 'disconnected'))
					.onAny((event: string, args: { message: never; }) => {
						if (event !== 'disconnect') {
							console.log(event, args);
							const relay = {
								content: args?.message || args,
								from:    socket.serverId || socket.clientId
							};
							socket.broadcast.emit(event, relay);
						}
					});
			});
	}
};