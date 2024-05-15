<script lang="ts">
  import ProInput                  from "$input/ProInput.svelte";
  import { createEventDispatcher } from "svelte";
  import AttributeInput           from "$input/AttributeInput.svelte";
  import type { Stat } from "$api/stat";

  export let data: Stat;
  export let name: string | undefined    = "";
  export let tooltip: string | undefined = undefined;

  const dispatch = createEventDispatcher();
  $: if (data) dispatch("save");
</script>

<ProInput label={name} {tooltip}>
  <AttributeInput bind:value={data} />
</ProInput>