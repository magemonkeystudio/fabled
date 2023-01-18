<script lang="ts">
  import MaterialSelect from "./MaterialSelect.svelte";
  import type { Icon } from "../../api/types";
  import LoreInput from "./LoreInput.svelte";
  import ProInput from "./ProInput.svelte";
  import { activeType } from "../../data/store";

  export let icon: Icon;
</script>

<ProInput label="Icon"
          tooltip="The item that represents the {$activeType} in GUIs">
  <MaterialSelect bind:selected={icon.material} />
</ProInput>
<ProInput label="CustomModelData" type="number" intMode={true}
          tooltip="The CustomModelData of the item that represents the {$activeType} in GUIs"
          bind:value={icon.customModelData} />
<ProInput label="Icon Lore"
          tooltip="The item's lore description">
  <LoreInput bind:value={icon.lore} />
</ProInput>