<script lang='ts'>
	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';
	import SearchableSelect          from '$input/SearchableSelect.svelte';
	import { getSkill, skills }      from '../../data/skill-store';
	import ProSkill                  from '$api/proskill';

	export let data: ProSkill[] | ProSkill | string[] | string = [];
	export let name: string | undefined                        = '';
	export let tooltip: string | undefined                     = undefined;
	export let multiple                                        = true;

	$: if (!multiple && (!data || data.length === 0)) data = '';

	const dispatch = createEventDispatcher();
	$: if (data instanceof Array) {
		data = data.map(cl => {
			if (cl instanceof ProSkill) return cl;

			const sk = getSkill(cl);
			if (sk) return sk;
		});
		dispatch('save');
	} else {
		if (data && !(data instanceof ProSkill)) {
			const sk = getSkill(<string>data);
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