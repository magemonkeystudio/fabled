<script lang="ts">
  export let value: string[] = [];
  export let wrap = false;
  let str: string = value.join("\n");
  $: value = str.split("\n");
</script>

<textarea rows="5" bind:value={str}
style:white-space={wrap ? "wrap" : "nowrap"}/>

<style>
  textarea {
      width: 100%;
      resize: none;
  }
</style>