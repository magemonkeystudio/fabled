<script lang='ts'>
	export let data: string;
</script>

<div>
	{data}
	<hr />
</div>

<style>
    div {
        grid-column: span 2;
        text-align: center;
        font-size: 1.25rem;
        font-weight: bold;
    }

    hr {
        width: 50%;
        margin-top: 0.25rem;
    }
</style>