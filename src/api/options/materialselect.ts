import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import MaterialSelectOption     from '$components/options/MaterialSelectOption.svelte';
import type { Unknown }         from '$api/types';

export default class MaterialSelect extends Requirements implements ComponentOption {
	component                   = MaterialSelectOption;
	key                         = 'material';
	data                        = { material: 'Dirt', any: false };
	tooltip: string | undefined = undefined;

	constructor(any = true, def?: string) {
		super();
		this.data.any = any;
		if (def) this.data.material = def;
	}

	setTooltip = (tooltip: string): this => {
		this.tooltip = tooltip;
		return this;
	};

	clone = (): ComponentOption => {
		const select         = new MaterialSelect();
		select.data.material = this.data.material;
		select.data.any      = this.data.any;
		return select;
	};

	getData = (): { [key: string]: string } => {
		const data: { [key: string]: string } = {};
		data.material                         = this.data.material;

		return data;
	};

	getSummary = (): string => this.data.material;

	deserialize = (yaml: Unknown) => this.data.material = <string>yaml[this.key] || 'Dirt';
}