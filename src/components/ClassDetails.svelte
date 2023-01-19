<script lang="ts">
  import type { Unsubscriber } from "svelte/types/runtime/store";
  import ProClass from "../api/proclass";
  import IconInput from "./input/IconInput.svelte";
  import MaterialSelect from "./input/MaterialSelect.svelte";
  import SearchableSelect from "./input/SearchableSelect.svelte";
  import { saveDataInternal, updateSidebar } from "../data/store";
  import AttributeInput from "./input/AttributeInput.svelte";
  import ByteSelect from "./input/ByteSelect.svelte";
  import { expSources } from "../version/data";
  import { toProperCase } from "../api/api";
  import { onDestroy, onMount } from "svelte";
  import { ProAttribute } from "../api/proattribute";
  import ProInput from "./input/ProInput.svelte";
  import { classes } from "../data/class-store";
  import { attributes } from "../data/attribute-store";
  import Toggle from "./input/Toggle.svelte";
  import { skills } from "../data/skill-store";

  export let data: ProClass;

  let sub: Unsubscriber;

  onMount(() => {
    sub = attributes.subscribe(attr => {
      const included: string[] = [];
      data.attributes = data.attributes.filter(a => {
        if (attr?.includes(a.name)) {
          included.push(a.name);
          return true;
        }
        return false;
      });

      attr = attr.filter(a => !included.includes(a));

      for (const attrib of attr) {
        data.attributes.push(new ProAttribute(attrib, 0, 0));
      }
    });
  });

  onDestroy(() => {
    if (sub) sub();
  });

  $: {
    if (data?.name) updateSidebar();
    saveDataInternal();
  }
</script>

{#if data}
  <ProInput label="Name"
            tooltip="The name of the class. This should not contain color codes"
            bind:value={data.name} />
  <ProInput label="Prefix"
            tooltip="The prefix given to players who profess as the class which can contain color codes"
            bind:value={data.prefix} />
  <ProInput label="Action Bar"
            tooltip="The format for the action bar. Leave blank to use the default formatting"
            bind:value={data.actionBar} />
  <ProInput label="Group"
            tooltip='A class group are things such as "race", "class", and "trade". Different groups can be professed through at the same time, one class from each group'
            bind:value={data.group} />
  <ProInput label="Mana Name"
            tooltip="The name the class uses for mana"
            bind:value={data.manaName} />
  <ProInput label="Max Level" type="number" intMode={true}
            tooltip="The maximum level the class can reach. If this class turns into other classes, this will also be the level it can profess into those classes"
            bind:value={data.maxLevel} />
  <ProInput label="Parent"
            tooltip="The class that turns into this one. For example, if Fighter turns into Knight, then Knight would have its parent as Fighter">
    <SearchableSelect id="parent"
                      data={$classes.filter(c => c !== data)}
                      bind:selected={data.parent}
                      display={(c) => c.name} />
  </ProInput>
  <ProInput label="Permission"
            tooltip='Whether the class requires a permission to be professed as. The permission would be "skillapi.class.{data.name.toLowerCase()}"'>
    <Toggle bind:data={data.permission} />
  </ProInput>
  <ProInput label="Exp Sources"
            tooltip='The experience sources the class goes up from. Most of these only work if "use-exp-orbs" is enabled in the config.yml'>
    <ByteSelect
      data={expSources}
      bind:value={data.expSources} />
  </ProInput>
  <ProInput label="Health"
            tooltip="The amount of health the class has">
    <AttributeInput bind:value={data.health} />
  </ProInput>
  <ProInput label="Mana"
            tooltip="The amount of mana the class has">
    <AttributeInput bind:value={data.mana} />
  </ProInput>

  <div class="info">Drag & Drop your attributes file to use your custom attributes</div>
  {#each data.attributes as attr (attr.name)}
    <ProInput label={toProperCase(attr.name)}
              tooltip="The amount of {attr.name} the class has">
      <AttributeInput bind:value={attr} />
    </ProInput>
  {/each}

  <ProInput label="Mana Regen" type="number"
            tooltip="The amount of mana the class regenerates at each interval. The interval is in the config.yml and by default is once every second. If you want to regen a decimal amount per second, increase the interval"
            bind:value={data.manaRegen} />
  <ProInput label="Skill Tree"
            tooltip="The type of skill tree to use">
    <select id="skill-tree" bind:value={data.skillTree}>
      <option value="Requirement">Requirement</option>
      <option value="Basic Horizontal">Basic Horizontal</option>
      <option value="Basic Vertical">Basic Vertical</option>
      <option value="Level Horizontal">Level Horizontal</option>
      <option value="Level Vertical">Level Vertical</option>
      <option value="Flood">Flood</option>
    </select>
  </ProInput>

  <ProInput label="Skills"
            tooltip="The skills the class is able to use">
    <SearchableSelect id="skills"
                      data={$skills}
                      multiple="true"
                      bind:selected={data.skills}
                      display={(skill) => skill.name}
                      placeholder="No Skills" />
  </ProInput>

  <IconInput bind:icon={data.icon} />

  <ProInput label="Unusable items"
            tooltip="The types of items that the class cannot use">
    <MaterialSelect multiple bind:selected={data.unusableItems} />
  </ProInput>
{/if}

<style>
    .info {
        grid-column: 1 / span 2;
        text-align: center;
        margin-left: 5rem;
        color: rgba(255, 255, 255, 0.7);
    }
</style>