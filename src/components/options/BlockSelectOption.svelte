<script lang='ts'>
	import { run } from 'svelte/legacy';

	import BlockSelect               from '$input/BlockSelect.svelte';
	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';

	interface Props {
		data: { material: string[], data: number, materialTooltip: string, dataTooltip: string };
	}

	let { data = $bindable() }: Props = $props();

	const dispatch = createEventDispatcher();
	run(() => {
		if (data) dispatch('save');
	});
</script>

<ProInput label='Material' tooltip='[material] {data.materialTooltip}'>
	<BlockSelect any bind:selected={data.material} multiple />
</ProInput>
<ProInput type='number' intMode
					label='Data'
					bind:value={data.data}
					tooltip='[data] {data.dataTooltip}' />