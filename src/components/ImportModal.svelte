<script lang="ts">
  import { setImporting } from "../data/store";

  const closeModal = () => setImporting(false);
  const clickModal = (e: MouseEvent) => e.stopPropagation();
</script>

<div class="backdrop" on:click={closeModal}>
  <div class="modal-content" on:click={clickModal}>
    <div class="options">
      <div class="option">
        <div>Import from URL</div>
      </div>
      <div class="or"><span>OR</span></div>
      <div class="option">
        <div>Upload File</div>
      </div>
    </div>
    <div class="button">Import</div>
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
        border: 2px solid black;
        border-radius: 0.7rem;
        box-shadow: 0.5rem 0.5rem 1rem black;
    }

    .button {
        padding: 0.5rem;
        border-radius: 0.4rem;
        background-color: #0083ef;
    }

    .options {
        display: grid;
        grid-template-columns: 1fr auto 1fr;
        justify-content: center;
        align-content: center;
        margin: 0 0 1rem;
    }

    .option {
        text-align: center;
    }

    .or {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 2px;
        font-size: 1rem;
        margin-inline: 1rem;
        background-color: var(--color-fg);
        height: 3rem;
    }

    .or span {
        background-color: var(--color-bg);
    }
</style>