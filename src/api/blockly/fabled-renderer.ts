import * as Blockly from 'blockly';

const FabledTheme = Blockly.Theme.defineTheme('fabled', {
	name: 'fabled',
	base: Blockly.Themes.Zelos,
	componentStyles: {
		workspaceBackgroundColour: '#111',
		toolboxBackgroundColour: '#222',
		toolboxForegroundColour: '#ddd',
		flyoutBackgroundColour: '#181818',
		flyoutForegroundColour: '#bbb',
		flyoutOpacity: 0.9,
		scrollbarColour: '#fff',
		scrollbarOpacity: 0.1,
		insertionMarkerColour: '#fff',
		insertionMarkerOpacity: 0.3,
		markerColour: '#fc3',
		cursorColour: '#d0d0d0'
	},
	fontStyle: {
		family: 'Monaco, Menlo, Ubuntu Mono, monospace',
		weight: 'normal',
		size: 12
	},
	startHats: false
});

class FabledConstantsProvider extends Blockly.blockRendering.ConstantProvider {
	constructor() {
		super();
		this.NOTCH_WIDTH = 0;
		this.NOTCH_HEIGHT = 0;
		this.CORNER_RADIUS = 4;
		this.STATEMENT_INPUT_PADDING_LEFT = 18;
		this.MIN_BLOCK_HEIGHT = 26;
		this.MEDIUM_PADDING = 8;
		this.LARGE_PADDING = 16;
		this.FIELD_BORDER_RECT_RADIUS = 2;
		this.FIELD_BORDER_RECT_X_PADDING = 4;
		this.FIELD_BORDER_RECT_Y_PADDING = 2;
	}
}

class FabledRenderer extends Blockly.blockRendering.Renderer {
	constructor(name: string) {
		super(name);
	}

	protected makeConstants_(): Blockly.blockRendering.ConstantProvider {
		return new FabledConstantsProvider();
	}
}

Blockly.blockRendering.register('fabled', FabledRenderer);

export function initializeFabledRenderer(workspace?: Blockly.WorkspaceSvg): void {
	if (workspace) {
		workspace.setTheme(FabledTheme);
	}
}

export { FabledTheme };
