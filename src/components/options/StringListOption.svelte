<script lang='ts'>
	import ProInput                  from '$input/ProInput.svelte';
	import LoreInput                 from '$input/LoreInput.svelte';

	interface Props {
		data: { value: string[] };
		name?: string | undefined;
		tooltip?: string | undefined;
		onsave?: () => void;
	}

	let { data = $bindable(), name = '', tooltip = undefined, onsave }: Props = $props();

	$effect(() => {
		if (data?.value) onsave?.();
	});
</script>
<ProInput label={name} {tooltip}>
	<LoreInput bind:value={data.value} />
</ProInput>