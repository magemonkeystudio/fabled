<script lang="ts">
  export let value: string[] = [];
  export let wrap = false;

  const press = (e: KeyboardEvent) => value = (<HTMLTextAreaElement>e.target).value.split("\n");
</script>

<textarea rows="5" value={value.join("\n")}
          style:white-space={wrap ? "wrap" : "nowrap"}
          on:keyup={press} />

<style>
    textarea {
        width: 100%;
        resize: none;
    }
</style>