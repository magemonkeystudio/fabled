<script lang='ts'>
	import { version, VERSIONS }                              from '../../version/data';
	import { animationEnabled, showSummaryItems, useSymbols } from '../../data/settings';
	import ProInput                                           from '$input/ProInput.svelte';
	import Toggle                                             from '$input/Toggle.svelte';
	import Modal                                              from '$components/Modal.svelte';
	import { skillStore }                                     from '../../data/skill-store.js';
	import { classStore }                                     from '../../data/class-store';
	import { attributeStore }                                 from '../../data/attribute-store.js';

	let modalOpen = true;

	const clearData = () => {
		if (!confirm('Are you sure you want to clear all data?')) return;

		classStore.classes.set([]);
		classStore.classFolders.set([]);
		skillStore.skills.set([]);
		skillStore.skillFolders.set([]);
		attributeStore.attributes.set([]);
	};
</script>

<Modal bind:open={modalOpen} on:close width='50rem'>
	<h1>Settings</h1>
	<hr />
	<div class='settings-container'>
		<ProInput label='Server' tooltip='This should match your target Spigot server version'>
			<select bind:value={$version}>
				{#each Object.keys(VERSIONS).reverse() as opt}
					<option value={opt}>1.{opt}</option>
				{/each}
			</select>
		</ProInput>
		<ProInput label='Use Symbols' tooltip='If skill components should use symbols instead of text'>
			<Toggle left='Symbols' right='Text' bind:data={$useSymbols} />
		</ProInput>
		<ProInput
			label='Show Summary Items'
			tooltip='If skill components should show a simplified summary on the element'
		>
			<Toggle left='True' right='False' bind:data={$showSummaryItems} />
		</ProInput>
		<ProInput
			label='Waterfall Animation'
			tooltip='If the waterfall animation should play in the sidebar'
		>
			<Toggle bind:data={$animationEnabled} />
		</ProInput>
		<hr class='span' />
		<!--		<ProInput label='Clear data' tooltip='Clear all data stored in the editor'>-->
		<button class='button btn-danger span' on:click={clearData}>Clear Data</button>
		<!--		</ProInput>-->
	</div>
</Modal>

<style>
    .settings-container {
        display: grid;
        grid-template-columns: 50% 50%;
        width: 100%;
        padding-inline: 0.5rem;
        padding-top: 0.25rem;
    }

    .span {
        grid-column: 1 / span 2;
    }

    .btn-danger {
        background-color: #dc3545;
        color: white;
        width: fit-content;
        margin: 0 auto;
        font-weight: bold;
        padding: 0.5rem 2rem;
    }
</style>
