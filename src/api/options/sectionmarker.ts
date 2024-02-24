import type { ComponentOption } from '$api/options/options';
import { Requirements }         from '$api/options/options';
import SectionMarkerComponent   from '$input/SectionMarkerComponent.svelte';

export default class SectionMarker extends Requirements implements ComponentOption {
	key       = 'section-marker';
	component = SectionMarkerComponent;
	data: string;

	constructor(name: string) {
		super();
		this.data = name;
	}

	setTooltip = (): this => this;

	clone = (): ComponentOption => new SectionMarker(this.data);

	getData = (): { [key: string]: unknown } => ({});

	getSummary = (): string => this.data;

	deserialize = () => {
		// No-op
	};
}