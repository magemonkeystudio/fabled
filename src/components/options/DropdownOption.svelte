<script lang="ts">
  import ProInput from "$input/ProInput.svelte";
  import SearchableSelect from "$input/SearchableSelect.svelte";
  import { createEventDispatcher } from "svelte";

  export let data: { selected: string, value: string[] };

  const dispatch = createEventDispatcher();
  $: if (data) dispatch("save");
</script>

<ProInput label="Type">
  <SearchableSelect bind:selected={data.selected} bind:data={data.value} />
</ProInput>