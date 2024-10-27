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
	}

	setTooltip = (tooltip: string): this => {
		this.tooltip = tooltip;
		return this;
	};

	clone = (): ComponentOption => {
		const select = new SkillSelect(this.name, this.key);
		select.data  = this.data;
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
		if (skillName instanceof Array) {
			this.data = skillName.map(skill => skillStore.getSkill(skill) || new FabledSkill({ name: skill }));
		} else if (skillName)
			this.data = skillStore.getSkill(skillName) || new FabledSkill({ name: skillName });
		else
			this.data = this.multiple ? [] : '';
	};
}