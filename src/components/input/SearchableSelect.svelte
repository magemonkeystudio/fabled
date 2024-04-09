<script lang='ts'>
	import { fly }                                       from 'svelte/transition';
	import { flip }                                      from 'svelte/animate';
	import { clickOutside }                              from '$api/clickoutside';
	import { createEventDispatcher, onDestroy, onMount } from 'svelte';
	import { browser }                                   from '$app/environment';
	import type { Transformer }                          from '$api/transformer';
	import { NameTransformer }                           from '$api/transformer';

	export let id: string | undefined                    = undefined;
	export let data: unknown[]                           = [];
	export let transformer: Transformer<unknown, string> = new NameTransformer();
	export let placeholder                               = '';
	export let multiple                                  = false;
	export let selected: unknown[] | unknown             = undefined;
	export let filtered: unknown[]                       = [];
	export let autoFocus                                 = false;

	let focused    = false;
	let focusedIn  = false;
	let input: HTMLElement;
	let criteria: string;
	let height: number;
	let y: number;
	let clientHeight: number;
	let heightOffset: number;
	let selectElm: HTMLElement;
	const dispatch = createEventDispatcher();

	const updateY = () => y = selectElm?.getBoundingClientRect().y;
	$: y = selectElm?.getBoundingClientRect().y;

	onMount(() => {
		if (!browser) return;

		window.addEventListener('scroll', updateY, true);
		if (autoFocus) focus();
	});

	onDestroy(() => {
		if (!browser) return;

		window.removeEventListener('scroll', updateY, true);
	});

	export const focus = () => input.focus();

	const select = (item: unknown, event?: KeyboardEvent) => {
		if (event && event?.key != 'Enter' && event?.key != ' ') return;

		if (event) {
			event.stopPropagation();
			event.preventDefault();
		}

		if (!multiple) {
			const cancelled = dispatch('select', item, { cancelable: true });

			if (!cancelled) {
				selected = item;
				criteria = '';
			}

			return;
		}

		if (!selected || (selected instanceof Array && selected.includes(item))) return;

		const cancelled = dispatch('select', [...<Array<unknown>>selected, item], { cancelable: true });
		if (cancelled) return;

		selected = [...<Array<unknown>>selected, item];
		criteria = '';
		focus();
	};

	const remove = (e: MouseEvent | KeyboardEvent, item: unknown) => {
		e.stopPropagation();

		const cancelled = dispatch('remove', item, { cancelable: true });
		if (cancelled) return;

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

		if (selected instanceof Array)
			remove(e, selected[selected.length - 1]);
		else
			selected = undefined;
	};

	$: {
		filtered = data.filter(s => {
			if (!criteria || transformer.transform(s).toLowerCase().includes(criteria.toLowerCase()))
				return (multiple && selected instanceof Array && !selected.includes(s))
					|| (!multiple && selected != s);
		}).sort((a, b) => transformer.transform(a).localeCompare(transformer.transform(b)));
	}
</script>

<svelte:window bind:innerHeight={height} />

<div id='wrapper'
		 class:multiple
		 use:clickOutside={clickOut}>
	<div {id} class='input'
			 tabindex='0'
			 role='searchbox'
			 on:click={() => focus()}
			 on:keypress={(e) => { if (e.key === 'Enter') e.preventDefault(); }}
			 class:focused={focused}>
		{#if multiple && selected instanceof Array}
			{#each selected as sel (transformer.transform(sel))}
				<div class='chip'
						 title='Click to remove'
						 tabindex='0'
						 role='button'
						 transition:fly={{y: -25}}
						 animate:flip={{duration: 500}}
						 on:click={(e) => remove(e, sel)}
						 on:keypress={(e) => { if (e.key === 'Enter' || e.key === ' ') remove(e, sel); }}
				>{transformer.transform(sel)}</div>
			{/each}
		{:else if selected}
			<div class='single-chip'
					 title='Click to remove'
					 tabindex='0'
					 role='button'
					 transition:fly={{y: -25}}
					 on:click={(e) => remove(e, selected)}
					 on:keypress={(e) => { if (e.key === 'Enter' || e.key === ' ') remove(e, selected); }}
			>{transformer.transform(selected)}</div>
		{/if}
		{#if !focused && !criteria && (!selected || (selected instanceof Array && selected.length === 0))}
			<span class='placeholder' in:fly={{y: 25, delay: 250}}>{placeholder}</span>
		{/if}
		<div class='input-box'
				 contenteditable
				 tabindex='0'
				 role='textbox'
				 bind:this={input}
				 bind:textContent={criteria}
				 on:keydown={checkDelete}
				 on:focus={() => {
           focused = true;
           updateY();
         }}
				 on:blur={() => setTimeout(() => focused = document.activeElement ? document.activeElement.classList.contains('input-box') : false, 250) }>
		</div>
		<div class='clear' />
	</div>

	{#if filtered.length > 0 && (focused || focusedIn)}
		<div class='select-wrapper'
				 bind:this={selectElm}
				 bind:clientHeight={heightOffset}>
			<div class='select'
					 class:focusedIn
					 bind:clientHeight={clientHeight}
					 style:top={y+clientHeight > height ? y-clientHeight + "px" : y+heightOffset + "px"}>
				{#each filtered as datum}
					<div class='select-item'
							 tabindex='0'
							 role='option'
							 aria-selected='false'
							 on:click={() => select(datum)}
							 on:focus={() => focusedIn = true}
							 on:blur={() => setTimeout(() => focusedIn = document.activeElement ? document.activeElement.classList.contains('select-item') : false, 250) }
							 on:keypress={(e) => select(datum, e)}>{transformer.transform(datum)}</div>
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

    .chip, .single-chip {
        float: left;
        margin-right: 0.4rem;
    }

    .chip:hover, .single-chip:hover {
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