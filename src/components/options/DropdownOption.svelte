<script lang='ts'>
	import { run } from 'svelte/legacy';

	import ProInput                  from '$input/ProInput.svelte';
	import SearchableSelect          from '$input/SearchableSelect.svelte';
	import { createEventDispatcher } from 'svelte';

	interface Props {
		data: { selected: string | string[], value: string[], multiple: boolean };
		name?: string | undefined;
		tooltip?: string | undefined;
	}

	let { data = $bindable(), name = '', tooltip = undefined }: Props = $props();

	const dispatch = createEventDispatcher();
	run(() => {
		if (data) dispatch('save');
	});
</script>
<ProInput label={name} {tooltip}>
	<SearchableSelect bind:selected={data.selected} bind:data={data.value} bind:multiple={data.multiple} />
</ProInput>