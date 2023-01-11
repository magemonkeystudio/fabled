<script lang="ts">
  import type { Attribute } from "../../api/types";

  export let value: Attribute;
</script>

<div class="wrap">
  <input class="base"
         type="number"
         bind:value={value.base} />
  +(
  <input class="modifier"
         type="number"
         bind:value={value.scale} />
  )
</div>

<style>
    input[type="number"]::-webkit-outer-spin-button,
    input[type="number"]::-webkit-inner-spin-button {
        -webkit-appearance: none;
        margin: 0;
    }

    input[type="number"] {
        -moz-appearance: textfield;
    }

    .wrap {
        display: inline-block;
        padding-inline: 0.25rem;
        background-color: var(--color-select-bg);
    }

    .base {
        width: 5rem;
    }

    .modifier {
        width: 3rem;
    }
</style>