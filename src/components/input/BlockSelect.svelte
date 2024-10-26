<script lang='ts'>
	import SearchableSelect from './SearchableSelect.svelte';
	import { versionData }  from '../../version/data';

	interface Props {
		id?: string | undefined;
		placeholder?: string;
		multiple?: boolean;
		any?: boolean;
		selected?: any[] | any;
	}

	let {
		id = undefined,
		placeholder = '',
		multiple = false,
		any = false,
		selected = $bindable(undefined)
	}: Props = $props();
</script>

<SearchableSelect
	{id}
	{placeholder}
	{multiple}
	bind:selected={selected}
	data={any ? ["Any", ...$versionData.BLOCKS] : $versionData.BLOCKS}
/>