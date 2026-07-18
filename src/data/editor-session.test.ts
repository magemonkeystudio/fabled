import 'fake-indexeddb/auto';

import { beforeEach, describe, expect, it, vi } from 'vitest';
import { get } from 'svelte/store';
import type { ClassYamlData, SkillYamlData } from '$api/types';

vi.mock('$app/environment', () => ({
	browser: true
}));

vi.mock('$app/navigation', () => ({
	goto: vi.fn(() => Promise.resolve())
}));

vi.mock('$app/paths', () => ({
	base: ''
}));

vi.mock('$api/notification-service', () => ({
	notify: vi.fn()
}));

vi.mock('$api/socket/socket-connector', () => ({
	socketService: {
		onConnect: vi.fn(),
		offConnect: vi.fn(),
		onDisconnect: vi.fn(),
		connect: vi.fn(),
		getSkills: vi.fn(() => Promise.resolve([])),
		getClasses: vi.fn(() => Promise.resolve([])),
		getSkillYaml: vi.fn(() => Promise.resolve('')),
		getClassYaml: vi.fn(() => Promise.resolve(''))
	}
}));

const skillYaml = (name: string): SkillYamlData =>
	({
		name,
		type: 'Dynamic',
		'max-level': 7,
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
	}) as SkillYamlData;

const classYaml = (name: string): ClassYamlData =>
	({
		name,
		'action-bar': '',
		prefix: `&6${name}`,
		group: 'class',
		mana: '&2Mana',
		'max-level': 40,
		parent: '',
		'needs-permission': false,
		attributes: {
			'health-base': 22,
			'health-scale': 1,
			'mana-base': 20,
			'mana-scale': 1
		},
		'mana-regen': 2.5,
		'skill-tree': 'REQUIREMENT',
		blacklist: [],
		skills: [],
		icon: 'stone',
		'icon-data': 0,
		'icon-lore': [],
		'exp-source': 273,
		'combo-starters': {}
	}) as ClassYamlData;

describe('editor store hydration from IndexedDB', () => {
	beforeEach(async () => {
		localStorage.clear();
		const persistence = await import('./editor-persistence');
		await persistence.resetEditorPersistenceForTests();
		const session = await import('./editor-session');
		session.resetEditorHydrationForTests();
	});

	it('hydrates skill, class and attribute stores and resolves folder references', async () => {
		const persistence = await import('./editor-persistence');
		await persistence.savePersistedSkill('Meteor', skillYaml('Meteor'));
		await persistence.savePersistedSkill('Fireball', skillYaml('Fireball'));
		await persistence.savePersistedClass('Mage', classYaml('Mage'));
		await persistence.savePersistedAttributes([
			{
				name: 'strength',
				data: {
					display: 'Strength',
					max: 999,
					cost_base: 1,
					cost_modifier: 0,
					icon: 'Ink sac',
					'icon-data': 0,
					'icon-lore': [],
					global: { target: {}, condition: {}, mechanic: {} },
					stats: {}
				}
			}
		]);
		await persistence.savePersistedFolders('skill', [
			{
				location: 'local',
				dataType: 'folder',
				name: 'Magic',
				data: ['Meteor'],
				open: true
			}
		]);

		const { hydrateEditorData } = await import('./editor-session');
		await hydrateEditorData();

		const { skillStore } = await import('./skill-store.svelte');
		const { classStore } = await import('./class-store.svelte');
		const { attributeStore } = await import('./attribute-store');

		expect(get(skillStore.skills).map((skill) => skill.name)).toEqual(['Fireball', 'Meteor']);
		expect(get(classStore.classes).map((clazz) => clazz.name)).toEqual(['Mage']);
		expect(get(attributeStore.attributes).map((attr) => attr.name)).toEqual(['strength']);

		const folders = get(skillStore.skillFolders);
		expect(folders).toHaveLength(1);
		expect(folders[0].name).toBe('Magic');
		expect(folders[0].open).toBe(true);
		// Folder contents resolve to the same instances the store holds
		expect(folders[0].data).toContain(skillStore.getSkill('Meteor'));
	});

	it('lazily loads a hydrated skill and class from IndexedDB with correct field mapping', async () => {
		const persistence = await import('./editor-persistence');
		await persistence.savePersistedSkill('Meteor', skillYaml('Meteor'));
		await persistence.savePersistedClass('Mage', classYaml('Mage'));

		const { initComponents } = await import('$api/components/components.svelte');
		initComponents();

		const { hydrateEditorData } = await import('./editor-session');
		await hydrateEditorData();

		const { skillStore } = await import('./skill-store.svelte');
		const { classStore } = await import('./class-store.svelte');

		const skill = skillStore.getSkill('Meteor');
		expect(skill).toBeDefined();
		expect(skill!.loaded).toBe(false);
		await skillStore.loadSkill(skill!);
		expect(skill!.loaded).toBe(true);
		expect(skill!.maxLevel).toBe(7);

		const clazz = classStore.getClass('Mage');
		expect(clazz).toBeDefined();
		await classStore.loadClass(clazz!);
		expect(clazz!.loaded).toBe(true);
		// Mana regen survives the round trip (regression guard for import defaults)
		expect(clazz!.manaRegen).toBe(2.5);
		expect(clazz!.health.base).toBe(22);
	});

	it('round-trips an edited skill back through persistence across a reload', async () => {
		const persistence = await import('./editor-persistence');
		await persistence.savePersistedSkill('Meteor', skillYaml('Meteor'));

		const { initComponents } = await import('$api/components/components.svelte');
		initComponents();

		const { hydrateEditorData } = await import('./editor-session');
		await hydrateEditorData();

		const { skillStore } = await import('./skill-store.svelte');
		const skill = skillStore.getSkill('Meteor');
		await skillStore.loadSkill(skill!);

		skill!.maxLevel = 10;
		await persistence.savePersistedSkill(skill!.name, skill!.serializeYaml());

		const stored = await persistence.getPersistedSkill('Meteor');
		expect(stored?.['max-level']).toBe(10);
		// And the persisted shape is loadable again
		const { default: FabledSkill } = await import('./skill-store.svelte');
		const reloaded = new FabledSkill({ name: 'Meteor', location: 'local' });
		await reloaded.load(stored!);
		expect(reloaded.maxLevel).toBe(10);
	});
});
