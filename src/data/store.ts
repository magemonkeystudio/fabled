import type { Writable } from "svelte/store";
import { get, writable } from "svelte/store";
import { ProClass } from "../api/proclass";
import { ProSkill } from "../api/proskill";
import { ProFolder } from "../api/profolder";

export const active: Writable<ProClass | ProSkill> = writable();
export const activeType: Writable<"class" | "skill"> = writable();
export const showSidebar: Writable<boolean> = writable(true);
export const isShowClasses: Writable<boolean> = writable(true);
export const importing: Writable<boolean> = writable(false);
export let skills: ProSkill[] = [
  new ProSkill({
    name: "Particle Blast",
    type: "Dynamic",
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
    castMessage: "&6{player} &2has cast &6{skill}",
    indicator: "2D",
    icon: {
      material: "Diamond",
      customModelData: 0
    },
    triggers: []
  }),
  new ProSkill({
    name: "Poison Dart",
    type: "Dynamic",
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
    castMessage: "&6{player} &2has cast &6{skill}",
    indicator: "2D",
    icon: {
      material: "Diamond",
      customModelData: 0
    },
    triggers: []
  }),
  new ProSkill({
    name: "Storm",
    type: "Dynamic",
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
    castMessage: "&6{player} &2has cast &6{skill}",
    indicator: "2D",
    icon: {
      material: "Diamond",
      customModelData: 0
    },
    triggers: []
  })
];
export const classes: Writable<ProClass[]> = writable([
  new ProClass({
    name: "Honor Guard",
    group: "class",
    manaName: "&2Mana",
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
    skillTree: "Requirement",
    skills: [],
    icon: {
      material: "Pumpkin",
      customModelData: 0,
      lore: ["This is a class"]
    },
    unusableItems: []
  }),
  new ProClass({
    name: "Assassin",
    group: "class",
    manaName: "&2Mana",
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
    skillTree: "Requirement",
    skills: [skills[0], skills[1]],
    icon: {
      material: "Pumpkin",
      customModelData: 0,
      lore: ["This is a class"]
    },
    unusableItems: []
  }),
  new ProClass({
    name: "Archer",
    group: "class",
    manaName: "&2Mana",
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
    skillTree: "Requirement",
    skills: [],
    icon: {
      material: "Pumpkin",
      customModelData: 0,
      lore: ["This is a class"]
    },
    unusableItems: []
  })
]);
export const classFolders: Writable<ProFolder[]> = writable([
  new ProFolder([new ProFolder([get(classes)[0]])])
]);

export const setActive = (act: ProClass | ProSkill, type: "class" | "skill") => {
  active.set(act);
  activeType.set(type);
};
export const toggleSidebar = () => showSidebar.set(!get(showSidebar));
export const closeSidebar = () => showSidebar.set(false);
export const showClasses = () => isShowClasses.set(true);
export const showSkills = () => isShowClasses.set(false);
export const setImporting = (bool: boolean) => importing.set(bool);

export const addClass = () => {
  const cl = get(classes);
  cl.push(new ProClass({ name: "Class " + (cl.length + 1) }));

  classes.set(cl);
  console.log(cl);
};

export const deleteFolder = (folder: ProFolder) => {
  if (folder.parent) {
    folder.parent.deleteFolder(folder);
    updateFolders();
  } else {
    const folders = get(classFolders).filter(f => f != folder);
    classFolders.set(folders);
  }
};

export const updateFolders = () => {
  classFolders.set(get(classFolders));
};