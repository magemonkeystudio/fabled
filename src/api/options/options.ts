import type FabledComponent from '$api/components/fabled-component';
import type { Component } from 'svelte';

export interface ComponentOption extends Cloneable<ComponentOption> {
	key: string;
	getData: () => { [key: string]: unknown };
	deserialize: (yaml: { [key: string]: unknown }) => void;
	setTooltip: (tooltip: string) => this;
	meetsRequirements: (comp: FabledComponent) => boolean;
	meetsPreviewRequirements: (comp: FabledComponent) => boolean;
	getSummary: () => string;

	component?: Component<any, any, any>;
	data?: unknown;
	name?: string;
	tooltip?: string;
	multiple?: boolean;
}

interface Cloneable<T> {
	clone: () => T;
}

export abstract class Requirements {
	private requirements: { [key: string]: any[] } = {};
	public requireValue = (key: string, value: any[]): this => {
		this.requirements[key] = value;
		return this;
	};

	meetsRequirements = (comp: FabledComponent): boolean => {
		for (const key in this.requirements) {
			if (!this.requirements[key].includes(comp.getData(true)[key])) return false;
		}
		return true;
	};

	meetsPreviewRequirements = (comp: FabledComponent): boolean => {
		for (const key in this.requirements) {
			if (!this.requirements[key].includes(comp.getRawPreviewData()[key])) return false;
		}
		return true;
	};
}
