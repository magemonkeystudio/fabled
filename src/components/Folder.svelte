<script lang='ts'>
	import Folder from './Folder.svelte';

	import { slide }                     from 'svelte/transition';
	import { dragging, sidebarOpen }     from '../data/store';
	import { get }                       from 'svelte/store';
	import SidebarEntry                  from '$components/sidebar/SidebarEntry.svelte';
	import { goto }                      from '$app/navigation';
	import { base }                      from '$app/paths';
	import type FabledAttribute          from '$api/fabled-attribute.svelte';
	import type FabledClass              from '../data/class-store.svelte';
	import type FabledSkill              from '../data/skill-store.svelte';
	import { FabledFolder, folderStore } from '../data/folder-store.svelte';

	interface Props {
		folder: FabledFolder;
	}

	let { folder = $bindable() }: Props = $props();
	let elm: HTMLElement | undefined    = $state();

	let focus = (e?: Event) => {
		e?.stopPropagation();
		if (!elm) return;

		elm.contentEditable = 'true';
		elm.focus();
		setTimeout(() => {
			if (!elm) return;
			const range: Range = document.createRange();
			range.setStart(elm, 0);
			range.setEnd(elm, 1);
			const sel = window.getSelection();
			sel?.removeAllRanges();
			sel?.addRange(range);
		}, 1);
	};

	const keydown = (e: KeyboardEvent) => {
		if (e.key == 'Enter') {
			e.preventDefault();
			e.stopPropagation();
			elm?.blur();
		}
	};

	const deleteF = (e?: Event) => {
		e?.stopPropagation();
		folderStore.deleteFolder(folder);
	};

	const addFolder = (e?: Event) => {
		e?.stopPropagation();
		folder.createFolder();
		folder.open = true;
		folderStore.updateFolders();
	};


	let over = $state(false);

	const startDrag = () => {
		dragging.set(folder);
	};

	const drop = (e: Event) => {
		e.stopPropagation();
		e.preventDefault();
		over                                                                       = false;
		const dragData: FabledClass | FabledSkill | FabledAttribute | FabledFolder = get(dragging);
		if (!dragData) return;
		if (folder.data.includes(dragData)) return;

		const containing = folderStore.getFolder(dragData);
		if (containing) containing.remove(dragData);

		if (dragData instanceof FabledFolder) {
			folderStore.removeFolder(dragData);
			dragData.parent = folder;
		}

		folder.add(dragData);
	};

	const blur = (e: Event) => {
		if (elm) {
			elm.contentEditable = 'false';
			// If the folder name is different, set it and update the store
			if (folder.name !== (<HTMLElement>e.target).textContent) {
				folder.name = (<HTMLElement>e.target).textContent || '';
				folderStore.updateFolders();
			}
		}
	};

	const dragOver = (e: Event) => {
		e.preventDefault();
		if (folder === get(dragging)) return;
		over = true;
	};
</script>

<div class='folder'
		 class:over
		 draggable='true'
		 in:slide={{duration: ($sidebarOpen ? 0 : 400)}}
		 onclick={() => folder.open = !folder.open}
		 ondragleave={() => over = false}
		 ondragover={dragOver}
		 ondragstart={startDrag}
		 ondrop={drop}
		 onkeypress={(e) => {
       if (e.key === "Enter") {
         e.stopPropagation();
         folder.open = !folder.open;
       }
     }}
		 out:slide
		 role='menuitem'
		 tabindex='0'>
  <span class='material-symbols-rounded folder-icon'>
    folder
  </span>
	<span bind:this={elm}
				class='name'
				class:server={folder.location === 'server'}
				contenteditable='false'
				onblur={blur}
				onkeydown={keydown}
				role='textbox'
				tabindex='0'>{folder.name}</span>
	<div class='buttons'>
		<div class='icon add' onclick={addFolder}
				 onkeypress={(e) => {
            if (e.key === "Enter") {
              e.stopPropagation();
              addFolder();
            }
         }}
				 role='button'
				 tabindex='0'
				 title='Add Folder'>
      <span class='material-symbols-rounded'>
        add
      </span>
		</div>
		<div class='icon' onclick={focus}
				 onkeypress={(e) => {
              if (e.key === "Enter") {
                e.stopPropagation();
                focus();
              }
         }}
				 role='button'
				 tabindex='0'
				 title='Rename'>
      <span class='material-symbols-rounded'>
        edit
      </span>
		</div>
		<div class='icon delete' onclick={deleteF}
				 onkeypress={(e) => {
							if (e.key === "Enter") {
								e.stopPropagation();
								deleteF();
							}
				 }}
				 role='button'
				 tabindex='0'
				 title='Delete Folder'>
      <span class='material-symbols-rounded'>
        delete
      </span>
		</div>
	</div>
</div>

{#if folder.open}
	<div class='folder-content' transition:slide>
		{#each folder.data as data (data?.key)}
			{#if data instanceof FabledFolder}
				<Folder folder={data} />
			{:else}
				<SidebarEntry {data}
											onclick={() => goto(`${base}/${data.dataType === 'class' ? 'class' : 'skill'}/${data.name}${data.dataType === 'class' ? '/edit' : ''}`)}>
					{data.name}{data.location === 'server' ? '*' : ''}
				</SidebarEntry>
			{/if}
		{/each}
	</div>
{/if}

<style>
    .folder-icon {
        color: #0083ef;
    }

    .folder {
        display: flex;
        justify-content: space-between;
        align-items: center;
        background-color: #444;
        padding: 0.3rem 0.5rem;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: break-spaces;
        border-left: 0 solid var(--color-accent);
        transition: background-color 0.25s ease-in-out,
        border-left-width 0.25s ease-in-out;
        user-select: none;
        -webkit-user-select: none;
        margin-inline: 0.4rem;
        border-bottom: 1px solid #aaa;
    }

    .folder.over {
        border-bottom: 10px solid rgba(0, 79, 143, 0.7);
    }

    .folder:hover {
        cursor: pointer;
    }

    .buttons {
        display: flex;
        opacity: 0;
        position: absolute;
        right: 0.25rem;
        font-size: 1.3rem;
        justify-content: center;
        align-items: stretch;
        background: rgba(0, 0, 0, 0.7);
        border-radius: 100vw;
        transition: opacity 0.25s ease;
    }

    .folder:hover .buttons {
        opacity: 1;
    }

    .name {
        flex: 1;
        margin-left: 0.5rem;
    }

    .name.server::after {
        content: '*';
    }

    .icon {
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 0.3rem;
        border-radius: 50%;
        transition: background-color 0.25s ease;
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

    .folder-content {
        margin-left: 0.3rem;
    }
</style>