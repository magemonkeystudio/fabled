<script lang='ts'>
	import ProInput from '$input/ProInput.svelte';
	import Toggle   from '$input/Toggle.svelte';

	interface Props {
		data: boolean;
		name?: string | undefined;
		tooltip?: string | undefined;
		onsave?: () => void;
	}

	let { data = $bindable(), name = '', tooltip = undefined, onsave }: Props = $props();

	$effect(() => {
		if (data || !data) onsave?.();
	});
</script>

<ProInput label={name} {tooltip}>
	<Toggle bind:data={data} />
</ProInput>