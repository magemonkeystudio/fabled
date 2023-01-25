<!--suppress CssUnresolvedCustomProperty -->
<script lang="ts">
  import ProComponent from "$api/procomponent";
  import ProTrigger from "$api/triggers";
  import ProCondition from "$api/conditions";
  import { slide } from "svelte/transition";
  import { backOut } from "svelte/easing";
  import { squish } from "../data/squish";
  import { draggingComponent } from "../data/store";
  import Modal from "$components/Modal.svelte";
  import ProInput from "$input/ProInput.svelte";
  import Toggle from "$input/Toggle.svelte";
  import ProSkill from "$api/proskill";
  import { createEventDispatcher } from "svelte";

  export let skill: ProSkill;
  export let component: ProComponent;

  const dispatch = createEventDispatcher();

  let modalOpen = false;
  let collapsed = false;
  const getName = () => {
    if (component instanceof ProTrigger) {
      return "Trigger";
    } else if (component instanceof ProCondition) {
      return "Condition";
    }

    return "???";
  };

  const getColor = () => {
    if (component instanceof ProTrigger) {
      return "#0083ef";
    } else if (component instanceof ProCondition) {
      return "#feac00";
    }

    return "orange";
  };

  function spin(node, { duration }) {
    return {
      duration,
      css: t => {
        const eased = backOut(t);

        return `transform: rotate(${180 - (eased * 180)}deg);`;
      }
    };
  }
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
      <div class="children" transition:slide|local>
        {#each component.components as child}
        <span transition:slide|local>
          <svelte:self {skill} bind:component={child} on:update />
        </span>
        {/each}
        <div class="chip">
          + Add Component
        </div>
      </div>
    {/if}
  </div>
  {#if !collapsed}
    <div class="side-buttons" transition:squish={{duration: 300}}>
      <div class="side-button copy material-symbols-rounded"
           title="Copy"
           on:click|stopPropagation={() => console.log('clicked copy')}
           transition:slide>
        content_copy
      </div>
      <div class="side-button delete material-symbols-rounded"
           title="Delete"
           on:click|stopPropagation={() => {
             skill.removeComponent(component);
             dispatch("update");
           }}
           transition:slide>
        delete
      </div>
    </div>
  {/if}
</div>

{#if modalOpen}
  <Modal on:close={() => modalOpen = false} width="70%">
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
        <svelte:component this={datum.component} bind:data={datum.data} color />
      {/each}
    </div>
  </Modal>
{/if}

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
        margin: 0.5rem 1rem 0.5rem 0.25rem;
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

    .side-buttons {
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        overflow: hidden;
        margin-top: 0.2rem;
        margin-right: 0.25rem;
        user-select: none;
    }

    .side-button {
        background: red;
        padding: 0.2rem;
        border-top-right-radius: 0.4rem;
        border-bottom-right-radius: 0.4rem;
        margin-top: 0.25rem;
        border-top: 2px solid rgba(0, 0, 0, 0.4);
        border-right: 2px solid rgba(0, 0, 0, 0.4);
        border-bottom: 2px solid rgba(0, 0, 0, 0.4);
        color: rgba(0, 0, 0, 0.4);
        transition: color 0.1s ease;
    }

    .side-button:active {
        color: rgba(255, 255, 255, 0.5);
        box-shadow: inset 0 0 0.5rem rgba(0, 0, 0, 0.4);
    }

    .copy {
        background-color: #0083ef;
    }

    .side-button:hover {
        cursor: pointer;
    }
</style>