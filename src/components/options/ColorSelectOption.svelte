<script lang='ts'>
	import ProInput from '$input/ProInput.svelte';

	interface Props {
		data: string;
		name?: string | undefined;
		tooltip?: string | undefined;
		onsave?: () => void;
	}

	let { data = $bindable(), name = '', tooltip = undefined, onsave }: Props = $props();

	// $effect(() => {
	// 	if (data || !data) onsave?.();
	// });
</script>

<ProInput label={name} {tooltip}>
	<div class='wrap'>
		<input bind:value={data} class='text' />
		<input bind:value={data} type='color' />
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