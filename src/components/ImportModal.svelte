<script lang="ts">
  import Modal from "./Modal.svelte";
  import { loadFile, loadRaw, setImporting } from "../data/store";
  import { getHaste } from "../api/hastebin";

  let importUrl: string | undefined;
  let files: File[] | undefined;

  const onClose = () => {
    importUrl = files = undefined;
    setImporting(false);
  };

  const importFromUrl = () => {
    if (!importUrl) return;
    if (!importUrl.startsWith("http")) importUrl = "https://" + importUrl;

    getHaste({ url: importUrl })
      .then(text => {
        onClose();
        loadRaw(text);
      })
      .catch(err => {
        console.error(err);
      });
  };

  $: if (files && files.length > 0) {
    for (const file of files) {
      if (file.name.indexOf(".yml") == -1) continue;
      loadFile(file);
      onClose();
    }
  }
</script>

<Modal on:close={onClose}>
  <div class="options">
    <div class="option">
      <div>Upload File</div>
      <label for="file-upload" class="button">Select File</label>
      <input id="file-upload" type="file" bind:files={files} class="hidden" multiple />
    </div>
    <div class="or"><span>OR</span></div>
    <div class="option">
      <div>Import from URL</div>
      <input bind:value={importUrl} />
      <div class="button" on:click={importFromUrl}>Import</div>
    </div>
  </div>
</Modal>

<style>
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