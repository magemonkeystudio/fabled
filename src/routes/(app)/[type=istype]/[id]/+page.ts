import { active, shownTab }        from '../../../../data/store';
import { get }                     from 'svelte/store';
import { redirect }                from '@sveltejs/kit';
import type { MultiSkillYamlData } from '$api/types';
import { socketService }           from '$api/socket/socket-connector';
import { base }                    from '$app/paths';
import { parseYaml }               from '$api/yaml';
import { Tab }                     from '$api/tab';
import FabledSkill, { skillStore } from '../../../../data/skill-store';

export const ssr = false;

// noinspection JSUnusedGlobalSymbols
/** @type {import('../../../../../.svelte-kit/types/src/routes').PageLoad} */
export async function load({ params }) {
	const name    = params.id;
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
				let yamlData: MultiSkillYamlData;
				if (data.location === 'local') {
					yamlData = <MultiSkillYamlData>parseYaml(localStorage.getItem(`sapi.skill.${data.name}`) || '');
				} else {
					const yaml: string = await socketService.getSkillYaml(data.name);

					yamlData = <MultiSkillYamlData>parseYaml(yaml);
				}

				if (yamlData && Object.keys(yamlData).length > 0) {
					await (<FabledSkill>data).load(Object.values(yamlData)[0]);
				}
				(<FabledSkill>data).postLoad();
			}

			active.set(data);
			shownTab.set(Tab.SKILLS);
			return { data };
		}
	}
	redirect(302, `${base}/${params.type}/${params.id}/edit`);
}