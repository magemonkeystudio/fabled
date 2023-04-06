<script lang="ts">
  import ProInput from "$input/ProInput.svelte";
  import SearchableSelect from "$input/SearchableSelect.svelte";
  import { createEventDispatcher } from "svelte";

  export let data: { selected: string | string[], value: string[], multiple: boolean };
  export let name: string | undefined = "";
  export let tooltip: string | undefined = undefined;

  const dispatch = createEventDispatcher();
  $: if (data) dispatch("save");
</script>
<ProInput label={name} {tooltip}>
  <SearchableSelect bind:selected={data.selected} bind:data={data.value} bind:multiple={data.multiple} />
</ProInput>