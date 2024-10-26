<script lang='ts'>
	import { run } from 'svelte/legacy';

	import MaterialSelect            from '$input/MaterialSelect.svelte';
	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';

	interface Props {
		data: { material: string, any?: boolean };
		tooltip?: string | undefined;
	}

	let { data = $bindable(), tooltip = undefined }: Props = $props();

	const dispatch = createEventDispatcher();
	run(() => {
		if (data) dispatch('save');
	});
</script>

<ProInput label='Material' tooltip='[material] {tooltip}'>
	<MaterialSelect bind:selected={data.material} bind:any={data.any} />
</ProInput>