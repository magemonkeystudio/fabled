<script lang='ts'>
	import MaterialSelect from '$input/MaterialSelect.svelte';
	import ProInput       from '$input/ProInput.svelte';

	interface Props {
		data: { material: string, any?: boolean };
		tooltip?: string | undefined;
	}

	let { data = $bindable(), tooltip = undefined }: Props = $props();
</script>

<ProInput label='Material' tooltip='[material] {tooltip}'>
	<MaterialSelect bind:selected={data.material} bind:any={data.any} />
</ProInput>