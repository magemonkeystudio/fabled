<script lang="ts">
  import ProSkill from "$api/proskill";
  import ComponentWidget from "$components/ComponentWidget.svelte";
  import Modal from "$components/Modal.svelte";
  import { triggers } from "$api/triggers";
  import { draggingComponent } from "../../../../data/store";
  import { get } from "svelte/store";
  import ProInput from "$input/ProInput.svelte";

  export let data: { data: ProSkill };
  let skill: ProSkill = data.data;
  let triggerModal = false;
  let hovered = false;
  let searchParams = "";
  let sortedTriggers;

  $: {
    sortedTriggers = Object.values(triggers)
      .map(trigger => new trigger())
      .filter(trigger => trigger.name.toLowerCase().includes(searchParams.toLowerCase()))
      .sort(trigger => trigger.name);
  }

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
  };

  const update = () => {
    skill.triggers = [...skill.triggers];
  };
</script>

<svelte:head>
  <title>ProSkillAPI Dynamic Editor - {skill.name}</title>
</svelte:head>
<div class="header">
  <h2>{skill.name}</h2>
</div>
<hr />
<div class="container">
  {#each skill.triggers as comp}
    <div class="widget">
      <ComponentWidget {skill} component={comp} on:update={update} />
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
    <div class="modal-header-wrapper">
      <div />
      <h2 class="modal-header">Select New Trigger</h2>
      <div class="search-bar">
        <ProInput bind:value={searchParams} placeholder="Search..." />
      </div>
    </div>
    <hr />
    <div class="triggers">
      {#each sortedTriggers as trigger}
        <div class="comp-select" on:click={() => onSelectTrigger({detail: trigger.clone()})}>
          { trigger.name }
        </div>
      {/each}
    </div>
    <hr />
    <div class="cancel" on:click={() => triggerModal = false}>Cancel</div>
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

    .cancel {
        background: #333;
        font-size: 1.2rem;
        margin-top: 0.5rem;
    }

    .cancel:hover {
        cursor: pointer;
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
        margin-right: 0.5rem;
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

    .modal-header-wrapper {
        display: grid;
        grid-template-columns: 1fr 2fr 1fr;
        width: 100%;
        text-align: center;
    }

    h2.modal-header {
        text-align: center;
    }

    .triggers {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;
    }

    .comp-select, .cancel {
        display: inline-block;
        padding: 0.5rem;
        background-color: #333;
        border: 1px solid #222;
        margin: 0.1rem;
        transition: background-color 250ms ease;
    }

    .comp-select:hover {
        cursor: pointer;
        background-color: var(--color-accent);
    }

    .cancel:hover {
        cursor: pointer;
        background-color: var(--color-select-bg);
    }
</style>