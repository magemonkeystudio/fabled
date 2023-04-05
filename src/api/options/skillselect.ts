import type { SvelteComponent } from "svelte";
import type ComponentOption     from "$api/options/options";
import type { YAMLObject }      from "$api/yaml";
import SkillSelectOption        from "$components/options/SkillSelectOption.svelte";
import ProSkill                 from "$api/proskill";

export default class SkillSelect implements ComponentOption {
  component: typeof SvelteComponent = SkillSelectOption;
  name: string;
  key: string;
  data: ProSkill[] | string[]       = [];
  tooltip: string | undefined       = undefined;

  constructor(name: string, key: string) {
    this.name = name;
    this.key  = key;
  }

  setTooltip = (tooltip: string): SkillSelect => {
    this.tooltip = tooltip;
    return this;
  };

  clone = (): ComponentOption => {
    const select = new SkillSelect(this.name, this.key);
    select.data  = this.data;
    return select;
  };

  getData = (): { [key: string]: any } => {
    const data: { [key: string]: any } = {};

    data[this.key] = this.data.map(skill => skill instanceof ProSkill ? skill.name : skill);
    return data;
  };

  deserialize = (yaml: YAMLObject) => {
    this.data = yaml.get<string[], string[]>(this.key, []);
  };
}