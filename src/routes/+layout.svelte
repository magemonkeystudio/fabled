<script lang='ts'>
	import '../app.css';
	import { active, importing, loadFile, saveData } from '../data/store';
	import { onDestroy, onMount }                    from 'svelte';
	import { browser }                               from '$app/environment';
	import ImportModal                               from '$components/ImportModal.svelte';
	import NavBar                                    from '$components/NavBar.svelte';
	import HeaderBar                                 from '$components/HeaderBar.svelte';
	import { initComponents }                        from '$api/components/components';
	import Modal                                     from '$components/Modal.svelte';
	import Toggle                                    from '$input/Toggle.svelte';
	import ProInput                                  from '$input/ProInput.svelte';
	import { animationEnabled, useSymbols }          from '../data/settings';
	import { serverOptions, version }                from '../version/data';
	import { isSaving }                              from '../data/skill-store';
	import { fly }                                   from 'svelte/transition';
	import type { Unsubscriber }                     from 'svelte/types/runtime/store';

	let dragging    = false;
	let settings    = false;
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

			if (saveTask) {
				clearTimeout(saveTask);
			}

			saveTask = setTimeout(() => displaySave = true, 1000);
		});

		initComponents();
	});

	onDestroy(() => {
		if (!browser) return;
		document.removeEventListener('dragover', dragover);
		document.removeEventListener('drop', loadFiles);

		if (saveSub) saveSub();
	});

	const backup = () => {
		alert('This feature isn\'t implemented yet');
	};

	const dragover = (e: DragEvent) => {
		if (!(e.dataTransfer?.types?.length > 0 && e.dataTransfer?.types[0] == 'Files')) return;
		e.dataTransfer.dropEffect = 'copy';
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
			if (file.name.indexOf('.yml') == -1) continue;

			loadFile(file);
		}
		e.stopPropagation();
		e.preventDefault();
	};
</script>

<HeaderBar />
<NavBar />
<div id='body-container' class:empty={!$active}>
	<div id='body' class:centered={!$active}>
		<slot />
	</div>
</div>
<div id='floating-buttons'>
	<div class='button backup' title='Backup All Data' on:click={backup}>
		<span class='material-symbols-rounded'>cloud_download</span>
	</div>
	<div class='button settings' title='Change Settings' on:click={() => settings = true}>
		<span class='material-symbols-rounded'>settings</span>
	</div>
	<div class='button save' title='Save' on:click={() => saveData()}>
		<span class='material-symbols-rounded'>save</span>
	</div>
</div>

<footer>&copy; ProMCTeam {new Date().getFullYear()}</footer>

{#if $importing}
	<ImportModal />
{/if}

<Modal bind:open={settings}>
	<h1>Settings</h1>
	<hr />
	<div class='settings-container'>
		<ProInput label='Server' tooltip='This should match your target Spigot server version'>
			<select bind:value={$version}>
				{#each serverOptions as opt}
					<option value={opt.substring(2)}>{opt}</option>
				{/each}
			</select>
		</ProInput>
		<ProInput label='Use Symbols' tooltip='If skill components should use symbols instead of text'>
			<Toggle left='Symbols' right='Text' bind:data={$useSymbols} />
		</ProInput>
		<ProInput label='Waterfall Animation' tooltip='If the waterfall animation should play in the sidebar'>
			<Toggle bind:data={$animationEnabled} />
		</ProInput>
	</div>
</Modal>

{#if displaySave}
	<div class='saving' transition:fly={{y: -20}}>{$isSaving ? 'Saving...' : 'Saved!'}</div>
{/if}

{#if dragging}
	<div class='dragging' on:dragleave={dragleave}>
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

    .settings-container {
        display: grid;
        grid-template-columns: 40% 60%;
        width: 100%;
        padding-inline: 0.5rem;
        padding-top: 0.25rem;
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
</style>