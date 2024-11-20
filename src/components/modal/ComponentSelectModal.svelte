<script lang='ts'>
	import { squash }           from '../../data/squish';
	import ProInput             from '$input/ProInput.svelte';
	import Modal                from '$components/Modal.svelte';
	import {
		closeModal
	}                           from '../../data/modal-service.svelte';
	import {
		filteredConditions,
		filteredMechanics,
		filteredTargets,
		filterParams,
		mechanicSections
	}                           from '$api/components/registry';
	import type FabledComponent from '$api/components/fabled-component.svelte';
	import ComponentSection     from '$components/modal/component/ComponentSection.svelte';
	import {
		deprecated
	}                           from '$api/components/components.svelte.js';

	interface Props {
		data: FabledComponent;
		onsave?: () => void;
	}

	let { data, onsave }: Props = $props();

	let targetsShown    = $state(true);
	let conditionsShown = $state(true);
	let mechanicsShown  = $state(true);

	const addComponent = (comp: typeof FabledComponent & { new: () => { defaultOpen: () => FabledComponent } }) => {
		const component        = comp.new().defaultOpen();
		component.isDeprecated = deprecated.includes(comp);
		data.addComponent(component);
		filterParams.set('');
		onsave?.();
		closeModal();
	};
</script>

<Modal width='70%' open={true} onclose={closeModal}>
	<div class='modal-header-wrapper'>
		<div></div>
		<h2>Add a Component</h2>
		<div class='search-bar'>
			<ProInput bind:value={$filterParams} placeholder='Search...' autofocus />
		</div>
	</div>
	{#if $filteredTargets.length > 0}
		<hr />
		<div
			class='comp-modal-header'
			onclick={() => (targetsShown = !targetsShown)}
			onkeypress={(e) => {
				if (e.key === 'Enter') targetsShown = !targetsShown;
			}}
		>
			<h3>
				Targets
				<span class='icon material-symbols-rounded' class:expanded={targetsShown}>
					expand_more
				</span>
			</h3>
		</div>
		{#if targetsShown}
			<div class='component-section' transition:squash={{ duration: 200 }}>
				{#each $filteredTargets as target}
					<div
						class='comp-select'
						onclick={() => addComponent(target.component)}
						onkeypress={(e) => {
							if (e.key === 'Enter') addComponent(target.component);
						}}
					>
						{#if deprecated.includes(target.component)}<s>{target.name}</s>{:else}{target.name}{/if}
					</div>
				{/each}
			</div>
		{/if}
	{/if}
	{#if $filteredConditions.length > 0}
		<hr />
		<div
			class='comp-modal-header'
			onclick={() => (conditionsShown = !conditionsShown)}
			onkeypress={(e) => {
				if (e.key === 'Enter') conditionsShown = !conditionsShown;
			}}
		>
			<h3>
				Conditions
				<span class='icon material-symbols-rounded' class:expanded={conditionsShown}>
					expand_more
				</span>
			</h3>
		</div>
		{#if conditionsShown}
			<div class='component-section' transition:squash={{ duration: 200 }}>
				{#each $filteredConditions as condition}
					<div
						class='comp-select'
						onclick={() => addComponent(condition.component)}
						onkeypress={(e) => {
							if (e.key === 'Enter') addComponent(condition.component);
						}}
					>
						{#if deprecated.includes(condition.component)}<s>{condition.name}</s
						>{:else}{condition.name}{/if}
					</div>
				{/each}
			</div>
		{/if}
	{/if}
	{#if $filteredMechanics.length > 0}
		<hr />
		<div
			class='comp-modal-header'
			onclick={() => (mechanicsShown = !mechanicsShown)}
			onkeypress={(e) => {
				if (e.key === 'Enter') mechanicsShown = !mechanicsShown;
			}}
		>
			<h3>
				Mechanics
				<span class='icon material-symbols-rounded' class:expanded={mechanicsShown}>
					expand_more
				</span>
			</h3>
		</div>
		{#if mechanicsShown}
			<div class='component-section' transition:squash={{ duration: 200 }}>
				{#each Object.keys($mechanicSections) as sectionName}
					{#if $mechanicSections[sectionName].length > 0}
						<ComponentSection
							{sectionName}
							components={$mechanicSections[sectionName]}
							{addComponent}
						/>
					{/if}
				{/each}
			</div>
		{/if}
	{/if}
	<hr />
	<div
		class='cancel'
		onclick={closeModal}
		onkeypress={(e) => {
			if (e.key === 'Enter') closeModal();
		}}
	>
		Cancel
	</div>
</Modal>

<style>
    .component-section {
        flex-grow: 1;
        flex-shrink: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;

        width: 100%;
        overflow-y: hidden;
        user-select: none;
    }

    .comp-modal-header {
        display: flex;
        justify-content: space-between;
        width: 100%;
    }

    .comp-modal-header:hover {
        cursor: pointer;
    }

    h3 {
        text-decoration: underline;
    }

    .icon {
        font-size: 1.25rem;
        transition: transform 0.2s;
    }

    .expanded {
        transform: rotate(180deg);
    }
</style>
