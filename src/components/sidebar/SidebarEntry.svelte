<script lang='ts'>
	import { active, deleteProData, dragging, saveData, shownTab, sidebarOpen } from '../../data/store';
	import FabledAttribute                                                      from '$api/fabled-attribute';
	import { get }                                                              from 'svelte/store';
	import { fly, type TransitionConfig }                                       from 'svelte/transition';
	import Modal                                                                from '../Modal.svelte';
	import { animationEnabled }                                                 from '../../data/settings';
	import { base }                                                             from '$app/paths';
	import { createEventDispatcher }                                            from 'svelte';
	import { Tab }                                                              from '$api/tab';
	import FabledSkill, { skillStore }                                          from '../../data/skill-store';
	import FabledClass, { classStore }                                          from '../../data/class-store';
	import { FabledFolder, folderStore }                                        from '../../data/folder-store.js';
	import { attributeStore }                                                   from '../../data/attribute-store';

	export let delay                                                         = 0;
	export let direction: 'right' | 'left'                                   = 'left';
	export let data: FabledSkill | FabledClass | FabledAttribute | undefined = undefined;

	let over     = false;
	let deleting = false;

	const dispatch = createEventDispatcher();

	const startDrag = (e: DragEvent) => {
		if (!data) {
			e.preventDefault();
			return;
		}
		dragging.set(data);
	};

	const drop = () => {
		const dragData: FabledClass | FabledSkill | FabledAttribute | FabledFolder = get(dragging);
		let targetFolder;
		if (data) {
			targetFolder = folderStore.getFolder(data);
		}

		const containing = folderStore.getFolder(dragData);
		if (containing) containing.remove(dragData);
		if (targetFolder) {
			targetFolder.add(dragData);
			over = false;
			folderStore.updateFolders();
			return;
		}
		if (dragData instanceof FabledFolder) {
			switch (get(shownTab)) {
				case Tab.CLASSES: {
					classStore.addClassFolder(dragData);
					break;
				}
				case Tab.SKILLS: {
					skillStore.addSkillFolder(dragData);
					break;
				}
			}
			dragData.parent = undefined;
		}

		over = false;
	};

	const dragOver = () => {
		const dragData = get(dragging);
		if (data === dragData) return;
		over = true;
	};


	const maybe = (node: Element, options: {
		fn: (node: Element, options: object) => TransitionConfig
	} & TransitionConfig & { x?: number }) => {
		if (!get(animationEnabled)) {
			options.delay = 0;
		}
		return options.fn(node, options);
	};

	const cloneData = (data?: FabledClass | FabledSkill | FabledAttribute) => {
		if (!data) return;

		if (data instanceof FabledClass) {
			classStore.cloneClass(data);
		} else if (data instanceof FabledSkill) {
			skillStore.cloneSkill(data);
		} else if (data instanceof FabledAttribute) {
			attributeStore.cloneAttribute(data);
		}
	};
</script>


<div class='sidebar-entry'
		 class:over
		 class:active={data && $active === data}
		 class:in-folder={!!folderStore.getFolder(data)}
		 draggable='{!!data}'
		 on:dragstart={startDrag}
		 on:drop|preventDefault|stopPropagation={drop}
		 on:dragover|preventDefault={dragOver}
		 on:dragleave={() => over = false}
		 on:click
		 on:keypress={(e) => {
			 if (e.key === 'Enter') dispatch('click');
		 }}
		 tabindex='0'
		 role='menuitem'
		 in:maybe={{fn: fly, x: (direction === "left" ? -100 : 100), duration: 500, delay: $sidebarOpen ? 0 : delay}}
		 out:fly={{x: (direction === "left" ? -100 : 100), duration: 500}}>
	<slot />
	{#if data}
		<div class='buttons'>
			{#if data instanceof FabledSkill}
				<a href='{base}/skill/{data.name}/edit'
					 class='edit'
					 title='Edit Skill'>
          <span class='material-symbols-rounded'>
            edit
          </span>
				</a>
			{/if}
			<div on:click|preventDefault|stopPropagation={() => saveData(data)}
					 on:keypress|preventDefault|stopPropagation={(event) => {if (event?.key === 'Enter') saveData(data);}}
					 tabindex='0'
					 role='button'
					 class='download'
					 title='Save {data.dataType.substring(0, 1).toUpperCase()+data.dataType.substring(1)}'>
        <span class='material-symbols-rounded'>
          save
        </span>
			</div>
			<div on:click|preventDefault|stopPropagation={() => cloneData(data)}
					 on:keypress|preventDefault|stopPropagation={(event) => { if (event?.key === 'Enter') cloneData(data); }}
					 tabindex='0'
					 role='button'
					 class='clone'
					 title='Clone {data.dataType.substring(0, 1).toUpperCase()+data.dataType.substring(1)}'>
        <span class='material-symbols-rounded'>
          content_copy
        </span>
			</div>
			<div on:click|preventDefault|stopPropagation={(event) => {
						// If holding shift, delete without confirmation
						if (event?.shiftKey) {
							deleteProData(data);
							return;
						}
						deleting = true
					}}
					 on:keypress|preventDefault|stopPropagation={(event) => {
						 if (event?.key === 'Enter') {
							 // If holding shift, delete without confirmation
							 if (event?.shiftKey) {
								 deleteProData(data);
								 return;
							 }
							 deleting = true;
						 }
					 }}
					 tabindex='0'
					 role='button'
					 class='delete'
					 title='Delete {data.dataType.substring(0, 1).toUpperCase()+data.dataType.substring(1)}'>
        <span class='material-symbols-rounded'>
          delete
        </span>
			</div>
		</div>
	{/if}
</div>

<Modal bind:open={deleting}>
	<h3>Do you really want to delete {data?.name}?</h3>
	<div class='modal-buttons'>
		<div class='button' on:click={() => deleting = false}
				 on:keypress={(event) => { if (event?.key === 'Enter') deleting = false; }}
				 tabindex='0'
				 role='button'
		>Cancel
		</div>
		<div class='button modal-delete' on:click={() => deleteProData(data)}
				 on:keypress={(event) => { if (event?.key === 'Enter') deleteProData(data); }}
				 tabindex='0'
				 role='button'
		>Delete
		</div>
	</div>
</Modal>

<style>
    .sidebar-entry {
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

    .download, .delete, .edit, .clone {
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 0.3rem;
        border-radius: 50%;
        transition: background-color 0.25s ease;
        text-decoration: none;
        color: white;
    }

    .sidebar-entry:hover .buttons {
        opacity: 1;
    }

    .download:hover {
        background-color: #1dad36;
    }

    .delete:hover {
        background-color: #b60000;
    }

    .edit:hover {
        background-color: #0083ef;
    }

    .clone:hover {
        background-color: #00568c;

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