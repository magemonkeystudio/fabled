<script lang='ts'>
	import ProInput from '$input/ProInput.svelte';
	import Toggle   from '$input/Toggle.svelte';

	interface Props {
		data: boolean;
		name?: string | undefined;
		tooltip?: string | undefined;
	}

	let { data = $bindable(), name = '', tooltip = undefined }: Props = $props();
</script>

<ProInput label={name} {tooltip}>
	<Toggle bind:data={data} />
</ProInput>