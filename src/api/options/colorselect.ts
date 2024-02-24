import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import ColorSelectOption        from '$components/options/ColorSelectOption.svelte';
import type { Unknown }         from '$api/types';

export default class ColorSelect extends Requirements implements ComponentOption {
	component                   = ColorSelectOption;
	name: string;
	key: string;
	data: string;
	tooltip: string | undefined = undefined;

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

	clone = (): ComponentOption => new ColorSelect(this.name, this.key, this.data);

	getData = (): { [key: string]: string } => {
		const data: { [key: string]: string } = {};

		data[this.key] = this.data || '#000000';
		return data;
	};

	getSummary = (): string => {
		return this.data;
	};

	deserialize = (yaml: Unknown) => this.data = <string>yaml[this.key] || '#12cfab';
}