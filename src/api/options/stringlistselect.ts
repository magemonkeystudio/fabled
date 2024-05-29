import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import StringListOption         from '$components/options/StringListOption.svelte';
import type { Unknown }         from '$api/types';

export default class StringListSelect extends Requirements implements ComponentOption {
	component                   = StringListOption;
	data: { value: string[] }   = { value: [] };
	name                        = '';
	key                         = '';
	tooltip: string | undefined = undefined;

	constructor(name: string, key: string, def?: string[]) {
		super();
		this.name = name;
		this.key  = key;

		this.data.value = def || [];
	}

	setTooltip = (tooltip: string): this => {
		this.tooltip = tooltip;
		return this;
	};

	clone = (): ComponentOption => new StringListSelect(this.name, this.key, [...this.data.value]);

	getData = (): { [key: string]: string[] } => {
		const data: { [key: string]: string[] } = {};

		data[this.key] = this.data.value;
		return data;
	};

	getSummary = (): string => this.data?.value ? this.data.value.join(', ') : '';

	deserialize = (yaml: Unknown) => this.data.value = <string[]>yaml[this.key] || [];
}