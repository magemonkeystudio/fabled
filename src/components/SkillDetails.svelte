<script lang="ts">
  import { saveDataInternal, skills, updateSidebar } from "../data/store";
  import ProInput from "./input/ProInput.svelte";
  import ProSkill from "../api/proskill";
  import SearchableSelect from "./input/SearchableSelect.svelte";
  import Toggle from "./input/Toggle.svelte";
  import AttributeInput from "./input/AttributeInput.svelte";
  import IconInput from "./input/IconInput.svelte";
  import LoreInput from "./input/LoreInput.svelte";

  export let data: ProSkill;

  $: {
    if (data?.name) updateSidebar();
    saveDataInternal();
  }
</script>

{#if data}
  <ProInput label="Name"
            tooltip="The name of the skill. This should not contain color codes"
            bind:value={data.name} />
  <ProInput label="Type"
            tooltip="The format for the action bar. Leave blank to use the default formatting"
            bind:value={data.type} />
  <ProInput label="Max Level" type="number"
            tooltip="The format for the action bar. Leave blank to use the default formatting"
            bind:value={data.maxLevel} />
  <ProInput label="Skill Requirement"
            tooltip={`A class group are things such as "race", "class", and "trade". Different groups can be professed through at the same time, one class from each group`}>
    <SearchableSelect bind:selected={data.skillReq}
                      data={$skills.filter(s => s !== data)}
                      display={(s) => s.name} />
  </ProInput>
  <ProInput label="Skill Req Level" type="number" intMode
            bind:value={data.skillReqLevel} />
  <ProInput label="Permission"
            tooltip={`Whether the class requires a permission to be professed as. The permission would be "skillapi.class.${data.name.toLowerCase()}"`}>
    <Toggle bind:data={data.permission} />
  </ProInput>
  <ProInput label="Level Req">
    <AttributeInput bind:value={data.levelReq} />
  </ProInput>
  <ProInput label="Cost">
    <AttributeInput bind:value={data.cost} />
  </ProInput>
  <ProInput label="Cooldown">
    <AttributeInput bind:value={data.cooldown} />
  </ProInput>
  <ProInput label="Cooldown Message">
    <Toggle bind:value={data.cooldownMessage} />
  </ProInput>
  <ProInput label="Mana">
    <AttributeInput bind:value={data.mana} />
  </ProInput>
  <ProInput label="Min Spent">
    <AttributeInput bind:value={data.minSpent} />
  </ProInput>
  <ProInput label="Cast Message"
            bind:value={data.castMessage} />
  <ProInput label="Combo"
            bind:value={data.combo} />
  <ProInput label="Indicator">
    <select bind:value={data.indicator}>
      <option value="2D">2D</option>
      <option value="3D">3D</option>
      <option value="None">None</option>
    </select>
  </ProInput>
  <IconInput bind:icon={data.icon} />
  <ProInput label="Incompatible">
    <LoreInput bind:value={data.incompatible} />
  </ProInput>
{/if}

<style>

</style>