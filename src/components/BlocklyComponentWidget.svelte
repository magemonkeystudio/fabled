<script lang="ts" module>
	import { get, writable, type Writable } from 'svelte/store';
	import { slide } from 'svelte/transition';

	import * as Registry from '$api/components/registry';
	import FabledComponent from '$api/components/fabled-component.svelte';
	import FabledSkill from '../data/skill-store.svelte';
	import FabledTrigger from '$api/components/triggers.svelte';

	import * as Blockly from 'blockly';
	import '$api/blockly/toolbox-search';
	import { useSymbols, showSummaryItems } from '../data/settings';
	import FabledCondition from '$api/components/conditions.svelte';
	import FabledTarget from '$api/components/targets.svelte';
	import FabledMechanic from '$api/components/mechanics.svelte';
	import BooleanSelectOption from '$components/options/BooleanSelectOption.svelte';
	import ProInput from '$input/ProInput.svelte';
	// NEW: import shared blockly logic
	import { workspace_config, Toolbox, setupToolbox, setupStyle, componentToBlock, workspaceToSkill, blockToComponent, type FabledBlockSvg, focusSearch, copySelectedBlock, pasteBlock, hasClipboardData, setupCopyPasteContextMenu, removeCopyPasteContextMenu } from '$api/blockly/blockly.svelte';
	import { BlockDuplicator } from '$api/blockly/block-duplicator';
	import ClipboardIndicator from './ClipboardIndicator.svelte';

	// Removed duplicated logic now residing in $api/blockly/blockly.svelte
</script>

<script lang="ts">
	interface Props {
		skill: FabledSkill;
		onsave?: () => void;
		onupdate?: () => void;
		onaddskill?: (e: { comp: FabledComponent; relativeTo: FabledComponent; above: boolean; }) => void;
	}
	let { skill, onupdate, onsave }: Props = $props();
	let workspace: Blockly.WorkspaceSvg;
	let selected: Writable<string | undefined> = writable(undefined);
	let blockDuplicator: BlockDuplicator;

	function blocklyInit(node: HTMLElement) {
		console.debug('Blockly init');
		Blockly.ShortcutRegistry.registry.reset();
		setupToolbox();
		setupStyle();
		workspace = Blockly.inject(node, { toolbox: Toolbox.get(), ...workspace_config });
		new Blockly.WorkspaceAudio(workspace).preload();
		
		// Initialize block duplicator with update callback
		blockDuplicator = new BlockDuplicator(workspace, () => {
			workspaceToSkill(workspace, skill);
			if (onupdate) onupdate();
		});
		
		// Setup context menu for copy/paste
		setupCopyPasteContextMenu(workspace, () => {
			workspaceToSkill(workspace, skill);
			if (onupdate) onupdate();
		});
		
		skill.triggers.forEach((trigger) => { componentToBlock(workspace, trigger); });
		workspace.addChangeListener((e) => {
			if (['change', 'delete', 'create'].includes(e.type)) {
				workspaceToSkill(workspace, skill);
				if (onupdate) onupdate();
			}
		});
		workspace.addChangeListener((e) => {
			if (['viewport_change', 'drag', 'delete'].includes(e.type)) {
				updateSelected();
				$selected = undefined;
			}
			if (e.type !== 'click') return;
			// @ts-ignore
			const blockId = e.blockId;
			updateSelected();
			$selected = blockId;
		});
		
		let lastClickId: string = '';
		let lastClickTime: number = 0;
		// @ts-ignore
		workspace.addChangeListener((e: { blockId: string; type: string }) => {
			if (e.type !== 'click') return;
			if (e.blockId === lastClickId && Date.now() - lastClickTime < 300) {
				const targetBlock = workspace.getBlockById(e.blockId);
				if (targetBlock) targetBlock.setCollapsed(!targetBlock.isCollapsed());
			}
			lastClickId = e.blockId;
			lastClickTime = Date.now();
		});
		workspace.render();
		workspace.cleanUp = function () {
			this.setResizesEnabled(false);
			Blockly.Events.setGroup(true);
			const blocks = this.getTopBlocks(true);
			let x = 0;
			for (let i = 0, block; (block = blocks[i]); i++) {
				if (!block.isMovable()) continue;
				const pos = block.getRelativeToSurfaceXY();
				block.moveBy(x - pos.x, -pos.y, ['cleanup']);
				block.snapToGrid();
				// @ts-ignore
				const minHeight = workspace.renderer.getConstants().MIN_BLOCK_HEIGHT;
				x = block.getRelativeToSurfaceXY().x + block.getHeightWidth().width + minHeight;
			}
			Blockly.Events.setGroup(false);
			this.setResizesEnabled(true);
		};
		setTimeout(() => { workspace.cleanUp(); }, 100);
		
		// Return cleanup function
		return {
			destroy() {
				removeCopyPasteContextMenu();
				if (blockDuplicator) {
					blockDuplicator.dispose();
				}
			}
		};
	}

	function handleGlobalKey(e: KeyboardEvent) {
		if (!workspace) return;
		
		// Handle Ctrl+D for duplication (existing functionality)
		if (e.type === 'keydown' && e.key === 'd' && e.ctrlKey && !e.altKey && !e.shiftKey && !e.metaKey) {
			const target = e.target as HTMLElement | null;
			if (target && (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable)) return;
			
			e.preventDefault();
			if (blockDuplicator && $selected) {
				const newBlockId = blockDuplicator.duplicateSelectedBlock();
				if (newBlockId) {
					$selected = newBlockId;
					workspaceToSkill(workspace, skill);
					if (onupdate) onupdate();
				}
			}
		}
		
		// Handle Ctrl+C for cross-tab copy
		if (e.type === 'keydown' && e.key === 'c' && e.ctrlKey && !e.altKey && !e.shiftKey && !e.metaKey) {
			const target = e.target as HTMLElement | null;
			if (target && (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable)) return;
			
			const selectedBlock = Blockly.getSelected();
			if (selectedBlock && selectedBlock instanceof Blockly.BlockSvg) {
				e.preventDefault();
				copySelectedBlock().then(success => {
					if (success) {
						// Show visual feedback
						showCopyFeedback();
					}
				});
			}
		}
		
		// Handle Ctrl+V for cross-tab paste
		if (e.type === 'keydown' && e.key === 'v' && e.ctrlKey && !e.altKey && !e.shiftKey && !e.metaKey) {
			const target = e.target as HTMLElement | null;
			if (target && (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable)) return;
			
			hasClipboardData().then(hasData => {
				if (hasData) {
					e.preventDefault();
					pasteBlock(workspace, () => {
						workspaceToSkill(workspace, skill);
						if (onupdate) onupdate();
					}).then(newBlockId => {
						if (newBlockId) {
							$selected = newBlockId;
							// Show visual feedback
							showPasteFeedback();
						}
					});
				}
			});
		}
		
		// Handle / for search (existing functionality)
		if (e.key === '/' && !e.altKey && !e.ctrlKey && !e.metaKey) {
			const target = e.target as HTMLElement | null;
			if (target && (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable)) return;
			e.preventDefault();
			focusSearch(workspace);
		}
	}

	// Visual feedback functions
	function showCopyFeedback() {
		// Create a temporary notification
		const notification = document.createElement('div');
		notification.textContent = 'Block copied to clipboard';
		notification.style.cssText = `
			position: fixed;
			top: 20px;
			right: 20px;
			background: #4CAF50;
			color: white;
			padding: 10px 20px;
			border-radius: 4px;
			z-index: 10000;
			font-family: Arial, sans-serif;
			font-size: 14px;
			box-shadow: 0 2px 8px rgba(0,0,0,0.2);
			transition: opacity 0.3s ease;
		`;
		
		document.body.appendChild(notification);
		
		// Fade out and remove
		setTimeout(() => {
			notification.style.opacity = '0';
			setTimeout(() => {
				if (notification.parentNode) {
					notification.parentNode.removeChild(notification);
				}
			}, 300);
		}, 2000);
	}

	function showPasteFeedback() {
		// Create a temporary notification
		const notification = document.createElement('div');
		notification.textContent = 'Block pasted from clipboard';
		notification.style.cssText = `
			position: fixed;
			top: 20px;
			right: 20px;
			background: #2196F3;
			color: white;
			padding: 10px 20px;
			border-radius: 4px;
			z-index: 10000;
			font-family: Arial, sans-serif;
			font-size: 14px;
			box-shadow: 0 2px 8px rgba(0,0,0,0.2);
			transition: opacity 0.3s ease;
		`;
		
		document.body.appendChild(notification);
		
		// Fade out and remove
		setTimeout(() => {
			notification.style.opacity = '0';
			setTimeout(() => {
				if (notification.parentNode) {
					notification.parentNode.removeChild(notification);
				}
			}, 300);
		}, 2000);
	}

	function getSelected(): FabledComponent | undefined {
		if (!$selected) return undefined;
		return (workspace.getBlockById($selected) as FabledBlockSvg)?.component;
	}
	function updateSelected() {
		if (!$selected) return;
		const block = workspace.getBlockById($selected) as FabledBlockSvg;
		if (!block) return;
		block.updateSummary();
		const component = block.component;
		block.setCommentText(component.comment.length > 0 ? component.comment : null);
		workspaceToSkill(workspace, skill);
		if (onupdate) onupdate();
	}
</script>

<svelte:window on:keydown={handleGlobalKey} />

{#key [skill, $showSummaryItems, $useSymbols]}
	<div class="wrapper">
		<div style="height: 100%; flex-grow: 1;" use:blocklyInit></div>
		{#key $selected}
		{#if $selected}
			{@const data = getSelected()!}
			<div class="selected-block" style="height: 100%; width: 30em;">
				<div class="component-editor" style="width: 100%;">
					<h2 class:deprecated={data.isDeprecated}><span>{data.name}</span></h2>
					{#if data.description}
						<div class="modal-desc">{@html data.description}</div>
					{/if}
					<hr />
					<div class="component-entry">
						<ProInput label="Comment" tooltip="[comment] A comment that will be displayed in the skill editor" bind:value={data.comment} />
						{#if data instanceof FabledTrigger && data.name !== 'Cast' && data.name !== 'Initialize' && data.name !== 'Cleanup'}
							<BooleanSelectOption name="Mana" tooltip="[mana] Whether this trigger requires the mana cost to activate" bind:data={data.mana} />
							<BooleanSelectOption name="Cooldown" tooltip="[cooldown] Whether this trigger requires to be off cooldown to activate" bind:data={data.cooldown} />
						{:else if data instanceof FabledTarget || data instanceof FabledCondition || data instanceof FabledMechanic}
							<ProInput label="Icon Key" bind:value={data.iconKey} tooltip={'[icon-key] The key used by the component in the Icon Lore. If this is set to "example" and has a value name of "value", it can be referenced using the string "{attr:example.value}"'} />
						{/if}
						{#if data instanceof FabledMechanic}
							<BooleanSelectOption name="Counts as Cast" tooltip={'[counts] Whether this mechanic running treats the skill as "casted" and will consume mana and start the cooldown. Set to false if it is a mechanic applled when the skill fails such as cleanup or an error message'} bind:data={data.countsAsCast} />
						{/if}
						{#each data.data as datum}
							{#if datum.meetsRequirements(data)}
								<datum.component bind:data={datum.data} name={datum.name} tooltip="{datum.key ? '[' + datum.key + '] ' : ''}{datum.tooltip}" multiple={datum.multiple} />
							{/if}
						{/each}
					</div>
				</div>
			</div>
		{/if}
		{/key}
		<ClipboardIndicator />
	</div>
{/key}

<style>
	.wrapper { display: flex; flex-direction: row; height: 100%; width: 100%; }
	.selected-block { border-left: #424242 solid 3px; color: #fff; overflow: hidden auto; }
	.component-editor { padding-inline: 1em; box-sizing: border-box; }
	.component-editor > h2 { text-align: center; margin-top: 0; margin-bottom: 0.2em; }
	.modal-desc { text-align: center; box-sizing: border-box; padding-inline: 1em; white-space: break-spaces; }
	.deprecated { align-items: center; display: flex; }
	.deprecated > span { text-decoration: line-through; }
	.deprecated::after { text-decoration: unset; margin-left: 0.5rem; content: 'deprecated'; font-size: 0.6em; color: goldenrod; }
	.component-entry { display: grid; grid-template-columns: calc(50% - 3rem) calc(50% + 3rem); width: 100%; padding-inline: 0.5rem; padding-top: 0.25rem; }
	.modal-desc { max-width: 100%; white-space: break-spaces; text-align: center; }
	:global(.blocklyMainBackground) { stroke-width: 0; }
	:global(.blocklyTreeRowContentContainer input:focus-visible) { outline: none; }
	:global(.blocklyTreeSeparator) { border-bottom: solid #979797 1px; }
	:global(.block-title) { font-weight: bold; }
	:global(.blocklyTextarea) { color: #000 !important; }
</style>
