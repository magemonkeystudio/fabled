import type { VersionData } from "../api/types";
import type { Writable } from "svelte/store";
import { writable } from "svelte/store";
import { DATA_1_19 } from "./1.19";
import { DATA_1_18 } from "./1.18";
import { DATA_1_17 } from "./1.17";
import { DATA_1_16 } from "./1.16";
import { browser } from "$app/environment";
import { localStore } from "../api/api";

export const expSources = ["Mob", "Block Break", "Block Place", "Craft", "Command", "Special", "Exp Bottle", "Smelt", "Quest"];

type Versions = "19" | "18" | "17" | "16";

const VERSIONS = {
  "19": DATA_1_19,
  "18": DATA_1_18,
  "17": DATA_1_17,
  "16": DATA_1_16
};
const versionKeys: string[] = Object.keys(VERSIONS).reverse();

export let DATA: VersionData = VERSIONS[<Versions>versionKeys[0]];

export const serverOptions: string[] = [];
versionKeys.forEach((v: string) => serverOptions.push("1." + v));
export const version: Writable<Versions> = localStore<Versions>("server-version", <Versions>versionKeys[0]);
version.subscribe((ver: Versions) => DATA = VERSIONS[ver]);

export const getMaterials = () => {
  return DATA.MATERIALS;
};

export const getDamageableMaterials = () => {
  return DATA.DAMAGEABLE_MATERIALS;
};

export const getAnyMaterials = () => {
  return ["Any", ...DATA.MATERIALS];
};

export const getSounds = () => {
  return DATA.SOUNDS;
};

export const getEntities = () => {
  return DATA.ENTITIES;
};

export const getAnyEntities = () => {
  return ["Any", ...DATA.ENTITIES];
};

export const getParticles = () => {
  return DATA.PARTICLES || [];
};

export const getBiomes = () => {
  return DATA.BIOMES;
};

export const getDamageTypes = () => {
  return DATA.DAMAGE_TYPES;
};

export const getPotionTypes = () => {
  return DATA.POTIONS;
};

export const getAnyPotion = () => {
  return ["Any", ...DATA.POTIONS];
};

export const getGoodPotions = () => {
  const list = DATA.POTIONS.filter(type => GOOD_POTIONS.includes(type));
  return ["All", ...list];
};

export const getBadPotions = () => {
  const list = DATA.POTIONS.filter(type => BAD_POTIONS.includes(type));
  return ["All", ...list];
};

export const getDyes = () => {
  return DYES;
};

export const getProjectiles = () => {
  return DATA.PROJECTILES;
};

export const getAnyProjectiles = () => {
  return ["Any", ...DATA.PROJECTILES];
};

export const getMobDisguises = () => {
  return DATA.MOB_DISGUISES;
};

export const getMiscDisguises = () => {
  return DATA.MISC_DISGUISES;
};

const GOOD_POTIONS: string[] = [
  "Speed",
  "Fast Digging",
  "Increase Damage",
  "Jump",
  "Regeneration",
  "Damage Resistance",
  "Fire Resistance",
  "Water Breathing",
  "Invisibility",
  "Night Vision",
  "Health Boost",
  "Absorption",
  "Saturation",
  "Glowing",
  "Luck",
  "Slow Falling",
  "Conduit Power",
  "Dolphins Grace"
];

const BAD_POTIONS: string[] = [
  "Slow",
  "Slow Digging",
  "Confusion",
  "Blindness",
  "Hunger",
  "Weakness",
  "Poison",
  "Wither",
  "Levitation",
  "Unluck"
];

const DYES: string[] = [
  "BLACK",
  "BLUE",
  "BROWN",
  "CYAN",
  "GRAY",
  "GREEN",
  "LIGHT_BLUE",
  "LIGHT_GRAY",
  "LIME",
  "MAGENTA",
  "ORANGE",
  "PINK",
  "PURPLE",
  "RED",
  "WHITE",
  "YELLOW"
];
