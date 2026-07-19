import { active, shownTab } from '../../../../data/store';
import { get } from 'svelte/store';
import { redirect } from '@sveltejs/kit';
import { base } from '$app/paths';
import { Tab } from '$api/tab';
import FabledSkill, { skillStore } from '../../../../data/skill-store.svelte';
import { hydrateEditorData } from '../../../../data/editor-session';

export const ssr = false;

// noinspection JSUnusedGlobalSymbols
/** @type {import('../../../../../.svelte-kit/types/src/routes').PageLoad} */
export async function load({ params }) {
	await hydrateEditorData();
	const name = params.id;
	const isSkill = params.type === 'skill';
	let data: FabledSkill | undefined;
	let fallback: FabledSkill | undefined;
	if (isSkill) {
		for (const c of get(skillStore.skills)) {
			if (!fallback) fallback = c;

			if (c.name == name) {
				data = c;
				break;
			}
		}

		if (data) {
			if (!data.loaded) {
				await skillStore.loadSkill(data);
			}

			active.set(data);
			shownTab.set(Tab.SKILLS);
			return { data };
		}
	}
	redirect(302, `${base}/${params.type}/${params.id}/edit`);
}
