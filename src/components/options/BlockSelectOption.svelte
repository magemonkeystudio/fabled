<script lang='ts'>
	import BlockSelect               from '$input/BlockSelect.svelte';
	import ProInput                  from '$input/ProInput.svelte';

	interface Props {
		data: { material: string[], data: number, materialTooltip: string, dataTooltip: string },
		onsave: () => void;
	}

	let { data = $bindable(), onsave }: Props = $props();

	$effect(() => {
		if (data || !data) onsave?.();
	});
</script>

<ProInput label='Material' tooltip='[material] {data.materialTooltip}'>
	<BlockSelect any bind:selected={data.material} multiple />
</ProInput>
<ProInput type='number' intMode
					label='Data'
					bind:value={data.data}
					tooltip='[data] {data.dataTooltip}' />