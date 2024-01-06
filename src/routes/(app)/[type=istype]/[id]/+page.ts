import { active, isShowClasses } from '../../../../data/store';
import { get }                   from 'svelte/store';
import { redirect }              from '@sveltejs/kit';
import { skills }                from '../../../../data/skill-store';
import type ProClass             from '$api/proclass';
import type ProSkill             from '$api/proskill';
import { socketService }         from '$api/socket/socket-connector';
import { parseYAML }             from '$api/yaml';

export const ssr = false;

// noinspection JSUnusedGlobalSymbols
/** @type {import('../../../../../.svelte-kit/types/src/routes').PageLoad} */
export async function load({ params }: any) {
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
				data.load(parseYAML(localStorage.getItem(`sapi.skill.${data.name}`) || ''));
			} else {
				let yaml: string;
				if (params.type == 'class') yaml = await socketService.getClassYaml(data.name);
				else yaml = await socketService.getSkillYaml(data.name);

				const parsedYaml = parseYAML(yaml);
				data.load(parsedYaml);
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