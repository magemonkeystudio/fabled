<script lang="ts">
  import MaterialSelect from "$input/MaterialSelect.svelte";
  import ProInput from "$input/ProInput.svelte";
  import { createEventDispatcher } from "svelte";

  export let data: { material: string[], data: number };

  const dispatch = createEventDispatcher();
  $: if (data) dispatch("save");
</script>

<ProInput label="Material">
  <MaterialSelect any bind:selected={data.material} multiple />
</ProInput>
<ProInput type="number" intMode
          label="Data"
          bind:value={data.data} />