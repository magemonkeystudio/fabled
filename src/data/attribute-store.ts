import type { Writable }       from 'svelte/store';
import { writable }            from 'svelte/store';
import { browser }             from '$app/environment';
import { updateAllAttributes } from './class-store';
import YAML                    from 'yaml';

export const attributes: Writable<string[]> = (() => {
	let saved = ['vitality', 'spirit', 'intelligence', 'dexterity', 'strength'];
	if (browser) {
		const stored = localStorage.getItem('attribs');
		if (stored) {
			saved = stored.split(',');
			updateAllAttributes(saved);
		}
	}

	const {
					subscribe,
					set,
					update
				} = writable<string[]>(saved);
	return {
		subscribe,
		set: (value: string[]) => {
			if (browser) localStorage.setItem('attribs', value.join(','));
			updateAllAttributes(value);
			return set(value);
		},
		update
	};
})();

/**
 * Loads attribute data from a file
 * e - event details
 */
export const loadAttributes = (text: string) => {
	const yaml = YAML.parse(text);
	const attr = Object.keys(yaml);
	attributes.set(attr);
};