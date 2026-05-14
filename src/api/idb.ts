/**
 * idb.ts — minimal IndexedDB wrapper replacing localStorage for skill/class/attribute data.
 *
 * Why: localStorage is capped at ~5–10 MB total per origin.
 *      IndexedDB supports hundreds of MB (limited only by disk).
 *
 * Stores:
 *   'skills'     — key: skill name,     value: YAML string
 *   'classes'    — key: class name,     value: YAML string
 *   'attributes' — key: 'all',          value: YAML string (monolithic)
 *   'meta'       — key: arbitrary,      value: string (names lists, folders, etc.)
 */

const DB_NAME    = 'fabled-editor';
const DB_VERSION = 1;

export type IdbStore = 'skills' | 'classes' | 'attributes' | 'meta';

let _db: IDBDatabase | null = null;

function openDB(): Promise<IDBDatabase> {
	if (_db) return Promise.resolve(_db);

	return new Promise((resolve, reject) => {
		const req = indexedDB.open(DB_NAME, DB_VERSION);

		req.onupgradeneeded = (e) => {
			const db: IDBDatabase = (e.target as IDBOpenDBRequest).result;
			const stores: IdbStore[] = ['skills', 'classes', 'attributes', 'meta'];
			for (const name of stores) {
				if (!db.objectStoreNames.contains(name)) {
					db.createObjectStore(name);
				}
			}
		};

		req.onsuccess = (e) => {
			_db = (e.target as IDBOpenDBRequest).result;
			resolve(_db);
		};

		req.onerror = (e) => reject((e.target as IDBOpenDBRequest).error);
	});
}

export async function idbGet(store: IdbStore, key: string): Promise<string | undefined> {
	const db = await openDB();
	return new Promise((resolve, reject) => {
		const req = db.transaction(store, 'readonly').objectStore(store).get(key);
		req.onsuccess = () => resolve(req.result as string | undefined);
		req.onerror   = () => reject(req.error);
	});
}

export async function idbSet(store: IdbStore, key: string, value: string): Promise<void> {
	const db = await openDB();
	return new Promise((resolve, reject) => {
		const req = db.transaction(store, 'readwrite').objectStore(store).put(value, key);
		req.onsuccess = () => resolve();
		req.onerror   = () => reject(req.error);
	});
}

export async function idbDelete(store: IdbStore, key: string): Promise<void> {
	const db = await openDB();
	return new Promise((resolve, reject) => {
		const req = db.transaction(store, 'readwrite').objectStore(store).delete(key);
		req.onsuccess = () => resolve();
		req.onerror   = () => reject(req.error);
	});
}

export async function idbGetAllKeys(store: IdbStore): Promise<string[]> {
	const db = await openDB();
	return new Promise((resolve, reject) => {
		const req = db.transaction(store, 'readonly').objectStore(store).getAllKeys();
		req.onsuccess = () => resolve(req.result as string[]);
		req.onerror   = () => reject(req.error);
	});
}

/**
 * One-time migration: move existing localStorage entries into IndexedDB.
 * Deletes the localStorage entries after migrating.
 *
 * @param lsKeyFn   function that returns the localStorage key for a given name
 * @param idbStore  target IndexedDB store
 * @param names     list of entry names to migrate
 */
export async function migrateFromLocalStorage(
	lsKeyFn: (name: string) => string,
	idbStore: IdbStore,
	names: string[]
): Promise<void> {
	for (const name of names) {
		const lsKey = lsKeyFn(name);
		const value = localStorage.getItem(lsKey);
		if (value !== null) {
			await idbSet(idbStore, name, value);
			localStorage.removeItem(lsKey);
		}
	}
}
