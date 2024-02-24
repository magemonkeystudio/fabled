import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import StringSelectOption       from '$components/options/StringSelectOption.svelte';
import type { Unknown }         from '$api/types';

export default class StringSelect extends Requirements implements ComponentOption {
	component                   = StringSelectOption;
	name: string;
	key: string;
	data: string;
	tooltip: string | undefined = undefined;

	constructor(name: string, key: string, def = '') {
		super();
		this.name = name;
		this.key  = key;
		this.data = def;
	}

	setTooltip = (tooltip: string): this => {
		this.tooltip = tooltip;
		return this;
	};

	clone = (): ComponentOption => {
		const select = new StringSelect(this.name, this.key, this.data);
		return select;
	};

	getData = (): { [key: string]: string } => {
		const data: { [key: string]: string } = {};

		data[this.key] = this.data || '';
		return data;
	};

	getSummary = (): string => this.data;

	deserialize = (yaml: Unknown) => this.data = <string>yaml[this.key] || '';
}