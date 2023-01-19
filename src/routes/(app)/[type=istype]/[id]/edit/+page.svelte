<script lang="ts">
  import ProSkill from "../../../../../api/proskill";
  import ComponentWidget from "../../../../../components/ComponentWidget.svelte";

  export let data: { data: ProSkill };
  let skill: ProSkill = data.data;
</script>

<svelte:head>
  <title>ProSkillAPI Dynamic Editor - {skill.name}</title>
</svelte:head>
<h2>{skill.name}</h2>
<hr />
<div class="container">
  {#each skill.triggers as comp}
    <ComponentWidget component={comp} />
  {/each}
  <div class="add-trigger chip" title="Add Trigger">
    <span class="material-symbols-rounded">
      variables
    </span>
  </div>
</div>


<style>
    h2 {
        margin-bottom: 0.1em;
        width: 95%;
    }

    .container {
        display: flex;
        flex-wrap: nowrap;
        width: 100%;
        padding-inline: 2rem;
    }

    .add-trigger {
        font-size: 3rem;
    }

    .add-trigger:hover {
        cursor: pointer;
    }

    .add-trigger .material-symbols-rounded {
        margin-bottom: -20%;
    }
</style>