<script lang='ts'>
	import StringSelectOption                     from '$components/options/StringSelectOption.svelte';
	import type { AttributeStat, AttributeStats } from '$api/fabled-attribute.svelte';
	import Control                                from '$components/control/Control.svelte';

	interface Props {
		stats: AttributeStats;
		stat: AttributeStat;
	}

	let { stats = $bindable(), stat = $bindable() }: Props = $props();
</script>

<div class='stat'>
	<div class='btn-del'>
		<Control title='Delete stat' icon='delete' color='red'
						 onclick={() => stats.removeStat(stat)}
						 onkeypress={() => stats.removeStat(stat)} />
	</div>
	<StringSelectOption bind:data={stat.key} name={'Stat'}
											tooltip={'Attribute option to modify based on attribute value'} />
	<StringSelectOption bind:data={stat.formula} name={'Formula'} tooltip='Formula to modify the option by' />
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
        margin: 0 0.2em;
        position: absolute;
        align-self: center;
    }
</style>