import type { SvelteComponent } from "svelte";
import type { ComponentOption }     from "$api/options/options";
import { Requirements }         from "$api/options/options";
import type { YAMLObject }      from "$api/yaml";
import BooleanSelectOption      from "$components/options/BooleanSelectOption.svelte";

export default class BooleanSelect extends Requirements implements ComponentOption {
  component: typeof SvelteComponent = BooleanSelectOption;
  name: string;
  key: string;
  data: boolean;
  tooltip: string | undefined       = undefined;

  constructor(name: string, key: string, def = false) {
    super();
    this.name = name;
    this.key  = key;
    this.data = def;
  }

  setTooltip = (tooltip: string): BooleanSelect => {
    this.tooltip = tooltip;
    return this;
  };

  clone = (): ComponentOption => {
    const select = new BooleanSelect(this.name, this.key, this.data);
    return select;
  };

  getData = (): { [key: string]: any } => {
    const data: { [key: string]: any } = {};

    data[this.key] = !!this.data;
    return data;
  };

  deserialize = (yaml: YAMLObject) => this.data = yaml.get<boolean, boolean>(this.key, false);
}