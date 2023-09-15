import type { SvelteComponent } from 'svelte';
import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import type { YAMLObject }      from '$api/yaml';
import ColorSelectOption        from '$components/options/ColorSelectOption.svelte';

export default class ColorSelect extends Requirements implements ComponentOption {
	component: typeof SvelteComponent = ColorSelectOption;
	name: string;
	key: string;
	data: string;
	tooltip: string | undefined       = undefined;

	constructor(name: string, key: string, def = '#12cfab') {
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
		const select = new ColorSelect(this.name, this.key, this.data);
		return select;
	};

	getData = (): { [key: string]: any } => {
		const data: { [key: string]: any } = {};

		data[this.key] = this.data || 0;
		return data;
	};

	getSummary = (): string => {
		return this.data;
	};

	deserialize = (yaml: YAMLObject) => this.data = yaml.get(this.key, '#12cfab');
}