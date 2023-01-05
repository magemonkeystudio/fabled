import type { Writable } from "svelte/store";
import { get, writable } from "svelte/store";

export const showSidebar: Writable<boolean> = writable(false);
export const isShowClasses: Writable<boolean> = writable(true);
export const importing: Writable<boolean> = writable(false);
export const classes: string[] = [
  "Honor Guard",
  "Assassin",
  "Archer"
];
export const skills: string[] = [
  "Particle Blast",
  "Poison Dart",
  "Storm"
];

export const toggleSidebar = () => showSidebar.set(!get(showSidebar));

export const showClasses = () => {
  isShowClasses.set(true);
};

export const showSkills = () => {
  isShowClasses.set(false);
};

export const setImporting = (bool: boolean) => {
  importing.set(bool);
};