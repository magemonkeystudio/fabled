import type { SvelteComponent } from "svelte";
import type ComponentOption     from "$api/options/options";
import DropdownOption           from "$components/options/DropdownOption.svelte";
import type { YAMLObject }      from "$api/yaml";

export default class DropdownSelect implements ComponentOption {
  component: typeof SvelteComponent = DropdownOption;
  dataSource: (() => string[]) | undefined;
  data: {
    selected: string,
    value: string[]
  }                                 = {
    selected: "",
    value:    []
  };
  name                              = "";
  key                               = "";
  tooltip: string | undefined       = undefined;

  constructor(name: string, key: string, items: string[] | (() => string[]), def?: string) {
    this.name = name;
    this.key  = key;

    if (typeof items === "function") this.dataSource = items;
    else this.data.value = items;
    if (def) this.data.selected = def;
  }

  init = () => {
    if (this.dataSource) {
      this.data.value = this.dataSource();
    }

    if (!this.data.selected && this.data.value.length > 0)
      this.data.selected = this.data.value[0];
  };

  setTooltip = (tooltip: string): DropdownSelect => {
    this.tooltip = tooltip;
    return this;
  };

  clone = (): ComponentOption => {
    return new DropdownSelect(this.name, this.key, [...this.data.value], this.data.selected);
  };

  getData = (): { [key: string]: any } => {
    const data: { [key: string]: any } = {};

    data[this.key] = this.data.selected;
    return data;
  };

  deserialize = (yaml: YAMLObject) => {
    this.data.selected = yaml.get<string, string>(this.key);
  };
}