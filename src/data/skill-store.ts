import type { Writable } from "svelte/store";
import { get, writable } from "svelte/store";
import ProFolder from "../api/profolder";
import { sort } from "../api/api";
import { parseYAML, YAMLObject } from "../api/yaml";
import { browser } from "$app/environment";
import ProSkill from "../api/proskill";
import { rename } from "./store";
import { ProAttribute } from "../api/proattribute";
import { triggers } from "../api/triggers";

const loadSkillTextToArray = (text: string): ProSkill[] => {
  const list: ProSkill[] = [];
  // Load skills
  const data: YAMLObject = parseYAML(text);
  let skill: ProSkill;
  // If we only have one skill, and it is the current YAML,
  // the structure is a bit different
  if (data.key && !data.data[data.key]) {
    const key: string = data.key;
    if (key === "loaded") return list;
    skill = new ProSkill({ name: key });
    skill.load(data);
    list.push(skill);
    return list;
  }

  for (const key in data.data) {
    if (key != "loaded" && data.data[key] instanceof YAMLObject) {
      skill = new ProSkill({ name: key });
      skill.load(data.data[key]);
      list.push(skill);
    }
  }
  return list;
};

const setupSkillStore = <T>(key: string,
                            def: T,
                            mapper: (data: string) => T,
                            setAction: (data: T) => T,
                            postLoad?: (saved: T) => void): Writable<T> => {
  let saved: T = def;
  if (browser) {
    const stored = localStorage.getItem(key);
    if (stored) {
      saved = mapper(stored);
      if (postLoad) postLoad(saved);
    }
  }

  const {
    subscribe,
    set,
    update
  } = writable<T>(saved);
  return {
    subscribe,
    set: (value: T) => {
      if (setAction) value = setAction(value);
      return set(value);
    },
    update
  };
};

export const skills: Writable<ProSkill[]> = setupSkillStore<ProSkill[]>("skillData", [
    new ProSkill({
      name: "Particle Blast",
      type: "Dynamic",
      maxLevel: 40,
      permission: false,
      levelReq: new ProAttribute("level", 1, 0),
      cost: new ProAttribute("cost", 1, 0),
      cooldown: new ProAttribute("cooldown", 0, 0),
      cooldownMessage: false,
      mana: new ProAttribute("mana", 0, 0),
      minSpent: new ProAttribute("points-spent-req", 0, 0),
      castMessage: "&6{player} &2has cast &6{skill}",
      indicator: "2D",
      triggers: []
    }),
    new ProSkill({
      name: "Poison Dart",
      type: "Dynamic",
      maxLevel: 40,
      permission: false,
      levelReq: new ProAttribute("level", 1, 0),
      cost: new ProAttribute("cost", 1, 0),
      cooldown: new ProAttribute("cooldown", 1, 0),
      cooldownMessage: false,
      mana: new ProAttribute("mana", 0, 0),
      minSpent: new ProAttribute("points-spent-req", 0, 0),
      castMessage: "&6{player} &2has cast &6{skill}",
      indicator: "2D",
      triggers: []
    }),
    new ProSkill({
      name: "Storm",
      type: "Dynamic",
      maxLevel: 40,
      permission: false,
      levelReq: new ProAttribute("level", 1, 0),
      cost: new ProAttribute("cost", 1, 0),
      cooldown: new ProAttribute("cooldown", 1, 0),
      cooldownMessage: false,
      mana: new ProAttribute("mana", 0, 0),
      minSpent: new ProAttribute("points-spent-req", 0, 0),
      castMessage: "&6{player} &2has cast &6{skill}",
      indicator: "2D",
      triggers: [triggers.BLOCK_BREAK]
    })
  ],
  (data: string) => sort<ProSkill>(loadSkillTextToArray(data)),
  (value: ProSkill[]) => {
    persistSkills(value);
    return sort<ProSkill>(value);
  });

export const getSkill = (name: string): ProSkill | undefined => {
  for (const c of get(skills)) {
    if (c.name == name) return c;
  }

  return undefined;
};

export const skillFolders: Writable<ProFolder[]> = setupSkillStore<ProFolder[]>("skillFolders", [],
  (data: string) => {
    const parsedData = JSON.parse(data, (key: string, value) => {
      if (/\d+/.test(key)) {
        if (typeof (value) === "string") {
          return getSkill(value);
        }

        const folder = new ProFolder(value.data);
        folder.name = value.name;
        return folder;
      }
      return value;
    });

    return parsedData;
  },
  (value: ProFolder[]) => {
    const data = JSON.stringify(value, (key, value: ProFolder | ProSkill | ProSkill) => {
      if (value instanceof ProSkill || value instanceof ProSkill) return value.name;
      else if (key === "parent") return undefined;
      return value;
    });
    localStorage.setItem("skillFolders", data);
    return sort<ProFolder>(value);
  });

export const isSkillNameTaken = (name: string): boolean => !!getSkill(name);

export const addSkill = (name?: string): ProSkill => {
  const cl = get(skills);
  const clazz = new ProSkill({ name: (name || "Skill " + (cl.length + 1)) });
  cl.push(clazz);

  skills.set(cl);
  return clazz;
};

export const addSkillFolder = (folder: ProFolder) => {
  const folders = get(skillFolders);
  if (folders.includes(folder)) return;

  rename(folder, folders);

  folders.push(folder);
  folders.sort((a, b) => a.name.localeCompare(b.name));
  skillFolders.set(folders);
};

export const deleteSkillFolder = (folder: ProFolder) => {
  const folders = get(skillFolders).filter(f => f != folder);
  skillFolders.set(folders);
};

export const deleteSkill = (data: ProSkill) => skills.set(get(skills).filter(c => c != data));

export const refreshSkills = () => skills.set(sort<ProSkill>(get(skills)));
export const refreshSkillFolders = () => {
  skillFolders.set(sort<ProFolder>(get(skillFolders)));
  refreshSkills();
};


/**
 *  Loads skill data from a string
 */
export const loadSkillText = (text: string) => {
  // Load new skills
  const data: YAMLObject = parseYAML(text);
  let skill: ProSkill;
  // If we only have one skill, and it is the current YAML,
  // the structure is a bit different
  if (data.key && !data.data[data.key]) {
    const key: string = data.key;
    skill = (<ProSkill>(isSkillNameTaken(key)
      ? getSkill(key)
      : addSkill(key)));
    skill.load(data);
    return;
  }

  for (const key in data.data) {
    if (key != "loaded" && data.data[key] instanceof YAMLObject && !isSkillNameTaken(key)) {
      skill = (<ProSkill>(isSkillNameTaken(key)
        ? getSkill(key)
        : addSkill(key)));
      skill.load(data.data[key]);
    }
  }
};

export const loadSkills = (e: ProgressEvent<FileReader>) => {
  const text: string = <string>e.target?.result;
  if (!text) return;

  loadSkillText(text);
};

export const persistSkills = (list?: ProSkill[]) => {
  const skillList = list || get(skills);
  const skillYaml = new YAMLObject();
  skillYaml.put("loaded", false);

  skillList.forEach(sk => skillYaml.put(sk.name, sk.serializeYaml()));

  if (skillList.length > 0)
    localStorage.setItem("skillData", skillYaml.toString());
};

get(skills).forEach(sk => sk.postLoad());