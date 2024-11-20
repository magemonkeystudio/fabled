<script lang='ts'>
	import SearchableSelect from './SearchableSelect.svelte';
	import { versionData }  from '../../version/data';

	interface Props {
		id?: string | undefined;
		placeholder?: string;
		multiple?: boolean;
		any?: boolean;
		selected?: string[] | string;
	}

	let {
				id          = undefined,
				placeholder = '',
				multiple    = $bindable(false),
				any         = $bindable(false),
				selected    = $bindable(undefined)
			}: Props = $props();
</script>

<SearchableSelect
	bind:selected={selected}
	data={any ? ["Any", ...$versionData.MATERIALS] : $versionData.MATERIALS}
	{id}
	{multiple}
	{placeholder}
/>