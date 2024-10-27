import type { ComponentOption }     from '$api/options/options';
import { Requirements }             from '$api/options/options';
import AttributeSelectOption        from '$components/options/AttributeSelectOption.svelte';
import type { IAttribute, Unknown } from '$api/types';

export default class AttributeSelect extends Requirements implements ComponentOption {
	component        = AttributeSelectOption;
	name: string;
	key: string;
	data: IAttribute = $state({
		name:  '',
		base:  0,
		scale: 0
	});
	tooltip?: string = $state(undefined);

	constructor(name: string, key: string, base: number | string = 0, scale: number | string = 0) {
		super();
		this.name       = name;
		this.key        = key;
		this.data.name  = name;
		this.data.base  = base;
		this.data.scale = scale;
	}

	setTooltip = (tooltip: string): this => {
		this.tooltip = tooltip;
		return this;
	};

	clone = (): ComponentOption => {
		return new AttributeSelect(this.name, this.key, this.data?.base, this.data.scale);
	};

	getData = (): { [key: string]: number | string } => {
		const data: { [key: string]: number | string } = {};

		data[`${this.key}-base`]  = this.data.base || 0;
		data[`${this.key}-scale`] = this.data.scale || 0;
		return data;
	};

	getSummary = (): string => {
		return this.data.base.toString();
	};

	deserialize = (yaml: Unknown) => {
		this.data.base  = <number>yaml[`${this.key}-base`] || 0;
		this.data.scale = <number>yaml[`${this.key}-scale`] || 0;
	};
}