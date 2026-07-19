import { browser } from '$app/environment';
import { attributeStore } from './attribute-store';
import { classStore } from './class-store.svelte';
import { ensureEditorPersistence } from './editor-persistence';
import { skillStore } from './skill-store.svelte';

let hydrationPromise: Promise<void> | undefined;

export const hydrateEditorData = async (): Promise<void> => {
	if (!browser) return;

	if (!hydrationPromise) {
		hydrationPromise = (async () => {
			await ensureEditorPersistence();
			await skillStore.hydratePersistedData();
			await classStore.hydratePersistedData();
			await attributeStore.hydratePersistedData();
		})();
	}

	await hydrationPromise;
};

export const resetEditorHydrationForTests = () => {
	hydrationPromise = undefined;
};
