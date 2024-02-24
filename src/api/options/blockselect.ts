import BlockSelectOption        from '$components/options/BlockSelectOption.svelte';
import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import type { Unknown }         from '$api/types';

export default class BlockSelect extends Requirements implements ComponentOption {
	key                                                                                      = 'block';
	component                                                                                = BlockSelectOption;
	data: { material: string[], data: number, materialTooltip: string, dataTooltip: string } = {
		material:        ['Any'],
		data:            -1,
		materialTooltip: '',
		dataTooltip:     ''
	};

	public constructor(materialTooltip?: string, dataTooltip?: string) {
		super();
		this.data.materialTooltip = materialTooltip || '';
		this.data.dataTooltip     = dataTooltip || '';
	}

	setTooltip = () => {
		throw new Error('Block Select requires tooltips be set in the constructor');
	};

	clone = (): ComponentOption => {
		const select = new BlockSelect();
		select.data  = JSON.parse(JSON.stringify(this.data));
		return select;
	};

	getData = (): { [key: string]: string[] | number } => {
		const data: { [key: string]: string[] | number } = {};
		data.material                                    = this.data.material;
		data.data                                        = this.data.data ?? -1;

		return data;
	};

	getSummary = (): string => {
		return this.data.material + (this.data.data != -1 ? ':' + this.data.data : '');
	};

	deserialize = (yaml: Unknown) => {
		this.data.material = <string[]>yaml.material || ['Any'];
		this.data.data     = <number>yaml.data || -1;
	};
}