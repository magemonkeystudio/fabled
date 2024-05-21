import { socketService }                                                       from '$api/socket/socket-connector';
import { active, shownTab }                                                    from '../../../../../data/store';
import { get }                                                                 from 'svelte/store';
import { redirect }                                                            from '@sveltejs/kit';
import { Attribute }                                                           from '$api/stat';
import type { MultiAttributeYamlData, MultiClassYamlData, MultiSkillYamlData } from '$api/types';
import FabledAttribute                                                         from '$api/fabled-attribute';
import { Tab }                                                                 from '$api/tab';
import { parseYaml }                                                           from '$api/yaml';
import FabledSkill, { skillStore }                                             from '../../../../../data/skill-store';
import FabledClass, { classStore }                                             from '../../../../../data/class-store';
import {
	attributeStore
}                                                                              from '../../../../../data/attribute-store';

export const ssr = false;

// noinspection JSUnusedGlobalSymbols
/** @type {import('../../../../../../.svelte-kit/types/src/routes').PageLoad} */
export async function load({ params }) {
	const name    = params.id;
	const isSkill = params.type === 'skill';

	let data: FabledClass | FabledSkill | FabledAttribute | undefined;
	let fallback: FabledClass | FabledSkill | FabledAttribute | undefined = undefined;
	switch (params.type) {
		case 'skill':
			for (const c of get(skillStore.skills)) {
				if (!fallback) fallback = c;

				if (c.name == name) {
					data = c;
					break;
				}
			}
			break;
		case 'attribute':
			for (const c of get(attributeStore.attributes)) {
				if (!fallback) fallback = c;

				if (c.name == name) {
					data = c;
					break;
				}
			}
			break;
		default:
			for (const c of get(classStore.classes)) {
				if (!fallback) fallback = c;

				if (c.name == name) {
					data = c;
					break;
				}
			}
			break;
	}
	if (data) {
		let classOrSkill = false;
		if (!data.loaded) {
			let yamlData: MultiSkillYamlData | MultiClassYamlData | MultiAttributeYamlData;
			if (data.location === 'local') {
				if (data instanceof FabledAttribute) {
					const text = localStorage.getItem('attribs') || '';
					if (text.split('\n').length > 2 || text.charAt(0) == '{') { // New format
						yamlData = <MultiAttributeYamlData>parseYaml(text);
					} else {
						yamlData = {};
					}
				} else {
					classOrSkill = true;
					yamlData     = <MultiSkillYamlData | MultiClassYamlData>parseYaml(localStorage.getItem(`sapi.${isSkill ? 'skill' : 'class'}.${data.name}`) || '');
				}
			} else {
				let yaml: string;
				if (params.type == 'class') yaml = await socketService.getClassYaml(data.name);
				else if (params.type === 'skill') yaml = await socketService.getSkillYaml(data.name);
				else yaml = await socketService.getAttributeYaml();

				yamlData = <MultiSkillYamlData | MultiClassYamlData | MultiAttributeYamlData>parseYaml(yaml);
			}


			if (yamlData && Object.keys(yamlData).length > 0) {
				if (data instanceof FabledAttribute) {
					data.load((<MultiAttributeYamlData>yamlData)[data.name]);
				} else {
					data.load(Object.values(yamlData)[0]);
				}
			}

			if (classOrSkill && isSkill) (<FabledSkill>data).postLoad();
		}

		if (classOrSkill && !isSkill) updateClassAttributes(<FabledClass>data);

		active.set(data);
		switch (params.type) {
			case 'skill':
				shownTab.set(Tab.SKILLS);
				break;
			case 'attribute':
				shownTab.set(Tab.ATTRIBUTES);
				break;
			default:
				shownTab.set(Tab.CLASSES);
				break;
		}
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
	for (const a of attributeStore.getAttributeNames()) {
		if (clazz.attributes.find(b => b.name === a))
			continue;

		clazz.attributes.push(new Attribute(a, 0, 0));
	}
};
