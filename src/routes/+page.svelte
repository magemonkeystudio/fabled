<!-- suppress CssUnusedSymbol -->
<script lang="ts">
  import { onMount } from "svelte";
  import { active }  from "../data/store";

  onMount(() => active.set(undefined));
</script>

<svelte:head>
  <title>Fabled Dynamic Editor</title>
</svelte:head>

<div class="start">Select/Add an item on the left to get started!</div>

<style>
    .start {
        display: flex;
        height: 100%;
        justify-content: center;
        align-items: center;
    }

    :global(#svelte-body) {
        min-height: 100vh;
    }
</style>