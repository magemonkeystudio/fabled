<script lang="ts">
  import ProSkill from "$api/proskill";
  import ComponentWidget from "$components/ComponentWidget.svelte";
  import Modal from "$components/Modal.svelte";
  import { draggingComponent } from "../../../../data/store";
  import { get } from "svelte/store";
  import ProInput from "$input/ProInput.svelte";
  import { Triggers } from "$api/components/components";
  import { skills } from "../../../../data/skill-store";

  export let data: { data: ProSkill };
  let skill: ProSkill;
  $: if (data) skill = data.data;
  let triggerModal = false;
  let hovered = false;
  let searchParams = "";
  let sortedTriggers;

  $: {
    sortedTriggers = Object.keys(Triggers)
      .filter(trigger => trigger.toLowerCase().includes(searchParams.toLowerCase()))
      .sort()
      .map(key => Triggers[key].component.new());
  }

  const onSelectTrigger = data => {
    skill.triggers.push(data.detail);
    update();
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
    save();
  };

  const save = () => skills.set([...get(skills)]);
</script>

<svelte:head>
  <title>ProSkillAPI Dynamic Editor - {skill.name}</title>
</svelte:head>
<div class="header">
  <h2>
    {skill.name}
    <a class="material-symbols-rounded edit-skill chip" title="Edit"
       href="/skill/{skill.name}/edit">edit</a>
    <div class="add-trigger chip" title="Add Trigger" on:click={() => triggerModal = true}>
    <span class="material-symbols-rounded">
      new_label
    </span>
    </div>
  </h2>
</div>
<hr />
<div class="container">
  {#each skill.triggers as comp}
    <div class="widget">
      <ComponentWidget {skill} component={comp} on:update={update} on:save={save} />
    </div>
  {/each}
  {#if skill.triggers.length == 0}
    <div>No triggers added yet.</div>
  {/if}
</div>

<Modal bind:open={triggerModal}>
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

    .add-trigger:hover {
        cursor: pointer;
    }

    .add-trigger, .edit-skill {
        display: inline-flex;
        justify-content: center;
        align-items: center;
        height: 100%;
        width: 6rem;
        overflow: hidden;
        font-size: inherit;
        color: white;
        text-decoration: none;
        transition: background-color 0.25s ease;
    }

    .edit-skill {
        margin-left: 1rem;
        background-color: #1dad36;
    }

    .edit-skill:hover {
        background-color: #2fd950;
    }

    .edit-skill:active {
        background-color: #157e2b;
        box-shadow: inset 0 0 5px #333;
    }
</style>