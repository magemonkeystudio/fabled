<script lang="ts">
  import ProSkill from "$api/proskill";
  import ComponentWidget from "$components/ComponentWidget.svelte";
  import Modal from "$components/Modal.svelte";
  import SearchableSelect from "$input/SearchableSelect.svelte";
  import { triggers } from "$api/triggers";
  import { draggingComponent } from "../../../../data/store";
  import { get } from "svelte/store";

  export let data: { data: ProSkill };
  let skill: ProSkill = data.data;
  let triggerModal = false;
  let hovered = false;

  const onSelectTrigger = data => {
    skill.triggers.push(data.detail);
    skill.triggers = [...skill.triggers];
    setTimeout(() => triggerModal = false);
  };

  const drop = () => {
    const comp = get(draggingComponent);
    hovered = false;
    skill.removeComponent(comp);
    skill.triggers = [...skill.triggers];
    console.log(comp);
  };
</script>

<svelte:head>
  <title>ProSkillAPI Dynamic Editor - {skill.name}</title>
</svelte:head>
<div class="header">
  <h2>{skill.name}</h2>
  <div class="delete-zone"
       on:dragover|preventDefault={() => hovered = true}
       on:dragleave={() => hovered = false}
       on:drop|preventDefault|stopPropagation={drop}
       class:hovered
  >
    <span class="material-symbols-rounded">delete</span>
    Drag components here to delete
  </div>
</div>
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
    .header {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        width: 100%;
        margin-top: 1rem;
    }

    h2 {
        margin: 0 3rem;
    }

    .delete-zone {
        display: flex;
        align-items: center;
        color: white;
        background-color: #b60000;
        padding: 0.5rem;
        padding-inline: 5%;
        border-radius: 0.25rem;
    }

    .delete-zone .material-symbols-rounded {
        margin-right: 1rem;
    }

    .delete-zone.hovered {
        outline: 0.2rem dashed white;
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