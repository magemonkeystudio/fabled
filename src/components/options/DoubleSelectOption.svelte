<script lang='ts'>
	import ProInput                  from '$input/ProInput.svelte';

	interface Props {
		data: number;
		name?: string | undefined;
		tooltip?: string | undefined;
		onsave?: () => void;
	}

	let { data = $bindable(), name = '', tooltip = undefined, onsave }: Props = $props();

	$effect(() => {
		if (data || !data) onsave?.();
	});
</script>

<ProInput label={name} {tooltip} bind:value={data} type='number' placeholder='0' />