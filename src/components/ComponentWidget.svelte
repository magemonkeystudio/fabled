<!--suppress CssUnresolvedCustomProperty -->
<script lang='ts'>
	import type ProComponent                             from '$api/components/procomponent';
	import ProTrigger                                    from '$api/components/triggers';
	import ProCondition                                  from '$api/components/conditions';
	import ProTarget                                     from '$api/components/targets';
	import ProMechanic                                   from '$api/components/mechanics';
	import { slide }                                     from 'svelte/transition';
	import { backOut }                                   from 'svelte/easing';
	import { draggingComponent }                         from '../data/store';
	import type ProSkill                                 from '$api/proskill';
	import { createEventDispatcher, onDestroy, onMount } from 'svelte';
	import type { Unsubscriber }                         from 'svelte/store';
	import { get }                                       from 'svelte/store';
	import { showSummaryItems, useSymbols }              from '../data/settings';
	import { openModal }                                 from '../data/modal-service';
	import ComponentModal                                from '$components/modal/ComponentModal.svelte';
	import ComponentSelectModal                          from '$components/modal/ComponentSelectModal.svelte';
	import PreviewModal                                  from '$components/modal/PreviewModal.svelte';
	import Registry                                      from '$api/components/registry';
	import Control                                       from '$components/control/Control.svelte';
	import { skills }                                    from '../data/skill-store';
	import type { YamlComponentData }                    from '$api/types';

	export let skill: ProSkill;
	export let component: ProComponent;
	let wrapper: HTMLElement;
	let children: HTMLElement;
	let childrenList: ProComponent[] = [];

	const dispatch = createEventDispatcher();

	let collapsed    = false;
	let over         = false;
	let overChildren = false;
	let top          = false;
	let bottom       = false;
	let commentOpen  = false;

	let childCompsSub: Unsubscriber;

	const openCompModal       = () => openModal(ComponentModal, component);
	const openCompSelectModal = () => openModal(ComponentSelectModal, component);
	const openPreviewModal    = () => openModal(PreviewModal, component);

	onMount(() => {
		childCompsSub = component.components
			.subscribe((comps: ProComponent[]) => childrenList = comps);

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
			if (component instanceof ProTrigger) {
				return '🚩';
			} else if (component instanceof ProCondition) {
				return '⚠';
			} else if (component instanceof ProTarget) {
				return '🎯';
			} else if (component instanceof ProMechanic) {
				return '🔧';
			}
		}

		if (component instanceof ProTrigger) {
			return 'Trigger';
		} else if (component instanceof ProCondition) {
			return 'Condition';
		} else if (component instanceof ProTarget) {
			return 'Target';
		} else if (component instanceof ProMechanic) {
			return 'Mechanic';
		}

		return '???';
	};

	const getColor = () => {
		if (component instanceof ProTrigger) {
			return '#0083ef';
		} else if (component instanceof ProCondition) {
			return '#feac00';
		} else if (component instanceof ProTarget) {
			return '#04af38';
		} else if (component instanceof ProMechanic) {
			return '#ff3a3a';
		}

		return 'orange';
	};

	const spin = (node: Node, { duration }: { duration: number }) => {
		return {
			duration,
			css: (t: number) => {
				const eased = backOut(t);

				return `transform: rotate(${180 - (eased * 180)}deg);`;
			}
		};
	};

	const move = (e: DragEvent) => {
		if (component == get(draggingComponent)) return;
		over = true;
		if (component instanceof ProTrigger) return;
		const rect = wrapper.getBoundingClientRect();

		top    = e.clientY < (rect.height / 2) + rect.top;
		bottom = e.clientY >= (rect.height / 2) + rect.top;
	};

	const clearStatus = () => {
		over   = false;
		top    = false;
		bottom = false;
	};

	const leave = (e: DragEvent) => {
		if (!e.relatedTarget || !wrapper?.contains(<Node>e.relatedTarget) || children?.contains(<Node>e.relatedTarget)) {
			clearStatus();
		}
	};

	const drop = () => {
		let comp = get(draggingComponent);

		dispatch('addskill', { comp, relativeTo: component, above: top });
		clearStatus();
	};

	const addSkill = (e: { detail: { comp: ProComponent, relativeTo: ProComponent, above: ProComponent } }) => {
		let comp       = e.detail.comp;
		let relativeTo = e.detail.relativeTo;
		let above      = e.detail.above;
		let index      = childrenList.indexOf(relativeTo);

		skill.removeComponent(comp);
		component.addComponent(comp, index + (!above ? 1 : 0));
		dispatch('save');
	};

	const clone = () => {
		const yamlData: YamlComponentData  = {};
		yamlData[`${component.name}-copy`] = component.toYamlObj();
		const cloned: ProComponent[]       = Registry.deserializeComponents(yamlData);

		if (component.parent) {
			cloned.forEach(c => component.parent?.addComponent(c));
		} else {
			cloned.forEach(c => skill.addComponent(c));
			dispatch('update');
		}
	};
</script>

<div class='wrapper'>
	<div out:slide
			 bind:this={wrapper}
			 draggable='true'
			 tabindex='0'
			 role='treeitem'
			 aria-selected='{$draggingComponent === component}'
			 on:dragstart|stopPropagation={() => draggingComponent.set(component)}
			 on:dragend={() => draggingComponent.set(undefined)}
			 on:drop|stopPropagation={drop}
			 on:click|stopPropagation={openCompModal}
			 on:keypress|stopPropagation={(e) => {
           if (e.key === 'Enter') openCompModal
         }}
			 on:dragover|preventDefault|stopPropagation={move}
			 on:dragleave|stopPropagation={leave}
			 class='comp-body'
			 class:over
			 class:top
			 class:bottom
			 class:dragging={$draggingComponent === component}
			 style:--comp-color={getColor()}>
		{#if collapsed}
			<span class='material-symbols-rounded' in:spin={{duration: 400}}>expand_more</span>
		{:else}
			<span class='material-symbols-rounded' in:spin={{duration: 400}}>expand_less</span>
		{/if}
		<div class='corner'
				 tabindex='0'
				 role='button'
				 on:click|stopPropagation={() => collapsed = !collapsed}
				 on:keypress|stopPropagation={(e) => {
            if (e.key === 'Enter') collapsed = !collapsed;
        }}
		/>
		<div class='name'>
			<span>{getName($useSymbols)}</span>{($useSymbols ? ' ' : ': ')}
			{#if component.isDeprecated}
				<s>{component.name}</s>
			{:else}
				{component.name}
			{/if}
		</div>

		{#if $showSummaryItems && component.summaryItems && component.summaryItems.length > 0}
			<div class='summary'>
				{#key $skills}
					{#each component.summaryItems as item}
						{#if component.getValue(item)}
							<span class='summary-item'>
								<span>{item}:</span>
								{#if item.includes("color")}
									<span style:background-color={component.getValue(item)} class='color-sample'></span>
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
					<Control title='Add Component'
									 icon='add'
									 color={getColor()}
									 on:click={openCompSelectModal} />
				{/if}
				{#if component.preview && component.preview.length > 0}
					<Control title='Preview Settings'
									 icon='visibility'
									 color='gray'
									 on:click={openPreviewModal} />
				{/if}
				<Control title='Clone'
								 icon='content_copy'
								 color='white'
								 on:click={clone}
				/>
				<Control title='Delete'
								 icon='delete'
								 color='red'
								 on:click={() => {
									 skill.removeComponent(component);
									 dispatch("update");
								 }} />
			</div>
			<div class='children' transition:slide>
				{#if childrenList.length === 0}
					{#if component.isParent && (over || overChildren)}
						<div class='filler'
								 role='none'
								 transition:slide
								 class:overChildren
								 on:dragenter|stopPropagation={() => {
                   overChildren = true;
                   over = false;
                 }}
								 on:dragover|preventDefault|stopPropagation={() => {

                 }}
								 on:dragleave={leave}
								 on:drop|preventDefault|stopPropagation={() => {
                   overChildren = false;

                   let comp = get(draggingComponent);
									 if (!comp) return;
                   skill.removeComponent(comp);
                   component.addComponent(comp);
                 }} />
					{/if}
				{:else}
					<div class='child-wrapper' bind:this={children}>
						{#each childrenList as child (child.id)}
            <span transition:slide>
              <svelte:self {skill} bind:component={child} on:update on:save on:addskill={addSkill} />
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
        z-index: 3;
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
        z-index: 2;
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