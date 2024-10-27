<script lang='ts'>
	interface Props {
		title?: string;
		icon?: string;
		color?: string;
		children?: import('svelte').Snippet;
		onclick?: (e: MouseEvent) => void;
		onkeypress?: (e: KeyboardEvent) => void;
	}

	let { title = '', icon = '', color = 'red', children, onclick, onkeypress }: Props = $props();

	const keypress = (e: KeyboardEvent) => {
		if (e.key === 'Enter') {
			e.stopPropagation();
			(<HTMLElement>e.target).click();
		}

		onkeypress?.(e);
	};
</script>

<div
	class='control'
	style:--color={color}
	{title}
	tabindex='0'
	role='button'
	{onclick}
	onkeypress={keypress}
>
	{#if icon}
		<span class='material-symbols-rounded'>{icon}</span>
	{/if}
	{@render children?.()}
</div>

<style>
    .control {
        user-select: none;
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        background: var(--color);
        padding: 0.25rem;
        border-radius: 0.4rem;
        margin: 0.15rem;
        border: 2px solid rgba(0, 0, 0, 0.4);
        color: rgba(0, 0, 0, 0.4);
        transition: color 0.3s ease,
        background 0.3s ease;
    }

    .control:hover {
        cursor: pointer;
        box-shadow: inset 0 0 0.5rem rgba(0, 0, 0, 0.4);
        background: color-mix(in srgb, var(--color), black 10%);
    }

    .control:active {
        background: color-mix(in srgb, var(--color), black 20%);
    }
</style>
