<!--suppress CssUnresolvedCustomProperty -->
<script lang='ts'>
	interface Props {
		selectedTab: number;
		data?: string[];
		color?: string;
		inline?: boolean;
	}

	let {
				selectedTab = $bindable(),
				data        = ['True', 'False'],
				color       = '#222',
				inline      = true
			}: Props = $props();
</script>

<div class='tabs' class:selected={selectedTab} style:--color={color} style:--count={data.length}
		 style:--selected={selectedTab} class:inline>
	{#each data as tab, i}
		<div onclick={() => selectedTab = i}
				 onkeypress={(e) => { if(e.key === 'Enter') selectedTab = i }}
				 tabindex='0'
				 role='radio'
				 aria-checked='{selectedTab == i}'
		>{tab}</div>
	{/each}
</div>

<style>
    .tabs {
        overflow: hidden;
        position: relative;
        display: flex;
        text-align: center;
        background-color: var(--color);
        border-radius: 0.4rem;
        user-select: none;
        -webkit-user-select: none;
    }

    .inline {
        display: inline-flex;
    }

    .tabs:before {
        content: '';
        height: 100%;
        width: calc(100% / var(--count));
        border-radius: 0.4rem;
        background-color: #0083ef;
        position: absolute;
        left: calc(100% / var(--count) * var(--selected));
        transition: left 350ms ease-in-out;
    }

    .tabs > div {
        position: relative;
        flex: 1;
        width: 5%;
        padding: 0.2rem;
        padding-inline: 1.5rem;

        display: flex;
        justify-content: center;
    }

    .tabs > div:hover {
        cursor: pointer;
    }
</style>