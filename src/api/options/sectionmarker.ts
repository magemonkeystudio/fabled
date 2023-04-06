import type { SvelteComponent } from "svelte";
import type ComponentOption     from "$api/options/options";
import type { YAMLObject }      from "$api/yaml";
import SectionMarkerComponent   from "$input/SectionMarkerComponent.svelte";

export default class SectionMarker implements ComponentOption {
  component: typeof SvelteComponent = SectionMarkerComponent;
  data: string;

  constructor(name: string) {
    this.data = name;
  }

  setTooltip = (_: string): SectionMarker => this;

  clone = (): ComponentOption => new SectionMarker(this.data);

  getData = (): { [key: string]: any } => ({});

  deserialize = (_: YAMLObject) => {
    // No-op
  };
}