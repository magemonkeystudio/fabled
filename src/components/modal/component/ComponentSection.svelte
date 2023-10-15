<script lang='ts'>
	import { squash }             from '../../../data/squish';
	import type { RegistryEntry } from '$api/components/registry';
	import type ProComponent      from '$api/components/procomponent';

	export let sectionName                 = '';
	export let components: RegistryEntry[] = [];
	export let addComponent: (comp: { new: () => { defaultOpen: () => ProComponent } }) => void;

	let expanded = true;
</script>

<div class='comp-subsection'>
	<h4 class='comp-subsection-header' on:click={() => expanded = !expanded}>
		{sectionName}
		<span class='icon material-symbols-rounded' class:expanded>
			expand_more
	</span>
	</h4>
	{#if expanded}
		<div class='component-section' transition:squash={{duration: 200}}>
			{#each components as comp}
				<div class='comp-select'
						 on:click={() => addComponent(comp.component)}
						 on:keypress={(e) => { if (e.key === 'Enter') addComponent(comp.component); }}
						 class:deprecated={comp.component.new().isDeprecated}>
					{comp.name}
				</div>
			{/each}
		</div>
	{/if}
</div>

<style>
    .comp-subsection {
        flex-basis: 100%;
        margin-top: 0.5rem;
        padding: 0.25rem;
        background: rgba(56, 89, 138, 0.5);
        border-radius: 0.25rem;
    }

    .comp-subsection-header {
        margin: 0.5em;
    }

    .comp-subsection-header:hover {
        cursor: pointer;
    }

    .component-section {
        display: flex;
        flex-wrap: wrap;
    }

    .deprecated {
        text-decoration: line-through;
    }

    .icon {
        font-size: 1rem;
        margin-left: 0.25rem;
        vertical-align: middle;

        transition: transform 0.2s ease-in-out;
    }

    .expanded {
        transform: rotate(180deg);
    }
</style>