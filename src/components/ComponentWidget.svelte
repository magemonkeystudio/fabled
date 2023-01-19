<!--suppress CssUnresolvedCustomProperty -->
<script lang="ts">
  import ProComponent from "../api/procomponent";
  import ProTrigger from "../api/triggers";

  export let component: ProComponent;
  export let color = "#0083ef";

  const getName = () => {
    if (component instanceof ProTrigger) {
      return "Trigger";
    }

    return "???";
  };
</script>

<div class="comp-body" style:--comp-color={color}>
  <div><span>{getName()}</span>: {component.name}</div>
</div>

<style>
    .comp-body {
        display: flex;
        align-items: center;
        justify-items: flex-start;
        padding: 0.5rem 1rem 0.5rem 0.5rem;
        background-color: #333;
        margin-right: 1rem;
        border-left: 0.3rem solid var(--comp-color);
    }

    .comp-body:after {
        position: absolute;
        right: 0;
        top: 0;
        content: '';
        border-top: 1.3rem solid var(--color-bg);
        border-left: 1.3rem solid transparent;

    }

    span {
        font-weight: bold;
        color: var(--comp-color);
    }
</style>