import { io, type Socket }              from 'socket.io-client';
import { get, type Writable, writable } from 'svelte/store';

type SocketMessage = {
	id: object;
	event?: string;
	from?: string;
	content: string;
};

export const messages: Writable<SocketMessage[]> = writable<SocketMessage[]>([]);
export const socketConnected: Writable<boolean>  = writable<boolean>(false);

class SocketService {
	private socket: Socket | null;
	private _onConnect: (() => void)[]    = [];
	private _onDisconnect: (() => void)[] = [];

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

	public connect(socketSecret: string, clientName: string) {
		if (this.socket) return this.socket;

		this.socket = io('ws://localhost:5173', {
			auth: {
				secret:   socketSecret,
				clientId: clientName
			}
		});

		this.socket.on('connect', () => {
			socketConnected.set(true);
			this._onConnect.forEach(cb => cb());
		});
		this.socket.on('disconnect', () => {
			socketConnected.set(false);
			this._onDisconnect.forEach(cb => cb());
			const disconnectMsg = { id: {}, content: 'Disconnected' };
			messages.set([disconnectMsg, ...get(messages)]);
			setTimeout(() => messages.set(get(messages).filter(m => m !== disconnectMsg)), 5000);
		});

		this.socket
			.onAny((event, args) => {
				const { from, content } = args;
				const tempArgs          = { ...args };
				delete tempArgs.from;
				delete tempArgs.content;
				// console.log(content, args.message);
				const message = { id: {}, event, from, content: content || args.message, tempArgs };
				messages.set([message, ...get(messages)]);
				setTimeout(() => messages.set(get(messages).filter(m => m !== message)), 5000);
			});
	}

	public disconnect() {
		if (!this.socket) return;
		this.socket.disconnect();
		this.socket = null;
	}

	public emit(event: string, args?: object) {
		if (!this.socket) return;
		this.socket.emit(event, args);

	}

	public reloadSapi() {
		this.emit('reload');
	}

	public getClasses(): Promise<string[]> {
		if (!this.socket) return Promise.reject('No socket');
		this.socket.emit('getClasses');

		return new Promise((resolve, reject) => {
			this.socket?.on('classes', ({ content }) => {
				this.socket?.off('classes');
				resolve(content);
			});

			setTimeout(() => reject('Timeout'), 3000);
		});
	}

	public getSkills(): Promise<string[]> {
		if (!this.socket) return Promise.reject('No socket');
		this.socket.emit('getSkills');

		return new Promise((resolve, reject) => {
			this.socket?.on('skills', ({ content }) => {
				this.socket?.off('skills');
				resolve(content);
			});

			setTimeout(() => reject('Timeout'), 3000);
		});
	}

	public getClassYaml(name: string): Promise<string> {
		if (!this.socket) return Promise.reject('No socket');
		this.socket.emit('getClassYaml', name);

		return new Promise((resolve, reject) => {
			this.socket?.on('classYaml', ({ content }) => {
				this.socket?.off('classYaml');
				resolve(content);
			});

			setTimeout(() => reject('Timeout'), 3000);
		});
	}

	public getSkillYaml(name: string): Promise<string> {
		if (!this.socket) return Promise.reject('No socket');
		this.socket.emit('getSkillYaml', name);

		return new Promise((resolve, reject) => {
			this.socket?.on('skillYaml', ({ content }) => {
				this.socket?.off('skillYaml');
				resolve(content);
			});

			setTimeout(() => reject('Timeout'), 3000);
		});
	}
}

export const socketService = new SocketService();