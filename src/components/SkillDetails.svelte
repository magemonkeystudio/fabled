<script lang="ts">
  import { saveDataInternal, updateSidebar } from "../data/store";
  import ProInput from "./input/ProInput.svelte";
  import ProSkill from "../api/proskill";
  import SearchableSelect from "./input/SearchableSelect.svelte";
  import Toggle from "./input/Toggle.svelte";
  import AttributeInput from "./input/AttributeInput.svelte";
  import IconInput from "./input/IconInput.svelte";
  import { skills } from "../api/skill-store";

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
            tooltip='The flavor text describing the skill such as "AOE utility" or whatever you want it to be'
            bind:value={data.type} />
  <ProInput label="Max Level" type="number"
            tooltip="The maximum level the skill can reach"
            bind:value={data.maxLevel} />
  <ProInput label="Skill Requirement"
            tooltip="The skill that needs to be upgraded before this one can be unlocked">
    <SearchableSelect bind:selected={data.skillReq}
                      data={$skills.filter(s => s !== data)}
                      display={(s) => s.name} />
  </ProInput>
  <ProInput label="Skill Req Level" type="number" intMode
            tooltip="The level that the required skill needs to reach before this one can be unlocked"
            bind:value={data.skillReqLevel} />
  <ProInput label="Permission"
            tooltip='Whether this skill requires a permission to unlock. The permission would be "skillapi.skill.${data.name}"'>
    <Toggle bind:data={data.permission} />
  </ProInput>
  <ProInput label="Level Req"
            tooltip="The class level the player needs to be before unlocking or upgrading this skill">
    <AttributeInput bind:value={data.levelReq} />
  </ProInput>
  <ProInput label="Cost"
            tooltip="The amount of skill points needed to unlock and upgrade this skill">
    <AttributeInput bind:value={data.cost} />
  </ProInput>
  <ProInput label="Cooldown"
            tooltip="The time in seconds before the skill can be cast again (only works with the Cast trigger)">
    <AttributeInput bind:value={data.cooldown} />
  </ProInput>
  <ProInput label="Cooldown Message"
            tooltip="Whether to send a message when attempting to run the skill while in cooldown">
    <Toggle bind:value={data.cooldownMessage} />
  </ProInput>
  <ProInput label="Mana"
            tooltip="The amount of mana it takes to cast the skill (only works with the Cast trigger)">
    <AttributeInput bind:value={data.mana} />
  </ProInput>
  <ProInput label="Min Spent"
            tooltip="The amount of skill points that need to be spent before upgrading this skill">
    <AttributeInput bind:value={data.minSpent} />
  </ProInput>
  <ProInput label="Cast Message"
            tooltip="The message to display to players around the caster when the skill is cast. The radius of the area is in the config.yml options"
            bind:value={data.castMessage} />
  <ProInput label="Combo"
            tooltip='The click combo to assign the skill (if enabled). Use L, R, S, LS, RS, P, Q and F for the types of clicks separated by spaces. For example, "L L R R" would work for 4 click combos.'
            bind:value={data.combo} />
  <ProInput label="Indicator"
            tooltip='What sort of display to use for cast previews. This applies to the "hover bar" in the casting bars setup'>
    <select bind:value={data.indicator}>
      <option value="2D">2D</option>
      <option value="3D">3D</option>
      <option value="None">None</option>
    </select>
  </ProInput>
  <IconInput bind:icon={data.icon} />
  <ProInput label="Incompatible"
            tooltip="List of skills that must not be upgraded in order to upgrade this skill">
    <SearchableSelect multiple={true}
                      data={$skills.filter(s => s!== data)}
                      display={(s) => s.name}
                      bind:selected={data.incompatible} />
  </ProInput>
{/if}

<style>

</style>