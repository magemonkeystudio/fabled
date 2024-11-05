<script lang='ts'>
	import MaterialSelect from '$input/MaterialSelect.svelte';
	import ProInput       from '$input/ProInput.svelte';

	interface Props {
		data: { material: string, any?: boolean };
		tooltip?: string | undefined;
		onsave?: () => void;
	}

	let { data = $bindable(), tooltip = undefined, onsave }: Props = $props();

	$effect(() => {
		if (data.material) onsave?.();
	});
</script>

<ProInput label='Material' tooltip='[material] {tooltip}'>
	<MaterialSelect bind:selected={data.material} bind:any={data.any} />
</ProInput>