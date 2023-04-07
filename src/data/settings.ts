import { browser }       from "$app/environment";
import type { Writable } from "svelte/store";
import { writable }      from "svelte/store";

export const useSymbols: Writable<boolean> = ((): Writable<boolean> => {
  let saved = false;
  if (browser) {
    const stored = localStorage.getItem("use-symbols");
    saved        = stored === "true";
  }

  const {
          subscribe,
          set,
          update
        } = writable<boolean>(saved);
  return {
    subscribe,
    set: (value: boolean) => {
      localStorage.setItem("use-symbols", String(value));
      return set(value);
    },
    update
  };
})();