import 'fake-indexeddb/auto';

import { beforeEach, describe, expect, it, vi } from 'vitest';
import YAML from 'yaml';
import type { AttributeYamlData, ClassYamlData, SkillYamlData } from '$api/types';
import { ATTRIBUTES_STORE, SKILLS_STORE } from './editor-persistence-shared';

vi.mock('$app/environment', () => ({
	browser: true
}));

const skillData: SkillYamlData = {
	name: 'Meteor',
	type: 'Dynamic',
	'max-level': 5,
	'skill-req': '',
	'skill-req-lvl': 0,
	'needs-permission': false,
	'cooldown-message': true,
	msg: 'cast',
	combo: '',
	icon: 'stone',
	'icon-data': 0,
	'icon-lore': [],
	attributes: {
		'level-base': 1,
		'level-scale': 0,
		'cost-base': 1,
		'cost-scale': 0,
		'cooldown-base': 1,
		'cooldown-scale': 0,
		'mana-base': 0,
		'mana-scale': 0,
		'points-spent-req-base': 0,
		'points-spent-req-scale': 0
	},
	incompatible: [],
	components: {}
};

const classData: ClassYamlData = {
	name: 'Mage',
	'action-bar': '',
	prefix: '&6Mage',
	group: 'class',
	mana: '&2Mana',
	'max-level': 40,
	parent: '',
	'needs-permission': false,
	attributes: {
		'health-base': 20,
		'health-scale': 1,
		'mana-base': 20,
		'mana-scale': 1
	},
	'mana-regen': 1,
	'skill-tree': 'REQUIREMENT',
	blacklist: [],
	skills: ['Meteor'],
	icon: 'stone',
	'icon-data': 0,
	'icon-lore': [],
	'exp-source': 273,
	'combo-starters': {}
};

const attributeData: AttributeYamlData = {
	display: 'Spirit',
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
};

describe('editor persistence', () => {
	beforeEach(async () => {
		localStorage.clear();
		const persistence = await import('./editor-persistence');
		await persistence.resetEditorPersistenceForTests();
	});

	it('migrates legacy localStorage editor data into IndexedDB-backed cache', async () => {
		localStorage.setItem('skillNames', 'Meteor');
		localStorage.setItem(
			'sapi.skill.Meteor',
			YAML.stringify({ Meteor: skillData }, { lineWidth: 0, aliasDuplicateObjects: false })
		);
		localStorage.setItem('classNames', 'Mage');
		localStorage.setItem(
			'sapi.class.Mage',
			YAML.stringify({ Mage: classData }, { lineWidth: 0, aliasDuplicateObjects: false })
		);
		localStorage.setItem(
			'attribs',
			YAML.stringify({ Spirit: attributeData }, { lineWidth: 0, aliasDuplicateObjects: false })
		);
		localStorage.setItem(
			'skillFolders',
			JSON.stringify([
				{
					location: 'local',
					dataType: 'folder',
					name: 'Magic',
					data: ['Meteor'],
					open: false
				}
			])
		);

		const persistence = await import('./editor-persistence');
		await persistence.ensureEditorPersistence();

		expect(persistence.getEditorPersistenceMode()).toBe('indexeddb');
		expect(persistence.listPersistedSkillNames()).toEqual(['Meteor']);
		expect(persistence.listPersistedClassNames()).toEqual(['Mage']);
		expect(persistence.listPersistedAttributeRecords()).toEqual([
			{ name: 'Spirit', data: attributeData }
		]);
		expect(persistence.getPersistedFolders('skill')).toEqual([
			{
				location: 'local',
				dataType: 'folder',
				name: 'Magic',
				data: ['Meteor'],
				open: false
			}
		]);
		expect(localStorage.getItem('skillNames')).toBeNull();
		expect(localStorage.getItem('sapi.skill.Meteor')).toBeNull();
	});

	it('normalizes proxy-backed class data into structured-clone-safe values', async () => {
		const persistence = await import('./editor-persistence');
		const proxiedClassData: ClassYamlData = {
			...classData,
			blacklist: new Proxy(['stick'], {}),
			'icon-lore': new Proxy(['Line 1'], {}),
			'combo-starters': {
				L: {
					inverted: true,
					whitelist: new Proxy(['wand'], {})
				}
			}
		};

		expect(() => structuredClone({ name: 'Mage', data: proxiedClassData })).toThrow();

		const normalized = persistence.normalizeForPersistence({
			name: 'Mage',
			data: proxiedClassData
		});

		expect(() => structuredClone(normalized)).not.toThrow();
		expect(normalized).toEqual({
			name: 'Mage',
			data: {
				...classData,
				blacklist: ['stick'],
				'icon-lore': ['Line 1'],
				'combo-starters': {
					L: {
						inverted: true,
						whitelist: ['wand']
					}
				}
			}
		});
		expect(Array.isArray(normalized.data.blacklist)).toBe(true);
		expect(Array.isArray(normalized.data['icon-lore'])).toBe(true);
		expect(normalized.data['combo-starters']).toEqual({
			L: {
				inverted: true,
				whitelist: ['wand']
			}
		});
		expect(normalized.data).toEqual({
			...classData,
			blacklist: ['stick'],
			'icon-lore': ['Line 1'],
			'combo-starters': {
				L: {
					inverted: true,
					whitelist: ['wand']
				}
			}
		});
	});

	it('removes deleted attributes from IndexedDB so they stay gone after reload', async () => {
		const persistence = await import('./editor-persistence');
		const { openEditorDatabase } = await import('./editor-persistence-db');

		await persistence.savePersistedAttributes([
			{ name: 'Spirit', data: attributeData },
			{ name: 'Vitality', data: attributeData }
		]);

		await persistence.deletePersistedAttribute('Spirit');

		expect(persistence.listPersistedAttributeRecords().map((record) => record.name)).toEqual([
			'Vitality'
		]);
		const db = await openEditorDatabase();
		expect(await db.get(ATTRIBUTES_STORE, 'Spirit')).toBeUndefined();
		expect(await db.get(ATTRIBUTES_STORE, 'Vitality')).toBeDefined();
	});

	it('returns clones from read APIs so callers cannot mutate the cache', async () => {
		const persistence = await import('./editor-persistence');

		await persistence.savePersistedSkill('Meteor', skillData);
		await persistence.savePersistedAttributes([{ name: 'Spirit', data: attributeData }]);

		const skill = await persistence.getPersistedSkill('Meteor');
		skill!['icon-lore'].push('mutated');
		skill!.msg = 'mutated';

		const attribute = persistence.listPersistedAttributeRecords()[0];
		attribute.data['icon-lore'].push('mutated');

		expect(await persistence.getPersistedSkill('Meteor')).toEqual(skillData);
		expect(persistence.listPersistedAttributeRecords()).toEqual([
			{ name: 'Spirit', data: attributeData }
		]);
	});

	it('does not overwrite newer persisted skills when saving attributes', async () => {
		const persistence = await import('./editor-persistence');
		const updatedSkillData: SkillYamlData = {
			...skillData,
			msg: 'updated'
		};
		const { openEditorDatabase, writeIndexedDbRecord } = await import('./editor-persistence-db');

		await persistence.savePersistedSkill('Meteor', skillData);
		await writeIndexedDbRecord(SKILLS_STORE, {
			name: 'Meteor',
			data: updatedSkillData
		});

		await persistence.savePersistedAttributes([{ name: 'Spirit', data: attributeData }]);

		const db = await openEditorDatabase();
		expect(await db.get(SKILLS_STORE, 'Meteor')).toEqual({
			name: 'Meteor',
			data: updatedSkillData
		});
		expect(await db.get(ATTRIBUTES_STORE, 'Spirit')).toEqual({
			name: 'Spirit',
			data: attributeData
		});
	});
});
