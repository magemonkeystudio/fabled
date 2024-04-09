import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import type { Unknown }         from '$api/types';
import EnchantSelectOption      from '$components/options/EnchantSelectOption.svelte';

export type Enchant = { name: string, level: number };

export default class EnchantSelect extends Requirements implements ComponentOption {
	component                     = EnchantSelectOption;
	key                           = 'enchant';
	data: { enchants: Enchant[] } = { enchants: [] };
	tooltip: string | undefined   = undefined;

	constructor(def?: Enchant) {
		super();
		if (def) this.data.enchants = [def];
	}

	setTooltip = (tooltip: string): this => {
		this.tooltip = tooltip;
		return this;
	};

	clone = (): ComponentOption => {
		const select         = new EnchantSelect();
		select.data.enchants = [...this.data.enchants];
		return select;
	};

	getData = (): { [key: string]: string[] } => {
		const data: { [key: string]: string[] } = {};
		data.enchants                           = this.data.enchants.map(({ name, level }) => `${name}:${level}`);

		return data;
	};

	getSummary = (): string => this.data.enchants.map(({ name, level }) => `${name} ${level}`).join(', ');

	deserialize = (yaml: Unknown) => {
		const raw = <string[]>yaml[this.key] || [];

		this.data.enchants = raw.map((str) => {
			const [name, level] = str.split(':');
			return { name, level: parseInt(level) };
		});
	};
}