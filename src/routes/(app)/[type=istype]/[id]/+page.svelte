<script lang='ts'>
	import BlocklyComponentWidget                         from '$components/BlocklyComponentWidget.svelte';
	import ComponentWidget                                from '$components/ComponentWidget.svelte';
	import GraphEditor                                    from '$lib/graph/GraphEditor.svelte';
	import Modal                                          from '$components/Modal.svelte';
	import ComponentSection                               from '$components/modal/component/ComponentSection.svelte';
	import { onMount }                                    from 'svelte';
	import type { Unsubscriber }                          from 'svelte/store';
	import { get }                                        from 'svelte/store';
	import ProInput                                       from '$input/ProInput.svelte';
	import { filterParams, initialized, triggerSections } from '$api/components/registry';
	import type FabledTrigger                             from '$api/components/triggers.svelte';
	import FabledComponent                                from '$api/components/fabled-component.svelte';
import ComponentModal                                  from '$components/modal/ComponentModal.svelte';
import { openModal }                                   from '../../../../data/modal-service.svelte';
	import ComponentSelectModal                            from '$components/modal/ComponentSelectModal.svelte';
	import { base }                                       from '$app/paths';
	import FabledSkill, { skillStore }                    from '../../../../data/skill-store.svelte';
	import { blocklyMode }                                from '../../../../data/settings';
	import { triggerAutoSync } from '../../../../data/store';
	import YAML                 from 'yaml';
	import { parseYaml }        from '$api/yaml';

	interface Props {
		data: { data: FabledSkill };
	}

	let { data }: Props    = $props();
	let skill: FabledSkill = $derived(data?.data);
	let triggerModal       = $state(false);
	let viewMode           = $state<'tree' | 'blockly' | 'graph'>($blocklyMode ? 'blockly' : 'tree');
	let graphFullscreen    = $state(false);

	onMount(() => {
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
		triggerAutoSync(skill);
	};

	const getSnapshot = () => {
		return YAML.stringify({ [skill.name]: skill.serializeYaml() }, { lineWidth: 0, aliasDuplicateObjects: false });
	};

	const restoreSnapshot = async (yaml: string) => {
		const data = parseYaml(yaml) as Record<string, any>;
		if (!data || Object.keys(data).length === 0) return;
		await skill.load(Object.values(data)[0] as any);
		skill.postLoad();
		update();
	};
</script>

<svelte:head>
	<title>Fabled Dynamic Editor - {skill.name}</title>
</svelte:head>
	<div class='header' class:hidden={graphFullscreen && viewMode === 'graph'}>
	<h2>
		{skill.name}
		<a class='material-symbols-rounded edit-skill chip' href='{base}/skill/{skill.name}/edit'
			 title='Edit'>edit</a>
		{#if viewMode !== 'blockly'}
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
	<div class='mode-switch'>
		<button class:active={viewMode === 'tree'} onclick={() => viewMode = 'tree'}>🌳 Tree</button>
		<button class:active={viewMode === 'blockly'} onclick={() => viewMode = 'blockly'}>🧩 Blockly</button>
		<button class:active={viewMode === 'graph'} onclick={() => viewMode = 'graph'}>🔗 Graph</button>
	</div>
	<hr />
</div>
<div class='container' class:blockly={viewMode === 'blockly'} class:graph={viewMode === 'graph'} class:fullscreen={graphFullscreen && viewMode === 'graph'}>
	{#if viewMode === 'blockly'}
		{#key skill}
			<BlocklyComponentWidget {skill} onupdate={update} onsave={save} />
		{/key}
	{:else if viewMode === 'graph'}
		<GraphEditor {skill} triggers={skill.triggers} onsave={save} {getSnapshot} {restoreSnapshot} fullscreen={graphFullscreen} onToggleFullscreen={() => graphFullscreen = !graphFullscreen}
    onEditNode={(comp: FabledComponent) => openModal(ComponentModal, comp)} onAddChild={(comp: FabledComponent, onAdded: () => void) => openModal(ComponentSelectModal, comp, () => { onAdded(); }, () => { save(); })} />
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
		.header { padding-top: 1rem; width: 100%; z-index: 20; position: sticky; top: 0; background: var(--color-bg); }
t	.header.hidden { display: none; }
		width: 100%;
		z-index: 20;
		position: sticky;
		top: 0;
		background: var(--color-bg);
		margin: 0 3rem;
	}

	.mode-switch {
		display: flex;
		gap: 4px;
		margin: 0.5rem 3rem;
	}
	.mode-switch button {
		background: #21262d;
		color: #8b949e;
		border: 1px solid #30363d;
		border-radius: 6px;
		padding: 4px 12px;
		font-size: 13px;
		cursor: pointer;
		transition: all 0.15s;
	}
	.mode-switch button:hover {
		background: #30363d;
		color: #c9d1d9;
	}
	.mode-switch button.active {
		background: #1f6feb;
		color: #fff;
		border-color: #1f6feb;
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

	.container.graph {
		align-self: stretch;
		padding-inline: 0;
		overflow: hidden;
		height: calc(100vh - 180px);
	}
t	.container.fullscreen { height: calc(100vh - 50px); }

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
