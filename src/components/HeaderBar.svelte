<script lang="ts">
  import DiscordLogo from "./DiscordLogo.svelte";
  import type { ProSkill } from "../api/types";
  import { goto } from "$app/navigation";
</script>

<div id="header">
  <div class="home" on:click={() => goto("/")}>
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

<style>
    #header {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 0.5rem;
    }

    @media screen and (min-width: 500px) {
        #header {
            flex-direction: row;
            justify-content: space-between;
        }
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
        padding: 0 0 0.5rem;
        margin: 0;
    }

    h1 {
        font-size: 2rem;
    }

    h2 {
        margin-left: 0.5rem;
        font-size: 1.25rem;
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