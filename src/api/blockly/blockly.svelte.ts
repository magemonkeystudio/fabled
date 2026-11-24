import { get } from 'svelte/store';
import * as Blockly from 'blockly';
import '$api/blockly/toolbox-search';
import * as Registry from '$api/components/registry';
import FabledComponent from '$api/components/fabled-component.svelte';
import FabledSkill from '$components/../data/skill-store.svelte';
import FabledTrigger from '$api/components/triggers.svelte';
import FabledCondition from '$api/components/conditions.svelte';
import FabledTarget from '$api/components/targets.svelte';
import FabledMechanic from '$api/components/mechanics.svelte';
import { useSymbols, showSummaryItems } from '$components/../data/settings';
import { EmbedField } from '$api/blockly/blockly-fields';
import DropdownSelect from '$api/options/dropdownselect.svelte';
import type { ComponentOption } from '$api/options/options';

import { initializeFabledRenderer } from '$api/blockly/fabled-renderer';
import { CrossTabClipboard } from '$api/blockly/cross-tab-clipboard';
import { addCopyPasteContextMenu, removeCopyPasteContextMenu as removeContextMenu } from '$api/blockly/context-menu';

// Workspace configuration (exported for injector usage)
export const workspace_config = {
	collapse: true,
	comments: true,
	disable: false,
	maxBlocks: Infinity,
	trashcan: false,
	horizontalLayout: false,
	toolboxPosition: 'start' as const,
	css: true,
	media: 'https://blockly-demo.appspot.com/static/media/',
	rtl: false,
	scrollbars: true,
	sounds: true,
	oneBasedIndex: true,
	zoom: {
		controls: false,
		wheel: true,
		startScale: 1,
		maxScale: 3,
		minScale: 0.3,
		scaleSpeed: 1.05
	},
	grid: {
		spacing: 20,
		length: 0,
		colour: '#555',
		snap: true
	},
	// Use custom renderer & theme
	renderer: 'fabled'
};

export const Toolbox = {
	isInitialized() {
		const toolbox = document.getElementById('toolbox');
		if (!toolbox) return false;
		if (toolbox.getAttribute('useSymbols') !== get(useSymbols).toString()) return false;
		if (toolbox.getAttribute('showSummaryItems') !== get(showSummaryItems).toString()) return false;
		return true;
	},
	get() {
		let toolbox = document.getElementById('toolbox');
		if (!toolbox) {
			toolbox = document.createElement('toolbox');
			toolbox.setAttribute('id', 'toolbox');
			toolbox.setAttribute('useSymbols', get(useSymbols).toString());
			toolbox.setAttribute('showSummaryItems', get(showSummaryItems).toString());
			toolbox.style.display = 'none';
			document.body.appendChild(toolbox);
		}
		return toolbox;
	},
	update(workspace: Blockly.WorkspaceSvg) {
		workspace.updateToolbox(this.get());
	},
	addSeparator() {
		const separator = document.createElement('sep');
		this.get().appendChild(separator);
	},
	clear() {
		this.get().innerHTML = '';
	}
};

export function getColor(type: string) {
	switch (type.toLowerCase()) {
		case 'trigger': 			return '#457B9D'; 
		case 'condition': 			return '#407a09'; 
		case 'target': 				return '#2A9D8F'; 
		case 'general mechanic': 	return '#2545ac'; 
		case 'particle mechanic': 	return '#8A2BE2'; 
		case 'flag mechanic': 		return '#E63946'; 
		case 'value mechanic': 		return '#D97706'; 
		case 'warp mechanic': 		return '#264653'; 
		default: 					return '#000';
	}
}

class Category {
	constructor(public name: string, color?: string) {
		if (color) this.color = color;
	}
	set color(value: string) {
		this.get().setAttribute('colour', value);
	}
	get() {
		const toolbox = Toolbox.get();
		let category = toolbox.querySelector(`category[name="${this.name}"]`);
		if (!category) {
			category = document.createElement('category');
			category.setAttribute('name', this.name);
			toolbox.appendChild(category);
		}
		return category as HTMLElement;
	}
	add(name: string, data?: any) {
		const category = this.get();
		const block = document.createElement('block');
		block.setAttribute('type', name);
		category.appendChild(block);
		if (data && Object.keys(data).length > 0) {
			const elements = parseBlockData(data);
			elements.forEach((element) => block.appendChild(element));
		}
	}
}

function parseBlockData(data: any) {
	const elements: HTMLElement[] = [];
	Object.entries(data).forEach(([key, value]) => {
		if (typeof value === 'string') {
			const fieldElement = document.createElement('field');
			fieldElement.setAttribute('name', key);
			fieldElement.textContent = value;
			elements.push(fieldElement);
		} else {
			const valueElement = document.createElement('value');
			valueElement.setAttribute('name', key);
			const { type, ...data } = value as { type: string; [key: string]: any };
			const shadow = document.createElement('shadow');
			shadow.setAttribute('type', type);
			valueElement.appendChild(shadow);
			if (data && Object.keys(data).length > 0) {
				const childElements = parseBlockData(data);
				childElements.forEach((element) => shadow.appendChild(element));
			}
			elements.push(valueElement);
		}
	});
	return elements;
}

export function setupStyle() {
	const theme = Blockly.Themes.Classic;
	const componentStyles = theme.componentStyles;
	componentStyles.workspaceBackgroundColour = '#111';
	componentStyles.flyoutBackgroundColour = '#181818';
	componentStyles.toolboxBackgroundColour = '#222';
	componentStyles.toolboxForegroundColour = '#ddd';
	componentStyles.flyoutForegroundColour = '#bbb';
	componentStyles.scrollbarColour = '#fff';
	componentStyles.scrollbarOpacity = 0.5;
	Blockly.Scrollbar.scrollbarThickness = 10;
}

export function getIcon(component: FabledComponent) {
	if (get(useSymbols)) {
		// Use component.category if available, otherwise fall back to instanceof
		const category = (component as any).category?.toLowerCase() || '';
		
		if (component instanceof FabledTrigger || category.includes('trigger')) return '🚩';
		else if (component instanceof FabledCondition || category.includes('condition')) return '⚠️';
		else if (component instanceof FabledTarget || category.includes('target')) return '🎯';
		else if (component instanceof FabledMechanic || category.includes('mechanic')) {
			if (category.includes('particle')) return '✨';
			else if (category.includes('flag')) return '🏷️';
			else if (category.includes('value')) return '🔢';
			else if (category.includes('warp')) return '🛸';
			else return '🧩';
		}
		return '❓';
	}
	
	// Non-symbol fallback
	const category = (component as any).category?.toLowerCase() || '';
	if (component instanceof FabledTrigger || category.includes('trigger')) return 'Trigger';
	else if (component instanceof FabledCondition || category.includes('condition')) return 'Condition';
	else if (component instanceof FabledTarget || category.includes('target')) return 'Target';
	else if (component instanceof FabledMechanic || category.includes('mechanic')) return 'Mechanic';
	return '???';
}

export type FabledBlockSvg = Blockly.BlockSvg & {
	component: FabledComponent;
	assign: (data: FabledComponent) => void;
	updateSummary: () => void;
};

function migrateRegistry() {
	const migrate = (
		type: string,
		name: string,
		color: string,
		registry: Registry.RegistryData
	) => {
		const category = new Category(name, color);
		const blocks = Object.entries(registry)
			.map(([key, value]) => {
				key = `${type}_${value.name.toLowerCase().replace(/\s/g, '_')}`;
				const definition: any = {
					init: function () {
						const self = this as FabledBlockSvg;
						// @ts-ignore
						const component = (new value.component() as FabledComponent).defaultOpen();
						
						// Set category information for safe icon determination
						(component as any).category = value.section || type;
						
						self.assign(component);
						if (!type.startsWith('trigger')) {
							self.setPreviousStatement(true, null);
							self.setNextStatement(true, null);
						}
						self.setColour(color);
						self.appendEndRowInput().appendField(new EmbedField(`&l${getIcon(component)} ${component.name}`));
						if (get(showSummaryItems) && component.summaryItems && component.summaryItems.length) {
							const alignLimit = 3;
							const rows = Math.ceil(component.summaryItems.length / alignLimit);
							for (let row = 0; row < rows; row++) {
								const summary = self.appendEndRowInput();
								const itemsPerRow = Math.ceil((component.summaryItems.length - row * alignLimit) / (rows - row));
								for (let i = 0; i < itemsPerRow && row * alignLimit + i < component.summaryItems.length; i++) {
									const field = new Blockly.FieldTextInput();
									const item = component.summaryItems[row * alignLimit + i];
									const value = component.getValue(item);
									field.setValue(`${item}: ${value}`);
									field.setEnabled(false);
									summary.appendField(field, item);
								}
							}
						}
						if (component.isParent) {
							self.appendStatementInput('CHILDREN').setCheck(null);
						}
					},
					onchange: function (event: Blockly.Events.Abstract) {
						const self = this as Blockly.Block;
						if (!self.isInFlyout) {
							const rootBlock = self.getRootBlock();
							self.setDisabledReason(!rootBlock.type.startsWith('trigger'), 'Block must be connected to a Trigger');
						}
					},
					assign: function (component: FabledComponent) {
						this.component = component;
						component.data
							.filter((dat: ComponentOption) => dat instanceof DropdownSelect)
							.forEach((dat: DropdownSelect) => { dat.init(); });
						if (component.comment) {
							this.setCommentText(component.comment);
						}
					},
					updateSummary: function () {
						const self = this as FabledBlockSvg;
						self.inputList.flatMap((input) => input.fieldRow).forEach((field) => {
							if (!field.name) return;
							let value = self.component.getValue(field.name);
							field.setValue(`${field.name}: ${value}`);
						});
					},
					saveExtraState: function () {
						return { 'component_data': this.component.toYamlObj() };
					},
					loadExtraState: function (state: any) {
						if (state && state.component_data && this.component) {
							try {
								this.component.deserialize(state.component_data);
								// Update the summary fields after loading state
								if (typeof this.updateSummary === 'function') {
									this.updateSummary();
								}
							} catch (error) {
								console.warn('Failed to load component state:', error);
							}
						}
					}
				};
				return [key, definition] as [string, any];
			})
			.reduce((acc: { [key: string]: any }, [key, definition]) => {
				acc[key] = definition;
				category.add(key);
				return acc;
			}, {});
		Blockly.common.defineBlocks(blocks);
	};
	const triggerGroup: { [key: string]: Registry.RegistryData } = {};
	Object.entries(get(Registry.triggers)).forEach(([key, value]) => {
		const group = value.section ?? 'General';
		if (!triggerGroup[group]) triggerGroup[group] = {};
		triggerGroup[group][key] = value;
	});
	Object.entries(triggerGroup).forEach(([group, registry]) => {
		migrate('trigger', `${group} Triggers`, getColor('Trigger'), registry);
	});
	Toolbox.addSeparator();
	migrate('target', 'Targeters', getColor('Target'), get(Registry.targets));
	migrate('condition', 'Conditions', getColor('Condition'), get(Registry.conditions));
	const mechanicGroups: { [key: string]: Registry.RegistryData } = {};
	Object.entries(get(Registry.mechanics)).forEach(([key, value]) => {
		const group = value.section ?? 'General';
		if (!mechanicGroups[group]) mechanicGroups[group] = {};
		mechanicGroups[group][key] = value;
	});
	Object.entries(mechanicGroups).forEach(([group, registry]) => {
		migrate('mechanic', `${group} Mechanics`, getColor(group + ' mechanic'), registry);
	});
}

export function setupToolbox() {
	if (Toolbox.isInitialized()) return;
	Toolbox.clear();
	Toolbox.get().appendChild((() => {
		const searchBox = document.createElement('search');
		searchBox.setAttribute('name', 'Search');
		searchBox.setAttribute('colour', '#5577ee');
		return searchBox;
	})());
	Toolbox.addSeparator();
	migrateRegistry();
}

/**
 * Initialize workspace with safe renderer setup
 */
export function initializeWorkspace(workspace: Blockly.WorkspaceSvg): void {
	// Initialize the custom renderer event system
	initializeFabledRenderer(workspace);
}

export function componentToBlock(
	workspace: Blockly.WorkspaceSvg,
	component: FabledComponent,
	parent: Blockly.BlockSvg | null = null
): Blockly.BlockSvg {
	initializeFabledRenderer(workspace);
	
	const key = `${component.type}_${component.name.toLowerCase().replace(/\s/g, '_')}`;
	const com = workspace.newBlock(key) as FabledBlockSvg;
	com.assign(component);
	if (parent) {
		const childrenInput = parent.getInput('CHILDREN');
		if (childrenInput) childrenInput.connection?.connect(com.previousConnection);
	}
	com.updateSummary();
	get(component.components).reverse().forEach((child) => { componentToBlock(workspace, child, com); });
	com.initSvg();
	com.render();
	return com;
}

export function workspaceToSkill(workspace: Blockly.WorkspaceSvg, skill: FabledSkill) {
	initializeFabledRenderer(workspace);
	
	const components = [
		...workspace.getTopBlocks(true)
			.filter((b) => b.isEnabled() && b.type.startsWith('trigger'))
			.map((b) => blockToComponent(b as FabledBlockSvg))
			.filter((b) => b !== undefined)
	];
	skill.triggers = components as FabledTrigger[];
}

export function blockToComponent(block: FabledBlockSvg): FabledComponent | undefined {
	const component = block.component;
	if (component && component.isParent) {
		component.components.set([]);
		const children = block.getInput('CHILDREN');
		let target = children?.connection?.targetBlock();
		while (target) {
			const child = blockToComponent(target as FabledBlockSvg);
			if (child) component.components.update((components) => [...components, child]);
			target = target.getNextBlock();
		}
	}
	component.comment = block.getCommentText() ?? '';
	return component;
}

export function focusSearch(workspace: Blockly.WorkspaceSvg) {
	initializeFabledRenderer(workspace);
	
	const toolbox: any = workspace.getToolbox?.();
	if (!toolbox) return;
	let index = -1;
	if (toolbox.getToolboxItems) {
		const items = toolbox.getToolboxItems();
		index = items.findIndex((it: any) => (it?.toolboxItemDef_?.kind || '').toLowerCase() === 'search');
	}
	if (index < 0) {
		const languageTree: any = workspace.options.languageTree;
		const contents = languageTree?.contents || [];
		index = contents.findIndex((c: any) => (c.kind || '').toLowerCase() === 'search');
	}
	if (index < 0) return;
	try {
		toolbox.selectItemByPosition(index);
		requestAnimationFrame(() => {
			const input: HTMLInputElement | null = toolbox.HtmlDiv?.querySelector('input[type="search"]');
			if (input) {
				input.focus();
				input.select();
			}
		});
	} catch (e) {
		console.warn('Unable to focus search category', e);
	}
}

/**
 * Copy the selected block to system clipboard
 */
export async function copySelectedBlock(): Promise<boolean> {
	return await CrossTabClipboard.copySelectedBlock();
}

/**
 * Paste block from system clipboard
 */
export async function pasteBlock(workspace: Blockly.WorkspaceSvg, onUpdate?: () => void): Promise<string | null> {
	initializeFabledRenderer(workspace);
	return await CrossTabClipboard.pasteBlock(workspace, onUpdate);
}

/**
 * Check if there's valid clipboard data available
 */
export async function hasClipboardData(): Promise<boolean> {
	return await CrossTabClipboard.hasClipboardData();
}

/**
 * Get a preview of what's in the clipboard
 */
export async function getClipboardPreview(): Promise<string | null> {
	return await CrossTabClipboard.getClipboardPreview();
}

/**
 * Clear the clipboard
 */
export async function clearClipboard(): Promise<void> {
	await CrossTabClipboard.clearClipboard();
}

/**
 * Setup context menu for copy/paste
 */
export function setupCopyPasteContextMenu(workspace: Blockly.WorkspaceSvg, onUpdate?: () => void): void {
	addCopyPasteContextMenu(workspace, onUpdate);
}

/**
 * Remove context menu for copy/paste
 */
export function removeCopyPasteContextMenu(): void {
	removeContextMenu();
}
