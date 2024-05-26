<script lang='ts'>
	import IconInput            from './input/IconInput.svelte';
	import { updateSidebar }    from '../data/store';
	import AttributeInput       from './input/AttributeInput.svelte';
	import ProInput             from './input/ProInput.svelte';
	import type FabledAttribute from '$api/fabled-attribute';
	import AttributeSection     from './attributes/AttributeSection.svelte';
	import AttributeStats       from './attributes/AttributeStats.svelte';

	export let data: FabledAttribute;

	$: {
		if (data?.name) updateSidebar();
		data.save();
	}
</script>

{#if data}
	<ProInput label='Name'
						tooltip='The name of the attribute. This should not contain color codes'
						bind:value={data.name} />
	<ProInput label='Max' type='number'
						tooltip='The maximum points of this attribute that a player can have'
						bind:value={data.max} />
	<ProInput label='Cost'
						tooltip='How many attribute points it costs to upgrade the attribute, increasing with the level of the attribute'>
		<AttributeInput bind:value={data.cost} />
	</ProInput>
	<IconInput bind:icon={data.icon} />
	<AttributeSection name={'Target'} bind:section={data.targets} />
	<AttributeSection name={'Condition'} bind:section={data.conditions} />
	<AttributeSection name={'Mechanic'} bind:section={data.mechanics} />
	<AttributeStats bind:stats={data.stats} />
{/if}