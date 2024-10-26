<script lang="ts">

	import ProInput from '$input/ProInput.svelte';
	import { createEventDispatcher } from 'svelte';
	import AttributeInput from '$input/AttributeInput.svelte';
	import type { Attribute } from '$api/stat';

	interface Props {
		data: Attribute;
		name?: string | undefined;
		tooltip?: string | undefined;
	}

	let { data = $bindable(), name = '', tooltip = undefined }: Props = $props();

	const dispatch = createEventDispatcher();
	$effect(() => {
		if (data) dispatch('save');
	});
</script>

<ProInput label={name} {tooltip}>
	<AttributeInput bind:value={data} />
</ProInput>
