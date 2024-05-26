<script lang='ts'>
	import BlockSelect               from '$input/BlockSelect.svelte';
	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';

	export let data: { material: string[], data: number, materialTooltip: string, dataTooltip: string };

	const dispatch = createEventDispatcher();
	$: if (data) dispatch('save');
</script>

<ProInput label='Material' tooltip='[material] {data.materialTooltip}'>
	<BlockSelect any bind:selected={data.material} multiple />
</ProInput>
<ProInput type='number' intMode
					label='Data'
					bind:value={data.data}
					tooltip='[data] {data.dataTooltip}' />