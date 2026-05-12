import { browser } from '$app/environment';
import { writable } from 'svelte/store';
import {
  clearLegacyEditorStorage,
  collectLegacyEditorData,
  hasLegacyEditorData
} from './editor-persistence-legacy';
import {
  deleteIndexedDbRecord,
  loadEditorDbData,
  openEditorDatabase,
  replaceIndexedDbData,
  resetEditorDatabaseForTests,
  writeIndexedDbMeta,
  writeIndexedDbRecord
} from './editor-persistence-db';
import {
  ATTRIBUTES_STORE,
  CLASS_FOLDERS_KEY,
  CLASSES_STORE,
  MIGRATION_KEY,
  normalizeForPersistence,
  type PersistedAttributeRecord,
  type PersistenceMode,
  type ReplaceEditorDataInput,
  SKILL_FOLDERS_KEY,
  SKILLS_STORE
} from './editor-persistence-shared';
import type { AttributeYamlData, ClassYamlData, SkillYamlData } from '$api/types';
import type { FolderProperties } from './folder-store.svelte';
import type { PersistenceWriteResult } from './persistence-state';
import { isStorageQuotaError } from './persistence-state';

const cache = {
  skills: new Map<string, SkillYamlData>(),
  classes: new Map<string, ClassYamlData>(),
  attributes: new Map<string, AttributeYamlData>(),
  meta: new Map<string, unknown>()
};

export const editorPersistenceUnsupported = writable<string | null>(null);

let persistenceMode: PersistenceMode = 'indexeddb';
let initializationPromise: Promise<void> | undefined;

const resetCache = () => {
  cache.skills.clear();
  cache.classes.clear();
  cache.attributes.clear();
  cache.meta.clear();
};

const unsupportedPersistenceError = (cause?: unknown) =>
  new Error(
    cause instanceof Error && cause.message
      ? `IndexedDB is unavailable in this browser: ${cause.message}`
      : 'IndexedDB is unavailable in this browser.'
  );

const loadCache = async () => {
  const db = await openEditorDatabase();
  const data = await loadEditorDbData(db);

  resetCache();
  data.skills.forEach((record) => cache.skills.set(record.name, record.data));
  data.classes.forEach((record) => cache.classes.set(record.name, record.data));
  data.attributes.forEach((record) => cache.attributes.set(record.name, record.data));
  data.meta.forEach((record) => cache.meta.set(record.key, record.value));
};

const replacePersistedAttributeCache = (records: PersistedAttributeRecord[]) => {
  cache.attributes.clear();
  records.forEach((record) => cache.attributes.set(record.name, record.data));
};

const migrateLegacyLocalStorage = async (): Promise<void> => {
  if (!browser || persistenceMode !== 'indexeddb') return;
  if (cache.meta.get(MIGRATION_KEY)) return;

  if (!hasLegacyEditorData()) {
    await writeIndexedDbMeta(MIGRATION_KEY, true);
    cache.meta.set(MIGRATION_KEY, true);
    return;
  }

  const data = collectLegacyEditorData();
  const db = await openEditorDatabase();
  await replaceIndexedDbData(db, data, {
    skills: [...cache.skills.keys()],
    classes: [...cache.classes.keys()],
    attributes: [...cache.attributes.keys()]
  });
  clearLegacyEditorStorage();
};

export const ensureEditorPersistence = async (): Promise<PersistenceMode> => {
  if (!browser) return 'unsupported';
  if (initializationPromise) {
    await initializationPromise;
    return persistenceMode;
  }

  initializationPromise = (async () => {
    editorPersistenceUnsupported.set(null);

    if (typeof indexedDB === 'undefined') {
      persistenceMode = 'unsupported';
      resetCache();
      editorPersistenceUnsupported.set('This browser does not support IndexedDB persistence.');
      return;
    }

    try {
      await loadCache();
      await migrateLegacyLocalStorage();
      await loadCache();
    } catch (error) {
      console.error('IndexedDB unavailable for editor persistence.', error);
      persistenceMode = 'unsupported';
      resetCache();
      editorPersistenceUnsupported.set(
        error instanceof Error && error.message
          ? `IndexedDB persistence is unavailable: ${error.message}`
          : 'IndexedDB persistence is unavailable in this browser.'
      );
    }
  })();

  await initializationPromise;
  return persistenceMode;
};

export const getEditorPersistenceMode = (): PersistenceMode => persistenceMode;

export const listPersistedSkillNames = (): string[] =>
  [...cache.skills.keys()].sort((left, right) => left.localeCompare(right));

export const listPersistedClassNames = (): string[] =>
  [...cache.classes.keys()].sort((left, right) => left.localeCompare(right));

export const listPersistedAttributeRecords = (): PersistedAttributeRecord[] =>
  [...cache.attributes.entries()]
    .sort(([left], [right]) => left.localeCompare(right))
    .map(([name, data]) => ({ name, data }));

export const getPersistedSkill = async (name: string): Promise<SkillYamlData | undefined> => {
  await ensureEditorPersistence();
  return cache.skills.get(name);
};

export const getPersistedClass = async (name: string): Promise<ClassYamlData | undefined> => {
  await ensureEditorPersistence();
  return cache.classes.get(name);
};

export const getPersistedAttribute = async (
  name: string
): Promise<AttributeYamlData | undefined> => {
  await ensureEditorPersistence();
  return cache.attributes.get(name);
};

export const getPersistedFolders = (type: 'skill' | 'class'): FolderProperties[] =>
  (
    (cache.meta.get(
      type === 'skill' ? SKILL_FOLDERS_KEY : CLASS_FOLDERS_KEY
    ) as FolderProperties[]) || []
  ).map((folder) => structuredClone(folder));

const unsupportedResult = (): PersistenceWriteResult => ({
  ok: false,
  quotaExceeded: false,
  error: unsupportedPersistenceError()
});

export const savePersistedSkill = async (
  name: string,
  data: SkillYamlData,
  previousName?: string
): Promise<PersistenceWriteResult> => {
  await ensureEditorPersistence();
  if (persistenceMode !== 'indexeddb') {
    return unsupportedResult();
  }

  const result = await writeIndexedDbRecord(SKILLS_STORE, { name, data }, previousName);
  if (result.ok) {
    if (previousName && previousName !== name) cache.skills.delete(previousName);
    cache.skills.set(name, normalizeForPersistence(data));
  }
  return result;
};

export const savePersistedClass = async (
  name: string,
  data: ClassYamlData,
  previousName?: string
): Promise<PersistenceWriteResult> => {
  await ensureEditorPersistence();
  if (persistenceMode !== 'indexeddb') {
    return unsupportedResult();
  }

  const result = await writeIndexedDbRecord(CLASSES_STORE, { name, data }, previousName);
  if (result.ok) {
    if (previousName && previousName !== name) cache.classes.delete(previousName);
    cache.classes.set(name, normalizeForPersistence(data));
  }
  return result;
};

export const savePersistedAttributes = async (
  records: PersistedAttributeRecord[]
): Promise<PersistenceWriteResult> => {
  await ensureEditorPersistence();
  if (persistenceMode !== 'indexeddb') {
    return unsupportedResult();
  }

  try {
    const db = await openEditorDatabase();
    const normalizedRecords = normalizeForPersistence(records);
    const transaction = db.transaction(ATTRIBUTES_STORE, 'readwrite');
    const store = transaction.store;
    const incomingNames = new Set(normalizedRecords.map((record) => record.name));

    [...cache.attributes.keys()]
      .filter((name) => !incomingNames.has(name))
      .forEach((name) => store.delete(name));
    normalizedRecords.forEach((record) => store.put(record));

    await transaction.done;
    replacePersistedAttributeCache(normalizedRecords);
    return { ok: true, quotaExceeded: false };
  } catch (error) {
    return { ok: false, quotaExceeded: isStorageQuotaError(error), error };
  }
};

export const savePersistedFolders = async (
  type: 'skill' | 'class',
  folders: FolderProperties[]
): Promise<PersistenceWriteResult> => {
  await ensureEditorPersistence();
  if (persistenceMode !== 'indexeddb') {
    return unsupportedResult();
  }

  const key = type === 'skill' ? SKILL_FOLDERS_KEY : CLASS_FOLDERS_KEY;
  const result = await writeIndexedDbMeta(key, folders);
  if (result.ok) {
    cache.meta.set(key, normalizeForPersistence(folders));
  }
  return result;
};

export const deletePersistedSkill = async (name: string): Promise<void> => {
  await ensureEditorPersistence();
  cache.skills.delete(name);
  if (persistenceMode !== 'indexeddb') return;
  await deleteIndexedDbRecord(SKILLS_STORE, name);
};

export const deletePersistedClass = async (name: string): Promise<void> => {
  await ensureEditorPersistence();
  cache.classes.delete(name);
  if (persistenceMode !== 'indexeddb') return;
  await deleteIndexedDbRecord(CLASSES_STORE, name);
};

export const deletePersistedAttribute = async (name: string): Promise<void> => {
  await ensureEditorPersistence();
  const previousAttribute = cache.attributes.get(name);
  cache.attributes.delete(name);
  if (persistenceMode !== 'indexeddb') return;

  try {
    const result = await savePersistedAttributes(listPersistedAttributeRecords());
    if (!result.ok) {
      if (previousAttribute !== undefined) {
        cache.attributes.set(name, previousAttribute);
      }
      throw new Error(`Failed to persist deletion of attribute "${name}"`);
    }
  } catch (error) {
    if (previousAttribute !== undefined && !cache.attributes.has(name)) {
      cache.attributes.set(name, previousAttribute);
    }
    throw error;
  }
};

export const replacePersistedEditorData = async (data: ReplaceEditorDataInput): Promise<void> => {
  await ensureEditorPersistence();
  if (persistenceMode !== 'indexeddb') {
    throw unsupportedPersistenceError();
  }

  const db = await openEditorDatabase();
  await replaceIndexedDbData(db, data, {
    skills: [...cache.skills.keys()],
    classes: [...cache.classes.keys()],
    attributes: [...cache.attributes.keys()]
  });
  await loadCache();
};

export const importLegacyMigrationData = async (input: {
  skillData: string;
  classData: string;
  attributes: string;
  skillFolders: string;
  classFolders: string;
}): Promise<void> => {
  const { parseYaml } = await import('$api/yaml');
  const skills = Object.entries((parseYaml(input.skillData) as Record<string, SkillYamlData>) || {})
    .filter(([name]) => name !== 'loaded')
    .map(([name, data]) => ({
      name,
      data
    }));
  const classes = Object.entries(
    (parseYaml(input.classData) as Record<string, ClassYamlData>) || {}
  )
    .filter(([name]) => name !== 'loaded')
    .map(([name, data]) => ({
      name,
      data
    }));
  const attributes = Object.entries(
    (parseYaml(input.attributes) as Record<string, AttributeYamlData>) || {}
  ).map(([name, data]) => ({
    name,
    data
  }));

  let skillFolders: FolderProperties[] = [];
  let classFolders: FolderProperties[] = [];
  try {
    skillFolders = input.skillFolders ? (JSON.parse(input.skillFolders) as FolderProperties[]) : [];
  } catch (_) {
    skillFolders = [];
  }

  try {
    classFolders = input.classFolders ? (JSON.parse(input.classFolders) as FolderProperties[]) : [];
  } catch (_) {
    classFolders = [];
  }

  await replacePersistedEditorData({
    skills,
    classes,
    attributes,
    skillFolders,
    classFolders
  });
};

export { normalizeForPersistence };

export const resetEditorPersistenceForTests = async () => {
  resetCache();
  initializationPromise = undefined;
  persistenceMode = 'indexeddb';
  editorPersistenceUnsupported.set(null);

  await resetEditorDatabaseForTests();
};
