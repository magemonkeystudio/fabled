<script lang="ts"></script>

<svelte:head>
  <title>ProSkillAPI Dynamic Editor</title>
</svelte:head>

<div class="start">Select/Add a class on the left to get started!</div>

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

    :global(#body-container) {
        display: flex;
        align-items: center;
        justify-content: center;
    }
</style>