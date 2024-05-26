interface SquishParams {
	delay?: number;
	duration?: number;
}

export const squish = (node: Element, {
	delay = 0,
	duration = 500
}: SquishParams = {}) => {
	const width   = getComputedStyle(node).width;
	const matcher = width.match(/([\d.]+)(.*)/);
	if (!matcher) {
		return {
			delay,
			duration,
			css: (t: number) => `max-width: unset; width: ${t * 100}%;`
		};
	}
	const w = Number.parseFloat(matcher[1]);

	return {
		delay,
		duration,
		css: (t: number) => {
			return `max-width: unset; width: ${t * w}${matcher[2]}; min-width: unset;`;
		}
	};
};

export const squash = (node: Element, {
	delay = 0,
	duration = 500
}) => {
	const height  = getComputedStyle(node).height;
	const matcher = height.match(/([\d.]+)(.*)/);
	if (!matcher) {
		return {
			delay,
			duration,
			css: (t: number) => `max-height: unset; height: ${t * 100}%;`
		};
	}
	const w = Number.parseFloat(matcher[1]);

	return {
		delay,
		duration,
		css: (t: number) => {
			return `max-height: unset; height: ${t * w}${matcher[2]}; min-height: unset;`;
		}
	};
};