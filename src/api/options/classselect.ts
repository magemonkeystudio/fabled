import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import ClassSelectOption        from '$components/options/ClassSelectOption.svelte';
import ProClass                 from '$api/proclass';
import type { Unknown }         from '$api/types';

export default class ClassSelect extends Requirements implements ComponentOption {
	component                                       = ClassSelectOption;
	name: string;
	key: string;
	data: ProClass[] | ProClass | string[] | string = [];
	tooltip: string | undefined                     = undefined;
	multiple                                        = true;

	constructor(name: string, key: string, multiple = true) {
		super();
		this.name     = name;
		this.key      = key;
		this.multiple = multiple;
	}

	setTooltip = (tooltip: string): this => {
		this.tooltip = tooltip;
		return this;
	};

	clone = (): ComponentOption => {
		const select = new ClassSelect(this.name, this.key);
		select.data  = this.data;
		return select;
	};

	getData = (): { [key: string]: ProClass[] | ProClass | string[] | string } => {
		const data: { [key: string]: ProClass[] | ProClass | string[] | string } = {};

		if (this.data instanceof Array)
			data[this.key] = this.data.map(cl => cl instanceof ProClass ? cl.name : cl);
		else
			data[this.key] = this.data instanceof ProClass ? this.data.name : this.data;
		return data;
	};

	getSummary = (): string => {
		if (this.data instanceof Array) {
			// Join the names of the classes with commas
			return this.data.map(cl => cl instanceof ProClass ? cl.name : cl).join(', ');
		} else if (this.data instanceof ProClass) {
			// Return the name of the class
			return this.data.name;
		} else {
			return this.data;
		}
	};

	deserialize = (yaml: Unknown) => this.data = <string | string[]>yaml[this.key] || (this.multiple ? [] : '');
}