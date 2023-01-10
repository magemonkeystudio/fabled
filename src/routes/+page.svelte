<script lang="ts">
  import { active, activeType } from "../data/store.js";
</script>

<svelte:head>
  <title>ProSkillAPI Dynamic Editor</title>
</svelte:head>

{#if $active}
  <div>{$activeType == 'class' ? "Class" : "Skill"}</div>
  <h1>{$active?.name}</h1>
  <hr />
  <p>More stuff to come. WIP</p>
{:else}
  <div class="start">Select/Add a class on the left to get started!</div>
{/if}

<style>
    .start {
        display: flex;
        height: 100%;
        justify-content: center;
        align-items: center;
    }
</style>