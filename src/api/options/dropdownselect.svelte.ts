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
	}                           = $state({
		selected: '',
		value:    [],
		multiple: false
	});
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
		if (def) {
			// Normalize the default so it always matches the select mode,
			// as a scalar default on a multi-select breaks filtering/removal
			if (multiple && !(def instanceof Array)) this.data.selected = [def];
			else if (!multiple && def instanceof Array) this.data.selected = def[0];
			else this.data.selected = def;
		}

		this.data.multiple = multiple;
	}

	public init = () => {
		if (this.dataSource) {
			const value = this.dataSource();
			// Only write when the contents actually changed. init() runs inside an
			// effect that also reads this state, so an unconditional write loops forever
			if (value.length !== this.data.value.length || value.some((item, i) => item !== this.data.value[i]))
				this.data.value = value;
		}

		if (!this.data.selected && this.data.value.length > 0 && !this.data.multiple)
			this.data.selected = this.data.value[0];
	};

	setTooltip = (tooltip: string): this => {
		this.tooltip = tooltip;
		return this;
	};

	clone = (): DropdownSelect => {
		return new DropdownSelect(
			this.name,
			this.key,
			this.dataSource || [...this.data.value],
			this.data.multiple ? [...this.data.selected] : this.data.selected,
			this.data.multiple);
	};

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
		const val = <string[] | string>yaml[this.key];
		// If selected is not a list and multiple is true, convert it to a list
		if (val !== undefined) {
			if (this.data.multiple && !(val instanceof Array)) this.data.selected = [val];
			else if (!this.data.multiple && val instanceof Array) this.data.selected = val[0];
			else this.data.selected = val;
		}
	};
}