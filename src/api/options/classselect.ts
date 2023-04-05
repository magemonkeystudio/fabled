import type { SvelteComponent } from "svelte";
import type ComponentOption     from "$api/options/options";
import type { YAMLObject }      from "$api/yaml";
import ClassSelectOption        from "$components/options/ClassSelectOption.svelte";
import ProClass                 from "$api/proclass";

export default class ClassSelect implements ComponentOption {
  component: typeof SvelteComponent = ClassSelectOption;
  name: string;
  key: string;
  data: ProClass[] | string[]       = [];
  tooltip: string | undefined       = undefined;

  constructor(name: string, key: string) {
    this.name = name;
    this.key  = key;
  }

  setTooltip = (tooltip: string): ClassSelect => {
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

    data[this.key] = this.data.map(clazz => clazz instanceof ProClass ? clazz.name : clazz);
    return data;
  };

  deserialize = (yaml: YAMLObject) => {
    this.data = yaml.get<string[], string[]>(this.key, []);
  };
}