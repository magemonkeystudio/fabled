import type { Readable, Writable } from "svelte/store";
import { derived, get, writable } from "svelte/store";
import ProClass from "../api/proclass";
import ProSkill from "../api/proskill";
import { ProFolder } from "../api/profolder";
import { YAMLObject } from "../api/yaml";
import { browser } from "$app/environment";
import {
  classFolders,
  deleteClass,
  deleteClassFolder,
  loadClasses,
  loadClassText,
  refreshClasses,
  refreshClassFolders
} from "./class-store";
import { localStore, sort } from "../api/api";
import { loadAttributes } from "./attribute-store";

export const active: Writable<ProClass | ProSkill | undefined> = writable(undefined);
export const activeType: Readable<"class" | "skill"> = derived(
  active,
  $active => $active instanceof ProClass ? "class" : "skill"
);
export const dragging: Writable<ProClass | ProSkill | ProFolder> = writable();
export const showSidebar: Writable<boolean> = localStore("sidebarOpen", true);
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
export const skillFolders: Writable<ProFolder[]> = writable([]);

export const updateSidebar = () => {
  if (!get(showSidebar)) return;
  if (get(activeType) == "class") refreshClasses();
  else skills.set(get(skills));
  updateFolders();
};
export const toggleSidebar = () => showSidebar.set(!get(showSidebar));
export const closeSidebar = () => showSidebar.set(false);
export const showClasses = () => isShowClasses.set(true);
export const showSkills = () => isShowClasses.set(false);
export const setImporting = (bool: boolean) => importing.set(bool);

export const getSkill = (name: string): ProSkill | undefined => {
  for (const sk of get(skills)) {
    if (sk.name == name) return sk;
  }

  return undefined;
};
export const isSkillNameTaken = (name: string): boolean => !!getSkill(name);
export const addSkill = (name?: string): ProSkill => {
  const sk = get(skills);
  const skill = new ProSkill({ name: (name || "Skill " + (sk.length + 1)) });
  sk.push(skill);

  skills.set(sk);
  return skill;
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
  } else if (get(isShowClasses)) deleteClassFolder(folder);
  else {
    const folders = get(skillFolders).filter(f => f != folder);
    skillFolders.set(folders);
  }
};

export const deleteProData = (data: ProClass | ProSkill | undefined) => {
  if (!data) return;

  getFolder(data)?.remove(data);
  if (data instanceof ProClass) deleteClass(data);
  skills.set(get(skills).filter(c => c != data));
  updateFolders();
};

export const updateFolders = () => {
  if (!get(showSidebar)) return;
  if (get(isShowClasses)) refreshClassFolders();
  else {
    skillFolders.set(sort<ProFolder>(get(skillFolders)));
    skills.set(sort<ProSkill>(get(skills)));
  }
};

export const saveDataInternal = () => {
  if (!browser) return;
  const skillList = get(skills);
  const skillYaml = new YAMLObject();
  skillYaml.put("loaded", false);

  if (skillList.length > 0)
    localStorage.setItem("skillData", skillYaml.toString());
  // console.log(skillYaml.toString());
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

/**
 * Loads skill data from a string
 */
export const loadSkillText = (text: string) => {
  // // Load new skills
  // const data = parseYAML(text);
  // for (let key in data) {
  //   if (data[key] instanceof YAMLObject && key != "loaded") {
  //     if (isSkillNameTaken(key)) {
  //       getSkill(key).load(data[key]);
  //       if (getSkill(key) == activeSkill) {
  //         activeSkill.apply();
  //         showSkillPage("builder");
  //       }
  //     } else {
  //       addSkill(key).load(data[key]);
  //     }
  //   }
  // }
};


export const loadSkills = (e: ProgressEvent<FileReader>) => {
  const text: string = <string>e.target?.result;
  if (!text) return;

  loadSkillText(text);
};

/**
 * Loads an individual skill or class file
 * @param e ProgressEvent
 */
export const loadIndividual = (e: ProgressEvent<FileReader>) => {
  const text: string = <string>e.target?.result;
  if (!text) return;

  if (text.indexOf("global:") >= 0) {
    loadAttributes(text);
  } else if (text.indexOf("components:") >= 0 || (text.indexOf("group:") == -1 && text.indexOf("combo:") == -1 && text.indexOf("skills:") == -1)) {
    loadSkillText(text);
  } else {
    loadClassText(text);
  }
  (<HTMLElement>document.activeElement).blur();
};

export const loadRaw = (text: string) => {
  if (text.indexOf("global:") >= 0) {
    loadAttributes(text);
  } else if (text.indexOf("components:") >= 0
    || (text.indexOf("group:") == -1 && text.indexOf("combo:") == -1 && text.indexOf("skills:") == -1)) {
    loadSkillText(text.replace("loaded: false\n", ""));
  } else {
    loadClassText(text.replace("loaded: false\n", ""));
  }
};

export const loadFile = (file: File) => {
  const reader = new FileReader();
  if (file.name.indexOf("skills") == 0) {
    reader.onload = loadSkills;
  } else if (file.name.indexOf("classes") == 0) {
    reader.onload = loadClasses;
  } else {
    reader.onload = loadIndividual;
  }
  reader.readAsText(file);
};

export const saveData = (data?: ProSkill | ProClass) => {
  const act = data || get(active);
  if (!act) return;

  saveToFile(act.name + ".yml", act.serializeYaml().toString());
};

/**
 * Saves text data to a file locally
 */
const saveToFile = (file: string, data: string) => {
  const textFileAsBlob = new Blob([data], { type: "text/plain;charset=utf-8" });

  const element = document.createElement("a");
  element.href = URL.createObjectURL(textFileAsBlob);
  element.download = file;
  element.style.display = "none";

  document.body.appendChild(element);
  element.click();
  document.body.removeChild(element);
};