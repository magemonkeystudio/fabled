<script lang='ts'>
	import '../app.css';
	import { active, importing, loadFile, saveAll, saveData, saveError, showSidebar } from '../data/store';
	import { onDestroy, onMount }                                                     from 'svelte';
	import { browser }                                                                from '$app/environment';
	import ImportModal
																																										from '$components/ImportModal.svelte';
	import NavBar                                                                     from '$components/NavBar.svelte';
	import HeaderBar                                                                  from '$components/HeaderBar.svelte';
	import { isSaving, skills }                                                       from '../data/skill-store';
	import { fly }                                                                    from 'svelte/transition';
	import type { Unsubscriber }                                                      from 'svelte/store';
	import { get }                                                                    from 'svelte/store';
	import Sidebar
																																										from '$components/sidebar/Sidebar.svelte';
	import { activeModal, closeModal, modalData, openModal }                          from '../data/modal-service';
	import SettingsModal
																																										from '$components/modal/SettingsModal.svelte';

	let dragging    = false;
	let displaySave = false;
	let saveTask: number;
	let saveSub: Unsubscriber;

	onMount(() => {
		if (!browser) return;
		document.addEventListener('dragover', dragover);
		document.addEventListener('drop', loadFiles);

		saveSub = isSaving.subscribe(saving => {
			if (!saving) {
				setTimeout(() => displaySave = false, 1000);
				return;
			}

			if (saveTask) clearTimeout(saveTask);

			saveTask = <number><unknown>setTimeout(() => displaySave = true, 1000);
		});
	});

	onDestroy(() => {
		if (!browser) return;
		document.removeEventListener('dragover', dragover);
		document.removeEventListener('drop', loadFiles);

		if (saveSub) saveSub();
	});

	const dragover = (e: DragEvent) => {
		if (!e?.dataTransfer?.types || !(e.dataTransfer.types.length > 0 && e.dataTransfer?.types[0] == 'Files')) return;

		e.dataTransfer.dropEffect = 'copy';
		e.stopPropagation();
		e.preventDefault();
		dragging = true;
	};

	const dragleave = () => {
		setTimeout(() => dragging = false, 50);
	};

	const loadFiles = (e: DragEvent) => {
		if (!e?.dataTransfer?.files || e.dataTransfer.files.length == 0) return;

		dragging = false;
		for (let i = 0; i < e.dataTransfer.files.length; i++) {
			const file = e.dataTransfer.files[i];
			if (file.name.indexOf('.yml') == -1) continue;

			loadFile(file);
		}
		e.stopPropagation();
		e.preventDefault();
	};

	const save = () => {
		skills.set([...get(skills)]);
		get(active)?.save();
	};
</script>

<HeaderBar />
<NavBar />
<div id='body-container' class:empty={!$active}>
	{#if $showSidebar}
		<Sidebar />
	{/if}
	<div id='body' class:centered={!$active}>
		<slot />
	</div>
</div>
<div id='floating-buttons'>
	<div class='button backup' title='Backup All Data'
			 tabindex='0'
			 role='button'
			 on:click={saveAll}
			 on:keypress={(e) => e.key === 'Enter' && saveAll()}
	>
		<span class='material-symbols-rounded'>cloud_download</span>
	</div>
	<div class='button settings' title='Change Settings'
			 tabindex='0'
			 role='button'
			 on:click={() => openModal(SettingsModal)}
			 on:keypress={(e) => e.key === 'Enter' && openModal(SettingsModal)}
	>
		<span class='material-symbols-rounded'>settings</span>
	</div>
	<div class='button save' title='Save'
			 tabindex='0'
			 role='button'
			 on:click={() => saveData()}
			 on:keypress={(e) => e.key === 'Enter' && saveData()}
	>
		<span class='material-symbols-rounded'>save</span>
	</div>
</div>

{#if $importing}
	<ImportModal />
{/if}

{#if $saveError}
	<div class='save-error' transition:fly={{y: -20}}>
		<strong>Failed to save {$saveError.name} - Data is too large.</strong>
		<div>We can keep it in memory for you to use, but will be unable to persist it to your browser's storage.</div>
		<div>Closing/Refreshing the page will cause you to lose this data.</div>
		<div>You'll need to export it and re-import later if you want to keep working with this.</div>
		<div class='acknowledge button'
				 tabindex='0'
				 role='button'
				 on:click={() => { get(saveError).acknowledged = true; saveError.set(null); }}
				 on:keypress={(e) => { if (e.key === 'Enter') { get(saveError).acknowledged = true; saveError.set(null); }}}
		>I Understand
		</div>
	</div>
{/if}

<!-- Display our active modal -->
<svelte:component
	this={$activeModal}
	data={$modalData}
	on:close={closeModal}
	on:save={save} />

{#if displaySave}
	<div class='saving' transition:fly={{y: -20}}>{$isSaving ? 'Saving...' : 'Saved!'}</div>
{/if}

{#if dragging}
	<div class='dragging' role='form' on:dragleave={dragleave}>
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
        max-width: 100dvw;
        height: 10%;
        flex-grow: 1;
        display: flex;
        flex-direction: row;
    }

    #body {
        display: flex;
        flex-direction: column;
        align-items: center;
        /*padding-bottom: 1rem;*/
        width: 10%;
        flex: 1;
        overflow: auto;
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
        box-shadow: inset 0 0 10px #222;
        margin: 0.5rem;
    }

    #floating-buttons .button .material-symbols-rounded {
        font-size: 1.75rem;
    }

    #floating-buttons .save {
        background-color: #1dad36;
    }

    #floating-buttons .settings {
        background-color: #777;
        position: absolute;
        top: 2.45rem;
        left: -2rem;
    }

    #floating-buttons .settings .material-symbols-rounded {
        font-size: 1rem;
    }

    #body-container.empty {
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .saving {
        position: fixed;
        z-index: 100;
        top: 0.75rem;
        left: 50%;
        transform: translateX(-50%);
        background-color: rgba(130, 130, 130, 0.6);
        backdrop-filter: blur(5px);
        border-radius: 0.75rem;
        padding: 0.75rem;
        box-shadow: inset 0 0 5px #222;
    }

    .save-error {
        position: fixed;
        z-index: 100;
        top: 0.75rem;
        left: 50%;
        transform: translateX(-50%);
        background-color: rgba(255, 29, 29, 0.6);
        backdrop-filter: blur(5px);
        border-radius: 0.75rem;
        padding: 0.75rem;
        box-shadow: inset 0 0 5px #222;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        text-align: center;
    }
</style>