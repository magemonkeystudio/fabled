<script lang='ts'>
	import MaterialSelect            from '$input/MaterialSelect.svelte';
	import ProInput                  from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';

	export let data: { material: string, any?: boolean };
	export let tooltip: string | undefined = undefined;

	const dispatch = createEventDispatcher();
	$: if (data) dispatch('save');
</script>

<ProInput label='Material' tooltip='[material] {tooltip}'>
	<MaterialSelect bind:selected={data.material} bind:any={data.any} />
</ProInput>