<script lang='ts'>
	export let value: string[] = [];
	$: if (!value) value = [];

	const press = (e: KeyboardEvent) => {
		const split = (<HTMLTextAreaElement>e.target).value.split(/\r?\n/g);
		if (split.length === 1 && split[0] === '') {
			value = [];
			return;
		}
		value = split;
	};
</script>

<textarea rows='5' value={value.join("\r\n")}
					on:keyup={press} />
<style>
    textarea {
        width: 100%;
    }
</style>