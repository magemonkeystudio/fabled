<script lang="ts">
  import MaterialSelect from "$input/MaterialSelect.svelte";
  import ProInput from "$input/ProInput.svelte";

  export let value: string[] = [];
  export let dataValue = -1;
  export let data: any;

  $: data = { value, data: dataValue };
</script>

<div>
  <MaterialSelect any bind:selected={value} multiple/>
  <ProInput type="number" intMode bind:value={dataValue} />
</div>

<style>

</style>