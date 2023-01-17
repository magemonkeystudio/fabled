import type { ProFolder } from "./profolder";
import type { ProClass } from "./proclass";
import type { ProSkill } from "./proskill";

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