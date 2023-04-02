import type { SvelteComponent } from "svelte";
import type ComponentOption from "$api/options/options";
import DropdownOption from "$components/options/DropdownOption.svelte";
import type { YAMLObject } from "$api/yaml";

export default class DropdownSelect implements ComponentOption {
  component: typeof SvelteComponent = DropdownOption;
  data: {
    selected: string,
    value: string[]
  } = {
    selected: "",
    value: []
  };
  name = "";

  constructor(name: string, items: string[], def?: string) {
    this.name = name;
    this.data.value = items;
    if (def) this.data.selected = def;
    else if (items.length > 0) this.data.selected = items[0];
  }

  clone = (): ComponentOption => {
    return new DropdownSelect(this.name, [...this.data.value], this.data.selected);
  };

  getData = (): { [key: string]: any } => {
    const data: { [key: string]: any } = {};
    data[this.name] = this.data.selected;

    return data;
  };

  deserialize = (yaml: YAMLObject) => {
    this.data.selected = yaml.get<string, string>(this.name);
  };
}