<script lang="ts">
  import {
    active,
    deleteProData,
    dragging,
    getFolder,
    isShowClasses,
    saveData,
    sidebarOpen,
    updateFolders
  } from "../../data/store";
  import ProSkill from "../../api/proskill";
  import ProClass from "../../api/proclass";
  import { get } from "svelte/store";
  import ProFolder from "../../api/profolder";
  import { fly, slide } from "svelte/transition";
  import Modal from "../Modal.svelte";
  import { addClassFolder } from "../../data/class-store";
  import { addSkillFolder } from "../../data/skill-store";

  export let delay = 0;
  export let direction: "right" | "left" = "left";
  export let data: ProSkill | ProClass | undefined = undefined;

  let over = false;
  let deleting = false;

  const startDrag = (e: DragEvent) => {
    if (!data) {
      e.preventDefault();
      return;
    }
    dragging.set(data);
  };

  const drop = () => {
    const dragData: ProClass | ProSkill | ProFolder = get(dragging);
    let targetFolder;
    if (data) {
      targetFolder = getFolder(data);
    }

    const containing = getFolder(dragData);
    if (containing) containing.remove(dragData);
    if (targetFolder) {
      targetFolder.add(dragData);
      over = false;
      updateFolders();
      return;
    }
    if (dragData instanceof ProFolder) {
      if (get(isShowClasses)) addClassFolder(dragData);
      else addSkillFolder(dragData);
      dragData.parent = undefined;
    }

    over = false;
  };

  const dragOver = () => {
    const dragData = get(dragging);
    if (data === dragData) return;
    over = true;
  };
</script>


<div class="sidebar-entry"
     class:over
     class:active={data && $active === data}
     class:in-folder={!!getFolder(data)}
     draggable="{!!data}"
     on:dragstart={startDrag}
     on:drop|preventDefault|stopPropagation={drop}
     on:dragover|preventDefault={dragOver}
     on:dragleave={() => over = false}
     on:click
     in:fly={{x: (direction === "left" ? -100 : 100), duration: 500, delay: $sidebarOpen ? 0 : delay}}
     out:slide>
  <slot />
  {#if data}
    <div class="buttons">
      <div
        on:click|preventDefault|stopPropagation={() => saveData(data)}
        class="download"
        title="Save {data.triggers ? 'Skill' : 'Class'}">
        <span class="material-symbols-rounded">
          save
        </span>
      </div>
      <div
        class="delete"
        on:click|preventDefault|stopPropagation={() => deleting = true}
        title="Delete {data.triggers ? 'Skill' : 'Class'}">
        <span class="material-symbols-rounded">
          delete
        </span>
      </div>
    </div>
  {/if}
</div>

{#if deleting}
  <Modal on:close={() => deleting = false}>
    <h3>Do you really want to delete {data.name}?</h3>
    <div class="buttons modal-buttons">
      <div class="button" on:click={() => deleting = false}>Cancel</div>
      <div class="button modal-delete" on:click={() => deleteProData(data)}>Delete</div>
    </div>
  </Modal>
{/if}

<style>
    .sidebar-entry {
        display: flex;
        justify-content: space-between;
        align-items: center;
        background-color: #444;
        padding: 0.3rem 0.5rem;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
        border-left: 0 solid var(--color-accent);
        transition: background-color 0.25s ease-in-out,
        border-left-width 0.25s ease-in-out;
        user-select: none;
        -webkit-user-select: none;
        margin-inline: 0.4rem;
    }

    .sidebar-entry:has(.new) {
        padding: unset;
    }

    .sidebar-entry:hover {
        cursor: pointer;
    }

    .sidebar-entry:not(.in-folder):last-child {
        position: sticky;
        margin-top: 0.5rem;
        bottom: 0;
        background-color: unset;
    }

    .sidebar-entry:not(:last-child), :global(.folder-content .sidebar-entry) {
        border-bottom: 1px solid #aaa;
    }

    .sidebar-entry.over:not(:last-child) {
        border-bottom: 10px solid rgba(0, 79, 143, 0.7);
    }

    .sidebar-entry.over:last-child {
        border-top: 10px solid rgba(0, 79, 143, 0.7);
    }

    .active {
        background-color: #005193;
        border-left-width: 4px;
    }

    .buttons {
        display: flex;
    }

    .download, .delete {
        opacity: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 0.3rem;
        border-radius: 50%;
        transition: background-color 0.25s ease,
        opacity 0.25s ease;
    }

    .sidebar-entry:hover .download, .sidebar-entry:hover .delete {
        opacity: 1;
    }

    .download:hover {
        background-color: #1dad36;
    }

    .delete:hover {
        background-color: #b60000;
    }

    .download .material-symbols-rounded, .delete .material-symbols-rounded {
        font-size: 1rem;
    }

    .modal-buttons {
        display: flex;
        justify-content: center;
    }

    .modal-buttons .button {
        margin-inline: 1rem;
    }

    .modal-delete {
        background-color: #b60000;
    }
</style>