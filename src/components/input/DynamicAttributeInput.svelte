<script lang='ts'>

	import ProInput       from '$input/ProInput.svelte';
	import AttributeInput from '$input/AttributeInput.svelte';
	import { Attribute }  from '$api/stat';

	export let value: Attribute[] = [];

	let name = '';
	$: attribute = new Attribute(name, 0, 0);

	const addAttribute = (e?: Event) => {
		if (!name) return;
		if (e && e.type === 'keypress' && (e as KeyboardEvent).key !== 'Enter') return;
		value = [...value, attribute];
		name  = '';
	};

	const removeAttribute = (attribute: Attribute) => {
		value = value.filter(a => a !== attribute);
	};
</script>

{#if value.length > 0}
	<div class='wrapper'>
		{#each value as attribute}
			<div class='attr'
					 on:click={() => removeAttribute(attribute)}
					 on:keypress={() => removeAttribute(attribute)}
					 tabindex='0'
					 role='button'
					 aria-label='Remove Attribute'
					 title='Remove Attribute'
			>
				<div class='name'>{attribute.name}</div>
				<div class='value'>{attribute.base}+({attribute.scale})</div>
			</div>
		{/each}
	</div>
{/if}

<ProInput label='Attribute Name' placeholder='Attribute Name' bind:value={name} />
<ProInput label='{name} Value'>
	<AttributeInput bind:value={attribute} />
</ProInput>

<div class='btn'
		 class:disabled={!name}
		 on:click={addAttribute}
		 on:keypress={addAttribute}
		 tabindex='0'
		 role='button'
		 aria-label='Add Attribute'
		 title='Add Attribute'>Add Attribute
</div>


<style>
    .wrapper {
        display: flex;
        gap: 0.5rem;
        flex-wrap: wrap;
        width: 40%;
        margin: 0 auto 1rem;
        grid-column: 1 / -1;
        justify-content: center;
    }

    .btn {
        margin: 1rem auto;
        grid-column: 1 / -1;
        padding: 0.5rem 1rem;
        border-radius: 0.5rem;
        background-color: #333;
        text-align: center;
        cursor: pointer;
    }

    .attr {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        padding: 0.5rem 1rem;
        border-radius: 1rem;
        background-color: #0083ef;
        color: #fff;
        cursor: pointer;
        user-select: none;
    }

    .name {
        font-weight: bold;
        font-size: 1.1rem;
    }

    .value {
        font-size: 0.9rem;
    }
</style>