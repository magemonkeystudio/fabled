import type { SvelteComponent } from "svelte";
import type ComponentOption     from "$api/options/options";
import type { YAMLObject }      from "$api/yaml";
import DoubleSelectOption from "$components/options/DoubleSelectOption.svelte";

export default class DoubleSelect implements ComponentOption {
  component: typeof SvelteComponent = DoubleSelectOption;
  name: string;
  key: string;
  data: number;
  tooltip: string | undefined       = undefined;

  constructor(name: string, key: string, def = 0) {
    this.name = name;
    this.key  = key;
    this.data = def;
  }

  setTooltip = (tooltip: string): DoubleSelect => {
    this.tooltip = tooltip;
    return this;
  };

  clone = (): ComponentOption => {
    const select = new DoubleSelect(this.name, this.key, this.data);
    return select;
  };

  getData = (): { [key: string]: any } => {
    const data: { [key: string]: any } = {};

    data[this.key] = this.data || 0;
    return data;
  };

  deserialize = (yaml: YAMLObject) => this.data = yaml.get<number, number>(this.key, 0);
}