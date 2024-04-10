<script lang='ts'>
	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';
	import EnchantSelect             from '$input/EnchantSelect.svelte';
	import type { Enchant }          from '$api/options/enchantselect';

	export let data: { enchants: Enchant[] };
	export let tooltip: string | undefined = undefined;

	const dispatch = createEventDispatcher();
	$: if (data) dispatch('save');
</script>

<ProInput label='Enchant' tooltip='[enchant] {tooltip}'>
	<EnchantSelect bind:selected={data.enchants} multiple/>
</ProInput>