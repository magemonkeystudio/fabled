<script lang="ts">
  import { fly } from "svelte/transition";
  import { numberOnly } from "$api/number-only";

  export let tooltip: string | undefined = undefined;
  export let label: string;
  export let type: "string" | "number" = "string";
  export let intMode = false;
  export let value: string | number | undefined = undefined;
  export let placeholder: string | undefined = undefined;
  let hovered = false;
</script>

<div class="label"
     on:mouseenter={() => hovered = true}
     on:mouseleave={() => hovered = false}>
  {#if tooltip && tooltip.length > 0 && hovered}
    <div class="tooltip"
         transition:fly={{y: 20}}>
      {tooltip}
    </div>
  {/if}
  <span>{label || ''}
    <slot name="label" /></span>
</div>
<div class="input-wrapper" class:labeled={!!label}>
  {#if type === "number"}
    <input type="number" bind:value use:numberOnly={{intMode, enabled: type === "number"}}
           {placeholder} />
  {:else}
    {#if !!value || value === "" || value === 0}
      <input bind:value {placeholder} />
    {/if}
  {/if}
  <slot />
</div>

<style>
    input {
        width: 100%;
        padding-inline: 0.5rem;
    }

    .label {
        padding-right: 1rem;
        display: flex;
        align-items: center;
        justify-content: flex-end;
        height: fit-content;
        align-self: center;
    }

    .tooltip {
        z-index: 30;
        text-align: center;
        background-color: #0a0a0a;
        padding: 0.75rem;
        position: absolute;
        max-width: 70%;
        bottom: 125%;
        right: 0;
        border-radius: 0.3rem;
        border: 1px solid #777;
        white-space: break-spaces;
    }

    .tooltip:before {
        position: absolute;
        top: 100%;
        right: 10%;
        border: 6px solid transparent;
        background: transparent;
        content: "";
        border-top-color: #777;
    }
</style>