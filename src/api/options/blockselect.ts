import type { SvelteComponent } from "svelte";
import BlockSelectOption from "$components/options/BlockSelectOption.svelte";
import type ComponentOption from "$api/options/options";
import type { YAMLObject } from "$api/yaml";

export default class BlockSelect implements ComponentOption {
  component: typeof SvelteComponent = BlockSelectOption;
  data: { material: string[], data: number } = {
    material: ["Any"],
    data: -1
  };

  clone = (): ComponentOption => {
    const select = new BlockSelect();
    select.data = JSON.parse(JSON.stringify(this.data));
    return select;
  };

  getData = (): { [key: string]: any } => {
    const data: { [key: string]: any } = {};
    data.material = this.data.material;
    data.data = this.data.data;

    return data;
  };

  deserialize = (yaml: YAMLObject) => {
    this.data.material = yaml.get<string[], string[]>("material", ["Any"]);
    this.data.data = yaml.get<number, number>("data", -1);
  };
}