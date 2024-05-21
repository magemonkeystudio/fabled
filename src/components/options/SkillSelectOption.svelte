<script lang='ts'>
	import ProInput                    from '$input/ProInput.svelte';
	import { createEventDispatcher }   from 'svelte';
	import SearchableSelect            from '$input/SearchableSelect.svelte';
	import FabledSkill, { skillStore } from '../../data/skill-store.js';

	export let data: FabledSkill[] | FabledSkill | string[] | string = [];
	export let name: string | undefined                              = '';
	export let tooltip: string | undefined                           = undefined;
	export let multiple                                              = true;

	$: if (!multiple && (!data || data.length === 0)) data = '';

	const skills = skillStore.skills;

	const dispatch = createEventDispatcher();
	$: if (data instanceof Array) {
		data = data.map(cl => {
			if (cl instanceof FabledSkill) return cl;

			const sk = skillStore.getSkill(cl);
			if (sk) return sk;
		});
		dispatch('save');
	} else {
		if (data && !(data instanceof FabledSkill)) {
			const sk = skillStore.getSkill(<string>data);
			if (sk) data = sk;
		}
		dispatch('save');
	}
</script>

<ProInput label={name} {tooltip}>
	<SearchableSelect bind:data={$skills}
										{multiple}
										bind:selected={data} />
</ProInput>