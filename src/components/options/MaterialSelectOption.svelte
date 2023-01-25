<script lang="ts">
  import MaterialSelect from "$input/MaterialSelect.svelte";
  import ProInput from "$input/ProInput.svelte";

  export let data: string;
</script>

<ProInput label="Material">
  <MaterialSelect bind:selected={data} />
</ProInput>