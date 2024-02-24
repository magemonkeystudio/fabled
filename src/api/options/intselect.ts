import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import IntSelectOption          from '$components/options/IntSelectOption.svelte';
import type { Unknown }         from '$api/types';

export default class IntSelect extends Requirements implements ComponentOption {
	component                   = IntSelectOption;
	name: string;
	key: string;
	data: number;
	tooltip: string | undefined = undefined;

	constructor(name: string, key: string, def = 0) {
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
		const select = new IntSelect(this.name, this.key, this.data);
		return select;
	};

	getData = (): { [key: string]: number } => {
		const data: { [key: string]: number } = {};

		data[this.key] = this.data || 0;
		return data;
	};

	getSummary = (): string => {
		return this.data.toString();
	};

	deserialize = (yaml: Unknown) => this.data = <number>yaml[this.key] || 0;
}