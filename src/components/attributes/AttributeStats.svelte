<script lang='ts'>
	import AttributeStat           from './AttributeStat.svelte';
	import Control                 from '$components/control/Control.svelte';
	import type { AttributeStats } from '$api/fabled-attribute.svelte';

	interface Props {
		stats: AttributeStats;
	}

	let { stats = $bindable() }: Props = $props();
</script>

<div class='stats'>
	<div class='header'>Stat modifiers</div>
	{#each stats.stats as stat}
		<AttributeStat bind:stats={stats} stat={stat} />
	{/each}
	<div class='btn'>
		<Control color='gray' icon='add' onclick={() => stats.addStat()}
						 onkeypress={() => stats.addStat()}
						 title={`Add Stat`} />
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