<script lang="ts">
  export let value: string[] = [];
  $: if (!value) value = [];

  const press = (e: KeyboardEvent) => value = (<HTMLTextAreaElement>e.target).value.split(/\r?\n/g)
</script>

<textarea rows="5" value={value.join("\r\n")}
          on:keyup={press} />

<style>
    textarea {
        width: 100%;
    }
</style>