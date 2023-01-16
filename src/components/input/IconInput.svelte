<script lang="ts">
  import MaterialSelect from "./MaterialSelect.svelte";
  import type { Icon } from "../../api/types";
  import LoreInput from "./LoreInput.svelte";

  export let icon: Icon;
</script>

<label>Icon</label>
<div class="input-wrapper">
  <MaterialSelect bind:selected={icon.material} />
</div>
<label for="icon-data">CustomModelData</label>
<div class="input-wrapper">
  <input id="icon-data" bind:value={icon.customModelData} />
</div>
<label>Icon Lore</label>
<div class="input-wrapper">
  <LoreInput bind:value={icon.lore} />
</div>

<style>
    label {
        text-align: right;
        padding-right: 1rem;
    }
</style>