<script lang="ts">
  import ProInput                  from "$input/ProInput.svelte";
  import { createEventDispatcher } from "svelte";
  import Toggle                    from "$input/Toggle.svelte";

  export let data: boolean;
  export let any: boolean;
  export let name: string | undefined    = "";
  export let tooltip: string | undefined = undefined;

  const dispatch = createEventDispatcher();
  $: if (data) dispatch("save");
</script>

<ProInput label={name} {tooltip}>
  <Toggle bind:data={data} />
</ProInput>