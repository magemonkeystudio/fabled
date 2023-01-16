<script lang="ts">
  import { ProClass } from "../../../api/proclass";
  import { ProSkill } from "../../../api/proskill";
  import ClassDetails from "../../../components/ClassDetails.svelte";

  export let data: { class?: ProClass, skill?: ProSkill };
</script>

<svelte:head>
  <title>ProSkillAPI Dynamic Editor - {data.class.name}</title>
</svelte:head>

<h1>{data?.class?.name || data?.skill?.name}</h1>
<hr />
<div class="container">
  {#if data?.class}
    <ClassDetails data="{data.class}" />
  {/if}
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
</style>