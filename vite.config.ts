import { sveltekit }       from '@sveltejs/kit/vite';

/** @type {import('vite').UserConfig} */
const config = {
	plugins: [sveltekit()],
	esbuild: {
		supported: {
			'top-level-await': true
		}
	}
};

export default config;
