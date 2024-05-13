<script lang='ts'>
	import type { AttributeSection } from '$api/fabled-attribute';
    import AttributeComponentSvelte   from '$components/attributes/AttributeComponent.svelte'
	import Control from '$components/control/Control.svelte';
	import { updateSidebar } from '../../data/store';

    export let name: 'Target' | 'Condition' | 'Mechanic';
    let color: string = name === 'Target' ? '#04af38' : name === 'Condition' ? '#feac00' : '#ff3a3a';
    export let section: AttributeSection
    $: components = section.components;
    $: availableComponents = section.availableComponents;
    $: {
        if ($components && section.attribute?.name) updateSidebar();
        section.attribute.save();
    }
</script>

<div class='section'>
    <div class='header'>{name} modifiers</div>
    {#each $components as component}
        <AttributeComponentSvelte color={color}
            bind:component={component} section={section}/>
    {/each}
    {#if $availableComponents.length > 0}
        <div class='btn'>
            <Control title={`Add ${name}`} icon='add' color={color}
                on:click={() => section.addComponent($availableComponents[0])}
                on:keypress={() => section.addComponent($availableComponents[0])}/>
        </div>
    {/if}
</div>

<style>
    .section {
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