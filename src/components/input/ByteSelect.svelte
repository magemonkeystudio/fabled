<script lang='ts'>

	export let value = 273;
	export let data: string[];

</script>

<div class='input'>
	{#each data as entry, i}
		<div class='entry'
				 tabindex='0'
				 role='menuitem'
				 on:click={() => value = value ^ Math.pow(2, i)}
				 on:keypress={(e) => {
           if (e.key === 'Enter') {
             value = value ^ Math.pow(2, i);
           }
         }}
				 class:selected={value & Math.pow(2, i)}>
			{entry}
		</div>
	{/each}
</div>

<style>
    .input {
        background-color: var(--color-select-bg);
    }

    .entry {
        padding: 0.2rem 0.5rem;
        user-select: none;
        -webkit-user-select: none;
        transition: background-color 0.2s ease-in-out;
    }

    .entry:hover {
        cursor: pointer;
    }

    .selected {
        background-color: #005193;
    }
</style>