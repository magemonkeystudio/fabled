<script lang='ts' generics='T'>
	/**
	 * VirtualList.svelte
	 * Renders only the items visible in the viewport + a buffer.
	 * Works as a self-contained scroll container.
	 *
	 * Props:
	 *   items       — full array of items
	 *   itemHeight  — estimated px height of each row (default: 44)
	 *   buffer      — extra rows above/below viewport to pre-render (default: 8)
	 *   key         — function returning a unique key for each item (default: item itself)
	 *   children    — snippet: { item: T, index: number }
	 *
	 * Usage:
	 *   <VirtualList {items} itemHeight={44} let:entry>
	 *     <SidebarEntry data={entry.item} />
	 *   </VirtualList>
	 */
	import { onMount } from 'svelte';

	interface Props {
		items: T[];
		itemHeight?: number;
		buffer?: number;
		key?: (item: T, index: number) => unknown;
		children: import('svelte').Snippet<[{ item: T; index: number }]>;
	}

	let {
		items,
		itemHeight = 44,
		buffer     = 8,
		key        = (_item: T, i: number) => i,
		children
	}: Props = $props();

	let containerEl: HTMLDivElement | undefined = $state();
	let containerH = $state(400);
	let scrollTop  = $state(0);

	// Recompute whenever items, scroll, or container size changes
	const startIdx     = $derived(Math.max(0, Math.floor(scrollTop / itemHeight) - buffer));
	const endIdx       = $derived(Math.min(items.length, startIdx + Math.ceil(containerH / itemHeight) + buffer * 2));
	const visibleItems = $derived(items.slice(startIdx, endIdx));
	const totalH       = $derived(items.length * itemHeight);
	const offsetY      = $derived(startIdx * itemHeight);

	function onScroll(e: Event) {
		scrollTop = (e.currentTarget as HTMLElement).scrollTop;
	}

	onMount(() => {
		if (!containerEl) return;
		const ro = new ResizeObserver(entries => {
			containerH = entries[0].contentRect.height;
		});
		ro.observe(containerEl);
		containerH = containerEl.clientHeight;
		return () => ro.disconnect();
	});
</script>

<div
	bind:this={containerEl}
	class='vl-container'
	onscroll={onScroll}
>
	<!-- Total height spacer keeps the scrollbar correct -->
	<div class='vl-spacer' style:height='{totalH}px'>
		<!-- Shift the visible window to the correct scroll offset -->
		<!-- NOTE: use `top` not `transform` — CSS transform creates a new containing block
		     for position:fixed children (e.g. Modal backdrops), breaking their viewport positioning. -->
		<div class='vl-window' style:top='{offsetY}px'>
			{#each visibleItems as item, localIdx (key(item, startIdx + localIdx))}
				{@render children({ item, index: startIdx + localIdx })}
			{/each}
		</div>
	</div>
</div>

<style>
	.vl-container {
		height: 100%;
		overflow-y: auto;
		overflow-x: hidden;
		position: relative;
	}

	.vl-spacer {
		position: relative;
		width: 100%;
	}

	.vl-window {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
	}
</style>
