<script lang='ts'>
	import { serverOptions, version }                         from '../../version/data';
	import { animationEnabled, showSummaryItems, useSymbols } from '../../data/settings';
	import ProInput                                           from '$input/ProInput.svelte';
	import Toggle                                             from '$input/Toggle.svelte';
	import Modal                                              from '$components/Modal.svelte';

	let modalOpen = true;
</script>

<Modal bind:open={modalOpen} on:close width='50rem'>
	<h1>Settings</h1>
	<hr />
	<div class='settings-container'>
		<ProInput label='Server' tooltip='This should match your target Spigot server version'>
			<select bind:value={$version}>
				{#each serverOptions as opt}
					<option value={opt}>1.{opt}</option>
				{/each}
			</select>
		</ProInput>
		<ProInput label='Use Symbols' tooltip='If skill components should use symbols instead of text'>
			<Toggle left='Symbols' right='Text' bind:data={$useSymbols} />
		</ProInput>
		<ProInput label='Show Summary Items' tooltip='If skill components should show a simplified summary on the element'>
			<Toggle left='True' right='False' bind:data={$showSummaryItems} />
		</ProInput>
		<ProInput label='Waterfall Animation' tooltip='If the waterfall animation should play in the sidebar'>
			<Toggle bind:data={$animationEnabled} />
		</ProInput>
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
</style>