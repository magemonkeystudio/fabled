import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import DropdownOption           from '$components/options/DropdownOption.svelte';
import type { Unknown }         from '$api/types';

export default class DropdownSelect extends Requirements implements ComponentOption {
	component                   = DropdownOption;
	dataSource: (() => string[]) | undefined;
	data: {
		selected: string | string[],
		value: string[],
		multiple: boolean
	}                           = {
		selected: '',
		value:    [],
		multiple: false
	};
	name                        = '';
	key                         = '';
	tooltip: string | undefined = undefined;

	constructor(name: string, key: string, items: string[] | (() => string[]), def?: string | string[], multiple = false) {
		super();
		this.name = name;
		this.key  = key;

		if (typeof items === 'function') this.dataSource = items;
		else this.data.value = items;
		if (multiple) this.data.selected = [];
		if (def) this.data.selected = def;

		this.data.multiple = multiple;
	}

	init = () => {
		if (this.dataSource) this.data.value = this.dataSource();

		if (!this.data.selected && this.data.value.length > 0 && !this.data.multiple)
			this.data.selected = this.data.value[0];
	};

	setTooltip = (tooltip: string): this => {
		this.tooltip = tooltip;
		return this;
	};

	clone = (): DropdownSelect => new DropdownSelect(this.name, this.key, [...this.data.value], this.data.selected);

	getData = (): { [key: string]: unknown } => {
		const data: { [key: string]: unknown } = {};

		data[this.key] = this.data.selected;
		return data;
	};

	getSummary = (): string => {
		if (this.data.selected instanceof Array) return this.data.selected.join(', ');
		return this.data.selected;
	};

	deserialize = (yaml: Unknown) => {
		this.data.selected = <string[] | string>yaml[this.key];
		// If selected is not a list and multiple is true, convert it to a list
		if (this.data.multiple && !(this.data.selected instanceof Array)) this.data.selected = [this.data.selected];
		else if (!this.data.multiple && this.data.selected instanceof Array) this.data.selected = this.data.selected[0];
	}
}