<script lang="ts">
  import { fade, fly } from "svelte/transition";
  import { clickOutside } from "$api/clickoutside";
  import { createEventDispatcher } from "svelte";

  const dispatch = createEventDispatcher();

  const closeModal = () => {
    dispatch("close");
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
       transition:fly={{y: -200}}>
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
        justify-content: center;
        align-items: center;
        background-color: var(--color-bg);
        padding: 1rem;
        border: 2px solid #444;
        border-radius: 0.7rem;
        box-shadow: 0 0 1rem #222;
    }
</style>