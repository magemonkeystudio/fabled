<script lang="ts">
  import {
    active,
    addClassFolder,
    addSkillFolder,
    dragging,
    getFolder,
    isShowClasses,
    sidebarOpen,
    updateFolders
  } from "../../data/store";
  import { ProSkill } from "../../api/proskill";
  import { ProClass } from "../../api/proclass";
  import { get } from "svelte/store";
  import { ProFolder } from "../../api/profolder";
  import { fly } from "svelte/transition";

  export let delay = 0;
  export let direction: "right" | "left" = "left";
  export let data: ProSkill | ProClass | undefined = undefined;
  export let useSlide = false;

  let over = false;

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

    getFolder(dragData)?.remove(dragData);
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

  const dragOver = (e: DragEvent) => {
    const dragData = get(dragging);
    if (data === dragData) return;
    e.preventDefault();
    over = true;
  };
</script>


<div class="sidebar-entry"
     class:over
     class:active={data && $active == data}
     class:in-folder={!!getFolder(data)}
     draggable="{!!data}"
     on:dragstart={startDrag}
     on:drop={drop}
     on:dragover={dragOver}
     on:dragleave={() => over = false}
     on:click
     in:fly={{x: (direction == "left" ? -100 : 100), duration: 500, delay: $sidebarOpen ? 0 : delay}}>
  <slot />
  {#if data}
    <div class="download" title="Save {data.triggers ? 'Skill' : 'Class'}">
        <span class="material-symbols-rounded">
          save
        </span>
    </div>
  {/if}
</div>

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
        border-left: 0px solid var(--color-accent);
        transition: background-color 0.25s ease-in-out,
        border-left-width 0.25s ease-in-out;
        user-select: none;
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

    .download {
        opacity: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 0.3rem;
        border-radius: 50%;
        transition: background-color 0.25s ease,
        opacity 0.25s ease;
    }

    .sidebar-entry:hover .download {
        opacity: 1;
    }

    .download:hover {
        background-color: #1dad36;
    }

    .download .material-symbols-rounded {
        font-size: 1rem;
    }
</style>