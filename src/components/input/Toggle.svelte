<!--suppress CssUnresolvedCustomProperty -->
<script lang="ts">
  export let data: boolean;
  export let left = "True";
  export let right = "False";
  export let color = "#222";
  export let inline = true;
</script>

<input type="checkbox" class="hidden" id="permission" bind:checked={data} />
<div class="toggle" class:selected={data} style:--color={color} class:inline>
  <div on:click={() => data = true}>{left}</div>
  <div on:click={() => data = false}>{right}</div>
</div>

<style>
    .toggle {
        overflow: hidden;
        display: flex;
        text-align: center;
        background-color: var(--color);
        border-radius: 0.4rem;
        user-select: none;
        -webkit-user-select: none;
    }

    .inline {
        display: inline-flex;
    }

    .toggle:before {
        content: '';
        height: 100%;
        width: 50%;
        border-radius: 0.4rem;
        background-color: #0083ef;
        position: absolute;
        left: 0;
        transition: left 350ms ease-in-out;
    }

    .toggle:not(.selected):before {
        left: 50%;
    }

    .toggle > div {
        flex: 1;
        padding: 0.2rem;
        padding-inline: 1.5rem;
    }

    .toggle > div:hover {
        cursor: pointer;
    }
</style>