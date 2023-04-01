<script lang="ts">
  import MaterialSelect from "$input/MaterialSelect.svelte";
  import ProInput from "$input/ProInput.svelte";

  export let data: { material: string, any?: boolean };
  export let any: boolean;
</script>

<ProInput label="Material">
  <MaterialSelect bind:selected={data.material} bind:any={data.any} />
</ProInput>