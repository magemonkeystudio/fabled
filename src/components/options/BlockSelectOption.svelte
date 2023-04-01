<script lang="ts">
  import MaterialSelect from "$input/MaterialSelect.svelte";
  import ProInput from "$input/ProInput.svelte";

  // export let value: string[] = [];
  // export let dataValue = -1;
  export let data: { material: string[], data: number };

  // $: data = { value, data: dataValue };
</script>

<ProInput label="Material">
  <MaterialSelect any bind:selected={data.material} multiple />
</ProInput>
<ProInput type="number" intMode
          label="Data"
          bind:value={data.data} />