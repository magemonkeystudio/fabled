<script lang='ts'>
	import { run } from 'svelte/legacy';

	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';

	interface Props {
		data: number;
		name?: string | undefined;
		tooltip?: string | undefined;
	}

	let { data = $bindable(), name = '', tooltip = undefined }: Props = $props();

	const dispatch = createEventDispatcher();
	run(() => {
		if (data || !data) dispatch('save');
	});
</script>

<ProInput label={name} {tooltip} bind:value={data} type='number' placeholder='0' />