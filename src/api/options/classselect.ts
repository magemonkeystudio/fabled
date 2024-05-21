import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import ClassSelectOption        from '$components/options/ClassSelectOption.svelte';
import type { Unknown }         from '$api/types';
import FabledClass              from '../../data/class-store';

export default class ClassSelect extends Requirements implements ComponentOption {
	component                                             = ClassSelectOption;
	name: string;
	key: string;
	data: FabledClass[] | FabledClass | string[] | string = [];
	tooltip: string | undefined                           = undefined;
	multiple                                              = true;

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

	getData = (): { [key: string]: FabledClass[] | FabledClass | string[] | string } => {
		const data: { [key: string]: FabledClass[] | FabledClass | string[] | string } = {};

		if (this.data instanceof Array)
			data[this.key] = this.data.map(cl => cl instanceof FabledClass ? cl.name : cl);
		else
			data[this.key] = this.data instanceof FabledClass ? this.data.name : this.data;
		return data;
	};

	getSummary = (): string => {
		if (this.data instanceof Array) {
			// Join the names of the classes with commas
			return this.data.map(cl => cl instanceof FabledClass ? cl.name : cl).join(', ');
		} else if (this.data instanceof FabledClass) {
			// Return the name of the class
			return this.data.name;
		} else {
			return this.data;
		}
	};

	deserialize = (yaml: Unknown) => this.data = <string | string[]>yaml[this.key] || (this.multiple ? [] : '');
}