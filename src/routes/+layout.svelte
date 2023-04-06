<script lang="ts">
  import "../app.css";
  import { active, importing, loadFile, saveData } from "../data/store";
  import { onDestroy, onMount }                    from "svelte";
  import { browser }                               from "$app/environment";
  import ImportModal                               from "$components/ImportModal.svelte";
  import NavBar                                    from "$components/NavBar.svelte";
  import HeaderBar                                 from "$components/HeaderBar.svelte";
  import { initComponents }                        from "$api/components/components";

  let dragging = false;

  onMount(() => {
    if (!browser) return;
    document.addEventListener("dragover", dragover);
    document.addEventListener("drop", loadFiles);

    initComponents();
  });

  onDestroy(() => {
    if (!browser) return;
    document.removeEventListener("dragover", dragover);
    document.removeEventListener("drop", loadFiles);
  });

  const backup = () => {
    alert("This feature isn't implemented yet");
  };

  const dragover = (e: DragEvent) => {
    if (!(e.dataTransfer?.types?.length > 0 && e.dataTransfer?.types[0] == "Files")) return;
    e.dataTransfer.dropEffect = "copy";
    e.stopPropagation();
    e.preventDefault();
    dragging = true;
  };

  const dragleave = () => {
    setTimeout(() => dragging = false, 50);
  };

  const loadFiles = (e: DragEvent) => {
    dragging = false;
    for (let i = 0; i < e.dataTransfer.files.length; i++) {
      const file = e.dataTransfer.files[i];
      if (file.name.indexOf(".yml") == -1) continue;

      loadFile(file);
    }
    e.stopPropagation();
    e.preventDefault();
  };
</script>

<HeaderBar />
<NavBar />
<div id="body-container" class:empty={!$active}>
  <div id="body" class:centered={!$active}>
    <slot />
  </div>
</div>
<div id="floating-buttons">
  <div class="button backup" title="Backup All Data" on:click={backup}>
    <span class="material-symbols-rounded">cloud_download</span>
  </div>
  <div class="button save" title="Save" on:click={() => saveData()}>
    <span class="material-symbols-rounded">save</span>
  </div>
</div>

<footer>&copy; ProMCTeam {new Date().getFullYear()}</footer>

{#if $importing}
  <ImportModal />
{/if}

{#if dragging}
  <div class="dragging" on:dragleave={dragleave}>
    Drop to Import
  </div>
{/if}

<style>
    .dragging {
        display: flex;
        align-items: center;
        justify-content: center;
        position: fixed;
        inset: 0;
        background-color: rgba(0, 0, 0, 0.5);
        backdrop-filter: blur(5px);
        z-index: 50;
        font-size: 2rem;
    }

    @media screen and (min-width: 500px) {
        #body-container {
            /*max-height: 100%;*/
            overflow: visible;
        }
    }

    #body-container {
        flex-grow: 1;
        display: flex;
        flex-direction: column;
        padding-bottom: 2rem
    }

    #body {
        display: flex;
        flex-direction: column;
        align-items: center;
        /*padding-bottom: 1rem;*/
        flex-grow: 1;
    }

    #body.centered {
        justify-content: center;
    }

    #floating-buttons {
        display: flex;
        flex-direction: column;
        position: fixed;
        right: 0.5rem;
        bottom: 0.5rem;
        align-items: flex-end;
    }

    #floating-buttons .button {
        background-color: #0083ef;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        padding: 0.7rem;
        box-shadow: 5px 5px 5px #444;
        margin: 0.5rem;
    }

    #floating-buttons .button .material-symbols-rounded {
        font-size: 1.75rem;
    }

    #floating-buttons .save {
        background-color: #1dad36;
    }

    #body-container.empty {
        display: flex;
        align-items: center;
        justify-content: center;
    }

    footer {
        background: #333;
        position: fixed;
        bottom: 0;
        left: 0;
        font-size: 0.8rem;
        padding: 0.5rem 0.5rem 0.25rem;
        border-top-right-radius: 0.5rem;
    }
</style>