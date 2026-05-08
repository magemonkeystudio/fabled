import { browser } from '$app/environment';
import { deleteDB, openDB, type IDBPDatabase } from 'idb';
import type { PersistenceWriteResult } from './persistence-state';
import { isStorageQuotaError } from './persistence-state';
import {
	ATTRIBUTES_STORE,
	CLASSES_STORE,
	CLASS_FOLDERS_KEY,
	DB_NAME,
	DB_VERSION,
	type EditorPersistenceSchema,
	type EntityStoreName,
	type MetaRecord,
	META_STORE,
	MIGRATION_KEY,
	normalizeForPersistence,
	type PersistedAttributeRecord,
	type PersistedClassRecord,
	type PersistedSkillRecord,
	type ReplaceEditorDataInput,
	SKILLS_STORE,
	SKILL_FOLDERS_KEY,
	type StoreName
} from './editor-persistence-shared';

export interface LoadedEditorData {
	skills: PersistedSkillRecord[];
	classes: PersistedClassRecord[];
	attributes: PersistedAttributeRecord[];
	meta: MetaRecord[];
}

let databasePromise: Promise<IDBPDatabase<EditorPersistenceSchema>> | undefined;

const createStorageResult = (error?: unknown): PersistenceWriteResult => ({
	ok: !error,
	quotaExceeded: !!error && isStorageQuotaError(error),
	error
});

export const openEditorDatabase = (): Promise<IDBPDatabase<EditorPersistenceSchema>> => {
	if (!browser || typeof indexedDB === 'undefined') {
		return Promise.reject(new Error('IndexedDB is unavailable.'));
	}

	if (!databasePromise) {
		databasePromise = openDB<EditorPersistenceSchema>(DB_NAME, DB_VERSION, {
			upgrade(db) {
				if (!db.objectStoreNames.contains(SKILLS_STORE)) {
					db.createObjectStore(SKILLS_STORE, { keyPath: 'name' });
				}
				if (!db.objectStoreNames.contains(CLASSES_STORE)) {
					db.createObjectStore(CLASSES_STORE, { keyPath: 'name' });
				}
				if (!db.objectStoreNames.contains(ATTRIBUTES_STORE)) {
					db.createObjectStore(ATTRIBUTES_STORE, { keyPath: 'name' });
				}
				if (!db.objectStoreNames.contains(META_STORE)) {
					db.createObjectStore(META_STORE, { keyPath: 'key' });
				}
			}
		});
	}

	return databasePromise;
};

const putAllIndexedDbRecords = async <
	T extends MetaRecord | PersistedSkillRecord | PersistedClassRecord | PersistedAttributeRecord
>(
	db: IDBPDatabase<EditorPersistenceSchema>,
	storeName: StoreName,
	records: T[]
): Promise<void> => {
	const transaction = db.transaction(storeName, 'readwrite');
	const store = transaction.store;
	records.forEach((record) => {
		void store.put(normalizeForPersistence(record));
	});
	await transaction.done;
};

const deleteIndexedDbKeys = async (
	db: IDBPDatabase<EditorPersistenceSchema>,
	storeName: StoreName,
	keys: string[]
): Promise<void> => {
	if (keys.length === 0) return;

	const transaction = db.transaction(storeName, 'readwrite');
	const store = transaction.store;
	keys.forEach((key) => {
		void store.delete(key);
	});
	await transaction.done;
};

const syncIndexedDbEntityStore = async <
	T extends PersistedSkillRecord | PersistedClassRecord | PersistedAttributeRecord
>(
	db: IDBPDatabase<EditorPersistenceSchema>,
	storeName: EntityStoreName,
	records: T[],
	existingNames: string[]
): Promise<void> => {
	const incomingNames = new Set(records.map((record) => record.name));
	await deleteIndexedDbKeys(
		db,
		storeName,
		existingNames.filter((name) => !incomingNames.has(name))
	);
	await putAllIndexedDbRecords(db, storeName, records);
};

export const loadEditorDbData = async (
	db: IDBPDatabase<EditorPersistenceSchema>
): Promise<LoadedEditorData> => {
	const [skills, classes, attributes, meta] = await Promise.all([
		db.getAll(SKILLS_STORE),
		db.getAll(CLASSES_STORE),
		db.getAll(ATTRIBUTES_STORE),
		db.getAll(META_STORE)
	]);

	return {
		skills,
		classes,
		attributes,
		meta
	};
};

export const replaceIndexedDbData = async (
	db: IDBPDatabase<EditorPersistenceSchema>,
	data: ReplaceEditorDataInput,
	existing: {
		skills: string[];
		classes: string[];
		attributes: string[];
	}
): Promise<void> => {
	await syncIndexedDbEntityStore(db, SKILLS_STORE, data.skills, existing.skills);
	await syncIndexedDbEntityStore(db, CLASSES_STORE, data.classes, existing.classes);
	await syncIndexedDbEntityStore(db, ATTRIBUTES_STORE, data.attributes, existing.attributes);
	await putAllIndexedDbRecords(db, META_STORE, [
		{ key: SKILL_FOLDERS_KEY, value: data.skillFolders },
		{ key: CLASS_FOLDERS_KEY, value: data.classFolders },
		{ key: MIGRATION_KEY, value: true }
	]);
};

export const writeIndexedDbRecord = async (
	storeName: EntityStoreName,
	record: PersistedSkillRecord | PersistedClassRecord | PersistedAttributeRecord,
	previousName?: string
): Promise<PersistenceWriteResult> => {
	try {
		const db = await openEditorDatabase();
		const transaction = db.transaction(storeName, 'readwrite');
		const store = transaction.store;
		const cloneableRecord = normalizeForPersistence(record);
		if (previousName && previousName !== record.name) {
			void store.delete(previousName);
		}
		void store.put(cloneableRecord);
		await transaction.done;
		return createStorageResult();
	} catch (error) {
		return createStorageResult(error);
	}
};

export const writeIndexedDbMeta = async <T>(
	key: string,
	value: T
): Promise<PersistenceWriteResult> => {
	try {
		const db = await openEditorDatabase();
		const cloneableRecord = normalizeForPersistence({ key, value });
		await db.put(META_STORE, cloneableRecord);
		return createStorageResult();
	} catch (error) {
		return createStorageResult(error);
	}
};

export const deleteIndexedDbRecord = async (
	storeName: EntityStoreName,
	name: string
): Promise<void> => {
	const db = await openEditorDatabase();
	await db.delete(storeName, name);
};

export const resetEditorDatabaseForTests = async () => {
	if (!browser || typeof indexedDB === 'undefined') {
		databasePromise = undefined;
		return;
	}

	const db = await databasePromise?.catch(() => undefined);
	db?.close();
	databasePromise = undefined;

	await deleteDB(DB_NAME, {
		blocked() {
			return;
		}
	});
};
