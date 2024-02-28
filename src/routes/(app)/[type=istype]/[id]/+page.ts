import { active, isShowClasses }   from '../../../../data/store';
import { get }                     from 'svelte/store';
import { redirect }                from '@sveltejs/kit';
import { skills }                  from '../../../../data/skill-store';
import type ProClass               from '$api/proclass';
import type ProSkill               from '$api/proskill';
import YAML                        from 'yaml';
import type { MultiSkillYamlData } from '$api/types';

export const ssr = false;

// noinspection JSUnusedGlobalSymbols
/** @type {import('../../../../../.svelte-kit/types/src/routes').PageLoad} */
export async function load({ params }) {
	const name    = params.id;
	const isSkill = params.type === 'skill';
	let data: ProClass | ProSkill | undefined;
	let fallback: ProClass | ProSkill | undefined;
	if (!isSkill) {
		redirect(302, `/${params.type}/${params.id}/edit`);
	} else if (isSkill) {
		for (const c of get(skills)) {
			if (!fallback) fallback = c;

			if (c.name == name) {
				data = c;
				break;
			}
		}
	}

	if (data) {
		if (!data.loaded) {
			if (data.location === 'local') {
				const yamlData = <MultiSkillYamlData>YAML.parse(localStorage.getItem(`sapi.skill.${data.name}`) || '');

				if (yamlData && Object.keys(yamlData).length > 0) {
					(<ProSkill>data).load(Object.values(yamlData)[0]);
				}
			} else {
				// TODO Load data from server
			}
			(<ProSkill>data).postLoad();
		}

		active.set(data);
		isShowClasses.set(!isSkill);
		return { data };
	} else {
		if (fallback) {
			redirect(302, `/${params.type}/${fallback.name}`);
		} else {
			redirect(302, '/');
		}
	}
}