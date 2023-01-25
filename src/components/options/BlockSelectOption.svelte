<script lang="ts">
  import MaterialSelect from "$input/MaterialSelect.svelte";
  import ProInput from "$input/ProInput.svelte";

  // export let value: string[] = [];
  // export let dataValue = -1;
  export let data: { value: string[], dataValue: number };

  // $: data = { value, data: dataValue };
</script>

<ProInput label="Material">
  <MaterialSelect any bind:selected={data.value} multiple />
</ProInput>
<ProInput type="number" intMode
          label="Data"
          bind:value={data.data} />