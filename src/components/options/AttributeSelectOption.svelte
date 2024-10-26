<script lang='ts'>
	import { run } from 'svelte/legacy';

	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';
	import AttributeInput            from '$input/AttributeInput.svelte';
	import type { Stat }             from '$api/stat';

	interface Props {
		data: Stat;
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
	<AttributeInput bind:value={data} />
</ProInput>