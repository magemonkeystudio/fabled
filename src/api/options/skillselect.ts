import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import SkillSelectOption        from '$components/options/SkillSelectOption.svelte';
import ProSkill                 from '$api/proskill';
import type { Unknown }         from '$api/types';
import { getSkill }             from '../../data/skill-store';

export default class SkillSelect extends Requirements implements ComponentOption {
	component                                       = SkillSelectOption;
	name: string;
	key: string;
	data: ProSkill[] | ProSkill | string[] | string = [];
	tooltip: string | undefined                     = undefined;
	multiple                                        = true;

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

	getData = (): { [key: string]: ProSkill[] | ProSkill | string[] | string } => {
		const data: { [key: string]: ProSkill[] | ProSkill | string[] | string } = {};

		if (this.data instanceof Array)
			data[this.key] = this.data.map(skill => skill instanceof ProSkill ? skill.name : skill);
		else
			data[this.key] = this.data instanceof ProSkill ? this.data.name : this.data;
		return data;
	};

	getSummary = (): string => {
		if (this.data instanceof Array)
			return this.data.map(skill => skill instanceof ProSkill ? skill.name : skill).join(', ');
		else
			return this.data instanceof ProSkill ? this.data.name : this.data;
	};

	deserialize = (yaml: Unknown) => {
		const skillName = <string | string[]>yaml[this.key];

		// Let's attempt to get the skill from the skill store before creating a dummy skill for display
		if (skillName instanceof Array) {
			this.data = skillName.map(skill => getSkill(skill) || new ProSkill({ name: skill }));
		} else if (skillName)
			this.data = getSkill(skillName) || new ProSkill({ name: skillName });
		else
			this.data = this.multiple ? [] : '';
	};
}