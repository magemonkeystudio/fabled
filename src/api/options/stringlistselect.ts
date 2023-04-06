import type { SvelteComponent } from "svelte";
import type ComponentOption     from "$api/options/options";
import type { YAMLObject }      from "$api/yaml";
import StringListOption         from "$components/options/StringListOption.svelte";

export default class StringListSelect implements ComponentOption {
  component: typeof SvelteComponent = StringListOption;
  data: { value: string[] }         = { value: [] };
  name                              = "";
  key                               = "";
  tooltip: string | undefined       = undefined;

  constructor(name: string, key: string, def?: string[]) {
    this.name = name;
    this.key  = key;

    this.data.value = def || [];
  }

  setTooltip = (tooltip: string): StringListSelect => {
    this.tooltip = tooltip;
    return this;
  };

  clone = (): ComponentOption => {
    return new StringListSelect(this.name, this.key, [...this.data.value]);
  };

  getData = (): { [key: string]: any } => {
    const data: { [key: string]: any } = {};

    data[this.key] = this.data.value;
    return data;
  };

  deserialize = (yaml: YAMLObject) => {
    this.data.value = yaml.get<string[], string[]>(this.key);
  };
}