<script lang='ts'>
	import ComponentWidget                                from '$components/ComponentWidget.svelte';
	import Modal                                          from '$components/Modal.svelte';
	import type { Unsubscriber }                          from 'svelte/store';
	import { get }                                        from 'svelte/store';
	import ProInput                                       from '$input/ProInput.svelte';
	import { filterParams, initialized, triggerSections } from '$api/components/registry';
	import { onMount }                                    from 'svelte';
	import type ProTrigger                                from '$api/components/triggers';
	import { base }                                       from '$app/paths';
	import ComponentSection                               from '$components/modal/component/ComponentSection.svelte';
	import ProComponent                                   from '$api/components/procomponent';
	import FabledSkill, { skillStore }                    from '../../../../data/skill-store';

	export let data: { data: FabledSkill };
	let skill: FabledSkill;
	$: if (data) skill = data.data;
	let triggerModal = false;

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

	const onSelectTrigger = (comp: { new: () => { defaultOpen: () => ProComponent } }) => {
		skill.triggers.push(<ProTrigger>comp.new().defaultOpen());
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
		<a class='material-symbols-rounded edit-skill chip' title='Edit'
			 href='{base}/skill/{skill.name}/edit'>edit</a>
		<span class='add-trigger chip'
					title='Add Trigger'
					tabindex='0'
					role='button'
					on:click={() => triggerModal = true}
					on:keypress={(e) => e.key === 'Enter' && (triggerModal = true)}>
			<span class='material-symbols-rounded'>
				new_label
			</span>
		</span>
	</h2>
	<hr />
</div>
<div class='container'>
	{#each skill.triggers as comp (comp.id)}
		<div class='widget'>
			<ComponentWidget {skill} component={comp} on:update={update} on:save={save} />
		</div>
	{/each}
	{#if skill.triggers.length === 0}
		<div>No triggers added yet.</div>
	{/if}
</div>

<Modal bind:open={triggerModal}>
	<div class='modal-header-wrapper'>
		<div />
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
	<div class='cancel' on:click={() => triggerModal = false}
			 on:keypress={(e) => e.key === 'Enter' && (triggerModal = false)}
			 tabindex='0'
			 role='button'
	>Cancel
	</div>
</Modal>

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