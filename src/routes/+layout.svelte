<script lang="ts">
  import "../app.css";
  import HeaderBar from "../components/HeaderBar.svelte";
  import { importing, showSidebar } from "../data/store";
  import ImportModal from "../components/ImportModal.svelte";
  import Sidebar from "../components/sidebar/Sidebar.svelte";
  import { active } from "../data/store.js";
</script>

<HeaderBar />

<div id="body-container">
  {#if $showSidebar}
    <Sidebar />
  {/if}
  <div id="body" class:centered={!$active}>
    <slot />
  </div>
</div>

<div id="floating-buttons">
  <div class="button backup" title="Backup All Data">
    <span class="material-symbols-rounded">cloud_download</span>
  </div>
  <div class="button save" title="Save">
    <span class="material-symbols-rounded">save</span>
  </div>
</div>

{#if $importing}
  <ImportModal />
{/if}

<style>
    #body-container {
        flex: 1;
        max-height: 100%;
        overflow: auto;
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