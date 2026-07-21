import { type ComponentOption, Requirements } from '$api/options/options';
import SkillSelectOption                      from '$components/options/SkillSelectOption.svelte';
import type { Unknown }                       from '$api/types';
import FabledSkill, { skillStore }            from '../../data/skill-store.svelte';

export default class SkillSelect extends Requirements implements ComponentOption {
	component                                             = SkillSelectOption;
	name: string;
	key: string;
	data: FabledSkill[] | FabledSkill | string[] | string = $state([]);
	tooltip: string | undefined                           = $state();
	multiple                                              = $state(true);

	constructor(name: string, key: string, multiple = true) {
		super();
		this.name     = name;
		this.key      = key;
		this.multiple = multiple;
		this.data     = multiple ? [] : '';
	}

	setTooltip = (tooltip: string): this => {
		this.tooltip = tooltip;
		return this;
	};

	clone = (): ComponentOption => {
		const select = new SkillSelect(this.name, this.key, this.multiple);
		select.data  = this.data instanceof Array ? [...this.data] : this.data;
		return select;
	};

	getData = (): { [key: string]: FabledSkill[] | FabledSkill | string[] | string } => {
		const data: { [key: string]: FabledSkill[] | FabledSkill | string[] | string } = {};

		if (this.data instanceof Array)
			data[this.key] = this.data.map(skill => skill instanceof FabledSkill ? skill.name : skill);
		else
			data[this.key] = this.data instanceof FabledSkill ? this.data.name : this.data;
		return data;
	};

	getSummary = (): string => {
		if (this.data instanceof Array)
			return this.data.map(skill => skill instanceof FabledSkill ? skill.name : skill).join(', ');
		else
			return this.data instanceof FabledSkill ? this.data.name : this.data;
	};

	deserialize = (yaml: Unknown) => {
		const skillName = <string | string[]>yaml[this.key];

		// Let's attempt to get the skill from the skill store before creating a dummy skill for display
		const lookup = (skill: string) => skillStore.getSkill(skill) || new FabledSkill({ name: skill });

		// Coerce the stored value to match the select mode, as a scalar on a
		// multi-select (or a list on a single select) breaks the dropdown
		if (this.multiple) {
			const names = !skillName ? [] : skillName instanceof Array ? skillName : [skillName];
			this.data   = names.map(lookup);
		} else {
			const name = skillName instanceof Array ? skillName[0] : skillName;
			this.data  = name ? lookup(name) : '';
		}
	};
}