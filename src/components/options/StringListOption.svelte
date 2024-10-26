<script lang='ts'>
	import { run } from 'svelte/legacy';

	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';
	import LoreInput                 from '$input/LoreInput.svelte';

	interface Props {
		data: { value: string[] };
		name?: string | undefined;
		tooltip?: string | undefined;
	}

	let { data = $bindable(), name = '', tooltip = undefined }: Props = $props();

	const dispatch = createEventDispatcher();
	run(() => {
		if (data) dispatch('save');
	});
</script>
<ProInput label={name} {tooltip}>
	<LoreInput bind:value={data.value} />
</ProInput>