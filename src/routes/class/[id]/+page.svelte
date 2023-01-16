<script lang="ts">
  import SearchableSelect from "../../../components/input/SearchableSelect.svelte";
  import { classes, saveDataInternal, skills, updateSidebar } from "../../../data/store";
  import { ProClass } from "../../../api/proclass";
  import AttributeInput from "../../../components/input/AttributeInput.svelte";
  import { numberOnly } from "../../../api/number-only";
  import ByteSelect from "../../../components/input/ByteSelect.svelte";
  import IconInput from "../../../components/input/IconInput.svelte";

  export let data: { class: ProClass };

  const expSources = ["Mob", "Block Break", "Block Place", "Craft", "Command", "Special", "Exp Bottle", "Smelt", "Quest"];

  $: {
    if (data?.class.name) updateSidebar();
    saveDataInternal();
  }
</script>

<svelte:head>
  <title>ProSkillAPI Dynamic Editor - {data.class.name}</title>
</svelte:head>

<h1>{data.class.name}</h1>
<hr />
<div class="container">
  <label for="name">Name</label>
  <div class="input-wrapper">
    <input id="name" bind:value={data.class.name} />
  </div>
  <label for="prefix">Prefix</label>
  <div class="input-wrapper">
    <input id="prefix" bind:value={data.class.prefix} />
  </div>
  <label for="group">Group</label>
  <div class="input-wrapper">
    <input id="group" bind:value={data.class.group} />
  </div>
  <label for="manaName">Mana Name</label>
  <div class="input-wrapper">
    <input id="manaName" bind:value={data.class.manaName} />
  </div>
  <label>Max Level</label>
  <div class="input-wrapper">
    <input type="number"
           id="maxLevel"
           use:numberOnly={true}
           bind:value={data.class.maxLevel} />
  </div>
  <label>Parent</label>
  <div class="input-wrapper">
    <SearchableSelect id="parent"
                      data={$classes}
                      bind:selected={data.class.parent}
                      display={(c) => c.name} />
  </div>
  <label for="permission">Permission</label>
  <div class="input-wrapper">
    <input type="checkbox" class="hidden" id="permission" bind:checked={data.class.permission} />
    <div class="toggle" class:selected={data.class.permission}>
      <div on:click={() => data.class.permission = true}>True</div>
      <div on:click={() => data.class.permission = false}>False</div>
    </div>
  </div>
  <label>Exp Sources</label>
  <div class="input-wrapper">
    <ByteSelect
      data={expSources}
      bind:value={data.class.expSources} />
  </div>
  <label>Health</label>
  <div class="input-wrapper">
    <AttributeInput bind:value={data.class.health} />
  </div>
  <label>Mana</label>
  <div class="input-wrapper">
    <AttributeInput bind:value={data.class.mana} />
  </div>

  {#each data.class.attributes as attr}
    <label>{attr.name}</label>
    <div class="input-wrapper">
      <AttributeInput bind:value={attr} />
    </div>
  {/each}

  <label for="mana-regen">Mana Regen</label>
  <div class="input-wrapper">
    <input id="mana-regen"
           use:numberOnly
           bind:value={data.class.manaRegen} />
  </div>
  <label for="skill-tree">Skill Tree</label>
  <div class="input-wrapper">
    <select id="skill-tree" bind:value={data.class.skillTree}>
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
                      bind:selected={data.class.skills}
                      display={(skill) => skill.name}
                      placeholder="No Skills" />
  </div>

  <IconInput bind:icon={data.class.icon} />
</div>


<style>
    h1 {
        margin-bottom: 0.1em;
    }

    .container {
        display: grid;
        grid-template-columns: 0.75fr auto;
        align-items: center;
        margin-inline: 1rem;
    }

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