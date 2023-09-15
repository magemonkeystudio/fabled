import type { SvelteComponent } from 'svelte';
import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import type { YAMLObject }      from '$api/yaml';
import DoubleSelectOption       from '$components/options/DoubleSelectOption.svelte';

export default class DoubleSelect extends Requirements implements ComponentOption {
	component: typeof SvelteComponent = DoubleSelectOption;
	name: string;
	key: string;
	data: number;
	tooltip: string | undefined       = undefined;

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

	getData = (): { [key: string]: any } => {
		const data: { [key: string]: any } = {};

		data[this.key] = this.data || 0;
		return data;
	};

	getSummary = (): string => {
		return this.data.toString();
	};

	deserialize = (yaml: YAMLObject) => this.data = yaml.get<number, number>(this.key, 0);
}