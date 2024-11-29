<script lang='ts'>
	import BlocklyComponentWidget                         from '$components/BlocklyComponentWidget.svelte';
	import ComponentWidget                                from '$components/ComponentWidget.svelte';
	import Modal                                          from '$components/Modal.svelte';
	import ComponentSection                               from '$components/modal/component/ComponentSection.svelte';
	import { onMount }                                    from 'svelte';
	import type { Unsubscriber }                          from 'svelte/store';
	import { get }                                        from 'svelte/store';
	import ProInput                                       from '$input/ProInput.svelte';
	import { filterParams, initialized, triggerSections } from '$api/components/registry';
	import type FabledTrigger                             from '$api/components/triggers.svelte';
	import FabledComponent                                from '$api/components/fabled-component.svelte';
	import { base }                                       from '$app/paths';
	import FabledSkill, { skillStore }                    from '../../../../data/skill-store.svelte';
	import { blocklyMode }                                from '../../../../data/settings';

	interface Props {
		data: { data: FabledSkill };
	}

	let { data }: Props    = $props();
	let skill: FabledSkill = $derived(data?.data);
	let triggerModal       = $state(false);

	onMount(() => {
		// This is by far my least favorite implementation... But with enough things going on,
		// there are circular dependencies, and this sort of resolves it.
		let initSub: Unsubscriber | undefined = undefined;
		initSub                               = initialized.subscribe(init => {
			if (!init) return;
			if (initSub) initSub();
			update();
		});
	});

	const onSelectTrigger = (comp: { new: () => { defaultOpen: () => FabledComponent } }) => {
		skill.triggers.push(<FabledTrigger>comp.new().defaultOpen());
		update();
		setTimeout(() => triggerModal = false);
	};

	const update = () => {
		skill.triggers = [...skill.triggers];
		save();
	};

	const save = () => {
		skillStore.skills.set([...get(skillStore.skills)]);
		skill.save();
	};
</script>

<svelte:head>
	<title>Fabled Dynamic Editor - {skill.name}</title>
</svelte:head>
<div class='header'>
	<h2>
		{skill.name}
		<a class='material-symbols-rounded edit-skill chip' href='{base}/skill/{skill.name}/edit'
			 title='Edit'>edit</a>
		{#if !$blocklyMode}
		<span class='add-trigger chip'
					onclick={() => triggerModal = true}
					onkeypress={(e) => e.key === 'Enter' && (triggerModal = true)}
					role='button'
					tabindex='0'
					title='Add Trigger'>
			<span class='material-symbols-rounded'>
				new_label
			</span>
		</span>
		{/if}
	</h2>
	<hr />
</div>
<div class='container' class:blockly={$blocklyMode}>
	{#if $blocklyMode}
		{#key skill}
			<BlocklyComponentWidget {skill} onupdate={update} onsave={save} />
		{/key}
	{:else}
		{#each skill.triggers as comp (comp.id)}
			<div class='widget'>
				<ComponentWidget {skill} component={comp} onupdate={update} onsave={save} />
			</div>
		{/each}
		{#if skill.triggers.length === 0}
			<div>No triggers added yet.</div>
		{/if}
	{/if}
</div>

{#if triggerModal}
	<Modal onclose={() => triggerModal = false}>
		<div class='modal-header-wrapper'>
			<div></div>
			<h2 class='modal-header'>Select New Trigger</h2>
			<div class='search-bar'>
				<ProInput bind:value={$filterParams} placeholder='Search...' autofocus />
			</div>
		</div>
		<hr />
		<div class='component-section'>
			{#each Object.keys($triggerSections) as sectionName}
				<ComponentSection sectionName={sectionName}
													components={$triggerSections[sectionName]}
													addComponent={onSelectTrigger}
				/>
			{/each}
		</div>
		<hr />
		<div class='cancel' onclick={() => triggerModal = false}
				 onkeypress={(e) => e.key === 'Enter' && (triggerModal = false)}
				 tabindex='0'
				 role='button'
		>Cancel
		</div>
	</Modal>
{/if}

<style>
    .header {
        padding-top: 1rem;
        width: 100%;
        z-index: 20;
        position: sticky;
        top: 0;
        background: var(--color-bg);
    }

    h2 {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        margin: 0 3rem;
    }

    .container {
        display: flex;
        align-self: flex-start;
        align-items: flex-start;
        flex-wrap: nowrap;
        width: 100%;
        max-width: 100%;
        overflow: auto;
        padding-inline: 2rem;
        flex-grow: 1;
    }

	.container.blockly {
		padding-inline: 0;
		border-left: 3px solid #444;
	}

    .widget {
        margin-right: 0.5rem;
        margin-bottom: 2rem;
        white-space: nowrap;
    }

    .add-trigger:hover {
        cursor: pointer;
    }

    .add-trigger, .edit-skill {
        display: inline-flex;
        justify-content: center;
        align-items: center;
        height: 100%;
        width: 6rem;
        overflow: hidden;
        font-size: inherit;
        color: white;
        margin-right: 0.5rem;
        text-decoration: none;
        transition: background-color 0.25s ease;
    }

    .edit-skill {
        margin-left: 1rem;
        background-color: #1dad36;
    }

    .edit-skill:hover {
        background-color: #2fd950;
    }

    .edit-skill:active {
        background-color: #157e2b;
        box-shadow: inset 0 0 5px #333;
    }

    .component-section {
        flex-direction: column;
        flex-grow: 1;
        flex-shrink: 0;

        width: 100%;
        overflow-y: hidden;
        user-select: none;
    }
</style>