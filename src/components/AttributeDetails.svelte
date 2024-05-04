<script lang='ts'>
	import IconInput        from './input/IconInput.svelte';
	import { updateSidebar }      from '../data/store';
	import AttributeInput         from './input/AttributeInput.svelte';
	import ProInput               from './input/ProInput.svelte';
	import type FabledAttribute from '$api/fabled-attribute';

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
    <div class='header'>Target modifiers</div> <!-- TODO -->
    <div class='header'>Condition modifiers</div>
    <div class='header'>Mechanic modifiers</div>
    <div class='header'>Stat modifiers</div>
{/if}

<style>
    .info {
        grid-column: 1 / span 2;
        text-align: center;
        margin-left: 5rem;
        color: rgba(255, 255, 255, 0.7);
        padding-top: 0.5rem;
        padding-bottom: 0.5rem;
    }

    .header {
        grid-column: 1 / -1;
        font-size: 1.2em;
        font-weight: bold;
        text-align: center;
        padding-bottom: 1rem;
    }

    .header::before {
        content: ' ';
        display: block;
        width: 40%;
        height: 1px;
        background: white;
        margin: 1rem auto;
    }

    .combos {
        cursor: pointer;
    }
</style>