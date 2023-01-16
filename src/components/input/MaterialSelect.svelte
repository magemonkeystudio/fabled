<script lang="ts">
  import SearchableSelect from "./SearchableSelect.svelte";
  import { DATA } from "../../version/data";

  export let id: string | undefined = undefined;
  export let placeholder = "";
  export let multiple = false;
  export let selected: any[] | any = undefined;
</script>

<SearchableSelect
  {id}
  {placeholder}
  {multiple}
  bind:selected={selected}
  data={DATA.MATERIALS}
/>