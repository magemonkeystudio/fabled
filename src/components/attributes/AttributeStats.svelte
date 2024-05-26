<script lang='ts'>
	import AttributeStatSvelte     from './AttributeStat.svelte';
	import Control                 from '$components/control/Control.svelte';
	import type { AttributeStats } from '$api/fabled-attribute';
	import { updateSidebar }       from '../../data/store';

	export let stats: AttributeStats;
	$: statArray = stats.stats;
	$: {
		if ($statArray && stats.attribute?.name) updateSidebar();
		stats.attribute.save();
	}
</script>

<div class='stats'>
	<div class='header'>Stat modifiers</div>
	{#each $statArray as stat}
		<AttributeStatSvelte bind:stats={stats} stat={stat} />
	{/each}
	<div class='btn'>
		<Control title={`Add Stat`} icon='add' color='gray'
						 on:click={() => stats.addStat()}
						 on:keypress={() => stats.addStat()} />
	</div>
</div>

<style>
    .stats {
        grid-column: 1 / -1;
    }

    .header {
        grid-column: 1 / -1;
        text-align: center;
        font-size: 1.2em;
        font-weight: bold;
        padding-bottom: 0.5rem;
    }

    .header::before {
        content: ' ';
        display: block;
        width: 40%;
        height: 1px;
        background: white;
        margin: 1rem auto;
    }

    .btn {
        width: 12%;
        margin: 0.3rem auto;
    }
</style>