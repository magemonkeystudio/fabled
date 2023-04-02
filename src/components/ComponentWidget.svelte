<!--suppress CssUnresolvedCustomProperty -->
<script lang="ts">
  import ProComponent from "$api/components/procomponent";
  import ProTrigger from "$api/components/triggers";
  import ProCondition from "$api/components/conditions";
  import ProTarget from "$api/components/targets";
  import ProMechanic from "$api/components/mechanics";
  import { slide } from "svelte/transition";
  import { backOut } from "svelte/easing";
  import { draggingComponent } from "../data/store";
  import Modal from "$components/Modal.svelte";
  import ProInput from "$input/ProInput.svelte";
  import Toggle from "$input/Toggle.svelte";
  import ProSkill from "$api/proskill";
  import { createEventDispatcher } from "svelte";
  import { Conditions, Mechanics, Targets } from "$api/components/components";

  export let skill: ProSkill;
  export let component: ProComponent;

  const dispatch = createEventDispatcher();

  let modalOpen = false;
  let componentModal = false;
  let collapsed = false;

  let searchParams = "";

  let sortedTargets: Array<ProTarget>;
  let sortedConditions: Array<ProCondition>;
  let sortedMechanics: Array<ProMechanic>;

  $: {
    sortedTargets = Object.keys(Targets.MAP)
      .filter(target => target.toLowerCase().includes(searchParams.toLowerCase()))
      .sort()
      .map(str => Targets.MAP[str].new());

    sortedConditions = Object.keys(Conditions.MAP)
      .filter(condition => condition.toLowerCase().includes(searchParams.toLowerCase()))
      .sort()
      .map(str => Conditions.MAP[str].new());

    sortedMechanics = Object.keys(Mechanics.MAP)
      .filter(mechanic => mechanic.toLowerCase().includes(searchParams.toLowerCase()))
      .sort()
      .map(str => Mechanics.MAP[str].new());
  }

  const getName = () => {
    if (component instanceof ProTrigger) {
      return "Trigger";
    } else if (component instanceof ProCondition) {
      return "Condition";
    } else if (component instanceof ProTarget) {
      return "Target";
    } else if (component instanceof ProMechanic) {
      return "Mechanic";
    }

    return "???";
  };

  const addComponent = (comp: ProComponent) => {
    component.components = [...component.components, comp];
    componentModal = false;
    searchParams = "";
    dispatch("save");
  };

  const getColor = () => {
    if (component instanceof ProTrigger) {
      return "#0083ef";
    } else if (component instanceof ProCondition) {
      return "#feac00";
    } else if (component instanceof ProTarget) {
      return "#04af38";
    } else if (component instanceof ProMechanic) {
      return "#ff3a3a";
    }

    return "orange";
  };

  const spin = (node, { duration }) => {
    return {
      duration,
      css: t => {
        const eased = backOut(t);

        return `transform: rotate(${180 - (eased * 180)}deg);`;
      }
    };
  };
</script>

<div class="wrapper">
  <div out:slide
       draggable="true"
       on:dragstart|stopPropagation={() => draggingComponent.set(component)}
       on:dragend={() => draggingComponent.set(undefined)}
       on:click|stopPropagation={() => modalOpen = true}
       class="comp-body"
       style:--comp-color={getColor()}>
    {#if collapsed}
      <span class="material-symbols-rounded" in:spin|local={{duration: 400}}>expand_more</span>
    {:else}
      <span class="material-symbols-rounded" in:spin|local={{duration: 400}}>expand_less</span>
    {/if}
    <div class="corner" on:click|stopPropagation={() => collapsed = !collapsed} />
    <div class="name"><span>{getName()}</span>: {component.name}</div>

    {#if !collapsed}
      <div class="controls">
        <div class="material-symbols-rounded control copy"
             title="Copy"
             transition:slide
             on:click|stopPropagation={() => console.log('clicked copy')}
        >content_copy
        </div>
        <div class="material-symbols-rounded control delete"
             title="Delete"
             transition:slide
             on:click|stopPropagation={() => {
               skill.removeComponent(component);
               dispatch("update");
             }}
        >delete
        </div>
      </div>
      <div class="children" transition:slide|local>
        {#each component.components as child}
        <span transition:slide|local>
          <svelte:self {skill} bind:component={child} on:update on:save />
        </span>
        {/each}
        {#if component.isParent}
          <div class="chip" on:click|stopPropagation={() => componentModal = true}>
            + Add Component
          </div>
        {/if}
      </div>
    {/if}
  </div>
</div>

<Modal bind:open={modalOpen} width="70%">
  <h2>{component.name}</h2>
  <hr />
  <div class="component-entry">
    {#if component instanceof ProTrigger}
      <ProInput label="Mana">
        <Toggle bind:data={component.mana} />
      </ProInput>
      <ProInput label="Cooldown">
        <Toggle bind:data={component.cooldown} />
      </ProInput>
    {/if}
    {#each component.data as datum}
      <svelte:component this={datum.component} bind:data={datum.data} color on:save />
    {/each}
  </div>
</Modal>

<Modal bind:open={componentModal} width="70%">
  <div class="modal-header-wrapper">
    <div />
    <h2>Add a Component</h2>
    <div class="search-bar">
      <ProInput bind:value={searchParams} placeholder="Search..." />
    </div>
  </div>
  {#if sortedConditions.length > 0}
    <hr />
    <div class="comp-modal-header">
      <h3>Conditions</h3>
    </div>
    <div class="triggers">
      {#each sortedConditions as condition}
        <div class="comp-select" on:click={() => addComponent(condition)}>{condition.name}</div>
      {/each}
    </div>
  {/if}
  {#if sortedTargets.length > 0}
    <hr />
    <div class="comp-modal-header">
      <h3>Targets</h3>
    </div>
    <div class="triggers">
      {#each sortedTargets as target}
        <div class="comp-select" on:click={() => addComponent(target)}>{target.name}</div>
      {/each}
    </div>
  {/if}
  {#if sortedMechanics.length > 0}
    <hr />
    <div class="comp-modal-header">
      <h3>Mechanics</h3>
    </div>
    <div class="triggers">
      {#each sortedMechanics as mechanic}
        <div class="comp-select" on:click={() => addComponent(mechanic)}>{mechanic.name}</div>
      {/each}
    </div>
  {/if}
  <hr />
  <div class="cancel" on:click={() => componentModal = false}>Cancel</div>
</Modal>

<style>
    span.material-symbols-rounded {
        color: rgba(0, 0, 0, 0.4);
        position: absolute;
        top: 0;
        right: 0;
        z-index: 3;
        pointer-events: none;
        /*text-shadow: 0 0 0.1rem black;*/
    }

    .comp-body {
        display: flex;
        flex-direction: column;
        align-items: stretch;
        justify-items: center;
        padding: 0.25rem;
        background-color: #333;
        box-shadow: inset 0 0 0.5rem #111;
        border: 0.13rem solid #444;
        border-left: 0.3rem solid var(--comp-color);
        border-radius: 0 0.4rem 0.4rem 0;
        overflow: hidden;
        user-select: none;
    }

    .corner {
        position: absolute;
        right: -3rem;
        top: 0;
        height: 2rem;
        width: 4rem;
        z-index: 2;
        transform-origin: 50% 0;
        transform: rotate(45deg);
        background-color: var(--comp-color);
        /*border-left: 1.3rem solid transparent;*/
        transition: right 0.25s ease;
        box-shadow: inset 0 -2px 0.5rem #222;
    }

    .corner:hover {
        right: -2rem;
        cursor: pointer;
    }

    .name {
        margin: 0.5rem 1rem 0 0.25rem;
    }

    .name span {
        font-weight: bold;
        color: var(--comp-color);
    }

    .children {
        display: flex;
        flex-direction: column;
        align-items: stretch;
    }

    .chip {
        background-color: #1a1a1a;
        margin: 0.5rem 0.25rem 0.25rem;
    }

    .chip:hover {
        background-color: #0083ef;
        cursor: pointer;
    }

    .component-entry {
        display: grid;
        grid-template-columns: 40% 60%;
        width: 100%;
        padding-inline: 0.5rem;
        padding-top: 0.25rem;
    }

    .wrapper {
        display: flex;
    }

    .control {
        user-select: none;
        flex: 1;
        text-align: center;
        background: red;
        padding: 0.25rem;
        border-radius: 0.4rem;
        margin: 0.15rem;
        border: 2px solid rgba(0, 0, 0, 0.4);
        color: rgba(0, 0, 0, 0.4);
        transition: color 0.3s ease, background 0.3s ease;
    }

    .copy {
        background: #0083ef;
    }

    .control:hover {
        cursor: pointer;
        background: #ff5656;
    }

    .control.copy:hover {
        background: #00a5ff;
    }

    .control:active {
        color: rgba(255, 255, 255, 0.5);
        box-shadow: inset 0 0 0.5rem rgba(0, 0, 0, 0.4);
        background: #b40000;
    }

    .control.copy:active {
        background: #006bc2;
    }

    .comp-modal-header {
        display: flex;
        justify-content: space-between;
        width: 100%;
    }

    h3 {
        text-decoration: underline;
    }

    .controls {
        display: flex;
        justify-content: stretch;
        align-items: center;
    }
</style>