<script lang='ts'>
	interface Props {
		value: string[];
	}

	let { value = $bindable() }: Props = $props();
	$effect.pre(() => {
		if (!value) value = [];
	});

	const press = (e: KeyboardEvent) => {
		const split = (<HTMLTextAreaElement>e.target).value.split(/\r?\n/g);
		if (split.length === 1 && split[0] === '') {
			value = [];
			return;
		}
		value = split;
	};
</script>

<textarea onkeyup={press} rows='5'
>{value.join('\r\n')}</textarea>

<style>
    textarea {
        width: 100%;
    }
</style>