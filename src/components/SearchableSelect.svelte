<script lang="ts">
  import { fly } from "svelte/transition";
  import { flip } from "svelte/animate";
  import { clickOutside } from "../api/clickoutside";

  export let id: string;
  export let data: any[] = [];
  export let display: (input: any) => string = (input: any) => input.toString();
  export let selected: any[] = [];
  export let filtered: any[] = [];

  let focused = false;
  let input: HTMLElement;
  let criteria: string;

  const select = (item: any, event: KeyboardEvent) => {
    if (event && event?.key != "Enter" && event?.key != " ") return;
    if (selected.includes(item)) return;

    selected.push(item);
    selected = [...selected];
    criteria = "";
    input.focus();
  };

  const remove = (e: MouseEvent, item: any) => {
    e.stopPropagation();
    selected = selected.filter(s => s != item);
  };

  const clickOut = () => {
    focused = false;
    criteria = "";
  };

  $: filtered = data.filter(s => criteria && display(s).toLowerCase().includes(criteria.toLowerCase()) && !selected.includes(s));
</script>

<div id="wrapper"
     use:clickOutside
     on:outclick={clickOut}>
  <div {id} class="input"
       on:click={() => input.focus()}
       class:focused={focused}>
    {#each selected as sel (display(sel))}
      <div class="chip"
           title="Click to remove"
           transition:fly={{y: -25}}
           animate:flip={{duration: 500}}
           on:click={(e) => remove(e, sel)}>{display(sel)}</div>
    {/each}
    {#if !focused && !criteria && (!selected || selected.length == 0)}
      <span class="placeholder" in:fly={{y: 25, delay: 250}}>No Skills</span>
    {/if}
    <div bind:this={input}
         bind:textContent={criteria}
         class="input-box"
         contenteditable
         on:focus={() => focused = true}
         on:blur={() => focused = false}>
    </div>
    <div class="clear" />
  </div>

  {#if filtered.length > 0}
    <div class="select-wrapper">
      <div class="select">
        {#each filtered as datum}
          <div on:click={() => select(datum)}
               tabindex="0"
               on:keypress={(e) => select(datum, e)}>{display(datum)}</div>
        {/each}
      </div>
    </div>
  {/if}
</div>


<style>
    .input {
        background-color: var(--color-select-bg);
        padding: 0.2rem 0.5rem;
    }

    .chip {
        float: left;
        margin-right: 0.4rem;
    }

    .chip:hover {
        cursor: pointer;
    }

    .placeholder {
        display: inline-block;

    }

    .input-box {
        display: inline-block;
    }

    .input-box:focus {
        outline: none;
    }

    .focused {
        outline: -webkit-focus-ring-color auto 1px;
    }

    .select {
        position: absolute;
        z-index: 5;
        width: 100%;
        border: 2px solid var(--color-accent);
        background-color: var(--color-select-bg);
    }

    .select > * {
        padding: 0.25rem 0.5rem;
    }

    .select > *:hover {
        background-color: #0083ef;
        cursor: pointer;
    }
</style>