import type {
	AttributeYamlData,
	ClassYamlData,
	SkillYamlData
} from '$api/types';
import type { DBSchema } from 'idb';
import type { FolderProperties } from './folder-store.svelte';

export const DB_NAME = 'fabled-editor';
export const DB_VERSION = 1;

export const SKILLS_STORE = 'skills';
export const CLASSES_STORE = 'classes';
export const ATTRIBUTES_STORE = 'attributes';
export const META_STORE = 'meta';

export const MIGRATION_KEY = 'editor-storage-migrated';
export const SKILL_FOLDERS_KEY = 'skillFolders';
export const CLASS_FOLDERS_KEY = 'classFolders';

export type PersistenceMode = 'indexeddb' | 'unsupported';

export interface PersistedSkillRecord {
	name: string;
	data: SkillYamlData;
}

export interface PersistedClassRecord {
	name: string;
	data: ClassYamlData;
}

export interface PersistedAttributeRecord {
	name: string;
	data: AttributeYamlData;
}

export interface MetaRecord<T = unknown> {
	key: string;
	value: T;
}

export interface ReplaceEditorDataInput {
	skills: PersistedSkillRecord[];
	classes: PersistedClassRecord[];
	attributes: PersistedAttributeRecord[];
	skillFolders: FolderProperties[];
	classFolders: FolderProperties[];
}

export interface EditorPersistenceSchema extends DBSchema {
	[SKILLS_STORE]: {
		key: string;
		value: PersistedSkillRecord;
	};
	[CLASSES_STORE]: {
		key: string;
		value: PersistedClassRecord;
	};
	[ATTRIBUTES_STORE]: {
		key: string;
		value: PersistedAttributeRecord;
	};
	[META_STORE]: {
		key: string;
		value: MetaRecord;
	};
}

export type EntityStoreName = typeof SKILLS_STORE | typeof CLASSES_STORE | typeof ATTRIBUTES_STORE;
export type StoreName = EntityStoreName | typeof META_STORE;

export const normalizeForPersistence = <T>(value: T): T =>
	JSON.parse(JSON.stringify(value)) as T;
