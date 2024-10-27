<script lang='ts'>
	import IconInput            from './input/IconInput.svelte';
	import AttributeInput       from './input/AttributeInput.svelte';
	import ProInput             from './input/ProInput.svelte';
	import type FabledAttribute from '$api/fabled-attribute.svelte';
	import AttributeSection     from './attributes/AttributeSection.svelte';
	import AttributeStats       from './attributes/AttributeStats.svelte';

	interface Props {
		data: FabledAttribute;
	}

	let { data = $bindable() }: Props = $props();
	$effect(() => data.save());
</script>

{#if data}
	<ProInput label='Name'
						tooltip='The name of the attribute. This should not contain color codes'
						bind:value={data.name} />
	<ProInput label='Display'
						tooltip='The name of the attribute as it will be displayed in the game'
						bind:value={data.display} />
	<ProInput label='Max'
						tooltip='The maximum points of this attribute that a player can have'
						bind:value={data.max} />
	<ProInput label='Cost'
						tooltip='How many attribute points it costs to upgrade the attribute, increasing with the level of the attribute'>
		<AttributeInput value={data.cost} />
	</ProInput>
	<IconInput bind:icon={data.icon} />
	<AttributeSection name='Target' section={data.targets} />
	<AttributeSection name='Condition' section={data.conditions} />
	<AttributeSection name='Mechanic' section={data.mechanics} />
	<AttributeStats stats={data.stats} />
{/if}