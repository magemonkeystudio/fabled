<script lang='ts'>
	import type ProSkill          from '$api/proskill';
	import ComponentWidget        from '$components/ComponentWidget.svelte';
	import Modal                  from '$components/Modal.svelte';
	import { draggingComponent }  from '../../../../data/store';
	import { get }                from 'svelte/store';
	import ProInput               from '$input/ProInput.svelte';
	import { skills }             from '../../../../data/skill-store';
	import Registry               from '$api/components/registry';
	import { onDestroy, onMount } from 'svelte';
	import type { Unsubscriber }  from 'svelte/types/runtime/store';
	import type ProTrigger        from '$api/components/triggers';
	import { base }               from '$app/paths';

	export let data: { data: ProSkill };
	let skill: ProSkill;
	$: if (data) skill = data.data;
	let triggerModal                                                                = false;
	let hovered                                                                     = false;
	let searchParams                                                                = '';
	let sortedTriggers: ProTrigger[]                                                = [];
	let unsub: Unsubscriber;
	let triggers: { [key: string]: { name: string, component: typeof ProTrigger } } = {};

	onMount(() => {
		unsub = Registry.triggers.subscribe(tri => triggers = tri);
		// This is by far my least favorite implementation... But with enough things going on,
		// there are circular dependencies, and this sort of resolves it.
		let initSub: Unsubscriber | undefined = undefined;
		initSub                               = Registry.initialized.subscribe(init => {
			if (!init) return;
			if (initSub) initSub();
			update();
		});
	});

	onDestroy(() => {
		if (unsub) unsub();
	});

	$: {
		sortedTriggers = Object.keys(triggers)
			.filter(trigger => trigger.toLowerCase().includes(searchParams.toLowerCase()))
			.sort((a, b) => (triggers[a].component.new().isDeprecated ? 0 : -1) - (triggers[b].component.new().isDeprecated ? 0 : -1))
			.map(key => triggers[key].component.new());
	}

	const onSelectTrigger = data => {
		skill.triggers.push(data.detail);
		update();
		setTimeout(() => triggerModal = false);
	};

	const drop = () => {
		const comp = get(draggingComponent);
		hovered    = false;
		skill.removeComponent(comp);
		skill.triggers = [...skill.triggers];
	};

	const update = () => {
		skill.triggers = [...skill.triggers];
		save();
	};

	const save = () => skills.set([...get(skills)]);
</script>

<svelte:head>
	<title>ProSkillAPI Dynamic Editor - {skill.name}</title>
</svelte:head>
<div class='header'>
	<h2>
		{skill.name}
		<a class='material-symbols-rounded edit-skill chip' title='Edit'
			 href='{base}/skill/{skill.name}/edit'>edit</a>
		<div class='add-trigger chip' title='Add Trigger' on:click={() => triggerModal = true}>
			<span class='material-symbols-rounded'>
				new_label
			</span>
		</div>
	</h2>
	<hr />
</div>
<div class='container'>
	{#each skill.triggers as comp (comp.id)}
		<div class='widget'>
			<ComponentWidget {skill} component={comp} on:update={update} on:save={save} />
		</div>
	{/each}
	{#if skill.triggers.length == 0}
		<div>No triggers added yet.</div>
	{/if}
</div>

<Modal bind:open={triggerModal}>
	<div class='modal-header-wrapper'>
		<div />
		<h2 class='modal-header'>Select New Trigger</h2>
		<div class='search-bar'>
			<ProInput bind:value={searchParams} placeholder='Search...' />
		</div>
	</div>
	<hr />
	<div class='triggers'>
		{#each sortedTriggers as trigger}
			<div class='comp-select' on:click={() => onSelectTrigger({detail: trigger.clone()})}>
				{#if trigger.isDeprecated}<s>{trigger.name}</s>{:else}{trigger.name}{/if}
			</div>
		{/each}
	</div>
	<hr />
	<div class='cancel' on:click={() => triggerModal = false}>Cancel</div>
</Modal>

<style>
    .header {
        padding-top: 1rem;
        width: 100%;
        z-index: 20;
        position: sticky;
        top: 3rem;
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
        overflow-x: auto;
        padding-inline: 2rem;
        overflow-x: auto;
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
</style>