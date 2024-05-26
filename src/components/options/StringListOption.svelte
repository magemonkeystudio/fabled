<script lang='ts'>
	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';
	import LoreInput                 from '$input/LoreInput.svelte';

	export let data: { value: string[] };
	export let name: string | undefined    = '';
	export let tooltip: string | undefined = undefined;

	const dispatch = createEventDispatcher();
	$: if (data) dispatch('save');
</script>
<ProInput label={name} {tooltip}>
	<LoreInput bind:value={data.value} />
</ProInput>