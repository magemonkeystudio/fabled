<script lang='ts'>
	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';

	export let data: string;
	export let name: string | undefined    = '';
	export let tooltip: string | undefined = undefined;

	const dispatch = createEventDispatcher();
	$: if (data || !data) dispatch('save');
</script>

<ProInput label={name} {tooltip}>
	<div class='wrap'>
		<input bind:value={data} class='text' />
		<input type='color' bind:value={data} />
	</div>
</ProInput>

<style>
    .wrap {
        display: flex;
        flex-direction: row;
        align-items: stretch;
        justify-content: center;
    }

    .text {
        width: 30%;
        flex: 1;
    }

    input[type="color"] {
        padding: 0;
        height: auto;
        display: block;
        flex: 2.5;
    }
</style>