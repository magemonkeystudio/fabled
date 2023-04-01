<script lang="ts">
  import ProInput from "$input/ProInput.svelte";
  import SearchableSelect from "$input/SearchableSelect.svelte";

  export let data: { selected: string, value: string[] };

</script>

<ProInput label="Type">
  <SearchableSelect bind:selected={data.selected} bind:data={data.value} />
</ProInput>