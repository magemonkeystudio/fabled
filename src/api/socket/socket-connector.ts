import { io, type Socket }              from 'socket.io-client';
import { get, type Writable, writable } from 'svelte/store';
import { v4 as uuid }                   from 'uuid';
import { synthesisEnabled }             from '../../data/settings';

type SocketMessage = {
	id: object;
	event?: string;
	from?: string;
	content?: string;
};

export const messages: Writable<SocketMessage[]> = writable<SocketMessage[]>([]);
export const socketConnected: Writable<boolean>  = writable<boolean>(false);
export const socketTrusted: Writable<boolean>    = writable<boolean>(false);
export const dcWarning: Writable<number>         = writable<number>(-1);

class SocketService {
	private socket: Socket | null;
	private sessionId                     = '';
	private clientId                      = '';
	private serverId                      = '';
	private dcTimeout                     = 4 * 60 * 1000;
	private _dcTask: number | null        = null;
	private _dcWarningTask: number | null = null;
	private _onConnect: (() => void)[]    = [];
	private _onDisconnect: (() => void)[] = [];

	public keyphrase = writable('');

	constructor() {
		this.socket = null;
	}

	public onConnect(cb: () => void) {
		if (this._onConnect.includes(cb)) return;
		this._onConnect.push(cb);
	}

	public offConnect(cb: () => void) {
		this._onConnect = this._onConnect.filter(c => c !== cb);
	}

	public clearOnConnect() {
		this._onConnect = [];
	}

	public onDisconnect(cb: () => void) {
		if (this._onDisconnect.includes(cb)) return;
		this._onDisconnect.push(cb);
	}

	public offDisconnect(cb: () => void) {
		this._onDisconnect = this._onDisconnect.filter(c => c !== cb);
	}

	public clearOnDisconnect() {
		this._onDisconnect = [];
	}

	public connect(sessionId: string, clientName: string | undefined | null = undefined) {
		if (!synthesisEnabled) return null;
		if (this.socket) return this.socket;
		if (!sessionId) return null;

		// If the client name is not set, generate a random uuid and use the first segment
		if (!clientName) {
			// Use local storage if it exists, otherwise generate a random uuid
			clientName = localStorage.getItem('clientName');

			if (!clientName) {
				clientName = uuid().split('-')[0];
				localStorage.setItem('clientName', clientName);
			}
		}
		this.sessionId  = sessionId;
		this.clientId   = clientName;
		const clientKey = Math.floor(10000 + Math.random() * 90000).toString();

		// this.socket = io('ws://localhost:5173', {
		this.socket = io('wss://synthesis.travja.dev', {
			auth: {
				sessionId: this.sessionId,
				clientId:  this.clientId,
				clientKey
			}
		});

		this.socket.on('connect', () => {
			this.resetTimeout();
			this.keyphrase.set(clientKey);
		});
		this.socket.on('disconnect', () => {
			socketConnected.set(false);
			this._onDisconnect.forEach(cb => cb());
			const disconnectMsg = { id: {}, content: 'Disconnected' };
			messages.set([disconnectMsg, ...get(messages)]);
			setTimeout(() => messages.set(get(messages).filter(m => m !== disconnectMsg)), 5000);
			if (this._dcTask) clearTimeout(this._dcTask);
			dcWarning.set(-1);
		});

		this.socket?.on('trust', (args, callback) => {
			const { content, from } = args;

			if (content[0] !== clientKey) return;

			this.serverId = from;
			console.log('Successfully trusted server');

			this.socket?.emit('join', { room: sessionId });

			socketTrusted.set(true);
			socketConnected.set(true);
			if (callback) callback({ success: true, client: this.clientId });

			this._onConnect.forEach(cb => cb());
		});

		this.socket
			.onAny((event, args) => {
				if (event === 'setTimeout') {
					this.dcTimeout = (args?.time || 4) * 60 * 1000;
					return;
				}

				if (!get(socketTrusted)) return;

				this.resetTimeout();

				const message: SocketMessage = {
					id:      {},
					event,
					from:    args?.from,
					content: args?.content || args?.message
				};

				messages.set([message, ...get(messages)]);
				setTimeout(() => messages.set(get(messages).filter(m => m !== message)), 5000);
			});
	}

	public disconnect() {
		if (!this.socket) return;
		this.socket.disconnect();
		this.socket = null;
	}

	public resetTimeout() {
		if (this._dcTask) window.clearTimeout(this._dcTask);
		if (this._dcWarningTask) window.clearTimeout(this._dcWarningTask);
		dcWarning.set(-1);

		if (this.dcTimeout >= 60000) {
			this._dcWarningTask = window.setTimeout(() => dcWarning.set(60), this.dcTimeout - 60000);
		} else if (this.dcTimeout >= 1000) {
			dcWarning.set(this.dcTimeout / 1000);
		}

		this._dcTask = window.setTimeout(() => this.socket?.disconnect(), this.dcTimeout);
	}

	public ping() {
		if (!this.socket) return;
		this.socket.emit('ping', { to: this.serverId });
		this.resetTimeout();
	}

	public emit(event: string, args?: object) {
		if (!this.socket || !get(socketTrusted)) return;
		this.socket.emit(event, args);
		this.resetTimeout();
	}

	public reloadSapi(): Promise<boolean> {
		return new Promise((resolve, reject) => {
			if (!this.socket || !get(socketTrusted)) {
				reject('No socket');
				return;
			}

			this.socket.timeout(10000).emitWithAck('reload', { to: this.serverId })
				.then((response) => {
					if (response[0] === true) {
						resolve(true);
						this.resetTimeout();
					} else
						reject(response[0]);
				})
				.catch((err: string) => reject(err));
		});
	}

	public getClasses(): Promise<string[]> {
		if (!this.socket || !get(socketTrusted)) return Promise.reject('No socket');
		this.socket.emit('getClasses', { to: this.serverId });

		return new Promise((resolve, reject) => {
			this.socket?.on('classes', ({ content }) => {
				this.socket?.off('classes');
				if (typeof (content) == 'string') content = [content];
				resolve(content);
			});

			setTimeout(() => reject('Timeout'), 3000);
		});
	}

	public getSkills(): Promise<string[]> {
		if (!this.socket || !get(socketTrusted)) return Promise.reject('No socket');
		this.socket.emit('getSkills', { to: this.serverId });

		return new Promise((resolve, reject) => {
			this.socket?.on('skills', ({ content }) => {
				this.socket?.off('skills');
				if (typeof (content) == 'string') content = [content];
				resolve(content);
			});

			setTimeout(() => reject('Timeout'), 3000);
		});
	}

	public getClassYaml(name: string): Promise<string> {
		if (!this.socket || !get(socketTrusted)) return Promise.reject('No socket');
		this.socket.emit('getClassYaml', { name, to: this.serverId });

		return new Promise((resolve, reject) => {
			this.socket?.on('classYaml', ({ content }) => {
				this.socket?.off('classYaml');
				resolve(content);
			});

			setTimeout(() => reject('Timeout'), 3000);
		});
	}

	public getSkillYaml(name: string): Promise<string> {
		if (!this.socket || !get(socketTrusted)) return Promise.reject('No socket');
		this.socket.emit('getSkillYaml', { name, to: this.serverId });
		return new Promise((resolve, reject) => {
			this.socket?.on('skillYaml', ({ content }) => {
				this.socket?.off('skillYaml');
				resolve(content);
			});

			setTimeout(() => reject('Timeout'), 3000);
		});
	}

	public getAttributeYaml(): Promise<string> {
		if (!this.socket || !get(socketTrusted)) return Promise.reject('No socket');
		this.socket.emit('getAttributeYaml', { to: this.serverId });
		return new Promise((resolve, reject) => {
			this.socket?.on('attributeYaml', ({ content }) => {
				this.socket?.off('attributeYaml');
				resolve(content);
			});

			setTimeout(() => reject('Timeout'), 3000);
		});
	}

	public async saveSkillToServer(name: string, yaml: string): Promise<boolean> {
		if (!this.socket || !get(socketTrusted)) return Promise.reject('No socket');
		try {
			const response = await this.socket.timeout(3000).emitWithAck('saveSkill', { name, yaml, to: this.serverId });
			const match    = response && response[0] === name;
			if (!match) {
				console.log('Error saving skill', response);
			}

			this.resetTimeout();

			return match;
		} catch (e) {
			console.log('Timeout saving skill to server', e);
			return false;
		}
	}

	public async saveClassToServer(name: string, yaml: string): Promise<boolean> {
		if (!this.socket || !get(socketTrusted)) return Promise.reject('No socket');
		try {
			const response = await this.socket.timeout(3000).emitWithAck('saveClass', { name, yaml, to: this.serverId });
			const match    = response && response[0] === name;
			if (!match) {
				console.log('Error saving class', response);
			}

			this.resetTimeout();

			return match;
		} catch (e) {
			console.log('Timeout saving class to server', e);
			return false;
		}
	}

	public async saveAttributesToServer(yaml: string): Promise<boolean> {
		if (!this.socket || !get(socketTrusted)) {
			console.log('socket not connected');
			return Promise.reject('No socket');
		}
		try {
			const response = await this.socket.timeout(3000).emitWithAck('saveAttributes', { yaml, to: this.serverId });
			const match    = response && response[0] === 'attributes';
			if (!match) {
				console.log('Error saving attribute', response);
			}

			this.resetTimeout();

			return match;
		} catch (e) {
			console.log('Timeout saving attribute to server', e);
			return false;
		}
	}

	public async exportAll(classYaml: string, skillYaml: string, attributeYaml: string): Promise<boolean> {
		if (!this.socket || !get(socketTrusted)) return Promise.reject('No socket');
		try {
			const response = await this.socket.timeout(4500).emitWithAck('exportAll', {
				to: this.serverId,
				classYaml,
				skillYaml,
				attributeYaml
			});
			if (response && !!response[0]) return !!response[0];

			this.resetTimeout();

			console.log('Error exporting all', response);
			return false;
		} catch (e) {
			console.log('Timeout exporting all', e);
			return false;
		}

	}
}

export const socketService = new SocketService();