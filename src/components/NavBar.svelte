<script lang="ts">
  import { active, activeType, setImporting, toggleSidebar } from "../data/store";
  import { get } from "svelte/store";
  import { createPaste } from "../api/hastebin";
  import { serverOptions, version } from "../version/data";
  import ProClass from "../api/proclass";
  import ProSkill from "../api/proskill";

  const haste = () => {
    let act: ProClass | ProSkill | undefined = get(active);
    if (!act) return;

    let data = act.serializeYaml().toString();
    createPaste(data)
      .then((urlToPaste) => {
        navigator?.clipboard?.writeText(urlToPaste);
        window.open(urlToPaste);
      })
      .catch((requestError) => console.error(requestError));
  };

  const openImport = () => {
    setImporting(true);
  };

  const toggle = (e: MouseEvent) => {
    e.stopPropagation();
    toggleSidebar();
  };
</script>

<nav>
  <div class="chip hamburger" on:click={toggle}>
    <span class="material-symbols-rounded">menu</span>
  </div>
  <label class="server">
    Server
    <select bind:value={$version}>
      {#each serverOptions as opt}
        <option value={opt.substring(2)}>{opt}</option>
      {/each}
    </select>
  </label>

  <div class="transfer">
    <div class="chip import"
         on:click={openImport}
         title="Import Data">
      Import
    </div>

    {#if $activeType}
      <div class="chip share"
           on:click={haste}
           title="Share {$activeType === 'class' ? 'Class' : 'Skill'}">
        Share {$activeType === 'class' ? 'Class' : 'Skill'}
      </div>
    {/if}
  </div>
</nav>

<style>
    label {
        display: flex;
        font-size: 1.5rem;
        font-weight: bold;
        align-items: center;
        justify-content: center;
    }

    .server {
        display: flex;
        align-items: center;
    }

    select {
        margin-left: 1rem;
        font-family: inherit;
        font-weight: inherit;
        font-size: 1rem;
        border-radius: 0.5rem;
    }

    nav {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr;
        background-color: #444;
        align-items: center;
        justify-content: space-between;
        padding: 0.5rem;
        position: sticky;
        top: 0;
        z-index: 5;
    }

    nav .chip {
        background: var(--color-bg);
        font-weight: bold;
        padding: 0.25rem;
        font-size: 1.1rem;
        text-align: center;
        border-radius: 100vw;
        white-space: nowrap;
    }

    nav .chip:not(.hamburger) {
        border-radius: 0.5rem;
        padding: 0.25rem 0.5rem;
        margin-left: 0.5rem;
        background-color: #004f8f;
        border: 1px solid var(--color-fg);
    }

    nav .chip.import {
        background-color: #077e1c;
    }

    .chip:hover {
        cursor: pointer;
    }

    .chip.hamburger {
        display: flex;
        justify-self: flex-start;
        align-items: center;
        justify-content: center;
        width: 3.5rem;
        font-size: 1.5rem;
    }

    .transfer {
        display: flex;
        justify-content: flex-end;
    }
</style>