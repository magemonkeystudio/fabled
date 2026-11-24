import * as Blockly from 'blockly';

// Ultra-optimized compact format - dynamically sized array only includes non-empty elements
// Format: [blockType, fields?, values?, children?, extraData?] (only includes elements if they have data)
export type CompactBlock = [string, {[key: string]: string}?, {[key: string]: CompactBlock}?, CompactBlock[]?, any?];

export interface ClipboardBlock {
	block: CompactBlock;
}

export class CrossTabClipboard {
	private static readonly STORAGE_KEY = 'fabled_blockly_clipboard';
	private static readonly CLIPBOARD_PREFIX = 'FABLED_BLOCK:';
	// Defaults caches
	private static componentDefaultsCache: Map<string, Record<string, any>> | null = null;
	private static componentBlockDefaultsCache: Map<string, Record<string, any>> | null = null;

	/**
	 * Copy the selected block to system clipboard (with localStorage fallback)
	 */
	static async copySelectedBlock(): Promise<boolean> {
		const selectedBlock = Blockly.getSelected();
		if (!selectedBlock || !(selectedBlock instanceof Blockly.BlockSvg)) {
			return false;
		}

		try {
			const compactBlock = await this.blockToCompact(selectedBlock);

			const clipboardData: ClipboardBlock = {
				block: compactBlock
			};

			const serializedData = JSON.stringify(clipboardData);
			const encodedData = this.encodeToBase64(serializedData);

			const finalClipboardString = this.CLIPBOARD_PREFIX + encodedData;

			try {
				await this.copyToSystemClipboard(finalClipboardString);
				localStorage.setItem(this.STORAGE_KEY, encodedData);
			} catch (error) {
				localStorage.setItem(this.STORAGE_KEY, encodedData);
			}
			
			return true;
		} catch (error) {
			console.error('Failed to copy block:', error);
			return false;
		}
	}

	/**
	 * Paste block from system clipboard (with localStorage fallback) to the specified workspace
	 */
	static async pasteBlock(workspace: Blockly.WorkspaceSvg, onUpdate?: () => void): Promise<string | null> {
		try {
			let clipboardData = await this.getFromSystemClipboard();
			
			if (!clipboardData) {
				clipboardData = this.getFromLocalStorage();
			}
			
			if (!clipboardData) {
				return null;
			}

			Blockly.Events.setGroup(true);

			const newBlock = await this.compactToBlock(clipboardData.block, workspace);
			
			if (!newBlock) {
				return null;
			}

			this.initializeAllBlocksInTree(newBlock);
			this.positionPastedBlock(newBlock, workspace);
			workspace.render();
			Blockly.common.setSelected(newBlock);
			Blockly.Events.setGroup(false);
			this.forceImmediateRender(newBlock, workspace);
			onUpdate?.();

			return newBlock.id;
		} catch (error) {
			console.error('Failed to paste block:', error);
			Blockly.Events.setGroup(false);
			return null;
		}
	}

	/**
	 * Check if there's valid clipboard data available (prioritize system clipboard)
	 */
	static async hasClipboardData(): Promise<boolean> {
		const systemData = await this.getFromSystemClipboard();
		if (systemData) {
			return true;
		}
		
		const localData = this.getFromLocalStorage();
		return localData !== null;
	}

	/**
	 * Clear both system clipboard and localStorage
	 */
	static async clearClipboard(): Promise<void> {
		localStorage.removeItem(this.STORAGE_KEY);
		
		try {
			if (navigator.clipboard && window.isSecureContext) {
				await navigator.clipboard.writeText('');
			}
		} catch (error) {
			// Ignore clipboard clear errors
		}
	}

	/**
	 * Get clipboard data from system clipboard
	 */
	private static async getFromSystemClipboard(): Promise<ClipboardBlock | null> {
		try {
			if (!navigator.clipboard || !window.isSecureContext) {
				return null;
			}

			const text = await navigator.clipboard.readText();
			if (!text || !text.startsWith(this.CLIPBOARD_PREFIX)) {
				return null;
			}

			const base64Data = text.substring(this.CLIPBOARD_PREFIX.length);
			const jsonData = this.decodeFromBase64(base64Data);
			
			return JSON.parse(jsonData) as ClipboardBlock;
		} catch (error) {
			return null;
		}
	}

	/**
	 * Get clipboard data from localStorage (fallback)
	 */
	private static getFromLocalStorage(): ClipboardBlock | null {
		try {
			const stored = localStorage.getItem(this.STORAGE_KEY);
			if (!stored) {
				return null;
			}
			
			const jsonData = this.decodeFromBase64(stored);
			return JSON.parse(jsonData) as ClipboardBlock;
		} catch {
			return null;
		}
	}

	/**
	 * Copy data to system clipboard
	 */
	private static async copyToSystemClipboard(data: string): Promise<boolean> {
		try {
			if (navigator.clipboard && window.isSecureContext) {
				await navigator.clipboard.writeText(data);
				return true;
			} else {
				const textarea = document.createElement('textarea');
				textarea.value = data;
				textarea.style.position = 'fixed';
				textarea.style.opacity = '0';
				textarea.style.left = '-9999px';
				document.body.appendChild(textarea);
				textarea.select();
				const success = document.execCommand('copy');
				document.body.removeChild(textarea);
				return success;
			}
		} catch (error) {
			return false;
		}
	}

	/**
	 * Position the pasted block appropriately
	 */
	private static positionPastedBlock(block: Blockly.BlockSvg, workspace: Blockly.WorkspaceSvg): void {
		const metrics = workspace.getMetrics();
		const blockSize = block.getHeightWidth();
		
		const centerX = metrics.viewLeft + (metrics.viewWidth / 2);
		const centerY = metrics.viewTop + (metrics.viewHeight / 2);
		
		const offsetX = centerX - (blockSize.width / 2);
		const offsetY = centerY - (blockSize.height / 2);
		
		const topBlocks = workspace.getTopBlocks(true);
		let finalX = offsetX;
		let finalY = offsetY;
		
		for (const existingBlock of topBlocks) {
			const existingPos = existingBlock.getRelativeToSurfaceXY();
			const existingSize = existingBlock.getHeightWidth();
			
			// Check if positions overlap
			if (Math.abs(existingPos.x - finalX) < existingSize.width && 
				Math.abs(existingPos.y - finalY) < existingSize.height) {
				// Offset to avoid overlap
				finalX += 50;
				finalY += 50;
			}
		}
		
		block.moveBy(finalX, finalY);
		block.snapToGrid();
	}

	/**
	 * Recursively render all blocks in a block tree to ensure proper display
	 */
	private static renderBlockTree(block: Blockly.BlockSvg): void {
		if (!block) return;

		try {
			// Initialize SVG if not already done
			if (!block.rendered) {
				block.initSvg();
			}

			// Render the current block
			block.render();

			// Force re-render if block appears incomplete
			if (block.getSvgRoot() && !block.getSvgRoot().parentNode) {
				block.initSvg();
				block.render();
			}

			// Render all connected blocks in inputs
			for (const input of block.inputList) {
				if (input.connection && input.connection.targetBlock()) {
					const targetBlock = input.connection.targetBlock() as Blockly.BlockSvg;
					this.renderBlockTree(targetBlock);
				}
			}

			// Render next blocks in chain
			let nextBlock = block.getNextBlock() as Blockly.BlockSvg;
			while (nextBlock) {
				if (!nextBlock.rendered) {
					nextBlock.initSvg();
				}
				nextBlock.render();
				
				// Continue with next block in chain
				nextBlock = nextBlock.getNextBlock() as Blockly.BlockSvg;
			}
		} catch (error) {
			console.warn('Error rendering block tree:', error);
			// Try a simple render as fallback
			try {
				block.render();
			} catch (fallbackError) {
				console.error('Fallback render also failed:', fallbackError);
			}
		}
	}

	/**
	 * Properly initialize all blocks in a tree after creation
	 */
	private static initializeBlockTree(block: Blockly.BlockSvg): void {
		if (!block) return;

		console.debug('Initializing block:', block.type, block.id);

		try {
			// Initialize SVG for the block
			if (!block.rendered) {
				block.initSvg();
			}
			
			// Render the block
			block.render();

			// Force update if block has custom update methods
			try {
				if (typeof (block as any).updateShape === 'function') {
					(block as any).updateShape();
				}
				if (typeof (block as any).updateDisplay === 'function') {
					(block as any).updateDisplay();
				}
				if (typeof (block as any).updatePreview === 'function') {
					(block as any).updatePreview();
				}
			} catch (error) {
				console.warn('  Error calling update methods:', error);
			}

			// Initialize all input blocks
			for (const input of block.inputList) {
				if (input.connection && input.connection.targetBlock()) {
					const targetBlock = input.connection.targetBlock() as Blockly.BlockSvg;
					this.initializeBlockTree(targetBlock);
				}
			}

			// Initialize next blocks
			const nextBlock = block.getNextBlock() as Blockly.BlockSvg;
			if (nextBlock) {
				this.initializeBlockTree(nextBlock);
			}

			// Final render to ensure everything is displayed correctly
			block.render();

		} catch (error) {
			console.warn('Error initializing block:', block.type, error);
		}
	}

	/**
	 * Initialize all blocks in tree and ensure they are immediately visible
	 */
	private static initializeAllBlocksInTree(block: Blockly.BlockSvg): void {
		if (!block) return;

		console.debug('=== INITIALIZING BLOCK TREE ===');
		const workspace = block.workspace as Blockly.WorkspaceSvg;

		// Disable events temporarily to prevent conflicts
		const wasEnabled = Blockly.Events.isEnabled();
		Blockly.Events.disable();

		try {
			// Step 1: Initialize SVG for all blocks first
			this.initializeSvgRecursive(block);

			// Step 2: Render all blocks
			this.renderRecursive(block);

			// Step 3: Force workspace update
			workspace.render();
			workspace.resizeContents();

			console.debug('Block tree initialization complete');
		} catch (error) {
			console.error('Error during block tree initialization:', error);
		} finally {
			// Re-enable events
			if (wasEnabled) {
				Blockly.Events.enable();
			}
		}
	}

	/**
	 * Initialize SVG for all blocks recursively
	 */
	private static initializeSvgRecursive(block: Blockly.BlockSvg): void {
		if (!block) return;

		try {
			// Initialize this block's SVG
			if (!block.rendered) {
				block.initSvg();
				console.debug('Initialized SVG for:', block.type);
			}

			// Initialize all connected input blocks
			for (const input of block.inputList) {
				if (input.connection && input.connection.targetBlock()) {
					const targetBlock = input.connection.targetBlock() as Blockly.BlockSvg;
					this.initializeSvgRecursive(targetBlock);
				}
			}

			// Initialize next blocks
			const nextBlock = block.getNextBlock() as Blockly.BlockSvg;
			if (nextBlock) {
				this.initializeSvgRecursive(nextBlock);
			}
		} catch (error) {
			console.warn('Error initializing SVG for block:', block.type, error);
		}
	}

	/**
	 * Render all blocks recursively
	 */
	private static renderRecursive(block: Blockly.BlockSvg): void {
		if (!block) return;

		try {
			// Render this block
			block.render();
			console.debug('Rendered block:', block.type);

			// Initialize all connected input blocks
			for (const input of block.inputList) {
				if (input.connection && input.connection.targetBlock()) {
					const targetBlock = input.connection.targetBlock() as Blockly.BlockSvg;
					this.renderRecursive(targetBlock);
				}
			}

			// Render next blocks
			const nextBlock = block.getNextBlock() as Blockly.BlockSvg;
			if (nextBlock) {
				this.renderRecursive(nextBlock);
			}
		} catch (error) {
			console.warn('Error rendering block:', block.type, error);
		}
	}

	/**
	 * Force immediate render with multiple attempts
	 */
	private static forceImmediateRender(block: Blockly.BlockSvg, workspace: Blockly.WorkspaceSvg): void {
		console.debug('=== FORCE IMMEDIATE RENDER ===');
		
		try {
			// Force render multiple times with delays to ensure visibility
			const renderAttempts = [0, 10, 50, 100];
			
			renderAttempts.forEach((delay, index) => {
				setTimeout(() => {
					try {
						console.debug(`Render attempt ${index + 1} at ${delay}ms`);
						
						// Re-render the specific block tree
						this.renderRecursive(block);
						
						// Re-render the workspace
						workspace.render();
						workspace.resizeContents();
						
						// Force a layout update
						if (workspace.getMetrics) {
							workspace.getMetrics();
						}
						
						// Try to trigger a repaint
						if (workspace.getParentSvg) {
							const svg = workspace.getParentSvg();
							if (svg && svg.style) {
								svg.style.display = 'none';
								// Force reflow using getBoundingClientRect
								svg.getBoundingClientRect();
								svg.style.display = '';
							}
						}
						
						console.debug(`Render attempt ${index + 1} completed`);
					} catch (error) {
						console.warn(`Render attempt ${index + 1} failed:`, error);
					}
				}, delay);
			});
			
		} catch (error) {
			console.error('Error in force immediate render:', error);
		}
	}

	/**
	 * Force workspace to update and refresh properly
	 */
	private static forceWorkspaceUpdate(workspace: Blockly.WorkspaceSvg): void {
		try {
			// Render the workspace
			workspace.render();
			
			// Resize contents to fit
			workspace.resizeContents();
			
			// Fire workspace change event to update UI
			if (workspace.getTopBlocks().length > 0) {
				const topBlock = workspace.getTopBlocks()[workspace.getTopBlocks().length - 1];
				workspace.fireChangeListener(new Blockly.Events.BlockCreate(topBlock));
			}
			
			// Additional async update for UI refresh
			setTimeout(() => {
				workspace.render();
				workspace.resizeContents();
			}, 0);

		} catch (error) {
			console.warn('Error updating workspace:', error);
			// Fallback: just render
			try {
				workspace.render();
			} catch (fallbackError) {
				console.warn('Fallback workspace render failed:', fallbackError);
			}
		}
	}

	/**
	 * Get a preview of what's in the clipboard (for UI purposes)
	 */
	static async getClipboardPreview(): Promise<string | null> {
		let data = await this.getFromSystemClipboard();
		
		if (!data) {
			data = this.getFromLocalStorage();
		}
		
		if (!data) {
			return null;
		}

		try {
			const blockType = data.block[0];
			
			return `${blockType} (clipboard)`;
		} catch {
			return 'Invalid clipboard data';
		}
	}

	private static async blockToCompact(block: Blockly.BlockSvg, skipNextBlocks: boolean = false): Promise<CompactBlock> {
		const fields: {[key: string]: string} = {};
		const values: {[key: string]: CompactBlock} = {};
		const children: CompactBlock[] = [];
		const extraData: any = {};

		console.debug('Processing block:', block.type, 'ID:', block.id, 'skipNext:', skipNextBlocks);

		// Only capture mutation data if it exists and is significant
		if (block.mutationToDom) {
			try {
				const mutationDom = block.mutationToDom();
				if (mutationDom && mutationDom.hasChildNodes()) {
					extraData.mutation = new XMLSerializer().serializeToString(mutationDom);
					console.debug('  Captured mutation data');
				}
			} catch (error) {
				console.warn('  Error capturing mutation:', error);
			}
		}

		// Only capture extra state if it contains meaningful data
		if (typeof (block as any).saveExtraState === 'function') {
			try {
				const extraState = (block as any).saveExtraState();
				if (extraState && Object.keys(extraState).length > 0) {
					// Filter out redundant data (now aware of block type for better defaults)
					const filteredState = await this.filterExtraState(extraState, block.type);
					if (Object.keys(filteredState).length > 0) {
						extraData.extraState = filteredState;
						console.debug('  Captured filtered extra state:', filteredState);
					}
				}
			} catch (error) {
				console.warn('  Error capturing extra state:', error);
			}
		}

		// Only capture block data if it's meaningful and different from default
		if ((block as any).data !== undefined && (block as any).data !== null) {
			try {
				const blockData = (block as any).data;
				// Skip empty objects or null/undefined
				if (blockData && typeof blockData === 'object' && Object.keys(blockData).length > 0) {
					extraData.data = JSON.parse(JSON.stringify(blockData));
					console.debug('  Captured block data:', extraData.data);
				}
			} catch (error) {
				console.warn('  Error capturing block data:', error);
			}
		}

		// Only capture non-default block properties
		if ((block as any).collapsed === true) {
			extraData.collapsed = true;
		}

		if ((block as any).disabled === true) {
			extraData.disabled = true;
		}

		// Extract fields efficiently - only store non-empty values
		const capturedFields = new Set<string>();
		for (const input of block.inputList) {
			for (const field of input.fieldRow) {
				if (field.name && field.name !== '' && !capturedFields.has(field.name)) {
					try {
						const fieldValue = field.getValue();
						// Only store non-empty, meaningful values
						if (fieldValue !== null && fieldValue !== undefined && fieldValue !== '') {
							fields[field.name] = String(fieldValue);
							capturedFields.add(field.name);
							console.debug('  Field:', field.name, '=', fieldValue);
						}
					} catch (error) {
						console.warn('  Error getting field value:', field.name, error);
					}
				}
			}
		}

		// Extract value inputs and statement inputs
		for (const input of block.inputList) {
			if (input.connection?.targetBlock()) {
				const targetBlock = input.connection.targetBlock() as Blockly.BlockSvg;
				
				console.debug('  Input:', input.name, 'Type:', input.type, 'Target:', targetBlock.type);
				
				if (input.type === 1) { // VALUE input
					values[input.name] = await this.blockToCompact(targetBlock);
				} else if (input.type === 3) { // STATEMENT input
					// For statement inputs, collect all connected blocks in the chain
					const statementBlocks: CompactBlock[] = [];
					let currentBlock = targetBlock;
					let blockCount = 0;
					while (currentBlock) {
						blockCount++;
						console.debug(`    Statement block ${blockCount}: ${currentBlock.type}`);
						// Important: Skip next blocks when processing statement chain items to avoid duplication
						statementBlocks.push(await this.blockToCompact(currentBlock, true));
						currentBlock = currentBlock.getNextBlock() as Blockly.BlockSvg;
					}
					// Use optimized statement chain format
					values[input.name] = ['STATEMENT_CHAIN', {}, {}, statementBlocks] as CompactBlock;
					console.debug('  Statement chain:', input.name, 'Total blocks:', statementBlocks.length);
				}
			}
		}

		// Extract next blocks (only if not skipping and only direct next, not statement chains)
		if (!skipNextBlocks) {
			const nextBlock = block.getNextBlock() as Blockly.BlockSvg;
			if (nextBlock) {
				console.debug('  Next block:', nextBlock.type);
				children.push(await this.blockToCompact(nextBlock));
			}
		} else {
			console.debug('  Skipping next blocks (statement chain processing)');
		}

		// Only include extraData if it has meaningful content
		const hasExtraData = Object.keys(extraData).length > 0;
		const hasChildren = children.length > 0;
		const hasValues = Object.keys(values).length > 0;
		const hasFields = Object.keys(fields).length > 0;

		// Create ultra-compact array - only include elements that have data
		let result: CompactBlock;
		if (hasExtraData) {
			result = [block.type, fields, values, children, extraData];
		} else if (hasChildren) {
			result = [block.type, fields, values, children];
		} else if (hasValues) {
			result = [block.type, fields, values];
		} else if (hasFields) {
			result = [block.type, fields];
		} else {
			result = [block.type];
		}

		console.debug('Ultra-compact result:', {
			type: result[0],
			arrayLength: result.length,
			fieldCount: hasFields ? Object.keys(fields).length : 0,
			valueCount: hasValues ? Object.keys(values).length : 0,
			childCount: hasChildren ? children.length : 0,
			hasExtraData,
			skipNextBlocks
		});

		return result;
	}

	/**
	 * Filter extra state to remove redundant or unnecessary data
	 */
	private static async filterExtraState(extraState: any, blockType?: string): Promise<any> {
		if (!extraState || typeof extraState !== 'object') {
			return {};
		}

		const filtered: any = {};

		// Keep only essential component data
		if (extraState.component_data) {
			const componentData = extraState.component_data;
			const essentialData: any = {};

			// Only keep non-default values
			if (componentData.type && componentData.type !== '') {
				essentialData.type = componentData.type;
			}

			if (componentData.comment && componentData.comment !== '') {
				essentialData.comment = componentData.comment;
			}

			// Filter data object to remove defaults (aware of block type)
			if (componentData.data && typeof componentData.data === 'object') {
				const filteredData = await this.filterDefaultValues(componentData.data, blockType, componentData.type);
				if (Object.keys(filteredData).length > 0) {
					essentialData.data = filteredData;
				}
			}

			// Don't duplicate children data - it's already in the block structure
			// Skip children in extraState as it's redundant

			// Only store component_data if it has meaningful content beyond just type
			const hasMeaningfulContent = 
				(essentialData.comment && essentialData.comment !== '') ||
				(essentialData.data && Object.keys(essentialData.data).length > 0);

			if (hasMeaningfulContent) {
				filtered.component_data = essentialData;
			} else {
				console.debug('  Skipped component_data with only default values for type:', componentData.type);
			}
		}

		// Keep other non-component extra state (but filter default-like values generically)
		for (const [key, value] of Object.entries(extraState)) {
			if (key === 'component_data') continue;
			if (value === null || value === undefined) continue;
			const normalized = this.normalizeForCompare(value);
			if (!this.isDefaultLike(normalized)) {
				filtered[key] = value;
			}
		}

		return filtered;
	}

	/**
	 * Filter out default/empty values from data objects - pure dynamic approach
	 */
	private static async filterDefaultValues(data: any, blockType?: string, componentType?: string): Promise<any> {
		if (!data || typeof data !== 'object') {
			return {};
		}

		const filtered: any = {};
		const defaults = await this.getDefaultsForBlock(blockType, componentType);

		for (const [key, rawValue] of Object.entries(data)) {
			const value = this.normalizeForCompare(rawValue);

			// If we have an explicit default, compare after normalization (deep)
			if (key in defaults) {
				const defaultValue = this.normalizeForCompare((defaults as any)[key]);
				if (this.deepEqual(value, defaultValue)) {
					continue;
				}
			} else {
				// No explicit default known - drop default-like values generically
				if (this.isDefaultLike(value)) {
					continue;
				}
			}

			// Only include if value is meaningful and different from default
			filtered[key] = rawValue;
		}

		return filtered;
	}

	/**
	 * Deep equality comparison for arrays and objects
	 */
	private static deepEqual(a: any, b: any): boolean {
		if (a === b) return true;
		
		if (a == null || b == null) return a === b;
		
		if (Array.isArray(a) && Array.isArray(b)) {
			if (a.length !== b.length) return false;
			for (let i = 0; i < a.length; i++) {
				if (!this.deepEqual(a[i], b[i])) return false;
			}
			return true;
		}
		
		if (typeof a === 'object' && typeof b === 'object') {
			const keysA = Object.keys(a);
			const keysB = Object.keys(b);
			if (keysA.length !== keysB.length) return false;
			for (const key of keysA) {
				if (!keysB.includes(key) || !this.deepEqual(a[key], b[key])) return false;
			}
			return true;
		}
		
		return false;
	}

	/**
	 * Infer component type from block type name - pure pattern-based approach
	 */
	private static inferComponentTypeFromBlockType(blockType: string): string | null {
		if (blockType.startsWith('trigger_')) return 'trigger';
		if (blockType.startsWith('condition_')) return 'condition';
		if (blockType.startsWith('target_')) return 'target';
		if (blockType.startsWith('mechanic_')) return 'mechanic';
		return null;
	}

	private static async compactToBlock(compact: CompactBlock, workspace: Blockly.WorkspaceSvg): Promise<Blockly.BlockSvg | null> {
		// Handle ultra-compact format - extract elements safely
		const blockType = compact[0];
		const fields = compact[1] || {};
		const values = compact[2] || {};
		const children = compact[3] || [];
		const extraData = compact[4];

		console.debug('Creating block:', blockType);
		console.debug('  Fields to set:', Object.keys(fields));
		console.debug('  Values to connect:', Object.keys(values));
		console.debug('  Children to add:', children.length);
		console.debug('  Extra data:', extraData ? Object.keys(extraData) : 'none');

		const block = workspace.newBlock(blockType) as Blockly.BlockSvg;
		if (!block) {
			console.error('Failed to create block of type:', blockType);
			return null;
		}

		// Initialize SVG immediately after creation
		try { block.initSvg(); } catch { /* noop */ }

		// Apply mutation first if available (this can change the block structure)
		if (extraData?.mutation) {
			try {
				const parser = new DOMParser();
				const mutationDom = parser.parseFromString(extraData.mutation, 'text/xml').documentElement;
				if ((block as any).domToMutation && mutationDom) (block as any).domToMutation(mutationDom);
			} catch (error) { console.warn('  Error applying mutation:', error); }
		}

		// Apply extra state if available
		if (extraData?.extraState && typeof (block as any).loadExtraState === 'function') {
			try {
				const completeState = await this.mergeWithDefaults(extraData.extraState, blockType);
				(block as any).loadExtraState(completeState);
			} catch (error) { console.warn('  Error applying extra state:', error); }
		} else if (typeof (block as any).loadExtraState === 'function') {
			try {
				const componentType = this.inferComponentTypeFromBlockType(blockType);
				if (componentType) {
					const defaults = await this.getDefaultsForBlock(blockType, componentType);
					(block as any).loadExtraState({ component_data: { type: componentType, comment: '', data: defaults } });
				}
			} catch (error) { console.warn('  Error applying reconstructed state:', error); }
		}

		// Restore block data if available
		if (extraData?.data !== undefined) {
			try { (block as any).data = JSON.parse(JSON.stringify(extraData.data)); } catch {}
		}

		// Apply block properties (only if they were explicitly set)
		if (extraData?.collapsed === true) { try { (block as any).setCollapsed(true); } catch {} }
		if (extraData?.disabled === true) { try { (block as any).setEnabled(false); } catch {} }

		// Set fields
		for (const [fieldName, value] of Object.entries(fields)) {
			try {
				const field = block.getField(fieldName);
				if (field) field.setValue(value);
				else console.warn('  Field not found:', fieldName);
			} catch (error) { console.warn('  Error setting field:', fieldName, error); }
		}

		// Set value inputs and statement inputs
		for (const [inputName, valueCompact] of Object.entries(values)) {
			const input = block.getInput(inputName);
			if (!input || !input.connection) { console.warn('  Input not found or no connection:', inputName); continue; }

			if (Array.isArray(valueCompact) && valueCompact[0] === 'STATEMENT_CHAIN') {
				const statementBlocks = valueCompact[3] as CompactBlock[];
				console.debug(`  Processing statement chain for input ${inputName}: ${statementBlocks.length} blocks`);
				if (statementBlocks.length > 0) {
					let previousBlock: Blockly.BlockSvg | null = null;
					for (let i = 0; i < statementBlocks.length; i++) {
						const statementCompact = statementBlocks[i];
						console.debug(`    Creating statement block ${i + 1}/${statementBlocks.length}: ${statementCompact[0]}`);
						const statementBlock = await this.compactToBlock(statementCompact, workspace);
						if (!statementBlock) {
							console.warn(`    Failed to create statement block ${i + 1}`);
							continue;
						}
						
						if (previousBlock === null) {
							// Connect first block to the statement input
							if (input.connection && statementBlock.previousConnection) {
								try { 
									input.connection.connect(statementBlock.previousConnection);
									console.debug(`    Connected first statement block: ${statementBlock.type}`);
								} catch (e) { console.warn('    Connect first statement failed', e); }
							}
						} else {
							// Connect subsequent blocks in chain
							if (previousBlock.nextConnection && statementBlock.previousConnection) {
								try { 
									previousBlock.nextConnection.connect(statementBlock.previousConnection);
									console.debug(`    Connected next statement block: ${statementBlock.type}`);
								} catch (e) { console.warn('    Connect next statement failed', e); }
							}
						}
						previousBlock = statementBlock;
					}
				}
			} else {
				// Handle value input
				console.debug(`  Processing value input ${inputName}: ${valueCompact[0]}`);
				const valueBlock = await this.compactToBlock(valueCompact, workspace);
				if (valueBlock && valueBlock.outputConnection && input.connection) {
					try { 
						input.connection.connect(valueBlock.outputConnection);
						console.debug(`    Connected value block: ${valueBlock.type}`);
					} catch (e) { console.warn('    Connect value failed', e); }
				} else if (valueBlock) {
					console.warn('    Value block missing output connection:', (valueBlock as any)?.type);
				}
			}
		}

		// Add next blocks (only if children exist)
		if (children.length > 0 && block.nextConnection) {
			const nextBlock = await this.compactToBlock(children[0], workspace);
			if (nextBlock && nextBlock.previousConnection) {
				try { block.nextConnection.connect(nextBlock.previousConnection); } catch (e) { console.warn('  Connect next failed', e); }
			}
		}

		// Final updates
		try { if (typeof (block as any).updateShape === 'function') (block as any).updateShape(); } catch {}
		try { block.render(); } catch {}
		return block;
	}

	/**
	 * Load component defaults from the Blockly workspace or component registry
	 */
	private static async loadComponentDefaults(): Promise<void> {
		if (this.componentDefaultsCache && this.componentBlockDefaultsCache) return;
		if (!this.componentDefaultsCache) this.componentDefaultsCache = new Map();
		if (!this.componentBlockDefaultsCache) this.componentBlockDefaultsCache = new Map();
		try {
			const workspace = Blockly.getMainWorkspace();
			if (workspace && (workspace as any).componentRegistry) { this.extractDefaultsFromRegistry((workspace as any).componentRegistry); return; }
			if (typeof window !== 'undefined' && (window as any).FabledComponentRegistry) { this.extractDefaultsFromRegistry((window as any).FabledComponentRegistry); return; }
			console.debug('Component registry not available, using dynamic/minimal defaults');
		} catch (error) { console.warn('Failed to load component defaults from registry:', error); }
	}

	/** Extract defaults from component registry data */
	private static extractDefaultsFromRegistry(registry: any): void {
		if (!registry || !Array.isArray(registry)) { console.warn('Invalid component registry format'); return; }
		if (!this.componentDefaultsCache) this.componentDefaultsCache = new Map();
		if (!this.componentBlockDefaultsCache) this.componentBlockDefaultsCache = new Map();
		for (const component of registry) {
			if (!component || !component.key || !component.type) continue;
			const componentKey = String(component.key).toLowerCase();
			const componentType = String(component.type).toLowerCase();
			const defaults: Record<string, any> = {};
			if (component.options && Array.isArray(component.options)) {
				for (const option of component.options) {
					if (option.key && Object.prototype.hasOwnProperty.call(option, 'default')) defaults[option.key] = option.default;
					else if (option.key) {
						switch (option.type) {
							case 'int':
							case 'double': defaults[option.key] = 0; break;
							case 'string': defaults[option.key] = ''; break;
							case 'bool': defaults[option.key] = false; break;
							default: defaults[option.key] = '';
						}
					}
				}
			}
			this.componentBlockDefaultsCache!.set(componentKey, defaults);
			const typeDefaults = this.componentDefaultsCache!.get(componentType) || {};
			this.componentDefaultsCache!.set(componentType, { ...typeDefaults, ...defaults });
		}
	}

	/** Get default values for a specific blockType, with dynamic loading/fallback */
	private static async getDefaultsForBlock(blockType?: string, componentType?: string): Promise<Record<string, any>> {
		await this.loadComponentDefaults();
		const normalizedBlockType = blockType?.toLowerCase();
		if (normalizedBlockType && this.componentBlockDefaultsCache?.has(normalizedBlockType)) {
			return { ...(this.componentBlockDefaultsCache.get(normalizedBlockType) as Record<string, any>) };
		}
		const normalizedType = componentType?.toLowerCase();
		if (normalizedType && this.componentDefaultsCache?.has(normalizedType)) {
			return { ...(this.componentDefaultsCache.get(normalizedType) as Record<string, any>) };
		}
		if (normalizedBlockType) {
			try {
				const ws = Blockly.getMainWorkspace() as Blockly.WorkspaceSvg | null;
				if (ws) {
					const eventsEnabled = Blockly.Events.isEnabled();
					try {
						if (eventsEnabled) Blockly.Events.disable();
						const temp = ws.newBlock(normalizedBlockType) as any;
						let defaults: Record<string, any> = {};
						if (temp && typeof temp.saveExtraState === 'function') {
							const state = temp.saveExtraState();
							if (state?.component_data?.data) defaults = this.normalizeForCompare(state.component_data.data);
						}
						if (Object.keys(defaults).length > 0) {
							this.componentBlockDefaultsCache!.set(normalizedBlockType, defaults);
							return { ...defaults };
						}
					} finally {
						try {
							const all = ws.getTopBlocks(false);
							for (const b of all) {
								if ((b as any).type?.toLowerCase?.() === normalizedBlockType && !(b as any).rendered) { b.dispose(false); break; }
							}
						} catch {}
						if (eventsEnabled) Blockly.Events.enable();
					}
				}
			} catch (e) { console.warn('Dynamic defaults inference failed for', normalizedBlockType, e); }
		}
		return {};
	}

	/** Get default values for a specific component type */
	private static async getDefaultsForComponentType(componentType: string): Promise<Record<string, any>> {
		return this.getDefaultsForBlock(undefined, componentType);
	}

	/** Merge filtered extra state with defaults to restore complete state */
	private static async mergeWithDefaults(filteredState: any, blockType?: string): Promise<any> {
		if (!filteredState || typeof filteredState !== 'object') return {};
		const complete: any = {};
		if (filteredState.component_data) {
			const componentData = filteredState.component_data;
			const defaults = await this.getDefaultsForBlock(blockType, componentData.type);
			complete.component_data = {
				type: componentData.type || '',
				comment: componentData.comment || '',
				data: componentData.data ? { ...defaults, ...componentData.data } : { ...defaults }
			};
		}
		for (const [key, value] of Object.entries(filteredState)) { if (key !== 'component_data') complete[key] = value; }
		return complete;
	}

	private static encodeToBase64(str: string): string {
		try { return btoa(unescape(encodeURIComponent(str))); } catch { throw new Error('Base64 encoding failed'); }
	}
	private static decodeFromBase64(base64Str: string): string {
		try { return decodeURIComponent(escape(atob(base64Str))); } catch { throw new Error('Base64 decoding failed'); }
	}

	// === Normalization helpers (generic, no hardcoded keys) ===
	private static normalizeForCompare(value: any): any {
		if (typeof value === 'string') {
			const trimmed = value.trim();
			const lower = trimmed.toLowerCase();
			if (lower === 'true') return true;
			if (lower === 'false') return false;
			if (/^[-+]?\d+(?:\.\d+)?$/.test(trimmed)) { const n = Number(trimmed); if (!Number.isNaN(n)) return n; }
			return trimmed;
		}
		if (Array.isArray(value)) return value.map(v => this.normalizeForCompare(v));
		if (value && typeof value === 'object') { const out: any = {}; for (const [k, v] of Object.entries(value)) out[k] = this.normalizeForCompare(v); return out; }
		return value;
	}
	private static isDefaultLike(value: any): boolean {
		if (value === null || value === undefined) return true;
		if (typeof value === 'boolean') return value === false;
		if (typeof value === 'number') return value === 0;
		if (typeof value === 'string') return value === '';
		if (Array.isArray(value)) return value.length === 0;
		if (typeof value === 'object') return Object.keys(value).length === 0;
		return false;
	}
}
