import type { Writable } from "svelte/store";
import { get, writable } from "svelte/store";
import ProFolder from "$api/profolder";
import { rename } from "./store";
import { sort } from "$api/api";
import { parseYAML, YAMLObject } from "$api/yaml";
import { browser } from "$app/environment";
import ProClass from "$api/proclass";
import ProSkill from "$api/proskill";

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

const setupClassStore = <T>(key: string,
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

export const classes: Writable<ProClass[]> = setupClassStore<ProClass[]>("classData", [],
  (data: string) => sort<ProClass>(loadClassTextToArray(data)),
  (value: ProClass[]) => {
    persistClasses(value);
    value.forEach(c => c.updateParent(value));
    return sort<ProClass>(value);
  },
  (saved: ProClass[]) => saved.forEach(c => c.updateParent(saved)));

export const getClass = (name: string): ProClass | undefined => {
  for (const c of get(classes)) {
    if (c.name == name) return c;
  }

  return undefined;
};

export const classFolders: Writable<ProFolder[]> = setupClassStore<ProFolder[]>("classFolders", [],
  (data: string) => {
    const parsedData = JSON.parse(data, (key: string, value) => {
      if (/\d+/.test(key)) {
        if(typeof(value) === 'string') {
          return getClass(value);
        }

        const folder = new ProFolder(value.data)
        folder.name = value.name;
        return folder;
      }
      return value;
    });

    return parsedData;
  },
  (value: ProFolder[]) => {
    const data = JSON.stringify(value, (key, value: ProFolder | ProClass | ProSkill) => {
      if (value instanceof ProClass || value instanceof ProSkill) return value.name;
      else if (key === "parent") return undefined;
      return value;
    });
    localStorage.setItem("classFolders", data);
    return sort<ProFolder>(value);
  });

export const updateAllAttributes = (attributes: string[]) =>
  get(classes).forEach(c => c.updateAttributes(attributes));

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