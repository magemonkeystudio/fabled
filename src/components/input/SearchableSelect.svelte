<script lang="ts">
	import { run } from 'svelte/legacy';

	import { fly } from 'svelte/transition';
	import { flip } from 'svelte/animate';
	import { clickOutside } from '$api/clickoutside';
	import { createEventDispatcher, onDestroy, onMount } from 'svelte';
	import { browser } from '$app/environment';
	import type { Transformer } from '$api/transformer';
	import { NameTransformer } from '$api/transformer';

	interface Props {
		id?: string | undefined;
		data?: unknown[];
		transformer?: Transformer<unknown, string>;
		placeholder?: string;
		multiple?: boolean;
		selected?: unknown[] | unknown;
		filtered?: unknown[];
		autoFocus?: boolean;
	}

	let {
		id = undefined,
		data = [],
		transformer = new NameTransformer(),
		placeholder = '',
		multiple = false,
		selected = $bindable(undefined),
		filtered = $bindable([]),
		autoFocus = false
	}: Props = $props();

	let focused = $state(false);
	let focusedIn = $state(false);
	let input: HTMLElement | undefined = $state();
	let criteria: string = $state('');
	let height: number = $state(0);
	let clientHeight: number = $state(0);
	let heightOffset: number = $state(0);
	let selectElm: HTMLElement | undefined = $state();
	let y: number = $state(0);
	const dispatch = createEventDispatcher();

	const updateY = () => (y = selectElm?.getBoundingClientRect().y || 0);

	onMount(() => {
		if (!browser) return;

		updateY();
		window.addEventListener('scroll', updateY, true);
		if (autoFocus) focus();
	});

	onDestroy(() => {
		if (!browser) return;

		window.removeEventListener('scroll', updateY, true);
	});

	export const focus = () => input?.focus();

	const select = (item: unknown, event?: KeyboardEvent) => {
		if (event && event?.key != 'Enter' && event?.key != ' ') return;

		if (event) {
			event.stopPropagation();
			event.preventDefault();
		}

		if (!multiple) {
			const notCancelled = dispatch('select', item, { cancelable: true });

			if (notCancelled) {
				selected = item;
				criteria = '';
			}

			return;
		}

		if (!selected || (selected instanceof Array && selected.includes(item))) return;

		const notCancelled = dispatch('select', [...(<Array<unknown>>selected), item], {
			cancelable: true
		});
		if (!notCancelled) return;

		selected = [...(<Array<unknown>>selected), item];
		criteria = '';
		focus();
	};

	const remove = (e: MouseEvent | KeyboardEvent, item: unknown) => {
		e.stopPropagation();

		const notCancelled = dispatch('remove', item, { cancelable: true });
		if (!notCancelled) return;

		if (multiple) selected = (<Array<unknown>>selected).filter((s: unknown) => s != item);
		else selected = undefined;
	};

	const clickOut = () => {
		focused = false;
		criteria = '';
	};

	const checkDelete = (e: KeyboardEvent) => {
		if (e.key == 'Enter') {
			e.stopPropagation();
			e.preventDefault();
			return;
		}
		if (e.key != 'Backspace' || criteria.length > 0 || !selected) return;

		if (selected instanceof Array) remove(e, selected[selected.length - 1]);
		else selected = undefined;
	};

	run(() => {
		filtered = data
			.filter((s) => {
				if (!criteria || transformer.transform(s).toLowerCase().includes(criteria.toLowerCase()))
					return (
						(multiple && selected instanceof Array && !selected.includes(s)) ||
						(!multiple && selected != s)
					);
			})
			.sort((a, b) => transformer.transform(a).localeCompare(transformer.transform(b)));
	});
</script>

<svelte:window bind:innerHeight={height} />

<div id="wrapper" class:multiple use:clickOutside={clickOut}>
	<div
		{id}
		class="input"
		tabindex="0"
		role="searchbox"
		onclick={() => focus()}
		onkeypress={(e) => {
			if (e.key === 'Enter') e.preventDefault();
		}}
		class:focused
	>
		{#if multiple && selected instanceof Array}
			{#each selected as sel (transformer.transform(sel))}
				<div
					class="chip"
					title="Click to remove"
					tabindex="0"
					role="button"
					transition:fly={{ y: -25 }}
					animate:flip={{ duration: 500 }}
					onclick={(e) => remove(e, sel)}
					onkeypress={(e) => {
						if (e.key === 'Enter' || e.key === ' ') remove(e, sel);
					}}
				>
					{transformer.transform(sel)}
				</div>
			{/each}
		{:else if selected}
			<div
				class="single-chip"
				title="Click to remove"
				tabindex="0"
				role="button"
				transition:fly={{ y: -25 }}
				onclick={(e) => remove(e, selected)}
				onkeypress={(e) => {
					if (e.key === 'Enter' || e.key === ' ') remove(e, selected);
				}}
			>
				{transformer.transform(selected)}
			</div>
		{/if}
		{#if !focused && !criteria && (!selected || (selected instanceof Array && selected.length === 0))}
			<span class="placeholder" in:fly={{ y: 25, delay: 250 }}>{placeholder}</span>
		{/if}
		<div
			class="input-box"
			contenteditable
			tabindex="0"
			role="textbox"
			bind:this={input}
			bind:textContent={criteria}
			onkeydown={checkDelete}
			onfocus={() => {
				focused = true;
				updateY();
			}}
			onblur={() =>
				setTimeout(
					() =>
						(focused = document.activeElement
							? document.activeElement.classList.contains('input-box')
							: false),
					250
				)}
		></div>
		<div class="clear"></div>
	</div>

	{#if filtered.length > 0 && (focused || focusedIn)}
		<div class="select-wrapper" bind:this={selectElm} bind:clientHeight={heightOffset}>
			<div
				class="select"
				class:focusedIn
				bind:clientHeight
				style:top={y + clientHeight > height ? y - clientHeight + 'px' : y + heightOffset + 'px'}
			>
				{#each filtered as datum}
					<div
						class="select-item"
						tabindex="0"
						role="option"
						aria-selected="false"
						onclick={() => select(datum)}
						onfocus={() => (focusedIn = true)}
						onblur={() =>
							setTimeout(
								() =>
									(focusedIn = document.activeElement
										? document.activeElement.classList.contains('select-item')
										: false),
								250
							)}
						onkeypress={(e) => select(datum, e)}
					>
						{transformer.transform(datum)}
					</div>
				{/each}
			</div>
		</div>
	{/if}
</div>

<style>
	.input {
		background-color: var(--color-select-bg);
		padding: 0.2rem 0.5rem;
	}

	.chip,
	.single-chip {
		float: left;
		margin-right: 0.4rem;
	}

	.chip:hover,
	.single-chip:hover {
		cursor: pointer;
	}

	.placeholder {
		display: inline-block;
	}

	.input-box {
		display: inline-block;
	}

	.input-box:focus {
		outline: none;
	}

	.focused {
		outline: -webkit-focus-ring-color auto 1px;
	}

	.select-wrapper {
		position: absolute;
		inset: 0;
	}

	.select {
		position: fixed;
		z-index: 5;
		max-height: 15em;
		top: auto;
		left: auto;
		right: auto;
		/*top: var(--top);*/
		/*bottom: var(--bottom);*/
		overflow: auto;
		width: max-content;
		background-color: var(--color-select-bg);
		border: 1px solid #666;
		border-radius: 0 0 0.25rem 0.25rem;
	}

	.select.focusedIn {
		border: 2px solid var(--color-accent);
	}

	.select > * {
		padding: 0.25rem 0.5rem;
	}

	.select > *:not(:first-child) {
		border-top: 1px solid #333;
	}

	.select > *:hover {
		background-color: #0083ef;
		cursor: pointer;
	}

	.input-box {
		min-width: 5rem;
	}
</style>
