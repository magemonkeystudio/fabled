import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import AttributeSelectOption from '$components/options/AttributeSelectOption.svelte';
import { Attribute }   from '$api/stat';
import type { Unknown }      from '$api/types';

export default class AttributeSelect extends Requirements implements ComponentOption {
	component                   = AttributeSelectOption;
	name: string;
	key: string;
	data: Attribute;
	tooltip: string | undefined = undefined;

	constructor(name: string, key: string, base = 0, scale = 0) {
		super();
		this.name = name;
		this.key  = key;
		this.data = new Attribute(name, base, scale);
	}

	setTooltip = (tooltip: string): this => {
		this.tooltip = tooltip;
		return this;
	};

	clone = (): ComponentOption => {
		const select = new AttributeSelect(this.name, this.key, this.data.base, this.data.scale);
		return select;
	};

	getData = (): { [key: string]: number } => {
		const data: { [key: string]: number } = {};

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