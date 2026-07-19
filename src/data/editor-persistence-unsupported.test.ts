import 'fake-indexeddb/auto';

import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import type { SkillYamlData } from '$api/types';

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

describe('editor persistence unsupported browser handling', () => {
	beforeEach(() => {
		vi.resetModules();
		vi.stubGlobal('indexedDB', undefined);
		localStorage.clear();
	});

	afterEach(() => {
		vi.unstubAllGlobals();
	});

	it('does not fall back to localStorage when IndexedDB is unavailable', async () => {
		const persistence = await import('./editor-persistence');
		const result = await persistence.savePersistedSkill('Meteor', skillData);

		expect(result.ok).toBe(false);
		expect(persistence.getEditorPersistenceMode()).toBe('unsupported');
		expect(localStorage.getItem('skillNames')).toBeNull();
		expect(localStorage.getItem('sapi.skill.Meteor')).toBeNull();
		expect(persistence.listPersistedSkillNames()).toEqual([]);
	});
});
