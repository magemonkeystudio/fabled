import type { SvelteComponent } from "svelte";
import BlockSelectOption from "$components/options/BlockSelectOption.svelte";
import ComponentOption from "$api/options/options";

export default class BlockSelect extends ComponentOption {
  component: typeof SvelteComponent = BlockSelectOption;
  data: { value: string[], data: number } = {
    value: ["Any"],
    data: -1
  };

  clone = (): ComponentOption => {
    const select = new BlockSelect();
    select.data = JSON.parse(JSON.stringify(this.data));
    return select;
  };
}