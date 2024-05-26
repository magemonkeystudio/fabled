<script lang='ts'>
	import MaterialSelect from './MaterialSelect.svelte';
	import type { Icon }  from '$api/types';
	import LoreInput      from './LoreInput.svelte';
	import ProInput       from './ProInput.svelte';
	import { activeType } from '../../data/store';

	export let icon: Icon;
</script>

<ProInput label='Icon'
					tooltip='[icon] The item that represents the {$activeType} in GUIs'>
	<MaterialSelect bind:selected={icon.material} />
</ProInput>
<ProInput label='CustomModelData' type='number' intMode={true}
					tooltip='[icon-data] The CustomModelData of the item that represents the {$activeType} in GUIs'
					bind:value={icon.customModelData} />
<ProInput label='Icon Lore'
					tooltip={'[icon-lore]' + $activeType === 'class' ? "The item's lore description" : 'The description shown for the item in skill trees. Include values of mechanics such as damage dealt using their "Icon Key" values'}>
	<LoreInput bind:value={icon.lore} />
</ProInput>