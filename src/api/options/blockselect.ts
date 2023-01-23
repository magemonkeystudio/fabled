import type { SvelteComponent } from "svelte";
import BlockSelectComp from "../../components/options/BlockSelectComp.svelte";

export default class BlockSelect {
  component: typeof SvelteComponent = BlockSelectComp;
  data: { value: string[], data: number } = {
    value: [],
    data: -1
  };
}