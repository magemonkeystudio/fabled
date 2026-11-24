import * as Blockly from 'blockly';

export interface DuplicatorState {
	savedPositions: Map<string, { x: number; y: number }>;
}

export class BlockDuplicator {
	private state: DuplicatorState;
	private workspace: Blockly.WorkspaceSvg;
	private onUpdateCallback?: () => void;

	constructor(workspace: Blockly.WorkspaceSvg, onUpdate?: () => void) {
		this.workspace = workspace;
		this.onUpdateCallback = onUpdate;
		this.state = { savedPositions: new Map() };
		this.setupEventListeners();
	}

	duplicateSelectedBlock(): string | null {
		const selectedBlock = Blockly.getSelected();
		if (!selectedBlock || !(selectedBlock instanceof Blockly.BlockSvg)) return null;
		try {
			Blockly.Events.setGroup(true);
			
			// Save extra state before creating XML
			const extraState = this.collectExtraState(selectedBlock);
			
			const blockXml = this.blockToDomWithoutNext(selectedBlock);
			const duplicatedBlock = blockXml instanceof Element ? Blockly.Xml.domToBlock(blockXml, this.workspace) as Blockly.BlockSvg : Blockly.Xml.domToBlock(blockXml.firstElementChild!, this.workspace) as Blockly.BlockSvg;
			if (!duplicatedBlock) return null;
			
			duplicatedBlock.initSvg();
			duplicatedBlock.render();
			
			// Restore extra state after creating block
			this.restoreExtraState(duplicatedBlock, extraState);
			
			this.insertDuplicateAfterSelected(selectedBlock, duplicatedBlock);
			Blockly.common.setSelected(duplicatedBlock);
			this.onUpdateCallback?.();
			return duplicatedBlock.id;
		} catch {
			return null;
		} finally {
			Blockly.Events.setGroup(false);
		}
	}

	private blockToDomWithoutNext(block: Blockly.BlockSvg): Element | DocumentFragment {
		const nextBlock = block.nextConnection?.targetBlock() as Blockly.BlockSvg | null;
		if (nextBlock) block.nextConnection?.disconnect();
		const xml = Blockly.Xml.blockToDom(block);
		if (nextBlock) block.nextConnection?.connect(nextBlock.previousConnection);
		return xml;
	}

	private insertDuplicateAfterSelected(originalBlock: Blockly.BlockSvg, duplicatedBlock: Blockly.BlockSvg): void {
		const nextBlock = originalBlock.nextConnection?.targetBlock();
		originalBlock.nextConnection?.disconnect();
		if (!this.tryConnectBlocks(originalBlock, duplicatedBlock)) this.positionDuplicateManually(originalBlock, duplicatedBlock);
		if (nextBlock && !this.tryConnectBlocks(duplicatedBlock, nextBlock)) this.positionDuplicateManually(originalBlock, duplicatedBlock);
		this.workspace.render();
	}

	private tryConnectBlocks(blockA: Blockly.BlockSvg, blockB: Blockly.BlockSvg): boolean {
		try {
			blockA.nextConnection?.connect(blockB.previousConnection);
			return true;
		} catch {
			return false;
		}
	}

	private positionDuplicateManually(originalBlock: Blockly.BlockSvg, duplicatedBlock: Blockly.BlockSvg): void {
		const originalPos = originalBlock.getRelativeToSurfaceXY();
		const blockHeight = originalBlock.getHeightWidth().height;
		const duplicatePos = duplicatedBlock.getRelativeToSurfaceXY();
		duplicatedBlock.moveBy(originalPos.x - duplicatePos.x, originalPos.y + blockHeight + 20 - duplicatePos.y);
	}

	private saveBlockPosition(blockId: string): void {
		const block = this.workspace.getBlockById(blockId);
		if (block) this.state.savedPositions.set(blockId, block.getRelativeToSurfaceXY());
	}

	private setupEventListeners(): void {
		this.workspace.addChangeListener((e: any) => {
			if (e.type === 'move' && e.blockId) this.saveBlockPosition(e.blockId);
		});
	}

	/**
	 * Collect extra state from a block and all its children
	 */
	private collectExtraState(block: Blockly.BlockSvg): { [blockId: string]: any } {
		const extraState: { [blockId: string]: any } = {};
		
		const collectFromBlock = (b: Blockly.BlockSvg) => {
			// Save extra state if block has the method
			if (typeof (b as any).saveExtraState === 'function') {
				try {
					const state = (b as any).saveExtraState();
					if (state) {
						extraState[b.id] = state;
					}
				} catch (error) {
					console.warn('Failed to save extra state for block:', b.id, error);
				}
			}
			
			// Process child blocks
			const childBlocks = b.getChildren(false);
			childBlocks.forEach(collectFromBlock);
		};
		
		collectFromBlock(block);
		return extraState;
	}

	/**
	 * Restore extra state to a block and all its children
	 */
	private restoreExtraState(block: Blockly.BlockSvg, extraStateMap: { [blockId: string]: any }): void {
		const restoreToBlock = (b: Blockly.BlockSvg) => {
			// Find matching state (simple heuristic by position in tree)
			const matchingStates = Object.entries(extraStateMap);
			if (matchingStates.length > 0) {
				const state = matchingStates[0][1];
				// Remove used state
				delete extraStateMap[matchingStates[0][0]];
				
				// Load state if block supports it
				if (state && typeof (b as any).loadExtraState === 'function') {
					try {
						(b as any).loadExtraState(state);
					} catch (error) {
						console.warn('Failed to load extra state for block:', b.id, error);
					}
				}
			}
			
			// Process child blocks
			const childBlocks = b.getChildren(false);
			childBlocks.forEach(restoreToBlock);
		};
		
		restoreToBlock(block);
	}

	dispose(): void {}
}
