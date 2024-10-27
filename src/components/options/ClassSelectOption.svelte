<script lang='ts'>
	import ProInput                    from '$input/ProInput.svelte';
	import { createEventDispatcher }   from 'svelte';
	import SearchableSelect            from '$input/SearchableSelect.svelte';
	import FabledClass, { classStore } from '../../data/class-store.svelte';

	interface Props {
		data?: FabledClass[] | FabledClass | string[] | string;
		name?: string | undefined;
		tooltip?: string | undefined;
		multiple?: boolean;
	}

	let { data = $bindable([]), name = '', tooltip = undefined, multiple = true }: Props = $props();

	const classes = classStore.classes;

	const dispatch = createEventDispatcher();
	$effect.pre(() => {
		if (!multiple && !data) data = '';
		if (data instanceof Array) {
			data = data
				.map((cl) => {
					if (cl instanceof FabledClass) return cl;

					const clazz = classStore.getClass(cl);
					if (clazz) return clazz;
				})
				.filter((cl) => !!cl); // Remove any undefined values
			dispatch('save');
		} else {
			if (data && !(data instanceof FabledClass)) {
				const clazz = classStore.getClass(<string>data);
				if (clazz) data = clazz;
			}
			dispatch('save');
		}
	});
</script>

<ProInput label={name} {tooltip}>
	<SearchableSelect bind:data={$classes} {multiple} bind:selected={data} />
</ProInput>
