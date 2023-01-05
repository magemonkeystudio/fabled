import type { Writable } from "svelte/store";
import { get, writable } from "svelte/store";
import type { SapiClass, Skill } from "../api/types";

export const active: Writable<SapiClass | Skill> = writable();
export const activeType: Writable<'class' | 'skill'> = writable();
export const showSidebar: Writable<boolean> = writable(false);
export const isShowClasses: Writable<boolean> = writable(true);
export const importing: Writable<boolean> = writable(false);
export const classes: SapiClass[] = [
  {
    name: "Honor Guard",
    group: 'class',
    manaName: '&2Mana',
    maxLevel: 40,
    permission: false,
    expSources: [],
    health: 20,
    healthModifier: 1,
    mana: 20,
    manaModifier: 1,
    manaRegen: 1,
    attributes: {
      vitality: {
        base: 2,
        modifier: 1
      }
    },
    skillTree: 'Requirement',
    skills: [],
    icon: {
      material: 'Pumpkin',
      customModelData: 0,
      lore: 'This is a class'
    },
    usableItems: []
  },
  {
    name: "Assassin",
    group: 'class',
    manaName: '&2Mana',
    maxLevel: 40,
    permission: false,
    expSources: [],
    health: 20,
    healthModifier: 1,
    mana: 20,
    manaModifier: 1,
    manaRegen: 1,
    attributes: {
      vitality: {
        base: 2,
        modifier: 1
      }
    },
    skillTree: 'Requirement',
    skills: [],
    icon: {
      material: 'Pumpkin',
      customModelData: 0,
      lore: 'This is a class'
    },
    usableItems: []
  },
  {
    name: "Archer",
    group: 'class',
    manaName: '&2Mana',
    maxLevel: 40,
    permission: false,
    expSources: [],
    health: 20,
    healthModifier: 1,
    mana: 20,
    manaModifier: 1,
    manaRegen: 1,
    attributes: {
      vitality: {
        base: 2,
        modifier: 1
      }
    },
    skillTree: 'Requirement',
    skills: [],
    icon: {
      material: 'Pumpkin',
      customModelData: 0,
      lore: 'This is a class'
    },
    usableItems: []
  }
];
export const skills: Skill[] = [
  {
    name: 'Particle Blast',
    type: 'Dynamic',
    maxLevel: 40,
    permission: false,
    levelReq: 1,
    levelReqModifier: 0,
    cost: 0,
    costModifier: 0,
    cooldown: 0,
    cooldownModifier: 0,
    cooldownMessage: false,
    mana: 3,
    manaModifier: 0,
    minSpent: 0,
    minSpentModifier: 0,
    castMessage: '&6{player} &2has cast &6{skill}',
    indicator: '2D',
    icon: {
      material: 'Diamond',
      customModelData: 0
    },
    triggers: []
  },
  {
    name: 'Poison Dart',
    type: 'Dynamic',
    maxLevel: 40,
    permission: false,
    levelReq: 1,
    levelReqModifier: 0,
    cost: 0,
    costModifier: 0,
    cooldown: 0,
    cooldownModifier: 0,
    cooldownMessage: false,
    mana: 3,
    manaModifier: 0,
    minSpent: 0,
    minSpentModifier: 0,
    castMessage: '&6{player} &2has cast &6{skill}',
    indicator: '2D',
    icon: {
      material: 'Diamond',
      customModelData: 0
    },
    triggers: []
  },
  {
    name: 'Storm',
    type: 'Dynamic',
    maxLevel: 40,
    permission: false,
    levelReq: 1,
    levelReqModifier: 0,
    cost: 0,
    costModifier: 0,
    cooldown: 0,
    cooldownModifier: 0,
    cooldownMessage: false,
    mana: 3,
    manaModifier: 0,
    minSpent: 0,
    minSpentModifier: 0,
    castMessage: '&6{player} &2has cast &6{skill}',
    indicator: '2D',
    icon: {
      material: 'Diamond',
      customModelData: 0
    },
    triggers: []
  }
];

export const setActive = (act: SapiClass | Skill, type: 'class' | 'skill') => {
  active.set(act);
  activeType.set(type);
}
export const toggleSidebar = () => showSidebar.set(!get(showSidebar));
export const showClasses = () => isShowClasses.set(true);
export const showSkills = () => isShowClasses.set(false);
export const setImporting = (bool: boolean) => importing.set(bool);