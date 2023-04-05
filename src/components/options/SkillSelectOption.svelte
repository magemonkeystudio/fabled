<script lang="ts">
  import ProInput                  from "$input/ProInput.svelte";
  import { createEventDispatcher } from "svelte";
  import SearchableSelect     from "$input/SearchableSelect.svelte";
  import { getSkill, skills } from "../../data/skill-store";
  import ProSkill        from "$api/proskill";

  export let data: ProSkill[] | string[]              = [];
  export let any: boolean;
  export let name: string | undefined    = "";
  export let tooltip: string | undefined = undefined;

  const dispatch = createEventDispatcher();
  $: if (data) {
    data = data.map(sk => {
      if (sk instanceof ProSkill) return sk;

      const skill = getSkill(sk);
      if (skill) return skill;
    });
    dispatch("save");
  }
</script>

<ProInput label={name} {tooltip}>
  <SearchableSelect bind:data={$skills} multiple
                    display={(skill) => skill.name}
                    bind:selected={data} />
</ProInput>