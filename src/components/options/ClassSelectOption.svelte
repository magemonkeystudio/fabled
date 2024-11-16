<script lang='ts'>
	import ProInput                    from '$input/ProInput.svelte';
	import SearchableSelect            from '$input/SearchableSelect.svelte';
	import FabledClass, { classStore } from '../../data/class-store.svelte';

	interface Props {
		data?: FabledClass[] | FabledClass;
		name?: string | undefined;
		tooltip?: string | undefined;
		multiple?: boolean;
		onsave?: () => void;
	}

	let { data = $bindable([]), name = '', tooltip = undefined, multiple = true, onsave }: Props = $props();

	const classes = classStore.classes;

	// $effect(() => {
	// 	if (!multiple && !data) data = '';
	// 	if (data instanceof Array) {
	// 		data = data
	// 			.map((cl) => {
	// 				if (cl instanceof FabledClass) return cl;
	//
	// 				const clazz = classStore.getClass(cl);
	// 				if (clazz) return clazz;
	// 			})
	// 			.filter((cl) => !!cl); // Remove any undefined values
	// 	} else {
	// 		if (data && !(data instanceof FabledClass)) {
	// 			const clazz = classStore.getClass(<string>data);
	// 			if (clazz) data = clazz;
	// 		}
	// 	}
	// 	onsave?.();
	// });

	const changed = () => {
		if (data instanceof Array) {
			return data.map((d) => ({
				name: d.name
			}));
		} else {
			return data.name;
		}
	};

	$effect(() => {
		if (changed()) onsave?.();
	});

</script>

<ProInput label={name} {tooltip}>
	<SearchableSelect bind:data={$classes} bind:selected={data} {multiple} />
</ProInput>
