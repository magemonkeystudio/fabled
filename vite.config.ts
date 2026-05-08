import { sveltekit } from '@sveltejs/kit/vite';
import { webSocketServer } from './src/api/socket/socket-io-server';

/** @type {import('vite').UserConfig} */
const config = {
	plugins: [sveltekit(), webSocketServer],
	esbuild: {
		supported: {
			'top-level-await': true
		}
	},
	test: {
		environment: 'jsdom'
	}
};

export default config;
