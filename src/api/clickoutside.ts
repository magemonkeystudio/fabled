export function clickOutside(node: HTMLElement, callback: (event: MouseEvent) => any) {
	const handleClick = (event: MouseEvent) => {
		if (!node.contains(<HTMLElement>event.target)) {
			callback(event);
		}
	};

	document.addEventListener('click', handleClick, true);

	return {
		destroy() {
			document.removeEventListener('click', handleClick, true);
		}
	};
}