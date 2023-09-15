import type { SvelteComponent } from "svelte";
import type { ComponentOption }     from "$api/options/options";
import type { YAMLObject }      from "$api/yaml";
import StringSelectOption       from "$components/options/StringSelectOption.svelte";
import { Requirements }         from "$api/options/options";

export default class StringSelect extends Requirements implements ComponentOption {
  component: typeof SvelteComponent = StringSelectOption;
  name: string;
  key: string;
  data: string;
  tooltip: string | undefined       = undefined;

  constructor(name: string, key: string, def = "") {
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
    const select = new StringSelect(this.name, this.key, this.data);
    return select;
  };

  getData = (): { [key: string]: any } => {
    const data: { [key: string]: any } = {};

    data[this.key] = this.data || "";
    return data;
  };

  getSummary = (): string => this.data;

  deserialize = (yaml: YAMLObject) => this.data = yaml.get(this.key, "");
}