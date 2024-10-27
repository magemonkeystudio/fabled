import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import BooleanSelectOption      from '$components/options/BooleanSelectOption.svelte';
import type { Unknown }         from '$api/types';
import { parseBool }            from '$api/api';

export default class BooleanSelect extends Requirements implements ComponentOption {
	component                   = BooleanSelectOption;
	name: string;
	key: string;
	data: boolean               = $state(false);
	tooltip: string | undefined = $state(undefined);

	constructor(name: string, key: string, def = false) {
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
		return new BooleanSelect(this.name, this.key, this.data);
	};

	getData = (): { [key: string]: boolean } => {
		const data: { [key: string]: boolean } = {};

		data[this.key] = this.data;
		return data;
	};

	getSummary = (): string => {
		return this.data ? 'true' : '';
	};

	deserialize = (yaml: Unknown) => this.data = parseBool(<boolean | string>yaml[this.key]);
}