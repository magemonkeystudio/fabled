import { browser } from '$app/environment';
import type {
	AttributeYamlData,
	ClassYamlData,
	MultiAttributeYamlData,
	MultiClassYamlData,
	MultiSkillYamlData,
	SkillYamlData
} from '$api/types';
import { parseYaml } from '$api/yaml';
import type { FolderProperties } from './folder-store.svelte';
import type {
	PersistedAttributeRecord,
	PersistedClassRecord,
	PersistedSkillRecord,
	ReplaceEditorDataInput
} from './editor-persistence-shared';
import {
	CLASS_FOLDERS_KEY,
	SKILL_FOLDERS_KEY
} from './editor-persistence-shared';

const SKILL_PREFIX = 'sapi.skill.';
const CLASS_PREFIX = 'sapi.class.';

const defaultAttributeYaml = (name: string): AttributeYamlData => ({
	display: name,
	max: 999,
	cost_base: 1,
	cost_modifier: 0,
	icon: 'Ink sac',
	'icon-data': 0,
	'icon-lore': [],
	global: {
		target: {},
		condition: {},
		mechanic: {}
	},
	stats: {}
});

const normalizeMultiYamlRecords = <T extends SkillYamlData | ClassYamlData>(
	data: MultiSkillYamlData | MultiClassYamlData | undefined
): Array<{ name: string; data: T }> => {
	if (!data) return [];

	return Object.entries(data)
		.filter(([name]) => name !== 'loaded')
		.map(([name, value]) => ({
			name,
			data: value as T
		}));
};

const getLegacyNamedKeys = (prefix: string, metadataKey: string): string[] => {
	if (!browser) return [];

	const names = localStorage.getItem(metadataKey);
	if (names) {
		return names
			.split(', ')
			.map((name) => name.trim())
			.filter((name) => name.length > 0);
	}

	const fromKeys: string[] = [];
	for (let i = 0; i < localStorage.length; i++) {
		const key = localStorage.key(i);
		if (!key?.startsWith(prefix)) continue;
		fromKeys.push(key.substring(prefix.length));
	}
	return fromKeys;
};

const readLegacySkillRecords = (consumedKeys: string[]): PersistedSkillRecord[] => {
	if (!browser) return [];

	const names = getLegacyNamedKeys(SKILL_PREFIX, 'skillNames');
	if (names.length > 0) {
		return names
			.map((name) => {
				const storageKey = `${SKILL_PREFIX}${name}`;
				const stored = localStorage.getItem(storageKey);
				if (!stored) return undefined;
				const parsed = parseYaml(stored) as MultiSkillYamlData | undefined;
				const record = normalizeMultiYamlRecords<SkillYamlData>(parsed)[0];
				if (!record) return undefined;
				consumedKeys.push(storageKey);
				return { name: record.name, data: record.data };
			})
			.filter((record): record is PersistedSkillRecord => !!record);
	}

	const legacyData = localStorage.getItem('skillData');
	if (!legacyData) return [];
	const records = normalizeMultiYamlRecords<SkillYamlData>(
		parseYaml(legacyData) as MultiSkillYamlData
	).map((record) => ({
		name: record.name,
		data: record.data
	}));
	if (records.length > 0) consumedKeys.push('skillData');
	return records;
};

const readLegacyClassRecords = (consumedKeys: string[]): PersistedClassRecord[] => {
	if (!browser) return [];

	const names = getLegacyNamedKeys(CLASS_PREFIX, 'classNames');
	if (names.length > 0) {
		return names
			.map((name) => {
				const storageKey = `${CLASS_PREFIX}${name}`;
				const stored = localStorage.getItem(storageKey);
				if (!stored) return undefined;
				const parsed = parseYaml(stored) as MultiClassYamlData | undefined;
				const record = normalizeMultiYamlRecords<ClassYamlData>(parsed)[0];
				if (!record) return undefined;
				consumedKeys.push(storageKey);
				return { name: record.name, data: record.data };
			})
			.filter((record): record is PersistedClassRecord => !!record);
	}

	const legacyData = localStorage.getItem('classData');
	if (!legacyData) return [];
	const records = normalizeMultiYamlRecords<ClassYamlData>(
		parseYaml(legacyData) as MultiClassYamlData
	).map((record) => ({
		name: record.name,
		data: record.data
	}));
	if (records.length > 0) consumedKeys.push('classData');
	return records;
};

const readLegacyAttributeRecords = (consumedKeys: string[]): PersistedAttributeRecord[] => {
	if (!browser) return [];

	const stored = localStorage.getItem('attribs');
	if (!stored) return [];

	if (stored.split('\n').length < 3 && stored.charAt(0) !== '{') {
		const records = stored
			.replace('\n', '')
			.split(',')
			.map((name) => name.trim())
			.filter((name) => name.length > 0)
			.map((name) => ({
				name,
				data: defaultAttributeYaml(name)
			}));
		if (records.length > 0) consumedKeys.push('attribs');
		return records;
	}

	const parsed = parseYaml(stored) as MultiAttributeYamlData | undefined;
	if (!parsed) return [];

	const records = Object.entries(parsed).map(([name, data]) => ({
		name,
		data
	}));
	if (records.length > 0) consumedKeys.push('attribs');
	return records;
};

const parseFolderMeta = (key: string, consumedKeys: string[]): FolderProperties[] => {
	if (!browser) return [];

	const stored = localStorage.getItem(key);
	if (!stored || stored === 'null') return [];

	try {
		const folders = JSON.parse(stored) as FolderProperties[];
		consumedKeys.push(key);
		return folders;
	} catch (error) {
		console.error(`Failed to parse ${key} from localStorage`, error);
		return [];
	}
};

export const hasLegacyEditorData = () => {
	if (!browser) return false;

	return (
		getLegacyNamedKeys(SKILL_PREFIX, 'skillNames').length > 0 ||
		getLegacyNamedKeys(CLASS_PREFIX, 'classNames').length > 0 ||
		!!localStorage.getItem('skillData') ||
		!!localStorage.getItem('classData') ||
		!!localStorage.getItem('attribs') ||
		!!localStorage.getItem(SKILL_FOLDERS_KEY) ||
		!!localStorage.getItem(CLASS_FOLDERS_KEY)
	);
};

export interface CollectedLegacyData extends ReplaceEditorDataInput {
	consumedKeys: string[];
}

export const collectLegacyEditorData = (): CollectedLegacyData => {
	const consumedKeys: string[] = [];
	return {
		skills: readLegacySkillRecords(consumedKeys),
		classes: readLegacyClassRecords(consumedKeys),
		attributes: readLegacyAttributeRecords(consumedKeys),
		skillFolders: parseFolderMeta(SKILL_FOLDERS_KEY, consumedKeys),
		classFolders: parseFolderMeta(CLASS_FOLDERS_KEY, consumedKeys),
		consumedKeys
	};
};

// Only keys whose data actually made it into IndexedDB are removed; anything
// that failed to parse stays behind in localStorage as an inert backup rather
// than being destroyed. The name indexes are only meaningful to the old editor,
// so they are always dropped.
export const clearLegacyEditorStorage = (consumedKeys: string[]) => {
	if (!browser) return;

	consumedKeys.forEach((key) => localStorage.removeItem(key));
	localStorage.removeItem('skillNames');
	localStorage.removeItem('classNames');
};
