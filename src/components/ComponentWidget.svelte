<!--suppress CssUnresolvedCustomProperty -->
<script lang="ts">
  import ProComponent from "$api/procomponent";
  import ProTrigger from "$api/triggers";
  import ProCondition from "$api/conditions";
  import { slide } from "svelte/transition";
  import { backOut } from "svelte/easing";
  import { draggingComponent } from "../data/store";

  export let component: ProComponent;

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

<div draggable="true"
     out:slide
     on:dragstart|stopPropagation={() => draggingComponent.set(component)}
     on:dragend={() => draggingComponent.set(undefined)}
     class="comp-body"
     style:--comp-color={getColor()}>
  {#if collapsed}
    <span class="material-symbols-rounded" in:spin|local={{duration: 400}}>expand_more</span>
  {:else}
    <span class="material-symbols-rounded" in:spin|local={{duration: 400}}>expand_less</span>
  {/if}
  <div class="corner" on:click={() => collapsed = !collapsed} />
  <div class="name"><span>{getName()}</span>: {component.name}</div>

  {#if !collapsed}
    <div class="children" transition:slide|local>
      {#each component.components as child}
        <span transition:slide|local>
          <svelte:self bind:component={child} />
        </span>
      {/each}
    <div class="chip">
      + Add Component
    </div>
    </div>
  {/if}
</div>

<!--TODO Put this in a modal-->
<!--{#each component.data as datum}-->
<!--  <svelte:component this={datum.component} bind:data={datum.data} color />-->
<!--{/each}-->

<style>
    .material-symbols-rounded {
        color: var(--color-fg);
        position: absolute;
        top: 0;
        right: 0;
        z-index: 3;
        pointer-events: none;
        text-shadow: 0 0 0.1rem black;
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
        transform-origin: 50% 0;
        transform: rotate(45deg);
        background-color: var(--comp-color);
        /*border-left: 1.3rem solid transparent;*/
        transition: right 0.25s ease;
    }

    .corner:hover {
        right: -2rem;
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
</style>