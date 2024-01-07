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

		const io = new Server(server.httpServer,
			{
				// Max payload size of 50MB (default is 1MB)
				// This will allow us to send up to 10MB of data
				// for our skills.yml or classes.yml files
				maxHttpBufferSize: 1e7
			});

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
					.on('reload', ({ to }: { to: string }) => {
						console.log('Reloading server:', to);
						io.to(to).timeout(10000).emitWithAck('reload', { from: socket.clientId })
							.then((response) => {
								console.log('Reloaded', to, response);
								if (response[0] === true) {
									socket.emit('reloadComplete');
								} else {
									socket.emit('reloadFailed', { message: response[0] });
								}
							})
							.catch((err: string) => {
								console.error(`Failed to reload ${to}`, err);
								socket.emit('reloadFailed', { message: err });
							});
					})
					.on('saveSkill', ({ to, name, yaml }: { to: string, name: string; yaml: string; }, callback) => {
						if (socket.serverId !== to) return;

						console.log('Saving skill to server:', name);
						socket.emitWithAck('saveSkill', { name, yaml })
							.then(() => callback(true))
							.catch((err: string) => callback(err));
					})
					.onAny((event: string, args: { message: never; }) => {
						if (event !== 'disconnect' && event !== 'reload' && event !== 'saveSkill') {
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