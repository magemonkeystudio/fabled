<!--suppress CssUnresolvedCustomProperty -->
<script lang='ts'>
	import { fade, fly }             from 'svelte/transition';
	import { clickOutside }          from '$api/clickoutside';
	import { createEventDispatcher } from 'svelte';

	export let width = 'auto';
	export let open  = false;

	const dispatch = createEventDispatcher();

	const closeModal = (e?: CustomEvent) => {
		open = false;
		dispatch('close');
		e?.detail?.stopPropagation();
	};

	const checkClose = (e: KeyboardEvent) => {
		if (e.key == 'Escape') {
			e.preventDefault();
			e.stopPropagation();
			closeModal();
		}
	};
</script>

<svelte:window on:keyup={checkClose} />
{#if open}
	<div class='backdrop' transition:fade>
		<div class='modal-content'
				 use:clickOutside
				 on:outclick={closeModal}
				 on:click|stopPropagation
				 transition:fly={{y: -200}}
				 style:--width={width}>
			<div class='wrapper'>
				<slot />
			</div>
		</div>
	</div>
{/if}

<style>
    .backdrop {
        position: fixed;
        inset: 0;
        z-index: 100;

        display: flex;
        justify-content: center;
        align-items: center;

        background: rgba(0, 0, 0, 0.6);
    }

    .modal-content {
        display: flex;
        justify-content: center;
        background-color: var(--color-bg);
        border: 2px solid #444;
        border-radius: 0 1.5rem 0 1.5rem;
        box-shadow: 0 0 1rem #222;
        margin: 2rem;
        box-sizing: border-box;

        width: var(--width);
        max-height: 85vh;
        max-width: 90%;
				overflow-y: auto;
    }

    .wrapper {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin: 1rem;
        width: 100%;
        height: 100%;
    }

    @media screen and (max-width: 500px) {
        .modal-content {
            width: unset;
            min-width: var(--width);
        }
    }
</style>