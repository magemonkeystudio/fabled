import type { SvelteComponent } from "svelte";
import type ComponentOption     from "$api/options/options";
import type { YAMLObject }      from "$api/yaml";
import AttributeSelectOption    from "$components/options/AttributeSelectOption.svelte";
import { ProAttribute }    from "$api/proattribute";

export default class AttributeSelect implements ComponentOption {
  component: typeof SvelteComponent     = AttributeSelectOption;
  name: string;
  key: string;
  data: ProAttribute;
  tooltip: string | undefined           = undefined;

  constructor(name: string, key: string, base = 0, scale = 0) {
    this.name       = name;
    this.key        = key;
    this.data = new ProAttribute(name, base, scale);
  }

  setTooltip = (tooltip: string): AttributeSelect => {
    this.tooltip = tooltip;
    return this;
  };

  clone = (): ComponentOption => {
    const select = new AttributeSelect(this.name, this.key, this.data.base, this.data.scale);
    return select;
  };

  getData = (): { [key: string]: any } => {
    const data: { [key: string]: any } = {};

    data[this.key + "-base"] = this.data.base || 0;
    data[this.key + "-scale"] = this.data.scale || 0;
    return data;
  };

  deserialize = (yaml: YAMLObject) => {
    this.data.base = yaml.get<number, number>(this.key + "-base", 0);
    this.data.scale = yaml.get<number, number>(this.key + "-scale", 0);
  }
}