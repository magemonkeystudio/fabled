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
	SKILL_FOLDERS_KEY,
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

const readLegacySkillRecords = (): PersistedSkillRecord[] => {
	if (!browser) return [];

	const names = getLegacyNamedKeys(SKILL_PREFIX, 'skillNames');
	if (names.length > 0) {
		return names
			.map((name) => {
				const stored = localStorage.getItem(`${SKILL_PREFIX}${name}`);
				if (!stored) return undefined;
				const parsed = parseYaml(stored) as MultiSkillYamlData | undefined;
				const record = normalizeMultiYamlRecords<SkillYamlData>(parsed)[0];
				if (!record) return undefined;
				return { name: record.name, data: record.data };
			})
			.filter((record): record is PersistedSkillRecord => !!record);
	}

	const legacyData = localStorage.getItem('skillData');
	if (!legacyData) return [];
	return normalizeMultiYamlRecords<SkillYamlData>(parseYaml(legacyData) as MultiSkillYamlData).map(
		(record) => ({
			name: record.name,
			data: record.data
		})
	);
};

const readLegacyClassRecords = (): PersistedClassRecord[] => {
	if (!browser) return [];

	const names = getLegacyNamedKeys(CLASS_PREFIX, 'classNames');
	if (names.length > 0) {
		return names
			.map((name) => {
				const stored = localStorage.getItem(`${CLASS_PREFIX}${name}`);
				if (!stored) return undefined;
				const parsed = parseYaml(stored) as MultiClassYamlData | undefined;
				const record = normalizeMultiYamlRecords<ClassYamlData>(parsed)[0];
				if (!record) return undefined;
				return { name: record.name, data: record.data };
			})
			.filter((record): record is PersistedClassRecord => !!record);
	}

	const legacyData = localStorage.getItem('classData');
	if (!legacyData) return [];
	return normalizeMultiYamlRecords<ClassYamlData>(parseYaml(legacyData) as MultiClassYamlData).map(
		(record) => ({
			name: record.name,
			data: record.data
		})
	);
};

const readLegacyAttributeRecords = (): PersistedAttributeRecord[] => {
	if (!browser) return [];

	const stored = localStorage.getItem('attribs');
	if (!stored) return [];

	if (stored.split('\n').length < 3 && stored.charAt(0) !== '{') {
		return stored
			.replace('\n', '')
			.split(',')
			.map((name) => name.trim())
			.filter((name) => name.length > 0)
			.map((name) => ({
				name,
				data: defaultAttributeYaml(name)
			}));
	}

	const parsed = parseYaml(stored) as MultiAttributeYamlData | undefined;
	if (!parsed) return [];

	return Object.entries(parsed).map(([name, data]) => ({
		name,
		data
	}));
};

const parseFolderMeta = (key: string): FolderProperties[] => {
	if (!browser) return [];

	const stored = localStorage.getItem(key);
	if (!stored || stored === 'null') return [];

	try {
		return JSON.parse(stored) as FolderProperties[];
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

export const collectLegacyEditorData = (): ReplaceEditorDataInput => ({
	skills: readLegacySkillRecords(),
	classes: readLegacyClassRecords(),
	attributes: readLegacyAttributeRecords(),
	skillFolders: parseFolderMeta(SKILL_FOLDERS_KEY),
	classFolders: parseFolderMeta(CLASS_FOLDERS_KEY)
});

export const clearLegacyEditorStorage = () => {
	if (!browser) return;

	const skillNames = getLegacyNamedKeys(SKILL_PREFIX, 'skillNames');
	const classNames = getLegacyNamedKeys(CLASS_PREFIX, 'classNames');

	skillNames.forEach((name) => localStorage.removeItem(`${SKILL_PREFIX}${name}`));
	classNames.forEach((name) => localStorage.removeItem(`${CLASS_PREFIX}${name}`));

	localStorage.removeItem('skillNames');
	localStorage.removeItem('classNames');
	localStorage.removeItem('skillData');
	localStorage.removeItem('classData');
	localStorage.removeItem('attribs');
	localStorage.removeItem(SKILL_FOLDERS_KEY);
	localStorage.removeItem(CLASS_FOLDERS_KEY);
};
