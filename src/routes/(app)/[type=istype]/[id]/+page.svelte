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
    sortedTriggers = Object.keys(Triggers.MAP)
      .filter(trigger => trigger.toLowerCase().includes(searchParams.toLowerCase()))
      .sort()
      .map(key => new Triggers.MAP[key]());
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
  <h2>{skill.name}</h2>
</div>
<hr />
<div class="container">
  {#each skill.triggers as comp}
    <div class="widget">
      <ComponentWidget {skill} component={comp} on:update={update} on:save={save} />
    </div>
  {/each}
  <div class="add-trigger chip" title="Add Trigger" on:click={() => triggerModal = true}>
    <span class="material-symbols-rounded">
      variables
    </span>
  </div>
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