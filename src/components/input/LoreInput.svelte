<script lang="ts">
  export let value: string[] = [];
  export let wrap = false;

  $: if (!value) value = [];

  const press = (e: KeyboardEvent) => value = (<HTMLTextAreaElement>e.target).value.split(/\r?\n/g);
</script>

<textarea rows="5" value={value.join("\r\n")}
          style:white-space={wrap ? "wrap" : "nowrap"}
          on:keyup={press} />

<style>
    textarea {
        width: 100%;
        resize: none;
    }
</style>