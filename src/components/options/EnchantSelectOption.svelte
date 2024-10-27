<script lang='ts'>
	import { run } from 'svelte/legacy';

	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';
	import EnchantSelect             from '$input/EnchantSelect.svelte';
	import type { Enchant }          from '$api/options/enchantselect';

	interface Props {
		data: { enchants: Enchant[] };
		tooltip?: string | undefined;
	}

	let { data = $bindable(), tooltip = undefined }: Props = $props();

	const dispatch = createEventDispatcher();
	run(() => {
		if (data) dispatch('save');
	});
</script>

<ProInput label='Enchant' tooltip='[enchant] {tooltip}'>
	<EnchantSelect bind:selected={data.enchants} multiple={true} />
</ProInput>