<script lang='ts'>
    import ProInput        from '$input/ProInput.svelte';
	import StringSelectOption from '$components/options/StringSelectOption.svelte';
	import type { AttributeStat, AttributeStats } from '$api/fabled-attribute';
	import Control from '$components/control/Control.svelte';
	import { updateSidebar } from '../../data/store';

    export let stats: AttributeStats;
    export let stat: AttributeStat;
	$: selected = stat.key;
    $: {
        if ($selected && stat.formula && stats.attribute?.name) updateSidebar();
        stats.attribute.save();
    }
</script>

<div class='stat'>
    <div class='btn-del'>
        <Control title='Delete stat' icon='delete' color='red'
            on:click={() => stats.removeStat(stat)}
            on:keypress={() => stats.removeStat(stat)}/>
    </div>
    <StringSelectOption bind:data={$selected} name={'Stat'} tooltip={'Attribute option to modify based on attribute value'}/>
    <StringSelectOption bind:data={stat.formula} name={'Formula'} tooltip='Formula to modify the option by'/>
</div>

<style>
    .stat {
        width: 33.3%;
        margin: 0.4em auto;
        padding: 0.3em;
        display: grid;
        grid-template-columns: 1fr 1fr;
        height: fit-content;
        border: 0.13rem solid #444;
        border-radius: 0.4em;
    }

    .btn-del {
        width: fit-content;
        margin: 0em 0.2em;
        position: absolute;
        align-self: center;
    }
</style>