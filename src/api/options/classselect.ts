import type { SvelteComponent } from "svelte";
import type { ComponentOption }     from "$api/options/options";
import type { YAMLObject }      from "$api/yaml";
import ClassSelectOption        from "$components/options/ClassSelectOption.svelte";
import ProClass                 from "$api/proclass";
import { Requirements }         from "$api/options/options";
import ProSkill                 from "$api/proskill";

export default class ClassSelect extends Requirements implements ComponentOption {
  component: typeof SvelteComponent = ClassSelectOption;
  name: string;
  key: string;
  data: ProClass[] | string[] | ProClass | string       = [];
  tooltip: string | undefined       = undefined;
  multiple = true;

  constructor(name: string, key: string, multiple = true) {
    super();
    this.name = name;
    this.key  = key;
    this.multiple = multiple;
  }

  setTooltip = (tooltip: string): this => {
    this.tooltip = tooltip;
    return this;
  };

  clone = (): ComponentOption => {
    const select = new ClassSelect(this.name, this.key);
    select.data  = this.data;
    return select;
  };

  getData = (): { [key: string]: any } => {
    const data: { [key: string]: any } = {};

    if (this.data instanceof Array)
      data[this.key] = this.data.map(cl => cl instanceof ProClass ? cl.name : cl);
    else
      data[this.key] = this.data instanceof ProClass ? this.data.name : this.data;
    return data;
  };

  deserialize = (yaml: YAMLObject) => {
    this.data = yaml.get(this.key, this.multiple ? [] : "");
  };
}