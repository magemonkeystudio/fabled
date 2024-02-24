import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import DoubleSelectOption       from '$components/options/DoubleSelectOption.svelte';
import type { Unknown }         from '$api/types';

export default class DoubleSelect extends Requirements implements ComponentOption {
	component                   = DoubleSelectOption;
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
		const select = new DoubleSelect(this.name, this.key, this.data);
		return select;
	};

	getData = (): { [key: string]: number } => {
		const data: { [key: string]: number } = {};

		data[this.key] = this.data || 0;
		return data;
	};

	getSummary = (): string => this.data.toString();

	deserialize = (yaml: Unknown) => this.data = <number>yaml[this.key] || 0;
}