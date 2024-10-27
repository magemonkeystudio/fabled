<script lang='ts'>
	import ProInput            from '$input/ProInput.svelte';
	import AttributeInput      from '$input/AttributeInput.svelte';
	import type { IAttribute } from '$api/types';

	interface Props {
		data: IAttribute;
		name?: string | undefined;
		tooltip?: string | undefined;
	}

	let { data = $bindable(), name = '', tooltip = undefined }: Props = $props();
</script>

<ProInput label={name} {tooltip}>
	<AttributeInput bind:value={data} />
</ProInput>
