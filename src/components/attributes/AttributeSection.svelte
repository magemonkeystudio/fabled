<script lang='ts'>
	import type { AttributeSection } from '$api/fabled-attribute.svelte';
	import AttributeComponent        from '$components/attributes/AttributeComponent.svelte';
	import Control                   from '$components/control/Control.svelte';

	interface Props {
		name: 'Target' | 'Condition' | 'Mechanic';
		section: AttributeSection;
	}

	let { name, section }: Props = $props();
	let color: string            = name === 'Target' ? '#04af38' : name === 'Condition' ? '#feac00' : '#ff3a3a';
</script>

<div class='section'>
	<div class='header'>{name} modifiers</div>
	{#each section.components as component}
		<AttributeComponent {color}
												{component}
												{section} />
	{/each}
	{#if section.availableComponents.length > 0}
		<div class='btn'>
			<Control title={`Add ${name}`} icon='add' color={color}
							 onclick={() => section.addComponent(section.availableComponents[0])}
							 onkeypress={() => section.addComponent(section.availableComponents[0])} />
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