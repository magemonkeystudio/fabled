<script lang='ts'>
	import ComponentWidget from './ComponentWidget.svelte';

	import type FabledComponent             from '$api/components/fabled-component.svelte';
	import FabledTrigger                    from '$api/components/triggers.svelte';
	import FabledCondition                  from '$api/components/conditions.svelte';
	import FabledTarget                     from '$api/components/targets.svelte';
	import FabledMechanic                   from '$api/components/mechanics.svelte';
	import { slide }                        from 'svelte/transition';
	import { backOut }                      from 'svelte/easing';
	import { draggingComponent }            from '../data/store';
	import { onDestroy, onMount }           from 'svelte';
	import type { Unsubscriber }            from 'svelte/store';
	import { get }                          from 'svelte/store';
	import { showSummaryItems, useSymbols } from '../data/settings';
	import { openModal }                    from '../data/modal-service.svelte';
	import ComponentModal                   from '$components/modal/ComponentModal.svelte';
	import ComponentSelectModal             from '$components/modal/ComponentSelectModal.svelte';
	import PreviewModal                     from '$components/modal/PreviewModal.svelte';
	import Registry                         from '$api/components/registry';
	import Control                          from '$components/control/Control.svelte';
	import type { YamlComponentData }       from '$api/types';
	import FabledSkill, { skillStore }      from '../data/skill-store.svelte';

	interface Props {
		skill: FabledSkill;
		component: FabledComponent;
		onsave?: () => void;
		onupdate?: () => void;
		onaddskill?: (e: { comp: FabledComponent; relativeTo: FabledComponent; above: boolean }) => void;
	}

	let { skill, component, onsave, onupdate, onaddskill }: Props = $props();
	let wrapper: HTMLElement | undefined                          = $state();
	let children: HTMLElement | undefined                         = $state();
	let childrenList: FabledComponent[]                           = $state([]);

	const skills = skillStore.skills;

	let collapsed    = $state(false);
	let over         = $state(false);
	let overChildren = $state(false);
	let top          = $state(false);
	let bottom       = $state(false);
	let commentOpen  = false;

	let childCompsSub: Unsubscriber;

	const openCompModal       = (e?: Event) => {
		e?.stopPropagation();
		openModal(ComponentModal, component);
	};
	const openCompSelectModal = (e: Event) => {
		e.stopPropagation();
		openModal(ComponentSelectModal, component);
	};
	const openPreviewModal    = (e: Event) => {
		e.stopPropagation();
		openModal(PreviewModal, component);
	};

	onMount(() => {
		childCompsSub = component.components.subscribe(
			(comps: FabledComponent[]) => (childrenList = comps)
		);

		if (component._defaultOpen) {
			openCompModal();
			component._defaultOpen = false;
		}
	});

	onDestroy(() => {
		if (childCompsSub) childCompsSub();
	});

	const getName = (symbols = false) => {
		if (symbols) {
			if (component instanceof FabledTrigger) {
				return '🚩';
			} else if (component instanceof FabledCondition) {
				return '⚠';
			} else if (component instanceof FabledTarget) {
				return '🎯';
			} else if (component instanceof FabledMechanic) {
				return '🔧';
			}
		}

		if (component instanceof FabledTrigger) {
			return 'Trigger';
		} else if (component instanceof FabledCondition) {
			return 'Condition';
		} else if (component instanceof FabledTarget) {
			return 'Target';
		} else if (component instanceof FabledMechanic) {
			return 'Mechanic';
		}

		return '???';
	};

	const getColor = () => {
		if (component instanceof FabledTrigger) {
			return '#0083ef';
		} else if (component instanceof FabledCondition) {
			return '#feac00';
		} else if (component instanceof FabledTarget) {
			return '#04af38';
		} else if (component instanceof FabledMechanic) {
			return '#ff3a3a';
		}

		return 'orange';
	};

	const spin = (node: Node, { duration }: { duration: number }) => {
		return {
			duration,
			css: (t: number) => {
				const eased = backOut(t);

				return `transform: rotate(${180 - eased * 180}deg);`;
			}
		};
	};

	const move = (e: DragEvent) => {
		e.stopPropagation();
		e.preventDefault();
		if (component == get(draggingComponent)) return;
		over = true;
		if (component instanceof FabledTrigger) return;
		const rect = wrapper?.getBoundingClientRect() || { top: 0, height: 0 };

		top    = e.clientY < rect.height / 2 + rect.top;
		bottom = e.clientY >= rect.height / 2 + rect.top;
	};

	const clearStatus = () => {
		over   = false;
		top    = false;
		bottom = false;
	};

	const leave = (e: DragEvent) => {
		e.stopPropagation();
		if (
			!e.relatedTarget ||
			!wrapper?.contains(<Node>e.relatedTarget) ||
			children?.contains(<Node>e.relatedTarget)
		) {
			clearStatus();
		}
	};

	const drop = (e: Event) => {
		e.stopPropagation();
		let comp = get(draggingComponent);
		if (comp) onaddskill?.({ comp, relativeTo: component, above: top });
		clearStatus();
	};

	const addSkill = (e: {
		comp: FabledComponent; relativeTo: FabledComponent; above: boolean
	}) => {
		let { comp, relativeTo, above } = e;
		let index                       = childrenList.indexOf(relativeTo);

		skill.removeComponent(comp);
		component.addComponent(comp, index + (!above ? 1 : 0));
		onsave?.();
	};

	const clone = (e: Event) => {
		e.stopPropagation();
		const yamlData: YamlComponentData  = {};
		yamlData[`${component.name}-copy`] = component.toYamlObj();
		const cloned: FabledComponent[]    = Registry.deserializeComponents(yamlData);

		if (component.parent) {
			cloned.forEach((c) => component.parent?.addComponent(c));
		} else {
			cloned.forEach((c) => skill.addComponent(c));
			onupdate?.();
		}
	};
</script>

<div class='wrapper'>
	<div
		aria-selected={$draggingComponent === component}
		bind:this={wrapper}
		class='comp-body'
		class:bottom
		class:dragging={$draggingComponent === component}
		class:over
		class:top
		draggable='true'
		onclick={openCompModal}
		ondragend={() => draggingComponent.set(undefined)}
		ondragleave={leave}
		ondragover={move}
		ondragstart={(e) => {
			e.stopPropagation();
			draggingComponent.set(component)
		}}
		ondrop={drop}
		onkeypress={(e) => {
			if (e.key === 'Enter') {
				openCompModal(e);
			}
		}}
		out:slide
		role='treeitem'
		style:--comp-color={getColor()}
		tabindex='0'
	>
		<div class='name'>
			<span>{getName($useSymbols)}</span>{$useSymbols ? ' ' : ': '}
			{#if component.isDeprecated}
				<s>{component.name}</s>
			{:else}
				{component.name}
			{/if}
		</div>
		<div
			class='corner'
			onclick={(e) => {
				e.stopPropagation();
				collapsed = !collapsed;
			}}
			onkeypress={(e) => {
				if (e.key === 'Enter') {
					e.stopPropagation();
					collapsed = !collapsed;
				}
			}}
			role='button'
			tabindex='0'
		></div>
		{#if collapsed}
			<span class='material-symbols-rounded' in:spin={{ duration: 400 }}>expand_more</span>
		{:else}
			<span class='material-symbols-rounded' in:spin={{ duration: 400 }}>expand_less</span>
		{/if}

		{#if $showSummaryItems && component.summaryItems && component.summaryItems.length > 0}
			<div class='summary'>
				{#key $skills}
					{#each component.summaryItems as item}
						{#if component.getValue(item)}
							<span class='summary-item'>
								<span>{item}:</span>
								{#if item.includes('color')}
									<span style:background-color={component.getValue(item)} class='color-sample'
									></span>
								{:else}
									{component.getValue(item)}
								{/if}
							</span>
						{/if}
					{/each}
				{/key}
			</div>
		{/if}

		{#key $skills}
			{#if component.comment || commentOpen}
				<div class='comment'>
					{component.comment}
				</div>
			{/if}
		{/key}

		{#if !collapsed}
			<div class='controls' transition:slide>
				{#if component.isParent}
					<Control
						title='Add Component'
						icon='add'
						color={getColor()}
						onclick={openCompSelectModal}
					/>
				{/if}
				{#if component.preview && component.preview.length > 0}
					<Control
						title='Preview Settings'
						icon='visibility'
						color='gray'
						onclick={openPreviewModal}
					/>
				{/if}
				<Control title='Clone' icon='content_copy' color='white' onclick={clone} />
				<Control
					title='Delete'
					icon='delete'
					color='red'
					onclick={(e) => {
						e.stopPropagation();
						skill.removeComponent(component);
						onupdate?.();
					}}
				/>
			</div>
			<div class='children' transition:slide>
				{#if childrenList.length === 0}
					{#if component.isParent && (over || overChildren)}
						<div
							class='filler'
							role='none'
							transition:slide
							class:overChildren
							ondragenter={(e) => {
								e.stopPropagation();
								overChildren = true;
								over = false;
							}}
							ondragover={(e) => {
								e.stopPropagation();
								e.preventDefault();
							}}
							ondragleave={leave}
							ondrop={(e) => {
								e.stopPropagation();
								e.preventDefault();
								overChildren = false;

								let comp = get(draggingComponent);
								if (!comp) return;
								skill.removeComponent(comp);
								component.addComponent(comp);
							}}
						></div>
					{/if}
				{:else}
					<div class='child-wrapper' bind:this={children}>
						{#each childrenList as child (child.id)}
							<span transition:slide>
								<ComponentWidget
									{skill}
									component={child}
									{onupdate}
									{onsave}
									onaddskill={addSkill}
								/>
							</span>
						{/each}
					</div>
				{/if}
			</div>
		{/if}
	</div>
</div>

<style>
    span.material-symbols-rounded {
        color: rgba(0, 0, 0, 0.4);
        position: absolute;
        top: 0;
        right: 0;
        pointer-events: none;
        /*text-shadow: 0 0 0.1rem black;*/
    }

    .comp-body {
        display: flex;
        flex: 1;
        flex-direction: column;
        align-items: stretch;
        justify-items: center;
        padding: 0.25rem 0.5rem 0.75rem;
        background-color: #333;
        width: min-content;
        box-shadow: inset 0 0 0.5rem #111;
        border: 0.13rem solid #444;
        border-left: 0.3rem solid var(--comp-color);
        border-radius: 0 0.4rem 0.4rem 0;
        overflow: hidden;
        user-select: none;
        transition: border 0.2s ease;
    }

    .corner {
        position: absolute;
        right: -3rem;
        top: 0;
        height: 2rem;
        width: 4rem;
        transform-origin: 50% 0;
        transform: rotate(45deg);
        background-color: var(--comp-color);
        /*border-left: 1.3rem solid transparent;*/
        transition: right 0.25s ease;
        box-shadow: inset 0 -2px 0.5rem #222;
    }

    .corner:hover {
        right: -2rem;
        cursor: pointer;
    }

    .name {
        margin: 0.5rem 1rem 0 0.25rem;
    }

    .name span {
        font-weight: bold;
        color: var(--comp-color);
    }

    .children {
        display: flex;
        flex-direction: column;
        align-items: stretch;
        padding: 0 0.5rem;
    }

    .wrapper {
        display: flex;
    }

    .controls {
        display: flex;
        justify-content: stretch;
        align-items: center;
        margin-bottom: 0.1rem;
    }

    .over:not(.dragging).bottom {
        border-bottom: 1rem solid #0083ef;
    }

    .over:not(.dragging).top {
        border-top: 1rem solid #0083ef;
    }

    .dragging {
        opacity: 0.2;
    }

    .filler {
        height: 3rem;
        border: 5px dashed #666;
        border-radius: 0.5rem;
    }

    .overChildren {
        border: 5px solid #0083ef;
    }

    .comment {
        margin: 0 0.5rem 0.25rem;
        padding: 0.25rem;
        background-color: #222;
        border-radius: 0.25rem;
        color: #ccc;
        font-size: 0.9rem;
        font-style: italic;
        white-space: break-spaces;
    }

    .summary {
        display: flex;
        flex-wrap: wrap;
        justify-content: flex-start;
        gap: 0.25rem;

        margin: 0.4rem 0.5rem;
        font-size: 0.8rem;
    }

    .summary-item {
        padding: 0.25rem;
        border-radius: 0.25rem;
        background-color: #555;
        color: #eee;
        white-space: break-spaces;

        display: flex;
        align-items: center;
    }

    .color-sample {
        display: inline-block;
        width: 1em;
        height: 1em;
        border-radius: 0.25rem;
        margin-left: 0.25rem;
    }
</style>
