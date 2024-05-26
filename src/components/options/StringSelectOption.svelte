<script lang='ts'>
	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';

	export let data: string;
	export let name: string | undefined    = '';
	export let tooltip: string | undefined = undefined;

	const dispatch = createEventDispatcher();
	$: if (data || !data) dispatch('save');
</script>

<ProInput label={name} {tooltip} bind:value={data} />