<script lang="ts">
  import ProInput                  from "$input/ProInput.svelte";
  import { createEventDispatcher } from "svelte";
  import SearchableSelect          from "$input/SearchableSelect.svelte";
  import ProClass                  from "$api/proclass";
  import { classes, getClass }     from "../../data/class-store";

  export let data: ProClass[] | string[] | ProClass | string = [];
  export let any: boolean;
  export let name: string | undefined                        = "";
  export let tooltip: string | undefined                     = undefined;
  export let multiple                                        = true;

  const dispatch = createEventDispatcher();
  $: if (data instanceof Array) {
    data = data.map(cl => {
      if (cl instanceof ProClass) return cl;

      const clazz = getClass(cl);
      if (clazz) return clazz;
    });
    dispatch("save");
  } else if (data && !(data instanceof ProClass)) {
    console.log("data is string")
    const clazz = getClass(<string>data);
    if (clazz) data = clazz;
  }

  $: if (!(data instanceof Array)) {
    dispatch("save");
  }
</script>

<ProInput label={name} {tooltip}>
  <SearchableSelect bind:data={$classes}
                    display={(clazz) => clazz.name}
                    {multiple}
                    bind:selected={data} />
</ProInput>