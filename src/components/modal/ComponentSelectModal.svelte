<script lang='ts'>
	import { createEventDispatcher } from 'svelte';
	import {
		squash
	}                                from '../../data/squish';
	import ProInput                  from '$input/ProInput.svelte';
	import Modal                     from '$components/Modal.svelte';
	import {
		closeModal
	}                                from '../../data/modal-service';
	import {
		filteredConditions,
		filteredMechanics,
		filteredTargets,
		filterParams,
		mechanicSections
	}                           from '$api/components/registry';
	import type FabledComponent from '$api/components/fabled-component';
	import ComponentSection     from '$components/modal/component/ComponentSection.svelte';

	export let data: FabledComponent;

	let targetsShown    = true;
	let conditionsShown = true;
	let mechanicsShown  = true;

	const dispatch = createEventDispatcher();

	const addComponent =
					(comp: { new: () => { defaultOpen: () => FabledComponent } }) => {
						data.addComponent(comp.new().defaultOpen());
						filterParams.set('');
						dispatch('save');
						closeModal();
					};
</script>

<Modal width='70%'
			 open={true}
			 on:close={closeModal}>
	<div class='modal-header-wrapper'>
		<div />
		<h2>Add a Component</h2>
		<div class='search-bar'>
			<ProInput bind:value={$filterParams} placeholder='Search...' autofocus />
		</div>
	</div>
	{#if $filteredTargets.length > 0}
		<hr />
		<div class='comp-modal-header'
				 on:click={() => targetsShown = !targetsShown}
				 on:keypress={(e) => {
									 if (e.key === 'Enter') targetsShown = !targetsShown;
				 }}>
			<h3>Targets
				<span class='icon material-symbols-rounded' class:expanded={targetsShown}>
					expand_more
				</span>
			</h3>
		</div>
		{#if targetsShown}
			<div class='component-section' transition:squash={{duration: 200}}>
				{#each $filteredTargets as target}
					<div class='comp-select' on:click={() => addComponent(target.component)}
							 on:keypress={(e) => {
                       if (e.key === 'Enter') addComponent(target.component);
                     }}>
						{#if target.component.new().isDeprecated}<s>{target.name}</s>{:else}{target.name}{/if}
					</div>
				{/each}
			</div>
		{/if}
	{/if}
	{#if $filteredConditions.length > 0}
		<hr />
		<div class='comp-modal-header'
				 on:click={() => conditionsShown = !conditionsShown}
				 on:keypress={(e) => {
									 if (e.key === 'Enter') conditionsShown = !conditionsShown;
				 }}>
			<h3>Conditions
				<span class='icon material-symbols-rounded' class:expanded={conditionsShown}>
					expand_more
				</span>
			</h3>
		</div>
		{#if conditionsShown}
			<div class='component-section' transition:squash={{duration: 200}}>
				{#each $filteredConditions as condition}
					<div class='comp-select' on:click={() => addComponent(condition.component)}
							 on:keypress={(e) => {
                  if (e.key === 'Enter') addComponent(condition.component);
                }}
					>
						{#if condition.component.new().isDeprecated}<s>{condition.name}</s>{:else}{condition.name}{/if}
					</div>
				{/each}
			</div>
		{/if}
	{/if}
	{#if $filteredMechanics.length > 0}
		<hr />
		<div class='comp-modal-header'
				 on:click={() => mechanicsShown = !mechanicsShown}
				 on:keypress={(e) => {
									 if (e.key === 'Enter') mechanicsShown = !mechanicsShown;
				 }}>
			<h3>Mechanics
				<span class='icon material-symbols-rounded' class:expanded={mechanicsShown}>
					expand_more
				</span>
			</h3>
		</div>
		{#if mechanicsShown}
			<div class='component-section' transition:squash={{duration: 200}}>
				{#each Object.keys($mechanicSections) as sectionName}
					{#if $mechanicSections[sectionName].length > 0}
						<ComponentSection sectionName={sectionName}
															components={$mechanicSections[sectionName]}
															addComponent={addComponent} />
					{/if}
				{/each}
			</div>
		{/if}
	{/if}
	<hr />
	<div class='cancel' on:click={closeModal}
			 on:keypress={(e) => {
           if (e.key === 'Enter') closeModal();
         }}>
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