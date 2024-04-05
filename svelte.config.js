import adapter     from '@sveltejs/adapter-static';
import preprocess  from 'svelte-preprocess';
import { resolve } from 'path';

// const dev = process.argv.includes('dev');

/** @type {import('@sveltejs/kit').Config} */
const config = {
	// Consult https://github.com/sveltejs/svelte-preprocess
	// for more information about preprocessors
	preprocess: preprocess(),

	kit: {
		adapter: adapter({
			// default options are shown. On some platforms
			// these options are set automatically — see below
			pages:       'gh-pages',
			assets:      'gh-pages',
			fallback:    '404.html',
			precompress: false,
			strict:      true
		}),
		// paths:   {
		// 	base: dev ? '' : '/proskillapi'
		// },
		alias:   {
			$api:        resolve('./src/api'),
			$input:      resolve('./src/components/input'),
			$components: resolve('./src/components')
		}
	}
};

export default config;
