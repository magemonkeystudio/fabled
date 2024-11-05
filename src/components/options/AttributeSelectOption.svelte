<script lang='ts'>
	import ProInput            from '$input/ProInput.svelte';
	import AttributeInput      from '$input/AttributeInput.svelte';
	import type { IAttribute } from '$api/types';

	interface Props {
		data: IAttribute;
		name?: string | undefined;
		tooltip?: string | undefined;
		onsave?: () => void;
	}

	let { data = $bindable(), name = '', tooltip = undefined, onsave }: Props = $props();

	const changed = () => {
		return {
			base: data?.base !== undefined,
			scale: data?.scale !== undefined,
		}
	};

	$effect(() => {
		if (changed()) onsave?.();
	});
</script>

<ProInput label={name} {tooltip}>
	<AttributeInput bind:value={data} />
</ProInput>
