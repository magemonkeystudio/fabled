import type { SvelteComponent } from "svelte";
import type { ComponentOption }     from "$api/options/options";
import type { YAMLObject }      from "$api/yaml";
import SectionMarkerComponent   from "$input/SectionMarkerComponent.svelte";
import { Requirements }         from "$api/options/options";

export default class SectionMarker extends Requirements implements ComponentOption {
  key = "section-marker";
  component: typeof SvelteComponent<any> = SectionMarkerComponent;
  data: string;

  constructor(name: string) {
    super();
    this.data = name;
  }

  setTooltip = (tooltip: string): this => this;

  clone = (): ComponentOption => new SectionMarker(this.data);

  getData = (): { [key: string]: any } => ({});

  getSummary = (): string => this.data;

  deserialize = (_: YAMLObject) => {
    // No-op
  };
}