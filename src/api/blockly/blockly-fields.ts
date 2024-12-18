import * as Blockly from 'blockly/core';

export class EmbedField extends Blockly.Field<string> {
	private class: string | null = null;
	override EDITABLE = false;
	override maxDisplayLength = Infinity;
	private rawValue: string = "";

	constructor(
		value?: string | typeof Blockly.Field.SKIP_SETUP,
		textClass?: string,
		config?: Blockly.FieldLabelConfig,
	) {
		super(Blockly.Field.SKIP_SETUP);

		if (value === Blockly.Field.SKIP_SETUP) return;
		if (config) {
			this.configure_(config);
		} else {
			this.class = textClass || null;
		}
		this.setValue(EmbedField.simply(value || ""));
		this.rawValue = value || "";
	}
	static simply(value: string) {
		return value.replace(/&([a-fomnlr]|#[A-Fa-f0-9]{6})/g, "");
	}
	protected override configure_(config: Blockly.FieldLabelConfig) {
		super.configure_(config);
		if (config.class) this.class = config.class;
	}

	override initView() {
		this.createTextElement_();
		if (this.class) {
			this.getTextElement().classList.add(this.class);
		}
		this.getTextElement().innerHTML = "";
		const formatted = formatText(this.rawValue || "", "&");
		formatted.forEach(({ chunk, color, style }) => {
			const el = document.createElementNS("http://www.w3.org/2000/svg", "tspan");
			el.textContent = chunk;
			el.setAttribute("fill", color);
			el.setAttribute("font-weight", style.includes("bold") ? "bold" : "normal");
			this.getTextElement().appendChild(el);
		})
		this.updateSize_();
	}

	override updateSize_(a?: number) {
		const b = this.getConstants()!;
		a = void 0 !== a ? a : this.isFullBlockField() ? 0 : this.getConstants()!.FIELD_BORDER_RECT_X_PADDING;
		let c = 2 * a, d = b.FIELD_TEXT_HEIGHT, e = 0;
		this.textElement_ && (e = Blockly.utils.dom.getFastTextWidth(this.textElement_, b.FIELD_TEXT_FONTSIZE, "bold", b.FIELD_TEXT_FONTFAMILY), c += e);
		this.isFullBlockField() || (d = Math.max(d, b.FIELD_BORDER_RECT_HEIGHT));
		this.size_.height = d;
		this.size_.width = c;
		this.positionTextElement_(a, e);
		this.positionBorderRect_();
	}
}

function formatText(str: string, separator: string = "&"): { chunk: string, color: string, style: string[] }[] {
    str = str.replace(/\s+/g, ' ');
    const chars: { chunk: string, color: string, style: string[] }[] = [];
    let currentColor = 'f'; // Default color
    let currentStyles: string[] = [];

    const minecraftColors: { [key: string]: string } = {
        '0': '000000', // Black
        '1': '0000AA', // Dark Blue
        '2': '00AA00', // Dark Green
        '3': '00AAAA', // Dark Aqua
        '4': 'AA0000', // Dark Red
        '5': 'AA00AA', // Dark Purple
        '6': 'FFAA00', // Gold
        '7': 'AAAAAA', // Gray
        '8': '555555', // Dark Gray
        '9': '5555FF', // Blue
        'a': '55FF55', // Green
        'b': '55FFFF', // Aqua
        'c': 'FF5555', // Red
        'd': 'FF55FF', // Light Purple
        'e': 'FFFF55', // Yellow
        'f': 'FFFFFF'  // White
    };

   const styleMap: { [key: string]: string } = {
       'l': 'bold',
       'm': 'strikethrough',
       'n': 'underline',
       'o': 'italic'
   };

   for (let i = 0; i < str.length; i++) {
       if (str[i] === separator && i + 1 < str.length) {
           const code = str[i + 1].toLowerCase();
           if (styleMap[code]) {
               if (!currentStyles.includes(styleMap[code])) currentStyles.push(styleMap[code]);
               i++;
           } else if (code === 'r') {
               currentColor = 'f';
               currentStyles = [];
               i++;
           } else if (code === '#' && /[0-9A-Fa-f]{6}/.test(str.substring(i + 2, i + 8))) {
               currentColor = `#${str.substring(i + 2, i + 8)}`;
               currentStyles = [];
               i += 7;
           } else if (minecraftColors[code]) {
               currentColor = minecraftColors[code];
               currentStyles = [];
               i++;
           } else {
               chars.push(
                   { chunk: str[i], color: currentColor, style: [...currentStyles] },
                   { chunk: str[i + 1], color: currentColor, style: [...currentStyles] }
               );
               i++;
           }
           continue;
       }

       chars.push({
           chunk: str[i],
           color: currentColor.startsWith('#') ? currentColor : `#${currentColor}`,
           style: [...currentStyles]
       });
   }
    const mergedChars = chars.reduce((acc, curr, i) => {
        if (i === 0) return acc;
        const prev = acc[acc.length - 1];
        if (prev.color === curr.color && JSON.stringify(prev.style) === JSON.stringify(curr.style)) {
            prev.chunk += curr.chunk;
        } else {
            acc.push({ ...curr });
        }
        return acc;
    }, [{ ...chars[0] }]);

    return mergedChars;
}