import type { Writable } from "svelte/store";
import { get, writable } from "svelte/store";
import { ProClass } from "../api/proclass";
import { ProSkill } from "../api/proskill";
import { ProFolder } from "../api/profolder";
import { ProAttribute } from "../api/proattribute";
import { YAMLObject } from "../api/yaml";
import { browser } from "$app/environment";

export const active: Writable<ProClass | ProSkill> = writable();
export const activeType: Writable<"class" | "skill"> = writable();
export const dragging: Writable<ProClass | ProSkill | ProFolder> = writable();
export const showSidebar: Writable<boolean> = writable(true);
export const sidebarOpen: Writable<boolean> = writable(true);
export const isShowClasses: Writable<boolean> = writable(true);
export const importing: Writable<boolean> = writable(false);
export const skills: Writable<ProSkill[]> = writable([
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
]);
export const classes: Writable<ProClass[]> = writable([
  new ProClass({
    name: "Honor Guard",
    group: "class",
    manaName: "&2Mana",
    maxLevel: 40,
    permission: false,
    health: new ProAttribute("Health", 20, 1),
    mana: new ProAttribute("Mana", 20, 1),
    manaRegen: 1,
    attributes: [
      new ProAttribute("Vitality", 2, 1)
    ],
    skillTree: "Requirement",
    skills: [],
    icon: {
      material: "Pumpkin",
      customModelData: 0,
      lore: ["This is a class", "Lore line two"]
    },
    unusableItems: []
  }),
  new ProClass({
    name: "Assassin",
    group: "class",
    manaName: "&2Mana",
    maxLevel: 40,
    permission: false,
    health: new ProAttribute("Health", 20, 1),
    mana: new ProAttribute("Mana", 20, 1),
    manaRegen: 1,
    attributes: [
      new ProAttribute("Vitality", 2, 1),
      new ProAttribute("Bravery", 2, 1)
    ],
    skillTree: "Requirement",
    skills: [get(skills)[0], get(skills)[1]],
    icon: {
      material: "Pumpkin",
      customModelData: 0,
      lore: ["This is\" a class"]
    },
    unusableItems: []
  }),
  new ProClass({
    name: "Archer",
    group: "class",
    manaName: "&2Mana",
    maxLevel: 40,
    permission: false,
    health: new ProAttribute("Health", 20, 1),
    mana: new ProAttribute("Mana", 20, 1),
    manaRegen: 1,
    attributes: [
      new ProAttribute("Vitality", 2, 1)
    ],
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
export const skillFolders: Writable<ProFolder[]> = writable([
  new ProFolder([new ProFolder([get(skills)[0]])])
]);

export const setActive = (act: ProClass | ProSkill, type: "class" | "skill") => {
  active.set(act);
  activeType.set(type);
};

export const updateSidebar = () => {
  if (!get(showSidebar)) return;
  if (get(activeType) == "class") classes.set(get(classes));
  else skills.set(get(skills));
  updateFolders();
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
};

export const addSkill = () => {
  const sk = get(skills);
  sk.push(new ProSkill({ name: "Skill " + (sk.length + 1) }));

  skills.set(sk);
};

export const addClassFolder = (folder: ProFolder) => {
  const folders = get(classFolders);
  if (folders.includes(folder)) return;

  rename(folder, folders);

  folders.push(folder);
  folders.sort((a, b) => a.name.localeCompare(b.name));
  classFolders.set(folders);
};

export const addSkillFolder = (folder: ProFolder) => {
  const folders = get(skillFolders);
  if (folders.includes(folder)) return;

  rename(folder, folders);

  folders.push(folder);
  folders.sort((a, b) => a.name.localeCompare(b.name));
  skillFolders.set(folders);
};

export const rename = (folder: ProFolder, folders: Array<ProFolder | ProClass | ProSkill>) => {
  const origName = folder.name;
  let num = 1;
  while (folders.filter(f => f instanceof ProFolder && f.name == folder.name).length >= 1) {
    folder.name = origName + " (" + (num++) + ")";
  }
};

export const deleteFolder = (folder: ProFolder) => {
  if (folder.parent) {
    folder.parent.deleteFolder(folder);
    updateFolders();
  } else if (get(isShowClasses)) {
    const folders = get(classFolders).filter(f => f != folder);
    classFolders.set(folders);
  } else {
    const folders = get(skillFolders).filter(f => f != folder);
    skillFolders.set(folders);
  }
};

export const updateFolders = () => {
  if (!get(showSidebar)) return;
  if (get(isShowClasses)) {
    classFolders.set(sort<ProFolder>(get(classFolders)));
    classes.set(sort<ProClass>(get(classes)));
  } else {
    skillFolders.set(sort<ProFolder>(get(skillFolders)));
    skills.set(sort<ProSkill>(get(skills)));
  }
};

export const saveDataInternal = () => {
  if (!browser) return;
  const classList = get(classes);
  const skillList = get(skills);
  const classYaml = new YAMLObject();
  const skillYaml = new YAMLObject();
  classYaml.put("loaded", false);
  skillYaml.put("loaded", false);

  classList.forEach(c => classYaml.put(c.name, c.serializeYaml()));

  if (classList.length > 0)
    localStorage.setItem("classData", classYaml.toString());
  if (skillList.length > 0)
    localStorage.setItem("skillData", skillYaml.toString());
  console.log(skillYaml.toString());
};

const sort = <T extends ProFolder | ProClass | ProSkill>(data: T[]): T[] => {
  return data.sort((a, b) => a.name.localeCompare(b.name));
};

export const removeFolder = (folder: ProFolder) => {
  const classF = get(classFolders);
  const skillF = get(skillFolders);
  if (classF.includes(folder)) classFolders.set(classF.filter(f => f != folder));
  if (skillF.includes(folder)) skillFolders.set(skillF.filter(f => f != folder));
};

export const getFolder = (data: ProFolder | ProClass | ProSkill): (ProFolder | undefined) => {
  if (data instanceof ProFolder) return data.parent;
  const folders: ProFolder[] = data instanceof ProClass ? get(classFolders) : get(skillFolders);

  for (const folder of folders) {
    const containingFolder = folder.getContainingFolder(data);
    if (!containingFolder) continue;

    return containingFolder;
  }

  return undefined;
};