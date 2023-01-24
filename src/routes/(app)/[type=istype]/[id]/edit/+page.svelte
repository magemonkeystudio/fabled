<script lang="ts">
  import ProSkill from "$api/proskill";
  import ComponentWidget from "../../../../../components/ComponentWidget.svelte";
  import Modal from "../../../../../components/Modal.svelte";
  import SearchableSelect from "$input/SearchableSelect.svelte";
  import { triggers } from "$api/triggers";

  export let data: { data: ProSkill };
  let skill: ProSkill = data.data;
  let triggerModal = false;

  const onSelectTrigger = data => {
    skill.triggers.push(data.detail);
    skill.triggers = [...skill.triggers];
    setTimeout(() => triggerModal = false);
  };
</script>

<svelte:head>
  <title>ProSkillAPI Dynamic Editor - {skill.name}</title>
</svelte:head>
<h2>{skill.name}</h2>
<hr />
<div class="container">
  {#each skill.triggers as comp}
    <div class="widget">
      <ComponentWidget component={comp} />
    </div>
  {/each}
  <div class="add-trigger chip" title="Add Trigger" on:click={() => triggerModal = true}>
    <span class="material-symbols-rounded">
      variables
    </span>
  </div>
</div>

{#if triggerModal}
  <Modal on:close={() => triggerModal = false}>
    <h2 class="modal-header">Select New Trigger</h2>
    <hr />
    <SearchableSelect
      on:select={onSelectTrigger}
      data={Object.values(triggers).map(trigger => new trigger())}
      display={(trigger) => trigger.name} />
  </Modal>
{/if}

<style>
    h2 {
        margin-bottom: 0.1em;
        width: 95%;
    }

    .container {
        display: flex;
        align-self: flex-start;
        align-items: flex-start;
        flex-wrap: nowrap;
        width: 100%;
        padding-inline: 2rem;
        overflow-x: auto;
        flex-grow: 1;
    }

    .widget {
        margin-right: 1rem;
        white-space: nowrap;
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

    h2.modal-header {
        text-align: center;
    }
</style>