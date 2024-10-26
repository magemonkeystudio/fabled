<script lang='ts'>
    import { run } from 'svelte/legacy';

	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';

    interface Props {
        data: string;
        name?: string | undefined;
        tooltip?: string | undefined;
    }

    let { data = $bindable(), name = '', tooltip = undefined }: Props = $props();

	const dispatch = createEventDispatcher();
	run(() => {
        if (data || !data) dispatch('save');
    });
</script>

<ProInput label={name} {tooltip}>
	<div class='wrap'>
		<input bind:value={data} class='text' />
		<input type='color' bind:value={data} />
	</div>
</ProInput>

<style>
    .wrap {
        display: flex;
        flex-direction: row;
        align-items: stretch;
        justify-content: center;
    }

    .text {
        width: 30%;
        flex: 1;
    }

    input[type="color"] {
        padding: 0;
        height: auto;
        display: block;
        flex: 2.5;
    }
</style>