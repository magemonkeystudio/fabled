<script lang='ts'>
	import '../app.css';
	import {
		active,
		importing,
		loadFile,
		saveAll,
		saveAllToServer,
		saveData,
		saveDataToServer,
		saveError,
		showSidebar
	}                                                                        from '../data/store';
	import { onDestroy, onMount }                                            from 'svelte';
	import { browser }                                                       from '$app/environment';
	import ImportModal                                                       from '$components/ImportModal.svelte';
	import NavBar                                                            from '$components/NavBar.svelte';
	import HeaderBar                                                         from '$components/HeaderBar.svelte';
	import { fly }                                                           from 'svelte/transition';
	import { derived, get, type Readable, type Unsubscriber, type Writable } from 'svelte/store';
	import Sidebar                                                           from '$components/sidebar/Sidebar.svelte';
	import { activeModal, closeModal, modalData, openModal }                 from '../data/modal-service';
	import SettingsModal
																																					 from '$components/modal/SettingsModal.svelte';
	import { dcWarning, socketConnected, socketService, socketTrusted }      from '$api/socket/socket-connector';
	import { quadInOut }                                                     from 'svelte/easing';
	import Modal                                                             from '$components/Modal.svelte';
	import { skillStore }                                                    from '../data/skill-store.js';

	const isSaving = skillStore.isSaving;

	let dragging    = false;
	let displaySave = false;
	let saveTask: number;
	let saveSub: Unsubscriber;

	let button                                 = '';
	let serverSaveStatus                       = 'NONE';
	const statusMap: { [key: string]: string } = {
		'SAVING': 'hourglass_empty',
		'SAVED':  'check',
		'ERROR':  'error'
	};
	let copied                                 = false;

	const passphrase = socketService.keyphrase;
	let numButtons   = derived<Writable<boolean>, number>(socketConnected, (connected, set) => set(connected ? 6 : 3));
	let rotation     = derived<Readable<number>, number>(numButtons, (numButtons, set) => set(120 / ((numButtons - 1) * 2)));
	let distance     = derived<Readable<number>, number>(numButtons, (numButtons, set) => set((4.725 * (numButtons - 1) + 1.5) / Math.PI));
	let dcTime       = derived<Readable<number>, number>(dcWarning, (dcWarning, set) => {
		let interval: number;
		let seconds   = 0;
		const setTime = () => {
			let time = dcWarning - ++seconds;
			if (time <= 0) {
				clearInterval(interval);
				return;
			}
			set(time);
		};

		set(dcWarning);

		interval = window.setInterval(() => setTime(), 1000);
		return () => clearInterval(interval);
	});

	onMount(() => {
		if (!browser) return;
		document.addEventListener('dragover', dragover);
		document.addEventListener('drop', loadFiles);

		saveSub = skillStore.isSaving.subscribe(saving => {
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
		skillStore.skills.set([...get(skillStore.skills)]);
		get(active)?.save();
	};

	const acknowledgeSaveError = () => {
		const err = get(saveError);
		if (err) {
			err.acknowledged = true;
		}
		saveError.set(undefined);
	};

	const saveServerInfo = async () => {
		if (serverSaveStatus === 'SAVING') return;
		button           = 'save';
		serverSaveStatus = 'SAVING';
		let success      = await saveDataToServer();
		if (success) serverSaveStatus = 'SAVED';
		else serverSaveStatus = 'ERROR';
		setTimeout(() => serverSaveStatus = 'NONE', 2000);
	};

	const exportAllToServer = async () => {
		if (serverSaveStatus === 'SAVING') return;
		button           = 'export';
		serverSaveStatus = 'SAVING';
		let success      = await saveAllToServer();
		if (success) serverSaveStatus = 'SAVED';
		else serverSaveStatus = 'ERROR';
		setTimeout(() => serverSaveStatus = 'NONE', 2000);
	};

	const reload = async () => {
		if (serverSaveStatus === 'SAVING') return;
		button           = 'reload';
		serverSaveStatus = 'SAVING';
		let success      = await socketService.reloadSapi();
		if (success) serverSaveStatus = 'SAVED';
		else serverSaveStatus = 'ERROR';
		setTimeout(() => serverSaveStatus = 'NONE', 2000);
	};

	const copyText = () => {
		navigator.clipboard.writeText('/synth trust ' + $passphrase);
		copied = true;
		setTimeout(() => copied = false, 2000);
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
<!--<SocketPanel />-->

<div id='floating-buttons'>
	<div class='button backup' title='Backup All Data'
			 tabindex='0'
			 role='button'
			 style:--rotation='{$rotation}deg'
			 style:--distance='{$distance}rem'
			 on:click={saveAll}
			 on:keypress={(e) => e.key === 'Enter' && saveAll()}
	>
		<span class='material-symbols-rounded'>cloud_download</span>
	</div>
	<div class='button save' title='Save'
			 tabindex='0'
			 role='button'
			 style:--rotation='{$rotation * 3}deg'
			 style:--distance='{$distance}rem'
			 on:click={() => saveData()}
			 on:keypress={(e) => { if (e.key === 'Enter') saveData() }}
	>
		<span class='material-symbols-rounded'>save</span>
	</div>
	{#if $socketConnected}
		<!-- Rotation goes up by 2 for each button -->
		<div class='button socket-upload'
				 title='Save to Server'
				 tabindex='0'
				 role='button'
				 style:--rotation='{$rotation * 5}deg'
				 style:--distance='{$distance}rem'
				 transition:fly={{x: 100, easing: quadInOut}}
				 on:click={() => saveServerInfo()}
				 on:keypress={(e) => { if (e.key === 'Enter') saveServerInfo() }}
		>
			{#if button === 'save' && serverSaveStatus !== 'NONE'}
				<span class='material-symbols-rounded' transition:fly={{y: -20}}>{statusMap[serverSaveStatus]}</span>
			{:else}
				<span class='material-symbols-rounded' transition:fly={{y: 20}}>upload_file</span>
			{/if}
		</div>
		<div class='button socket-all'
				 title='Upload All to Server'
				 tabindex='0'
				 role='button'
				 style:--rotation='{$rotation * 7}deg'
				 style:--distance='{$distance}rem'
				 transition:fly={{x: 100, easing: quadInOut}}
				 on:click={() => exportAllToServer()}
				 on:keypress={(e) => { if (e.key === 'Enter') exportAllToServer() }}
		>
			{#if button === 'export' && serverSaveStatus !== 'NONE'}
				<span class='material-symbols-rounded' transition:fly={{y: -20}}>{statusMap[serverSaveStatus]}</span>
			{:else}
				<span class='material-symbols-rounded' transition:fly={{y: 20}}>cloud_upload</span>
			{/if}
		</div>
		<div class='button socket-reload'
				 title='Reload ProSkillAPI'
				 tabindex='0'
				 role='button'
				 style:--rotation='{$rotation * 9}deg'
				 style:--distance='{$distance}rem'
				 transition:fly={{x: 100, easing: quadInOut}}
				 on:click={() => reload()}
				 on:keypress={(e) => { if (e.key === 'Enter') reload() }}
		>
			{#if button === 'reload' && serverSaveStatus !== 'NONE'}
				<span class='material-symbols-rounded' transition:fly={{y: -20}}>{statusMap[serverSaveStatus]}</span>
			{:else}
				<span class='material-symbols-rounded' transition:fly={{y: 20}}>sync</span>
			{/if}
		</div>
	{/if}
	<div class='button settings' title='Change Settings'
			 tabindex='0'
			 role='button'
			 style:--rotation='60deg'
			 style:--distance='1rem'
			 on:click={() => openModal(SettingsModal)}
			 on:keypress={(e) => e.key === 'Enter' && openModal(SettingsModal)}
	>
		<span class='material-symbols-rounded'>settings</span>
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
				 on:click={() => { acknowledgeSaveError() }}
				 on:keypress={(e) => { if (e.key === 'Enter') { acknowledgeSaveError() }}}
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

<Modal open={!!$passphrase && !$socketTrusted}>
	<h3>Untrusted Connection to Server</h3>
	<div>Server is not trusted. Please run
		<div class='code'
				 class:copied
				 tabindex='0'
				 role='button'
				 on:click={copyText}
				 on:keypress={(e) => { if (e.key === 'Enter') copyText() }}
		>
			/synth trust {$passphrase}
		</div>
		from the server
	</div>
</Modal>

{#if $dcWarning > 0}
	<div class='dc-warning' transition:fly={{y: -20}}>
		<strong>You will lose connection in { $dcTime } seconds</strong>
		<div class='button'
				 tabindex='0'
				 role='button'
				 on:click={() => socketService.ping()}
				 on:keypress={(e) => { if (e.key === 'Enter') socketService.ping() }}
		>Click to remain connected
		</div>
	</div>
{/if}

<style>
    @property --rotation {
        syntax: "<angle>";
        inherits: false;
        initial-value: 0deg;
    }

    @property --distance {
        syntax: "<length>";
        inherits: false;
        initial-value: 0;
    }

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

    #floating-buttons .button {
        --rotation: 0deg;
        --distance: 3.5rem;
        position: absolute;
        bottom: 0;
        right: 0;
        translate: calc(-1 * var(--distance) * sin(105deg - var(--rotation))) calc(-1 * var(--distance) * cos(105deg - var(--rotation)));

        background-color: #0083ef;
        display: grid;
        place-items: center;
        border-radius: 50%;
        padding: 0.7rem;
        box-shadow: inset 0 0 10px #222;
        margin: 0;
        overflow: hidden;

        transition: --rotation 0.5s ease, --distance 0.5s ease;
    }

    #floating-buttons {
        position: fixed;
        right: 0.5rem;
        bottom: 0.5rem;
        z-index: 100;
    }

    #floating-buttons .button .material-symbols-rounded {
        font-size: 1.75em;
        grid-row: 1;
        grid-column: 1;
    }

    #floating-buttons .save {
        background-color: #1dad36;
    }

    #floating-buttons .settings {
        background-color: #777;
    }

    #floating-buttons .socket-upload {
        background-color: #ff9800;
    }

    #floating-buttons .socket-all {
        background-color: #c21b1b;
    }

    #floating-buttons .socket-reload {
        background-color: #363636;
    }

    #floating-buttons .settings .material-symbols-rounded {
        font-size: 1em;
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

    .code {
        display: inline;
        background-color: #555;
        padding: 0.25rem;
        border-radius: 0.25rem;
        font-family: monospace;
        font-size: 0.8em;
        cursor: grab;

        transition: background-color 0.5s ease;
    }

    .code.copied {
        background-color: #36ab36;
    }

    .dc-warning {
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

    .dc-warning > .button {
        background-color: #555555;
    }
</style>