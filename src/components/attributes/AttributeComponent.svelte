<script lang='ts'>
	import ProInput                                      from '$input/ProInput.svelte';
	import SearchableSelect                              from '$input/SearchableSelect.svelte';
	import AttributeComponentStat                        from './AttributeComponentStat.svelte';
	import type { AttributeComponent, AttributeSection } from '$api/fabled-attribute.svelte';
	import Control                                       from '$components/control/Control.svelte';

	interface Props {
		color: string;
		component: AttributeComponent;
		section: AttributeSection;
	}

	let { color, component, section }: Props = $props();

	$effect(() => component.section.attribute.save());
</script>

<div class='component' style:--comp-color={color}>
	<div class='componentTitle'>
		<ProInput label={'Component'} tooltip={'The component to modify options to'}>
			<SearchableSelect bind:selected={component.name} data={section.availableComponents} multiple={false} />
		</ProInput>
	</div>
	{#each component.stats as stat}
		<AttributeComponentStat {stat} {component} />
	{/each}
	<div class='controls'>
		{#if component.availableStats.length > 0}
			<Control title='Add Stat' icon='add' color='gray'
							 onclick={() => component.addStat(component.availableStats[0])}
							 onkeypress={() => component.addStat(component.availableStats[0])} />
		{/if}
		<Control title='Delete Component' icon='delete' color='red'
						 onclick={() => section.removeComponent(component)}
						 onkeypress={() => section.removeComponent(component)} />
	</div>
</div>

<style>
    .component {
        width: 40%;
        background-color: #111;
        box-shadow: inset 0 0 0.5rem #222;
        border: 0.13rem solid #444;
        border-left: 0.3rem solid var(--comp-color);
        border-radius: 0 0.4rem 0.4rem 0;
        margin: 0.3em auto;
    }

    .componentTitle {
        display: grid;
        grid-template-columns: 1fr 1fr;
        margin: 0.5em auto -0.2em auto;
        padding: 0em 2.4em;
        height: fit-content;
        color: var(--comp-color);
    }

    .componentTitle::after {
        grid-column: 1 / -1;
        content: ' ';
        display: block;
        width: 99%;
        height: 1px;
        background: var(--comp-color);
        margin: 0.5rem auto;
    }

    .controls {
        display: flex;
        justify-content: stretch;
        align-items: center;
        margin: 0.5em auto;
        width: fit-content;
    }
</style>