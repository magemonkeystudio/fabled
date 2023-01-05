<script lang="ts">
  import "../app.css";
  import HeaderBar from "../components/HeaderBar.svelte";
  import { squish } from "../data/squish";
  import { classes, importing, isShowClasses, showClasses, showSidebar, showSkills, skills } from "../data/store";
  import { fly } from "svelte/transition";
  import ImportModal from "../components/ImportModal.svelte";

  let sidebar: HTMLElement;
  let sidebarHeight = 200;

  const listener = () => {
    if (!sidebar) return;
    const rect = sidebar.getBoundingClientRect();
    const y = rect.y;
    const height = window.innerHeight;
    sidebarHeight = height - y;

  };

  $: if (sidebar) {
    listener();
  }
</script>

<svelte:window on:scroll={listener} on:resize={listener} />

<HeaderBar />

{#if $showSidebar}
  <div id="sidebar" style="height: {sidebarHeight}px" transition:squish bind:this={sidebar}>
    <div id="type-selector" class:c-selected={$isShowClasses}>
      <div class="classes" on:click={showClasses}>Classes</div>
      <div class="skills" on:click={showSkills}>Skills</div>
    </div>
    <hr />
    {#if $isShowClasses}
      {#each classes as cl, i}
        <div class="sidebar-entry" in:fly={{x: -100, duration: 500, delay: 200 + 100*i}}>{cl}</div>
      {/each}
      <div class="sidebar-entry" in:fly={{x: -100, duration: 500, delay: 200 + 100*(classes.length+1)}}>+ New Class
      </div>
    {:else}
      {#each skills as sk, i}
        <div class="sidebar-entry" in:fly={{x: -100, duration: 500, delay: 200 + 100*i}}>{sk}</div>
      {/each}
      <div class="sidebar-entry" in:fly={{x: -100, duration: 500, delay: 200 + 100*(classes.length+1)}}>+ New Skill
      </div>
    {/if}

  </div>
{/if}
<div id="body">
  <div class="content">
    <slot />
  </div>
</div>

<div id="floating-buttons">
  <div class="button backup" title="Backup All Data">
    <span class="material-symbols-rounded">cloud_download</span>
  </div>
  <div class="button save" title="Save">
    <span class="material-symbols-rounded">save</span>
  </div>
</div>

{#if $importing}
  <ImportModal />
{/if}

<style>
    #body {
        display: flex;
        justify-content: stretch;
    }

    #sidebar {
        position: sticky;
        top: 3.06rem;
        float: left;
        background-color: #222;
        padding: 0.4rem;
        width: 15rem;
        min-width: 10rem;
        max-width: 25vw;
        overflow-x: hidden;
        overflow-y: auto;
    }

    .sidebar-entry {
        display: block;
        background-color: #444;
        border-radius: 0.5rem;
        padding: 0.2rem 0.5rem;
        margin: 0.2rem 0;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
    }

    .sidebar-entry:hover {
        cursor: pointer;
    }

    .content {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .button:hover {
        cursor: pointer;
    }

    #floating-buttons {
        display: flex;
        flex-direction: column;
        position: fixed;
        right: 0.5rem;
        bottom: 0.5rem;
        align-items: flex-end;
    }

    #floating-buttons .button {
        background-color: #0083ef;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        padding: 0.7rem;
        box-shadow: 5px 5px 5px #444;
        margin: 0.5rem;
    }

    #floating-buttons .button .material-symbols-rounded {
        font-size: 3rem;
    }

    #floating-buttons .button.backup .material-symbols-rounded {
        font-size: 1.75rem;
    }

    #floating-buttons .save {
        background-color: #1dad36;
    }

    #type-selector {
        overflow: hidden;
        display: flex;
        text-align: center;
        background-color: #111;
        border-radius: 0.4rem;
    }

    #type-selector:before {
        content: '';
        height: 100%;
        width: 50%;
        border-radius: 0.4rem;
        background-color: #0083ef;
        position: absolute;
        left: 0;
        transition: left 350ms ease-in-out;
    }

    #type-selector:not(.c-selected):before {
        left: 50%;
    }

    #type-selector > div {
        flex: 1;
        padding: 0.2rem;
    }

    #type-selector > div:hover {
        cursor: pointer;
    }
</style>