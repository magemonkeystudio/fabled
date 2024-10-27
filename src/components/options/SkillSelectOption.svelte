<script lang='ts'>
	import ProInput                    from '$input/ProInput.svelte';
	import SearchableSelect            from '$input/SearchableSelect.svelte';
	import FabledSkill, { skillStore } from '../../data/skill-store.svelte.js';

	interface Props {
		data?: FabledSkill[] | FabledSkill | string[] | string;
		name?: string | undefined;
		tooltip?: string | undefined;
		multiple?: boolean;
		onsave?: () => void;
	}

	let { data = $bindable([]), name = '', tooltip = undefined, multiple = true, onsave }: Props = $props();

	const skills = skillStore.skills;

	$effect.pre(() => {
		console.log(data);
		if (!multiple && !data) data = '';

		if (data instanceof Array) {
			data = data
				.map((cl) => {
					if (cl instanceof FabledSkill) return cl;

					const sk = skillStore.getSkill(cl);
					if (sk) return sk;
				})
				.filter((cl) => !!cl); // Remove any undefined values
		} else {
			if (data && !(data instanceof FabledSkill)) {
				const sk = skillStore.getSkill(<string>data);
				if (sk) data = sk;
			}
		}
		onsave?.();
	});
</script>

<ProInput label={name} {tooltip}>
	<SearchableSelect bind:data={$skills} {multiple} bind:selected={data} />
</ProInput>
