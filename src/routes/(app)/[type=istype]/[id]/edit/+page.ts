import FabledClass          from '$api/fabled-class';
import FabledSkill          from '$api/fabled-skill';
import { active, shownTab, Tab } from '../../../../../data/store';
import { get }                                         from 'svelte/store';
import { redirect }                                    from '@sveltejs/kit';
import { classes }                                     from '../../../../../data/class-store';
import { skills }          from '../../../../../data/skill-store';
import { Attribute } from '$api/stat';
import { attributes, getAttributeNames }      from '../../../../../data/attribute-store';
import type { MultiAttributeYamlData, MultiClassYamlData, MultiSkillYamlData } from '$api/types';
import YAML                                            from 'yaml';
import FabledAttribute from '$api/fabled-attribute';

export const ssr = false;

// noinspection JSUnusedGlobalSymbols
/** @type {import('../../../../../../.svelte-kit/types/src/routes').PageLoad} */
export async function load({ params }) {
	const name    = params.id;
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
		if (!data.loaded) {
			if (data.location === 'local') {
				if (data instanceof FabledClass) {
					const yamlData = <MultiClassYamlData>YAML.parse(localStorage.getItem(`sapi.class.${data.name}`) || '');
					if (yamlData && Object.keys(yamlData).length > 0) {
						data.load(Object.values(yamlData)[0]);
					}
					updateClassAttributes(<FabledClass>data);
				} else if (data instanceof FabledSkill) {
					const yamlData = <MultiSkillYamlData>YAML.parse(localStorage.getItem(`sapi.skill.${data.name}`) || '');
					if (yamlData && Object.keys(yamlData).length > 0) {
						data.load(Object.values(yamlData)[0]);
					}
					data.postLoad();
				} else if (data instanceof FabledAttribute) {
					const text = localStorage.getItem('attribs') || '';
					if (text.split('\n').length > 2 || text.charAt(0) == '{') { // New format
						const yamlData = <MultiAttributeYamlData>YAML.parse(text);
						if (yamlData && Object.keys(yamlData).length > 0) {
							data.load(yamlData[data.name]);
						}
					}
				}
			} else {
				// TODO Load data from server
			}
		}
		active.set(data);
		switch (params.type) {
			case 'skill': shownTab.set(Tab.Skills); break;
			case 'attribute': shownTab.set(Tab.Attributes); break;
			default: shownTab.set(Tab.Classes); break;
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