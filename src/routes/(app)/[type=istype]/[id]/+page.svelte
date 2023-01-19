<script lang="ts">
  import ProClass from "../../../../api/proclass";
  import ProSkill from "../../../../api/proskill";
  import ClassDetails from "../../../../components/ClassDetails.svelte";
  import SkillDetails from "../../../../components/SkillDetails.svelte";

  export let data: { data: ProClass | ProSkill };
</script>

<svelte:head>
  <title>ProSkillAPI Dynamic Editor - {data.data.name}</title>
</svelte:head>
<h1>{data?.data?.name}
  {#if data?.data instanceof ProSkill}
    <a href="/skill/{data.data.name}/edit">Edit Skill</a>
  {/if}
</h1>
<hr />
<div class="container">
  {#if data?.data instanceof ProClass}
    <ClassDetails bind:data={data.data} />
  {:else}
    <SkillDetails bind:data={data.data} />
  {/if}
</div>


<style>
    h1 {
        margin-bottom: 0.1em;
    }

    .container {
        display: grid;
        grid-template-columns: 1fr 1fr;
        align-items: center;
        margin-inline: 1rem;
        width: 90%;
    }
</style>