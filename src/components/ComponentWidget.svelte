<!--suppress CssUnresolvedCustomProperty -->
<script lang="ts">
  import ProComponent from "$api/procomponent";
  import ProTrigger from "$api/triggers";
  import ProCondition from "$api/conditions";

  export let component: ProComponent;
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
</script>

<div draggable="true" class="comp-body" style:--comp-color={getColor()}>
  <div class="name"><span>{getName()}</span>: {component.name}</div>

  <div class="children">
    {#each component.components as child}
      <svelte:self bind:component={child} />
    {/each}
  </div>
</div>

<!--TODO Put this in a modal-->
<!--{#each component.data as datum}-->
<!--  <svelte:component this={datum.component} bind:data={datum.data} color />-->
<!--{/each}-->

<style>
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

    .comp-body:before {
        position: absolute;
        right: 0;
        top: 0;
        content: '';
        border-top: 1.3rem solid var(--comp-color);
        border-left: 1.3rem solid transparent;
    }

    .name {
        margin: 0.5rem 1rem 0.5rem 0.25rem;
    }

    span {
        font-weight: bold;
        color: var(--comp-color);
    }

    .children {
        display: flex;
        flex-direction: column;
        align-items: stretch;
    }
</style>