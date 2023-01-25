<!--suppress CssUnresolvedCustomProperty -->
<script lang="ts">
  import { fade, fly } from "svelte/transition";
  import { clickOutside } from "$api/clickoutside";
  import { createEventDispatcher } from "svelte";

  export let width = "auto";

  const dispatch = createEventDispatcher();

  const closeModal = (e?: CustomEvent) => {
    dispatch("close");
    e?.detail?.stopPropagation();
  };

  const checkClose = (e: KeyboardEvent) => {
    if (e.key == "Escape") {
      e.preventDefault();
      e.stopPropagation();
      closeModal();
    }
  };
</script>

<svelte:window on:keyup={checkClose} />

<div class="backdrop" transition:fade>
  <div class="modal-content"
       use:clickOutside
       on:outclick={closeModal}
       on:click|stopPropagation
       transition:fly={{y: -200}}
       style:--width={width}>
    <slot />
  </div>
</div>

<style>
    .backdrop {
        position: fixed;
        inset: 0;
        z-index: 100;

        display: flex;
        justify-content: center;
        align-items: center;

        background: rgba(0, 0, 0, 0.6);
    }

    .modal-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        background-color: var(--color-bg);
        padding: 1rem;
        border: 2px solid #444;
        border-radius: 0.7rem;
        box-shadow: 0 0 1rem #222;
        max-height: 95vh;
        width: 95%;
        overflow-y: auto;
    }

    @media screen and (min-width: 500px) {
        .modal-content {
            width: auto;
            min-width: var(--width);
        }
    }
</style>