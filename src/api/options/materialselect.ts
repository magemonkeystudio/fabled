import type { SvelteComponent } from "svelte";
import type ComponentOption from "$api/options/options";
import MaterialSelectOption from "$components/options/MaterialSelectOption.svelte";

export default class MaterialSelect implements ComponentOption {
  component: typeof SvelteComponent = MaterialSelectOption;
  data = {material: "Dirt", any: false};

  constructor(any = true) {
    this.data.any = any;
  }

  clone = (): ComponentOption => {
    const select = new MaterialSelect();
    select.data.material = this.data.material;
    select.data.any = this.data.any;
    return select;
  };

  getData = (): { [key: string]: any } => {
    const data: { [key: string]: any } = {};
    data.material = this.data.material;

    return data;
  };
}