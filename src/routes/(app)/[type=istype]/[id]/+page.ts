import { active, shownTab, Tab }   from '../../../../data/store';
import { get }                     from 'svelte/store';
import { redirect }                from '@sveltejs/kit';
import { skills }       from '../../../../data/skill-store';
import type FabledClass from '$api/fabled-class';
import type FabledSkill from '$api/fabled-skill';
import YAML             from 'yaml';
import type { MultiSkillYamlData } from '$api/types';
import type FabledAttribute from '$api/fabled-attribute';
import { base } from '$app/paths';

export const ssr = false;

// noinspection JSUnusedGlobalSymbols
/** @type {import('../../../../../.svelte-kit/types/src/routes').PageLoad} */
export async function load({ params }) {
	const name    = params.id;
	const isSkill = params.type === 'skill';
	let data: FabledSkill | undefined;
	let fallback: FabledSkill | undefined;
	if (isSkill) {
		for (const c of get(skills)) {
			if (!fallback) fallback = c;

			if (c.name == name) {
				data = c;
				break;
			}
		}

		if (data) {
			if (!data.loaded) {
				if (data.location === 'local') {
					const yamlData = <MultiSkillYamlData>YAML.parse(localStorage.getItem(`sapi.skill.${data.name}`) || '');
	
					if (yamlData && Object.keys(yamlData).length > 0) {
						(<FabledSkill>data).load(Object.values(yamlData)[0]);
					}
				} else {
					// TODO Load data from server
				}
				(<FabledSkill>data).postLoad();
			}
	
			active.set(data);
			shownTab.set(Tab.Skills);
			return { data };
		}
	}
	redirect(302, `${base}/${params.type}/${params.id}/edit`);
}