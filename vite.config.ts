/// <reference types="vitest/config" />
import adapter from '@sveltejs/adapter-static';
import { sveltekit } from '@sveltejs/kit/vite';
import { resolve } from 'path';
import { webSocketServer } from './src/api/socket/socket-io-server';

/** @type {import('vite').UserConfig} */
const config = {
	plugins: [
		sveltekit({
			adapter: adapter({
				pages: 'gh-pages',
				assets: 'gh-pages',
				fallback: '404.html',
				precompress: false,
				strict: true
			}),
			alias: {
				$api: resolve('./src/api'),
				$input: resolve('./src/components/input'),
				$components: resolve('./src/components')
			}
		}),
		webSocketServer
	],
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
