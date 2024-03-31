import type FabledClass          from '$api/fabled-class';
import type FabledSkill          from '$api/fabled-skill';
import { active, isShowClasses } from '../../../../../data/store';
import { get }                                         from 'svelte/store';
import { redirect }                                    from '@sveltejs/kit';
import { classes }                                     from '../../../../../data/class-store';
import { skills }          from '../../../../../data/skill-store';
import { FabledAttribute } from '$api/fabled-attribute';
import { attributes }      from '../../../../../data/attribute-store';
import type { MultiClassYamlData, MultiSkillYamlData } from '$api/types';
import YAML                                            from 'yaml';

export const ssr = false;

// noinspection JSUnusedGlobalSymbols
/** @type {import('../../../../../../.svelte-kit/types/src/routes').PageLoad} */
export async function load({ params }) {
	const name    = params.id;
	const isSkill = params.type === 'skill';
	let data: FabledClass | FabledSkill | undefined;
	let fallback: FabledClass | FabledSkill | undefined;
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
				const yamlData = <MultiSkillYamlData | MultiClassYamlData>YAML.parse(localStorage.getItem(`sapi.${isSkill ? 'skill' : 'class'}.${data.name}`) || '');

				if (yamlData && Object.keys(yamlData).length > 0) {
					data.load(Object.values(yamlData)[0]);
				}
			} else {
				// TODO Load data from server
			}
			if (isSkill) (<FabledSkill>data).postLoad();
		}

		if (!isSkill) updateClassAttributes(<FabledClass>data);
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

const updateClassAttributes = (clazz: FabledClass) => {
	for (const a of get(attributes)) {
		if (clazz.attributes.find(b => b.name === a))
			continue;

		clazz.attributes.push(new FabledAttribute(a, 0, 0));
	}
};