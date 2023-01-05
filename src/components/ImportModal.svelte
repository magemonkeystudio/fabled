<script lang="ts">
  import { setImporting } from "../data/store";

  let importUrl: string | undefined;
  let fileImport: any;

  const closeModal = () => {
    importUrl = fileImport = undefined;
    setImporting(false);
  };
  const clickModal = (e: MouseEvent) => e.stopPropagation();
</script>

<div class="backdrop" on:click={closeModal}>
  <div class="modal-content" on:click={clickModal}>
    <div class="options">
      <div class="option">
        <div>Upload File</div>
        <label for="file-upload" class="button">Select File</label>
        <input id="file-upload" type="file" bind:value={fileImport} class="hidden" />
      </div>
      <div class="or"><span>OR</span></div>
      <div class="option">
        <div>Import from URL</div>
        <input bind:value={importUrl} />
        <div class="button">Import</div>
      </div>
    </div>
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

    .button {
        display: block;
        padding: 0.5rem;
        margin: 0.5rem 0;
        border-radius: 0.4rem;
        background-color: #0083ef;
    }

    input {
        display: block;
        background-color: var(--color-select-bg);
        border: 1px solid #666;
        font-size: 1.5rem;
        color: white;
        padding: 0.5rem;
        border-radius: 0.4rem;
    }

    .options {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-content: center;
    }

    .option {
        text-align: center;
    }

    .or {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 2px;
        font-size: 1rem;
        margin: 1rem 0;
        background-color: var(--color-fg);
        width: 100%;
    }

    .or span {
        background-color: var(--color-bg);
        padding: 0.4rem;
    }

    .hidden {
        display: none;
    }
</style>