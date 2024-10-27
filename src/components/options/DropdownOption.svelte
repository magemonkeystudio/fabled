<script lang='ts'>
	import ProInput                  from '$input/ProInput.svelte';
	import SearchableSelect          from '$input/SearchableSelect.svelte';

	interface Props {
		data: { selected: string | string[], value: string[], multiple: boolean };
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
	<SearchableSelect bind:selected={data.selected} bind:data={data.value} bind:multiple={data.multiple} />
</ProInput>