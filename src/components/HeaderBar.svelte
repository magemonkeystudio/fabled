<script lang="ts">
  import DiscordLogo from "./DiscordLogo.svelte";
  import { active, activeType, setImporting, toggleSidebar } from "../data/store";
  import { createPaste } from "../api/hastebin";
  import type { ProSkill } from "../api/types";
  import { get } from "svelte/store";

  let serverOptions = ["1.19", "1.18", "1.17", "1.16"];

  const haste = () => {
    let act = get(active);
    if (!act) return;

    let data = JSON.stringify(act, null, 2);
    createPaste(data)
      .then((urlToPaste) => {
        console.log(urlToPaste);
        navigator.clipboard.writeText(urlToPaste);
        window.open(urlToPaste);
      })
      .catch((requestError) => {
        console.error(requestError);
      });
  };

  const openImport = () => {
    console.log("Clicked open");
    setImporting(true);
  };
</script>

<div id="header">
  <div class="home">
    <h1 class="accent">ProSkillAPI</h1>
    <h2>Dynamic Editor</h2>
  </div>

  <div class="socials">
    <a class="social discord" href="https://discord.gg/6UzkTe6RvW" title="Join our Discord">
      <DiscordLogo />
    </a>
    <a class="social github"
       href="https://github.com/promcteam/proskillapi"
       title="Check out our GitHub">
      <img src="/github-mark-white.svg" />
    </a>
    <a class="social spigot"
       href="https://www.spigotmc.org/resources/proskillapi-create-custom-races-classes-skills-spells-with-an-easy-online-editor.91913/"
       title="Check out our resource on Spigot">
      <img src="/spigot.png" />
    </a>
    <a class="social wiki"
       href="https://promcteam.com/wiki/index.php?title=Proskillapi:Proskillapi"
       title="Wiki">
      <span class="material-symbols-rounded">help</span>
    </a>
  </div>
</div>

<nav>
  <div class="chip hamburger" on:click={toggleSidebar}>
    <span class="material-symbols-rounded">menu</span>
  </div>
  <label class="server">
    Server
    <select>
      {#each serverOptions as opt}
        <option>{opt}</option>
      {/each}
    </select>
  </label>

  <div class="socials">
    <div class="chip import"
         on:click={openImport}
         title="Import Data">
      Import
    </div>

    {#if $activeType}
      <div class="chip share"
           on:click={haste}
           title="Share {$activeType == 'class' ? 'Class' : 'Skill'}">
        Share {$activeType == 'class' ? 'Class' : 'Skill'}
      </div>
    {/if}
  </div>
</nav>

<style>
    #header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.5rem;
    }

    div > * {
        text-align: center;
    }

    .home {
        display: flex;
        align-items: center;
    }

    h1, h2 {
        display: inline;
        font-family: Mojave-Web, sans-serif;
        padding: 0;
        margin: 0;
    }

    h1 {
        font-size: 2rem;
    }

    h2 {
        margin-left: 0.5rem;
        font-size: 1.25rem;
    }

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
        color: var(--color-fg);
        background-color: var(--color-select-bg);
        padding: 0.25rem;
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
        border: 1px solid white;
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
    }

    .socials {
        display: flex;
        justify-content: flex-end;
    }

    .social, .social:visited, .social:active {
        display: flex;
        justify-content: center;
        align-items: center;
        user-select: none;
        color: var(--color-fg);
        background-color: #0083ef;
        border-radius: 50%;
        height: 2rem;
        aspect-ratio: 1;
        padding: 0.3rem;
        margin: 0 0.2rem;
        text-decoration: none;
    }

    .social img {
        height: 100%;
        aspect-ratio: 1;
    }
</style>