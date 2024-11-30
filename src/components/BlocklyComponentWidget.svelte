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
	import { EmbedField } from '$api/blockly/blockly-fields';
	import BooleanSelectOption from '$components/options/BooleanSelectOption.svelte';
	import ProInput from '$input/ProInput.svelte';

	const workspace_config = {
		collapse: true,
		comments: true,
		disable: false,
		maxBlocks: Infinity,
		trashcan: false,
		horizontalLayout: false,
		toolboxPosition: 'start',
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
			scaleSpeed: 1.01
		},
		grid: {
			spacing: 20,
			length: 0,
			colour: '#555',
			snap: true
		}
	};

	const Toolbox = {
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

	function getColor(type: string) {
		switch (type.toLowerCase()) {
			case 'trigger':
				return '#0083ef';
			case 'condition':
				return '#e07b00';
			case 'target':
				return '#04af38';
			case 'mechanic':
				return '#D70000	';
			default:
				return '#fff';
		}
	}

	class Category {
		constructor(
			public name: string,
			color?: string
		) {
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
			return category;
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

	function setupStyle() {
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

	function getIcon(component: FabledComponent) {
		if (get(useSymbols)) {
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
			// BLOCK DEFINITION
			const blocks = Object.entries(registry)
				.map(([key, value]) => {
					key = `${type}_${value.name.toLowerCase().replace(/\s/g, '_')}`;
					const definition: any = {
						init: function () {
							const self = this as FabledBlockSvg;
							// @ts-ignore
							const component = new value.component();
							self.assign(component);
							if (!type.startsWith('trigger')) {
								self.setPreviousStatement(true, null);
								self.setNextStatement(true, null);
							}
							self.setColour(color);
							self.setTooltip(component.description); // todo: create a custom tooltip display to compatible with @html rendering
							self
								.appendEndRowInput()
								.appendField(new EmbedField(`&l${getIcon(component)}&r: ${component.name}`));

							if (
								get(showSummaryItems) &&
								component.summaryItems &&
								component.summaryItems.length
							) {
								const summary = self.appendEndRowInput();
								component.summaryItems.forEach((entry: string) => {
									const value = component.getValue(entry);
									const field = new Blockly.FieldTextInput(`${entry}: ${value}`);
									field.setEnabled(false);
									summary.appendField(field, entry);
								});
							}

							if (component.isParent) {
								self.appendStatementInput('CHILDREN').setCheck(null);
							}
						},
						onchange: function (event: Blockly.Events.Abstract) {
							const self = this as Blockly.Block;
							if (!self.isInFlyout) {
								const rootBlock = self.getRootBlock();
								self.setDisabledReason(
									!rootBlock.type.startsWith('trigger'),
									'Block must be connected to a Trigger'
								);
							}
						},
						assign: function (data: FabledComponent) {
							this.component = data;
						},
						updateSummary: function () {
							const self = this as FabledBlockSvg;
							self.inputList.flatMap((input) => input.fieldRow).forEach((field) => {
								if (!field.name) return;
								let value = self.component.getValue(field.name);
								if (value !== undefined) {
									field.setValue(`${field.name}: ${value}`);
								}
							});
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
			migrate('mechanic', `${group} Mechanics`, getColor('Mechanic'), registry);
		});
	}

	function setupToolbox() {
		if (Toolbox.isInitialized()) return;
		Toolbox.clear();
		Toolbox.get().appendChild(
			(() => {
				const searchBox = document.createElement('search');
				searchBox.setAttribute('name', 'Search');
				searchBox.setAttribute('colour', '#5577ee');
				return searchBox;
			})()
		);
		Toolbox.addSeparator();

		migrateRegistry();
	}

	export function componentToBlock(
		workspace: Blockly.WorkspaceSvg,
		component: FabledComponent,
		parent: Blockly.BlockSvg | null = null
	): Blockly.BlockSvg {
		const key = `${component.type}_${component.name.toLowerCase().replace(/\s/g, '_')}`;
		const com = workspace.newBlock(key) as FabledBlockSvg;
		com.assign(component);
		if (parent) {
			const childrenInput = parent.getInput('CHILDREN');
			if (childrenInput) {
				childrenInput.connection?.connect(com.previousConnection);
			}
		}
		com.updateSummary();
		get(component.components)
			.reverse()
			.forEach((child) => {
				componentToBlock(workspace, child, com);
			});
		com.initSvg();
		com.render();
		return com;
	}

	export function workspaceToSkill(workspace: Blockly.WorkspaceSvg, skill: FabledSkill) {
		const components = [
			...workspace
				.getTopBlocks(true)
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
			const children = block.getInput('CHILDREN')!;
			let target = children.connection?.targetBlock();
			while (target) {
				const child = blockToComponent(target as FabledBlockSvg);
				if (child) component.components.update((components) => [...components, child]);
				target = target.getNextBlock();
			}
		}
		return component;
	}
</script>

<script lang="ts">
	interface Props {
		skill: FabledSkill;
		onsave?: () => void;
		onupdate?: () => void;
		onaddskill?: (e: {
			comp: FabledComponent;
			relativeTo: FabledComponent;
			above: boolean;
		}) => void;
	}

	let { skill, onupdate, onsave }: Props = $props();
	let workspace: Blockly.WorkspaceSvg;
	let selected: Writable<string | undefined> = writable(undefined);

	function blocklyInit(node: HTMLElement) {
		console.debug('Blockly init');
		Blockly.ShortcutRegistry.registry.reset();
		setupToolbox();
		setupStyle();
		workspace = Blockly.inject(node, {
			toolbox: Toolbox.get(),
			...workspace_config
		});
		new Blockly.WorkspaceAudio(workspace).preload();
		skill.triggers.forEach((trigger) => {
			componentToBlock(workspace, trigger);
		});
		workspace.addChangeListener((e) => {
			if (['change', 'delete', 'create'].includes(e.type)) {
				workspaceToSkill(workspace, skill);
				if (onupdate) onupdate();
			}
		});
		workspace.addChangeListener((e) => {
			if (['viewport_change', 'drag', 'delete'].includes(e.type)) {
				$selected = undefined;
			}
			if (e.type !== 'click') return;
			// @ts-ignore
			const blockId = e.blockId;
			$selected = blockId;
			updateSelected();
		});

		let lastClickId: string = '';
		let lastClickTime: number = 0;
		// @ts-ignore
		workspace.addChangeListener((e: { blockId: string; type: string }) => {
			if (e.type !== 'click') return;
			if (e.blockId === lastClickId && Date.now() - lastClickTime < 300) {
				const targetBlock = workspace.getBlockById(e.blockId);
				if (targetBlock) {
					targetBlock.setCollapsed(!targetBlock.isCollapsed());
				}
			}
			lastClickId = e.blockId;
			lastClickTime = Date.now();
		});

		workspace.render();
		// horizontal cleanup layout
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
		setTimeout(() => {
			workspace.cleanUp();
		}, 100);
	}

	function getSelected(): FabledComponent | undefined {
		if (!$selected) return undefined;
		return (workspace.getBlockById($selected) as FabledBlockSvg)?.component;
	}

	function updateSelected() {
		if (!$selected) return;
		const block = workspace.getBlockById($selected)!;
		const component = getSelected()!;
		if (component.summaryItems && component.summaryItems.length) {
			component.summaryItems.forEach((item) => {
				block.getField(item)?.setValue(`${item}: ${component.getValue(item)}`);
			});
		}
		workspaceToSkill(workspace, skill);
		if (onupdate) onupdate();
	}
</script>

{#key [skill, $showSummaryItems, $useSymbols]}
	<div class="wrapper">
		<div style="height: 100%; flex-grow: 1;" use:blocklyInit></div>
		{#if $selected}
			{@const data = getSelected()!}
			<div
				class="selected-block"
				style="height: 100%; width: 30em;"
				transition:slide|local={{ axis: 'x', duration: 200 }}
				onintrostart={function (e) {
					const observer = new ResizeObserver(() => {
						Blockly.svgResize(workspace);
					});
					observer.observe(e.target as Element);
				}}
			>
				<div class="component-editor" style="width: 30em;">
					<h2 class:deprecated={data.isDeprecated}><span>{data.name}</span></h2>
					{#if data.description}
						<div class="modal-desc">{@html data.description}</div>
					{/if}
					<hr />
					<div class="component-entry">
						<ProInput
							label="Comment"
							tooltip="[comment] A comment that will be displayed in the skill editor"
							bind:value={data.comment}
						/>
						{#if data instanceof FabledTrigger && data.name !== 'Cast' && data.name !== 'Initialize' && data.name !== 'Cleanup'}
							<BooleanSelectOption
								name="Mana"
								tooltip="[mana] Whether this trigger requires the mana cost to activate"
								bind:data={data.mana}
							/>
							<BooleanSelectOption
								name="Cooldown"
								tooltip="[cooldown] Whether this trigger requires to be off cooldown to activate"
								bind:data={data.cooldown}
							/>
						{:else if data instanceof FabledTarget || data instanceof FabledCondition || data instanceof FabledMechanic}
							<ProInput
								label="Icon Key"
								bind:value={data.iconKey}
								tooltip={'[icon-key] The key used by the component in the Icon Lore. If this is set to "example" and has a value name of "value", it can be referenced using the string "{attr:example.value}"'}
							/>
						{/if}
						{#if data instanceof FabledMechanic}
							<BooleanSelectOption
								name="Counts as Cast"
								tooltip={'[counts] Whether this mechanic running treats the skill as "casted" and will consume mana and start the cooldown. Set to false if it is a mechanic applled when the skill fails such as cleanup or an error message'}
								bind:data={data.countsAsCast}
							/>
						{/if}

						{#each data.data as datum}
							{#if datum.meetsRequirements(data)}
								<datum.component
									bind:data={datum.data}
									name={datum.name}
									tooltip="{datum.key ? '[' + datum.key + '] ' : ''}{datum.tooltip}"
									multiple={datum.multiple}
								/>
							{/if}
						{/each}
					</div>
				</div>
			</div>
		{/if}
	</div>
{/key}

<style>
	.wrapper {
		display: flex;
		flex-direction: row;
		height: 100%;
		width: 100%;
	}

	.selected-block {
		border-left: #424242 solid 3px;
		color: #fff;
		overflow: hidden auto;
	}

	.component-editor {
		padding-inline: 1em;
		box-sizing: border-box;
	}

	.component-editor > h2 {
		text-align: center;
		margin-top: 0;
		margin-bottom: 0.2em;
	}

	.modal-desc {
		text-align: center;
		box-sizing: border-box;
		padding-inline: 1em;
		white-space: break-spaces;
	}

	.deprecated {
		align-items: center;
		display: flex;
	}

	.deprecated > span {
		text-decoration: line-through;
	}

	.deprecated::after {
		text-decoration: unset;
		margin-left: 0.5rem;
		content: 'deprecated';
		font-size: 0.6em;
		color: goldenrod;
	}

	.component-entry {
		display: grid;
		grid-template-columns: calc(50% - 3rem) calc(50% + 3rem);
		width: 100%;
		padding-inline: 0.5rem;
		padding-top: 0.25rem;
	}

	.component-entry {
		display: grid;
		grid-template-columns: calc(50% - 3rem) calc(50% + 3rem);
		width: 100%;
		padding-inline: 0.5rem;
		padding-top: 0.25rem;
	}

	.modal-desc {
		max-width: 100%;
		white-space: break-spaces;
		text-align: center;
	}

	:global(.blocklyMainBackground) {
		stroke-width: 0;
	}
	:global(.blocklyTreeRowContentContainer input:focus-visible) {
		outline: none;
	}
	:global(.blocklyTreeSeparator) {
		border-bottom: solid #979797 1px;
	}
	:global(.block-title) {
		font-weight: bold;
	}
	:global(.blocklyTextarea) {
		color: #000 !important;
	}
</style>
