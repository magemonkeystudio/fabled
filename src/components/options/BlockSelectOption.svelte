<script lang='ts'>
	import BlockSelect from '$input/BlockSelect.svelte';
	import ProInput    from '$input/ProInput.svelte';

	interface Props {
		data: { material: string[], data: number, materialTooltip: string, dataTooltip: string },
		onsave: () => void;
	}

	let { data = $bindable(), onsave }: Props = $props();

	const changed = () => {
		return {
			material: data.material,
			data:     data.data
		};
	};

	$effect(() => {
		if (changed()) onsave?.();
	});
</script>

<ProInput label='Material' tooltip='[material] {data.materialTooltip}'>
	<BlockSelect any bind:selected={data.material} multiple />
</ProInput>
<ProInput bind:value={data.data} intMode
					label='Data'
					tooltip='[data] {data.dataTooltip}'
					type='number' />