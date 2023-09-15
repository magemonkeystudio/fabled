import type { SvelteComponent } from "svelte";
import type { ComponentOption }     from "$api/options/options";
import type { YAMLObject }      from "$api/yaml";
import IntSelectOption          from "$components/options/IntSelectOption.svelte";
import { Requirements }         from "$api/options/options";

export default class IntSelect extends Requirements implements ComponentOption {
  component: typeof SvelteComponent = IntSelectOption;
  name: string;
  key: string;
  data: number;
  tooltip: string | undefined       = undefined;

  constructor(name: string, key: string, def = 0) {
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
    const select = new IntSelect(this.name, this.key, this.data);
    return select;
  };

  getData = (): { [key: string]: any } => {
    const data: { [key: string]: any } = {};

    data[this.key] = this.data || 0;
    return data;
  };

  getSummary = (): string => {
    return this.data.toString();
  };

  deserialize = (yaml: YAMLObject) => this.data = yaml.get<number, number>(this.key, 0);
}