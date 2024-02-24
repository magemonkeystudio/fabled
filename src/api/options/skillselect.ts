import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import SkillSelectOption        from '$components/options/SkillSelectOption.svelte';
import ProSkill                 from '$api/proskill';
import type { Unknown }         from '$api/types';

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

	deserialize = (yaml: Unknown) => this.data = <string[] | string>yaml[this.key] || this.multiple ? [] : '';
}