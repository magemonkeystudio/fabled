export function numberOnly(node: HTMLElement, opts: { intMode?: boolean, enabled?: boolean } = {
	intMode: false,
	enabled: true
}) {
	const intMode = opts.intMode;
	const enabled = opts.enabled;

	const checkNumber = (e: KeyboardEvent) => {
		if (!enabled) return;
		const target: HTMLInputElement = <HTMLInputElement>e.target;
		const value                    = target.value;
		if (!value) return;
		if ((intMode && !/^-?\d*$/.test(value))
			|| !/^-?\d*.\d*$/.test(value)) {
			e.preventDefault();
			const startsWithNeg = value.startsWith('-');
			target.value        = (startsWithNeg ? '-' : '') + value.replaceAll(/\D/g, '');
		}
	};

	const checkPress = (e: KeyboardEvent) => {
		if (!enabled) return;

		if (intMode && !/[-\d]/.test(e.key)
			|| !/[-\d.]/.test(e.key)) {
			e.preventDefault();
		}
	};

	node.addEventListener('keyup', checkNumber, true);
	node.addEventListener('keypress', checkPress, true);

	return {
		destroy() {
			node.removeEventListener('keyup', checkNumber, true);
			node.removeEventListener('keypress', checkPress, true);
		}
	};
}