<script lang='ts'>
	import ProInput                                   from '$input/ProInput.svelte';
	import SearchableSelect                           from '$input/SearchableSelect.svelte';
	import StringSelectOption                         from '$components/options/StringSelectOption.svelte';
	import type { AttributeComponent, AttributeStat } from '$api/fabled-attribute.svelte';
	import Control                                    from '$components/control/Control.svelte';

	interface Props {
		stat: AttributeStat;
		component: AttributeComponent;
	}

	let { stat = $bindable(), component }: Props = $props();
</script>

<div class='stat'>
	<div class='btn-del'>
		<Control title='Delete stat' icon='delete' color='red'
						 onclick={() => component.removeStat(stat)}
						 onkeypress={() => component.removeStat(stat)} />
	</div>
	<ProInput label={'Option'} tooltip={'Attribute option to modify based on attribute value'}>
		<SearchableSelect bind:selected={stat.key} data={component.availableStats} multiple={false} />
	</ProInput>
	<StringSelectOption bind:data={stat.formula} name={'Formula'} tooltip='Formula to modify the option by' />
</div>

<style>
    .stat {
        margin: 0.4em 2em;
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