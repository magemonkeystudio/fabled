import type ProFolder from "./profolder";
import type ProClass from "./proclass";
import type ProSkill from "./proskill";
import { browser } from "$app/environment";
import { writable } from "svelte/store";

export const toProperCase = (s: string) => {
  return s.replace("_", " ").toLowerCase()
    .replace(/^(.)|\s(.)/g, $1 => $1.toUpperCase());
};

export const toEditorCase = (s: string) => {
  s = s.replace("_", " ");
  return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
};

export const sort = <T extends ProFolder | ProClass | ProSkill>(data: T[]): T[] => {
  return data.sort((a, b) => a.name.localeCompare(b.name));
};

export const localStore = <T>(key: string, def: T) => {
  let saved: T = def;
  if (browser) {
    if (localStorage.getItem(key) === null) {
      localStorage.setItem(key, JSON.stringify(def));
    }

    saved = JSON.parse(<string>localStorage.getItem(key));
  }

  const { subscribe, set, update } = writable(saved);

  return {
    subscribe,
    set: (value: T) => {
      if (browser) localStorage.setItem(key, JSON.stringify(value));
      return set(value);
    },
    update
  };
};