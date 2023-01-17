import type { Writable } from "svelte/store";
import { get, writable } from "svelte/store";
import type { ProFolder } from "../api/profolder";
import { rename } from "./store";
import { sort } from "../api/api";
import { parseYAML, YAMLObject } from "../api/yaml";
import { browser } from "$app/environment";
import { ProClass } from "../api/proclass";

const loadClassTextToArray = (text: string): ProClass[] => {
  const list: ProClass[] = [];
  // Load classes
  const data: YAMLObject = parseYAML(text);
  let clazz: ProClass;
  // If we only have one class, and it is the current YAML,
  // the structure is a bit different
  if (data.key && !data.data[data.key]) {
    const key: string = data.key;
    if (key === "loaded") return list;
    clazz = new ProClass({ name: key });
    clazz.load(data);
    list.push(clazz);
    return list;
  }

  for (const key in data.data) {
    if (key != "loaded" && data.data[key] instanceof YAMLObject) {
      clazz = new ProClass({ name: key });
      clazz.load(data.data[key]);
      list.push(clazz);
    }
  }
  return list;
};

const setupClassStore = (): Writable<ProClass[]> => {
  let saved: ProClass[] = [];
  if (browser) {
    const stored = localStorage.getItem("classData");
    if (stored) {
      saved = sort<ProClass>(loadClassTextToArray(stored));
      saved.forEach(c => c.updateParent(saved));
    }
  }

  const {
    subscribe,
    set,
    update
  } = writable<ProClass[]>(saved);
  return {
    subscribe,
    set: (value: ProClass[]) => {
      persistClasses(value);
      value.forEach(c => c.updateParent(value));
      return set(sort<ProClass>(value));
    },
    update
  };
};

export const classes: Writable<ProClass[]> = setupClassStore();
export const classFolders: Writable<ProFolder[]> = writable([]);

export const updateAllAttributes = (attributes: string[]) =>
  get(classes).forEach(c => c.updateAttributes(attributes));

export const getClass = (name: string): ProClass | undefined => {
  for (const c of get(classes)) {
    if (c.name == name) return c;
  }

  return undefined;
};

export const isClassNameTaken = (name: string): boolean => !!getClass(name);

export const addClass = (name?: string): ProClass => {
  const cl = get(classes);
  const clazz = new ProClass({ name: (name || "Class " + (cl.length + 1)) });
  cl.push(clazz);

  classes.set(cl);
  return clazz;
};

export const addClassFolder = (folder: ProFolder) => {
  const folders = get(classFolders);
  if (folders.includes(folder)) return;

  rename(folder, folders);

  folders.push(folder);
  folders.sort((a, b) => a.name.localeCompare(b.name));
  classFolders.set(folders);
};

export const deleteClassFolder = (folder: ProFolder) => {
  const folders = get(classFolders).filter(f => f != folder);
  classFolders.set(folders);
};

export const deleteClass = (data: ProClass) => classes.set(get(classes).filter(c => c != data));

export const refreshClasses = () => classes.set(sort<ProClass>(get(classes)));
export const refreshClassFolders = () => {
  classFolders.set(sort<ProFolder>(get(classFolders)));
  refreshClasses();
};


/**
 *  Loads class data from a string
 */
export const loadClassText = (text: string) => {
  // Load new classes
  const data: YAMLObject = parseYAML(text);
  let clazz: ProClass;
  // If we only have one class, and it is the current YAML,
  // the structure is a bit different
  if (data.key && !data.data[data.key]) {
    const key: string = data.key;
    clazz = (<ProClass>(isClassNameTaken(key)
      ? getClass(key)
      : addClass(key)));
    clazz.load(data);
    return;
  }

  for (const key in data.data) {
    if (key != "loaded" && data.data[key] instanceof YAMLObject && !isClassNameTaken(key)) {
      clazz = (<ProClass>(isClassNameTaken(key)
        ? getClass(key)
        : addClass(key)));
      clazz.load(data.data[key]);
    }
  }
};

export const loadClasses = (e: ProgressEvent<FileReader>) => {
  const text: string = <string>e.target?.result;
  if (!text) return;

  loadClassText(text);
};

export const persistClasses = (list?: ProClass[]) => {
  const classList = list || get(classes);
  const classYaml = new YAMLObject();
  classYaml.put("loaded", false);

  classList.forEach(c => classYaml.put(c.name, c.serializeYaml()));

  if (classList.length > 0)
    localStorage.setItem("classData", classYaml.toString());
};