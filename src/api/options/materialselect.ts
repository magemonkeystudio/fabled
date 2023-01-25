import type { SvelteComponent } from "svelte";
import ComponentOption from "$api/options/options";
import MaterialSelectOption from "$components/options/MaterialSelectOption.svelte";

export default class MaterialSelect extends ComponentOption {
  component: typeof SvelteComponent = MaterialSelectOption;
  data = "Dirt";

  clone = (): ComponentOption => {
    const select = new MaterialSelect();
    select.data = this.data;
    return select;
  };
}