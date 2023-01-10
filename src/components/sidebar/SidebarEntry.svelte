<script lang="ts">
  import { active } from "../../data/store.js";
  import { fly, slide } from "svelte/transition";
  import { ProSkill } from "../../api/proskill";
  import { ProClass } from "../../api/proclass";

  export let delay: number;
  export let data: ProSkill | ProClass;
  export let useSlide;
</script>

{#if useSlide}
  <div class="sidebar-entry slide"
       class:active={!!data && $active == data}
       on:click
       transition:slide>
    {#if data}
      {data.name}
      <div class="download" title="Save {data.triggers ? 'Skill' : 'Class'}">
      <span class="material-symbols-rounded">
        save
      </span>
      </div>
    {/if}
    <slot />
  </div>
{:else}
  <div class="sidebar-entry"
       class:active={!!data && $active == data}
       on:click
       in:fly={{x: -100, duration: 500, delay}}>
    {#if data}
      {data.name}
      <div class="download" title="Save {data.triggers ? 'Skill' : 'Class'}">
      <span class="material-symbols-rounded">
        save
      </span>
      </div>
    {/if}
    <slot />
  </div>
{/if}

<style>
    .sidebar-entry {
        display: flex;
        justify-content: space-between;
        align-items: center;
        background-color: #444;
        padding: 0.3rem 0.5rem;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
        border-left: 0px solid var(--color-accent);
        transition: background-color 0.25s ease-in-out,
        border-left-width 0.25s ease-in-out;
        user-select: none;
        margin-inline: 0.4rem;
    }

    .sidebar-entry:hover {
        cursor: pointer;
    }

    .sidebar-entry:not(.slide):last-child {
        border-radius: 0 0 0.5rem 0.5rem;
    }

    .sidebar-entry:not(:last-child) {
        border-bottom: 1px solid #aaa;
    }

    .active {
        background-color: #005193;
        border-left-width: 4px;
    }

    .download {
        opacity: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 0.3rem;
        border-radius: 50%;
        transition: background-color 0.25s ease,
        opacity 0.25s ease;
    }

    .sidebar-entry:hover .download {
        opacity: 1;
    }

    .download:hover {
        background-color: #1dad36;
    }

    .download .material-symbols-rounded {
        font-size: 1rem;
    }
</style>