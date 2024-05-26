<script lang='ts'>
	import Modal                    from '$components/Modal.svelte';
	import type ProComponent        from '$api/components/procomponent';
	import type DropdownSelect      from '$api/options/dropdownselect';
	import type { ComponentOption } from '$api/options/options';
	import Toggle                   from '$input/Toggle.svelte';
	import ProInput                 from '$input/ProInput.svelte';

	export let data: ProComponent;
	let modalOpen = true;

	$: if (modalOpen && data) {
		data.preview.filter((dat: ComponentOption) => (dat['dataSource'])).forEach((dat: DropdownSelect) => dat.init());
	}
</script>

<Modal bind:open={modalOpen} on:close width='70%'>
	<h2 class:deprecated={data.isDeprecated}><span>{data.name} - Preview</span></h2>
	{#if data.description}
		<div class='modal-desc'>{data.description}</div>
	{/if}
	<hr />
	<div class='component-entry'>
		<ProInput label='Enable Preview'
							tooltip={'[enabled] Whether this component will show its preview while casting. Requires a compatible casting mode: Item, Bars (hover bar only), Action bar, Title, Subtitle or Chat'}>
			<Toggle bind:data={data.enablePreview} />
		</ProInput>
		{#if data.enablePreview}
			{#each data.preview as datum}
				{#if datum.meetsPreviewRequirements(data)}
					<svelte:component
						this={datum.component}
						bind:data={datum.data}
						name={datum.name}
						tooltip="{datum.key ? '[' + datum.key + '] ' : ''}{datum.tooltip}"
						multiple={datum.multiple}
						on:save />
				{/if}
			{/each}
		{/if}
	</div>
</Modal>

<style>
    .deprecated {
        align-items: center;
        display: flex;
    }

    .deprecated > span {
        text-decoration: line-through;
    }

    .deprecated::after {
        text-decoration: unset;
        margin-left: 0.5rem;
        content: 'deprecated';
        font-size: 0.6em;
        color: goldenrod;
    }

    .component-entry {
        display: grid;
        grid-template-columns: calc(50% - 3rem) calc(50% + 3rem);
        width: 100%;
        padding-inline: 0.5rem;
        padding-top: 0.25rem;
    }

    .component-entry {
        display: grid;
        grid-template-columns: calc(50% - 3rem) calc(50% + 3rem);
        width: 100%;
        padding-inline: 0.5rem;
        padding-top: 0.25rem;
    }

    .modal-desc {
        max-width: 100%;
        white-space: break-spaces;
        text-align: center;
    }
</style>