<script lang='ts'>
	import ProInput                    from '$input/ProInput.svelte';
	import { createEventDispatcher }   from 'svelte';
	import SearchableSelect            from '$input/SearchableSelect.svelte';
	import FabledClass, { classStore } from '../../data/class-store';

	export let data: FabledClass[] | FabledClass | string[] | string = [];
	export let name: string | undefined                              = '';
	export let tooltip: string | undefined                           = undefined;
	export let multiple                                              = true;

	const classes = classStore.classes;

	$: if (!multiple && data.length === 0) data = '';

	const dispatch = createEventDispatcher();
	$: if (data instanceof Array) {
		data = data.map(cl => {
			if (cl instanceof FabledClass) return cl;

			const clazz = classStore.getClass(cl);
			if (clazz) return clazz;
		});
		dispatch('save');
	} else {
		if (data && !(data instanceof FabledClass)) {
			const clazz = classStore.getClass(<string>data);
			if (clazz) data = clazz;
		}
		dispatch('save');
	}
</script>

<ProInput label={name} {tooltip}>
	<SearchableSelect bind:data={$classes}
										{multiple}
										bind:selected={data} />
</ProInput>