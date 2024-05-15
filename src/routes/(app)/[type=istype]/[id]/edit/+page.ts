import type FabledClass                                                        from '$api/fabled-class';
import type FabledSkill                                                        from '$api/fabled-skill';
import { active, shownTab }                                                    from '../../../../../data/store';
import { get }                                                                 from 'svelte/store';
import { redirect }                                                            from '@sveltejs/kit';
import { classes }                                                             from '../../../../../data/class-store';
import { skills }                                                              from '../../../../../data/skill-store';
import { Attribute }                                                           from '$api/stat';
import {
	attributes,
	getAttributeNames
}                                                                              from '../../../../../data/attribute-store';
import type { MultiAttributeYamlData, MultiClassYamlData, MultiSkillYamlData } from '$api/types';
import YAML                                                                    from 'yaml';
import FabledAttribute                                                         from '$api/fabled-attribute';
import { Tab }                                                                 from '$api/tab';
import { parseYaml }                                                           from '$api/yaml';

export const ssr = false;

// noinspection JSUnusedGlobalSymbols
/** @type {import('../../../../../../.svelte-kit/types/src/routes').PageLoad} */
export async function load({ params }) {
	const name    = params.id;
	const isSkill = params.type === 'skill';

	let data: FabledClass | FabledSkill | FabledAttribute | undefined;
	let fallback: FabledClass | FabledSkill | FabledAttribute | undefined;
	switch (params.type) {
		case 'skill': {
			for (const c of get(skills)) {
				if (!fallback) fallback = c;

				if (c.name == name) {
					data = c;
					break;
				}
			}
			break;
		}
		case 'attribute': {
			for (const c of get(attributes)) {
				if (!fallback) fallback = c;

				if (c.name == name) {
					data = c;
					break;
				}
			}
			break;
		}
		default: {
			for (const c of get(classes)) {
				if (!fallback) fallback = c;

				if (c.name == name) {
					data = c;
					break;
				}
			}
			break;
		}
	}
	if (data) {
		let classOrSkill = false;
		if (!data.loaded) {
			if (data.location === 'local') {
				if (data instanceof FabledAttribute) {
					const text = localStorage.getItem('attribs') || '';
					if (text.split('\n').length > 2 || text.charAt(0) == '{') { // New format
						const yamlData = <MultiAttributeYamlData>parseYaml(text);
						if (yamlData && Object.keys(yamlData).length > 0) {
							data.load(yamlData[data.name]);
						}
					}
				} else {
					classOrSkill   = true;
					const yamlData = <MultiSkillYamlData | MultiClassYamlData>parseYaml(localStorage.getItem(`sapi.${isSkill ? 'skill' : 'class'}.${data.name}`) || '');

					if (yamlData && Object.keys(yamlData).length > 0) {
						data.load(Object.values(yamlData)[0]);
					}
				}
			} else {
				// TODO Load data from server
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
	for (const a of getAttributeNames()) {
		if (clazz.attributes.find(b => b.name === a))
			continue;

		clazz.attributes.push(new Attribute(a, 0, 0));
	}
};