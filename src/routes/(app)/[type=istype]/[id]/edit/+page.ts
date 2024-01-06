import type ProClass             from '$api/proclass';
import type ProSkill             from '$api/proskill';
import { active, isShowClasses } from '../../../../../data/store';
import { get }                   from 'svelte/store';
import { redirect }              from '@sveltejs/kit';
import { classes }               from '../../../../../data/class-store';
import { skills }                from '../../../../../data/skill-store';
import { socketService }         from '$api/socket/socket-connector';
import { parseYAML }             from '$api/yaml';
import { ProAttribute }          from '$api/proattribute';
import { attributes }            from '../../../../../data/attribute-store';

export const ssr = false;

// noinspection JSUnusedGlobalSymbols
/** @type {import('../../../../../../.svelte-kit/types/src/routes').PageLoad} */
export async function load({ params }: any) {
	const name    = params.id;
	const isSkill = params.type === 'skill';
	let data: ProClass | ProSkill | undefined;
	let fallback: ProClass | ProSkill | undefined;
	if (!isSkill) {
		for (const c of get(classes)) {
			if (!fallback) fallback = c;

			if (c.name == name) {
				data = c;
				break;
			}
		}
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
				data.load(parseYAML(localStorage.getItem(`sapi.${isSkill ? 'skill' : 'class'}.${data.name}`) || ''));
			} else {
				let yaml: string;
				if (params.type == 'class') yaml = await socketService.getClassYaml(data.name);
				else yaml = await socketService.getSkillYaml(data.name);

				const parsedYaml = parseYAML(yaml);
				data.load(parsedYaml);
			}
			if (isSkill) (<ProSkill>data).postLoad();
		}

		if (!isSkill) updateClassAttributes(<ProClass>data);
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

const updateClassAttributes = (clazz: ProClass) => {
	for (const a of get(attributes)) {
		if (clazz.attributes.find(b => b.name === a))
			continue;

		clazz.attributes.push(new ProAttribute(a, 0, 0));
	}
};