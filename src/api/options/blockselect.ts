import type { SvelteComponent } from "svelte";
import BlockSelectOption from "$components/options/BlockSelectOption.svelte";
import type ComponentOption from "$api/options/options";

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
}