<script lang='ts'>
	import { run } from 'svelte/legacy';

	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';
	import Toggle                    from '$input/Toggle.svelte';

	interface Props {
		data: boolean;
		name?: string | undefined;
		tooltip?: string | undefined;
	}

	let { data = $bindable(), name = '', tooltip = undefined }: Props = $props();

	const dispatch = createEventDispatcher();
	run(() => {
		if (data || !data) dispatch('save');
	});
</script>

<ProInput label={name} {tooltip}>
	<Toggle bind:data={data} />
</ProInput>