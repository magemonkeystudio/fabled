<script lang="ts">
  import "../app.css";
  import HeaderBar from "../components/HeaderBar.svelte";
  import { active, importing, loadClasses, loadFile, loadIndividual, loadSkills, showSidebar } from "../data/store";
  import ImportModal from "../components/ImportModal.svelte";
  import Sidebar from "../components/sidebar/Sidebar.svelte";
  import NavBar from "../components/NavBar.svelte";
  import { get } from "svelte/store";
  import { onDestroy, onMount } from "svelte";
  import { browser } from "$app/environment";

  const backup = () => {
    alert("This feature isn't implemented yet");
  };

  const save = () => {
    const act = get(active);
    if (!act) return;

    saveToFile(act.name + ".yml", act.serializeYaml().toString());
  };

  /**
   * Saves text data to a file locally
   */
  const saveToFile = (file, data) => {
    const textFileAsBlob = new Blob([data], { type: "text/plain;charset=utf-8" });

    let element = document.createElement("a");
    element.href = URL.createObjectURL(textFileAsBlob);
    element.download = file;
    element.style.display = "none";

    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  };

  const dragover = (e: DragEvent) => {
    e.dataTransfer.dropEffect = "copy";
    e.stopPropagation();
    e.preventDefault();
  };

  const loadFiles = (e: DragEvent) => {
    for (let i = 0; i < e.dataTransfer.files.length; i++) {
      const file = e.dataTransfer.files[i];
      if (file.name.indexOf(".yml") == -1) continue;

      loadFile(file);
    }
    e.stopPropagation();
    e.preventDefault();
  };

  onMount(() => {
    if (!browser) return;
    document.addEventListener("dragover", dragover);
    document.addEventListener("drop", loadFiles);
  });

  onDestroy(() => {
    if (!browser) return;
    document.removeEventListener("dragover", dragover);
    document.removeEventListener("drop", loadFiles);
  });
</script>

<HeaderBar />
<NavBar />
{#if $showSidebar}
  <Sidebar />
{/if}
<div id="body-container">
  <div id="body" class:centered={!$active}>
    <slot />
  </div>
</div>

<div id="floating-buttons">
  <div class="button backup" title="Backup All Data" on:click={backup}>
    <span class="material-symbols-rounded">cloud_download</span>
  </div>
  <div class="button save" title="Save" on:click={save}>
    <span class="material-symbols-rounded">save</span>
  </div>
</div>

{#if $importing}
  <ImportModal />
{/if}

<style>
    @media screen and (min-width: 500px) {
        #body-container {
            /*max-height: 100%;*/
            overflow: visible;
        }
    }

    #body-container {
        flex: 1;
    }

    #body {
        display: flex;
        flex-direction: column;
        align-items: center;
        min-height: 100%;
        padding-bottom: 1rem;
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
</style>