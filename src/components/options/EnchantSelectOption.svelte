<script lang='ts'>
	import ProInput                  from '$input/ProInput.svelte';
	import EnchantSelect             from '$input/EnchantSelect.svelte';
	import type { Enchant }          from '$api/options/enchantselect.svelte';

	interface Props {
		data: { enchants: Enchant[] };
		tooltip?: string | undefined;
		onsave?: () => void;
	}

	let { data = $bindable(), tooltip = undefined, onsave }: Props = $props();

	const changed = () => {
		return {
			enchants: data?.enchants
		}
	}

	$effect(() => {
		if (changed()) onsave?.();
	});
</script>

<ProInput label='Enchant' tooltip='[enchant] {tooltip}'>
	<EnchantSelect bind:selected={data.enchants} multiple={true} {onsave} />
</ProInput>