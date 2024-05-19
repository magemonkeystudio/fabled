import { Server, Socket }     from 'socket.io';
import type { ViteDevServer } from 'vite';

interface SocketProps {
	serverId?: string;
	clientId?: string;
	sessionId?: string;
	trustedClients?: string[];
}

type IDSocket = Socket & SocketProps;

const serverTimeout = 30; // 30 minutes
const clientTimeout = 15; // 15 minutes

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
				if (auth.sessionId) {
					socket.sessionId = auth.sessionId;
				}

				if (auth.serverId) {
					console.log('🖥️ Server connected', auth.serverId);
					socket.serverId = auth.serverId;
					if (socket.serverId) {
						socket.join(socket.serverId);
						if (socket.sessionId) socket.join(socket.sessionId);
					}
				} else if (auth.clientId) {
					console.log('📱 Client connected', auth.clientId);
					socket.clientId = auth.clientId;

					if (socket.clientId) {
						socket.join(socket.clientId);
						if (socket.sessionId) {
							socket.join(socket.sessionId);

							// Get the server socket in the room and check if we have trust
							const sockets = io.sockets.adapter.rooms.get(socket.sessionId);
							let serverSocket: IDSocket | undefined;
							if (sockets) {
								for (const s of sockets) {
									const socket = <IDSocket>io.sockets.sockets.get(s);
									if (socket?.serverId) {
										serverSocket = socket;
										break;
									}
								}
							}
							if (serverSocket) {
								console.log('associated server socket trust', serverSocket.trustedClients);
								const trusted = serverSocket.trustedClients?.includes(socket.clientId);
								console.log(trusted, auth.clientKey);
								if (trusted) {
									socket.emit('trust', { content: [auth.clientKey], from: serverSocket.serverId });
								}
							}
						}
					}
				}

				next();
			})
			.on('connection', (socket: IDSocket) => {
				socket.emit('message', { message: '👋 Welcome, ' + (socket.serverId || socket.clientId), from: 'server' });
				socket.emit('setTimeout', { time: socket.serverId ? serverTimeout : clientTimeout, from: 'server' });

				socket
					.on('disconnect', () => console.log(socket.serverId || socket.clientId, 'disconnected'))
					.on('reload', ({ to }: { to: string }, callback) => {
						console.log('Reloading server:', to);
						io.to(to).timeout(10000).emitWithAck('reload', { from: socket.clientId })
							.then((response) => {
								console.log('Reloaded', response);
								callback(response);
							})
							.catch((err: string) => {
								console.error(`Failed to reload ${to}`, err);
								callback(err);
							});
					})
					.on('saveClass', ({ to, name, yaml }: { to: string, name: string; yaml: string; }, callback) => {
						console.log('Saving class to server:', name);
						socket.to(to).timeout(2500).emitWithAck('saveClass', { name, yaml, from: socket.clientId })
							.then((args) => callback(args))
							.catch((err: string) => callback(err));
					})
					.on('saveSkill', ({ to, name, yaml }: { to: string, name: string; yaml: string; }, callback) => {
						console.log('Saving skill to server:', name);
						socket.to(to).timeout(2500).emitWithAck('saveSkill', { name, yaml, from: socket.clientId })
							.then((args) => callback(args))
							.catch((err: string) => callback(err));
					})
					.on('saveAttributes', ({ to, yaml }: { to: string, name: string; yaml: string; }, callback) => {
						console.log('Saving attributes to server');
						socket.to(to).timeout(2500).emitWithAck('saveAttributes', { yaml, from: socket.clientId })
							.then((args) => callback(args))
							.catch((err: string) => callback(err));
					})
					.on('exportAll', ({ to, classYaml, skillYaml, attributeYaml }: {
						to: string,
						classYaml: string,
						skillYaml: string,
						attributeYaml: string
					}, callback) => {
						console.log('Exporting all to server:', to);
						socket.to(to).timeout(4000).emitWithAck('exportAll', { classYaml, skillYaml, attributeYaml, from: socket.clientId })
							.then((args) => callback(args))
							.catch((err: string) => callback(err));
					})
					.on('trust', (args: { message: never, to: string }, callback) => {
						const relay = {
							content: args?.message || args,
							from:    socket.serverId || socket.clientId
						};

						socket.to(args?.to).timeout(2500).emitWithAck('trust', relay)
							.then(arg => {
								console.log('arg', arg);
								const args = arg[0];
								console.log('callback args', args);
								if (args.client) {
									if (!socket.trustedClients) socket.trustedClients = [];
									socket.trustedClients.push(args.client);
									console.log('added trusted client', args.client);
								}

								callback(args.success);
							})
							.catch(err => {
								console.error('callback error', err);
								callback(err);
							});
					})
					.onAny((event: string, args: { message: never; to: string }) => {
						// no-op
						if (event === 'setTimeout'
							|| event === 'disconnect'
							|| event === 'reload'
							|| event === 'saveSkill'
							|| event === 'saveClass'
							|| event === 'saveAttributes'
							|| event === 'exportAll'
							|| event === 'trust') return;

						console.log(event, args);
						const relay = {
							content: args?.message || args,
							from:    socket.serverId || socket.clientId
						};
						if (args?.to) {
							socket.to(args?.to).emit(event, relay);
						} else {
							socket.broadcast.emit(event, relay);
						}
					});
			});
	}
};