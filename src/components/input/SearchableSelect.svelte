<script lang='ts'>
	import { fly }                from 'svelte/transition';
	import { flip }               from 'svelte/animate';
	import { clickOutside }       from '$api/clickoutside';
	import { onDestroy, onMount } from 'svelte';
	import { browser }            from '$app/environment';
	import type { Transformer }   from '$api/transformer';
	import { NameTransformer }    from '$api/transformer';

	interface Props {
		id?: string | undefined;
		data?: unknown[];
		transformer?: Transformer<unknown, string>;
		placeholder?: string;
		multiple?: boolean;
		selected?: unknown[] | unknown;
		autoFocus?: boolean;
		onselect?: (item: never) => boolean;
		onremove?: (item: never) => boolean;
	}

	let {
				id          = undefined,
				data        = $bindable([]),
				transformer = new NameTransformer(),
				placeholder = '',
				multiple    = $bindable(false),
				selected    = $bindable(undefined),
				autoFocus   = false,
				onselect,
				onremove
			}: Props = $props();

	let focused                        = $state(false);
	let focusedIn                      = $state(false);
	let input: HTMLElement | undefined = $state();
	let criteria: string               = $state('');
	let height: number                 = $state(0);
	let clientHeight: number           = $state(0);
	let heightOffset: number           = $state(0);

	let filtered = $derived.by(() => {
		return data
			.filter((s) => {
				if (!criteria || transformer.transform(s).toLowerCase().includes(criteria.toLowerCase()))
					return (
						(multiple && selected instanceof Array && !selected.includes(s)) ||
						(!multiple && selected != s)
					);
			})
			.sort((a, b) => transformer.transform(a).localeCompare(transformer.transform(b)));
	});

	let wrapperElm: HTMLElement | undefined = $state();
	const updateY                           = () => {
		y = wrapperElm?.getBoundingClientRect().y || 0;
	};
	let y: number                           = $state(0);

	onMount(() => {
		if (!browser) return;

		updateY();
		window.addEventListener('scroll', updateY, true);
		window.addEventListener('resize', updateY, true);
		if (autoFocus) focus();
	});

	onDestroy(() => {
		if (!browser) return;

		window.removeEventListener('scroll', updateY, true);
		window.removeEventListener('resize', updateY, true);
	});

	export const focus = () => input?.focus();

	const select = (item: unknown, event?: KeyboardEvent) => {
		if (event && event?.key != 'Enter' && event?.key != ' ') return;

		if (event) {
			event.stopPropagation();
			event.preventDefault();
		}

		if (!multiple) {
			const cancelled = onselect?.(<never>item) || false;

			if (!cancelled) {
				selected = item;
				criteria = '';
			}

			return;
		}

		if (!selected || (selected instanceof Array && selected.includes(item))) return;

		const cancelled = onselect?.(<never>[...(<Array<never>>selected), item]) || false;
		if (cancelled) return;

		(<Array<unknown>>selected).push(item);
		criteria = '';
		focus();
	};

	const remove = (e: MouseEvent | KeyboardEvent, item: unknown) => {
		e.stopPropagation();

		const cancelled = onremove?.(<never>item) || true;
		if (!cancelled) return;

		if (multiple) selected = (<Array<unknown>>selected).filter((s: unknown) => s != item);
		else selected = undefined;
	};

	const clickOut = () => {
		focused  = false;
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
</script>

<svelte:window bind:innerHeight={height} />

<div bind:this={wrapperElm} class:multiple id='wrapper' use:clickOutside={clickOut}>
	<div
		class='input'
		class:focused
		{id}
		onclick={() => focus()}
		onkeypress={(e) => {
			if (e.key === 'Enter') e.preventDefault();
		}}
		role='searchbox'
		tabindex='0'
	>
		{#if multiple && selected instanceof Array}
			{#each selected as sel (transformer.transform(sel))}
				<div
					class='chip'
					title='Click to remove'
					tabindex='0'
					role='button'
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
				class='single-chip'
				title='Click to remove'
				tabindex='0'
				role='button'
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
			<span class='placeholder' in:fly={{ y: 25, delay: 250 }}>{placeholder}</span>
		{/if}
		<div
			bind:textContent={criteria}
			bind:this={input}
			class='input-box'
			contenteditable
			onblur={() =>
				setTimeout(
					() =>
						(focused = document.activeElement
							? document.activeElement.classList.contains('input-box')
							: false),
					250
				)}
			onfocus={() => {
				focused = true;
				updateY();
			}}
			onkeydown={checkDelete}
			role='textbox'
			tabindex='0'
		></div>
		<div class='clear'></div>
	</div>

	{#if filtered.length > 0 && (focused || focusedIn)}
		<div class='select-wrapper' bind:clientHeight={heightOffset}>
			<div
				class='select'
				class:focusedIn
				bind:clientHeight
				style:top={y + clientHeight > height ? y - clientHeight + 'px' : y + heightOffset + 'px'}
			>
				{#each filtered as datum}
					<div
						class='select-item'
						tabindex='0'
						role='option'
						aria-selected='false'
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
        display: inline-block;
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
