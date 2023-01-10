<script lang="ts">
  import SearchableSelect from "../../../components/SearchableSelect.svelte";
  import { skills } from "../../../data/store.js";
  import { ProClass } from "../../../api/proclass";
  import { onMount } from "svelte";

  export let data: { class: ProClass };

  onMount(() => {
    console.log(data.class.serializeYaml());
  });
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
  <label for="parent">Parent</label>
  <div class="input-wrapper">
    <input id="parent" bind:value={data.class.parent} />
  </div>
  <label for="permission">Permission</label>
  <div class="input-wrapper">
    <input type="checkbox" class="hidden" id="permission" bind:checked={data.class.permission} />
    <div class="toggle" class:selected={data.class.permission}>
      <div on:click={() => data.class.permission = true}>True</div>
      <div on:click={() => data.class.permission = false}>False</div>
    </div>
  </div>
  <!-- TODO Exp Sources -->
  <label for="expSources">Exp Sources</label>
  <div class="input-wrapper">
    <input id="expSources" bind:value={data.class.expSources} />
  </div>
  <label for="health">Health</label>
  <div class="input-wrapper">
    <input id="health" bind:value={data.class.health} />
    <input id="health-modifier" bind:value={data.class.healthModifier} />
  </div>
  <label for="mana">Mana</label>
  <div class="input-wrapper">
    <input id="mana" bind:value={data.class.mana} />
    <input id="mana-modifier" bind:value={data.class.manaModifier} />
  </div>


  <label for="mana-regen">Mana Regen</label>
  <div class="input-wrapper">
    <input id="mana-regen" bind:value={data.class.manaRegen} />
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
                      data={skills}
                      bind:selected={data.class.skills}
                      display={(skill) => skill.name}
                      placeholder="No Skills" />
  </div>

  <label for="icon">Icon</label>
  <div class="input-wrapper">
    <input id="icon" bind:value={data.class.icon.material} />
  </div>
  <label for="icon-data">CustomModelData</label>
  <div class="input-wrapper">
    <input id="icon-data" bind:value={data.class.icon.customModelData} />
  </div>
  <label for="icon-lore">Icon Lore</label>
  <div class="input-wrapper">
    <input id="icon-lore" bind:value={data.class.icon.lore} />
  </div>
</div>


<style>
    h1 {
        margin-bottom: 0.1em;
    }

    .container {
        display: grid;
        grid-template-columns: 0.75fr 1fr;
        align-items: center;
        margin-inline: 1rem;
    }

    label {
        text-align: right;
        padding-right: 1rem;
    }

    .input-wrapper {
        align-self: flex-start;
        padding-left: 1rem;
        padding-bottom: 0.3rem;
        border-left: 3px solid #333;
    }


    select, input {
        color: var(--color-fg);
        background-color: var(--color-select-bg);
        padding: 0.3rem;
        font-family: inherit;
        font-weight: inherit;
        font-size: 1.2rem;
        border: none;
    }

    .toggle {
        overflow: hidden;
        display: flex;
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
    }

    .toggle > div:hover {
        cursor: pointer;
    }
</style>