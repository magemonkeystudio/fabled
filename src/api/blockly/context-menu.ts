import * as Blockly from 'blockly';
import { copySelectedBlock, pasteBlock, hasClipboardData } from '$api/blockly/blockly.svelte';

export interface ContextMenuOption {
	text: string;
	enabled: boolean;
	callback: () => void;
}

/**
 * Add copy/paste options to Blockly context menu
 */
export function addCopyPasteContextMenu(workspace: Blockly.WorkspaceSvg, onUpdate?: () => void) {
	// Remove any existing registrations first to prevent duplicates
	removeCopyPasteContextMenu();

	// Register copy option
	Blockly.ContextMenuRegistry.registry.register({
		id: 'fabled_copy_block',
		weight: 200,
		displayText: 'Copy (Cross-tab)',
		preconditionFn: (scope: any) => {
			// Only show for blocks
			return scope.block ? 'enabled' : 'hidden';
		},
		callback: (scope: any) => {
			if (scope.block) {
				Blockly.common.setSelected(scope.block);
				copySelectedBlock().then(success => {
					if (success) {
						console.log('Block copied to system clipboard (base64 encoded)');
					}
				});
			}
		},
		scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK,
	});

	// Register paste option  
	Blockly.ContextMenuRegistry.registry.register({
		id: 'fabled_paste_block',
		weight: 201,
		displayText: 'Paste (Cross-tab)',
		preconditionFn: (scope: any) => {
			// Always show enabled since clipboard check is async
			return 'enabled';
		},
		callback: async (scope: any) => {
			const hasData = await hasClipboardData();
			if (hasData) {
				const newBlockId = await pasteBlock(workspace, onUpdate);
				if (newBlockId) {
					console.log('Block pasted from cross-tab clipboard (base64 decoded)');
				}
			}
		},
		scopeType: Blockly.ContextMenuRegistry.ScopeType.WORKSPACE,
	});

	// Also register for block scope (paste after block)
	Blockly.ContextMenuRegistry.registry.register({
		id: 'fabled_paste_after_block',
		weight: 202,
		displayText: 'Paste After (Cross-tab)',
		preconditionFn: (scope: any) => {
			// Show if we're on a block - clipboard check is done in callback
			return scope.block ? 'enabled' : 'hidden';
		},
		callback: async (scope: any) => {
			if (scope.block) {
				const hasData = await hasClipboardData();
				if (hasData) {
					const newBlockId = await pasteBlock(workspace, onUpdate);
					if (newBlockId) {
						// Try to connect the pasted block after the selected block
						const selectedBlock = scope.block;
						const pastedBlock = workspace.getBlockById(newBlockId);
						
						if (pastedBlock && selectedBlock.nextConnection && pastedBlock.previousConnection) {
							// Disconnect any existing next block
							const existingNext = selectedBlock.nextConnection.targetBlock();
							if (existingNext) {
								selectedBlock.nextConnection.disconnect();
							}
							
							// Connect the pasted block
							selectedBlock.nextConnection.connect(pastedBlock.previousConnection);
							
							// If there was an existing next block, connect it after the pasted block
							if (existingNext && pastedBlock.nextConnection) {
								pastedBlock.nextConnection.connect(existingNext.previousConnection);
							}
						}
						
						console.log('Block pasted after selected block (base64 decoded)');
					}
				}
			}
		},
		scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK,
	});
}

/**
 * Remove custom context menu options
 */
export function removeCopyPasteContextMenu() {
	try {
		Blockly.ContextMenuRegistry.registry.unregister('fabled_copy_block');
		Blockly.ContextMenuRegistry.registry.unregister('fabled_paste_block');
		Blockly.ContextMenuRegistry.registry.unregister('fabled_paste_after_block');
	} catch (e) {
		// Ignore errors if items don't exist
	}
}
