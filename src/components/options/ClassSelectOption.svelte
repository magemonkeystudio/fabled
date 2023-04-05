<script lang="ts">
  import ProInput                  from "$input/ProInput.svelte";
  import { createEventDispatcher } from "svelte";
  import SearchableSelect          from "$input/SearchableSelect.svelte";
  import ProClass         from "$api/proclass";
  import { classes, getClass } from "../../data/class-store";

  export let data: ProClass[] | string[] = [];
  export let any: boolean;
  export let name: string | undefined    = "";
  export let tooltip: string | undefined = undefined;

  const dispatch = createEventDispatcher();
  $: if (data) {
    data = data.map(cl => {
      if (cl instanceof ProClass) return cl;

      const clazz = getClass(cl);
      if (clazz) return clazz;
    });
    dispatch("save");
  }
</script>

<ProInput label={name} {tooltip}>
  <SearchableSelect bind:data={$classes} multiple
                    display={(clazz) => clazz.name}
                    bind:selected={data} />
</ProInput>