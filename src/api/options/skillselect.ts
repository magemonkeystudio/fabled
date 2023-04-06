import type { SvelteComponent } from "svelte";
import type ComponentOption     from "$api/options/options";
import { Requirements }         from "$api/options/options";
import type { YAMLObject }      from "$api/yaml";
import SkillSelectOption        from "$components/options/SkillSelectOption.svelte";
import ProSkill                 from "$api/proskill";

export default class SkillSelect extends Requirements implements ComponentOption {
  component: typeof SvelteComponent               = SkillSelectOption;
  name: string;
  key: string;
  data: ProSkill[] | string[] | ProSkill | string = [];
  tooltip: string | undefined                     = undefined;
  multiple                                        = true;

  constructor(name: string, key: string, multiple = true) {
    super();
    this.name     = name;
    this.key      = key;
    this.multiple = multiple;
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

    if (this.data instanceof Array)
      data[this.key] = this.data.map(skill => skill instanceof ProSkill ? skill.name : skill);
    else
      data[this.key] = this.data instanceof ProSkill ? this.data.name : this.data;
    return data;
  };

  deserialize = (yaml: YAMLObject) => {
    this.data = yaml.get(this.key, this.multiple ? [] : "");
  };
}