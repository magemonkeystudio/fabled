<script lang="ts">
  import { ProFolder } from "../api/profolder";
  import SidebarEntry from "./sidebar/SidebarEntry.svelte";
  import { goto } from "$app/navigation";
  import { slide } from "svelte/transition";
  import { deleteFolder, updateFolders } from "../data/store";

  export let folder: ProFolder;
  let elm: HTMLElement;

  let focus = (e?: MouseEvent) => {
    if (e) {
      e.stopPropagation();
    }
    elm.contentEditable = "true";
    elm.focus();
    setTimeout(() => {
      const range: Range = document.createRange();
      range.setStart(elm, 0);
      range.setEnd(elm, 1);
      const sel = window.getSelection();
      sel.removeAllRanges();
      sel.addRange(range);
    }, 1);
  };

  const keydown = (e: KeyboardEvent) => {
    if (e.key == "Enter") {
      e.preventDefault();
      e.stopPropagation();
      elm.blur();
    }
  };

  const deleteF = (e: MouseEvent) => {
    e.stopPropagation();
    deleteFolder(folder);
  };

  const addFolder = (e: MouseEvent) => {
    e.stopPropagation();
    folder.addFolder();
    folder.open = true;
    updateFolders();
  };
</script>

<div class="entry" on:click={() => folder.open = !folder.open} transition:slide>
  <span class="material-symbols-rounded folder">
    folder
  </span>
  <span class="name" contenteditable="false"
        bind:this={elm}
        on:blur={elm.contentEditable = "false"}
        bind:textContent={folder.name}
        on:keydown={keydown} />
  <div class="icon add" title="Add Folder"
       on:click={addFolder}>
      <span class="material-symbols-rounded">
        add
      </span>
  </div>
  <div class="icon" title="Rename"
       on:click={focus}>
      <span class="material-symbols-rounded">
        edit
      </span>
  </div>
  <div class="icon delete" title="Delete Folder"
       on:click={deleteF}>
      <span class="material-symbols-rounded">
        delete
      </span>
  </div>
</div>

{#if folder.open}
  <div class="folder-content">
    {#each folder.data as data, i (data.key)}
      {#if data.isFolder}
        <svelte:self folder={data} />
      {:else}
        <SidebarEntry {data}
                      useSlide
                      on:click={() => goto(`/${data.isClass ? 'class' : 'skill'}/${data.name}`)} />
      {/if}
    {/each}
  </div>
{/if}

<style>
    .folder {
        color: #0083ef;
    }

    .entry {
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
        border-bottom: 1px solid #aaa;
    }

    .entry:hover {
        cursor: pointer;
    }

    .entry:hover .icon {
        opacity: 1;
    }

    .name {
        flex: 1;
        margin-left: 0.5rem;
    }

    .icon {
        opacity: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 0.3rem;
        border-radius: 50%;
        transition: background-color 0.25s ease,
        opacity 0.25s ease;
    }

    .icon:hover {
        background-color: #1dad36;
    }

    .icon.add:hover {
        background-color: #006c91;
    }

    .icon.delete:hover {
        background-color: #b60000;
    }

    .material-symbols-rounded {
        font-size: 1rem;
    }

    .folder-content {
        margin-left: 0.3rem;
    }
</style>