<script lang="ts">
  import { ProClass } from "../api/proclass";
  import IconInput from "./input/IconInput.svelte";
  import MaterialSelect from "./input/MaterialSelect.svelte";
  import SearchableSelect from "./input/SearchableSelect.svelte";
  import { classes, saveDataInternal, skills, updateSidebar } from "../data/store";
  import AttributeInput from "./input/AttributeInput.svelte";
  import { numberOnly } from "../api/number-only";
  import ByteSelect from "./input/ByteSelect.svelte";
  import { expSources } from "../version/data";
  import { onMount } from "svelte";

  export let data: ProClass;

  onMount(() => alert(JSON.stringify(data)))

  $: {
    if (data?.name) updateSidebar();
    saveDataInternal();
  }
</script>

<label for="name">Name</label>
<div class="input-wrapper">
  <input id="name" bind:value={data.name} />
</div>
<label for="prefix">Prefix</label>
<div class="input-wrapper">
  <input id="prefix" bind:value={data.prefix} />
</div>
<label>Action Bar</label>
<div class="input-wrapper">
  <input bind:value={data.actionBar} />
</div>
<label for="group">Group</label>
<div class="input-wrapper">
  <input id="group" bind:value={data.group} />
</div>
<label for="manaName">Mana Name</label>
<div class="input-wrapper">
  <input id="manaName" bind:value={data.manaName} />
</div>
<label>Max Level</label>
<div class="input-wrapper">
  <input type="number"
         id="maxLevel"
         use:numberOnly={true}
         bind:value={data.maxLevel} />
</div>
<label>Parent</label>
<div class="input-wrapper">
  <SearchableSelect id="parent"
                    data={$classes}
                    bind:selected={data.parent}
                    display={(c) => c.name} />
</div>
<label for="permission">Permission</label>
<div class="input-wrapper">
  <input type="checkbox" class="hidden" id="permission" bind:checked={data.permission} />
  <div class="toggle" class:selected={data.permission}>
    <div on:click={() => data.permission = true}>True</div>
    <div on:click={() => data.permission = false}>False</div>
  </div>
</div>
<label>Exp Sources</label>
<div class="input-wrapper">
  <ByteSelect
    data={expSources}
    bind:value={data.expSources} />
</div>
<label>Health</label>
<div class="input-wrapper">
  <AttributeInput bind:value={data.health} />
</div>
<label>Mana</label>
<div class="input-wrapper">
  <AttributeInput bind:value={data.mana} />
</div>

{#each data.attributes as attr}
  <label>{attr.name}</label>
  <div class="input-wrapper">
    <AttributeInput bind:value={attr} />
  </div>
{/each}

<label for="mana-regen">Mana Regen</label>
<div class="input-wrapper">
  <input id="mana-regen"
         use:numberOnly
         bind:value={data.manaRegen} />
</div>
<label for="skill-tree">Skill Tree</label>
<div class="input-wrapper">
  <select id="skill-tree" bind:value={data.skillTree}>
    <option value="Requirement">Requirement</option>
    <option value="Basic Horizontal">Basic Horizontal</option>
    <option value="Basic Vertical">Basic Vertical</option>
    <option value="Level Horizontal">Level Horizontal</option>
    <option value="Level Vertical">Level Vertical</option>
    <option value="Flood">Flood</option>
  </select>
</div>

<label>Skills</label>
<div class="input-wrapper">
  <SearchableSelect id="skills"
                    data={$skills}
                    multiple="true"
                    bind:selected={data.skills}
                    display={(skill) => skill.name}
                    placeholder="No Skills" />
</div>

<IconInput bind:icon={data.icon} />

<label>Unusable Items</label>
<div class="input-wrapper">
  <MaterialSelect multiple bind:selected={data.unusableItems} />
</div>

<style>
    label {
        text-align: right;
        padding-right: 1rem;
    }

    input {
        padding-inline: 0.5rem;
        width: 100%;
    }

    .toggle {
        overflow: hidden;
        display: inline-flex;
        text-align: center;
        background-color: var(--color-select-bg);
        border-radius: 0.4rem;
        user-select: none;
    }

    .toggle:before {
        content: '';
        height: 100%;
        width: 50%;
        border-radius: 0.4rem;
        background-color: #0083ef;
        position: absolute;
        left: 0;
        transition: left 350ms ease-in-out;
    }

    .toggle:not(.selected):before {
        left: 50%;
    }

    .toggle > div {
        flex: 1;
        padding: 0.2rem;
        padding-inline: 1.5rem;
    }

    .toggle > div:hover {
        cursor: pointer;
    }
</style>