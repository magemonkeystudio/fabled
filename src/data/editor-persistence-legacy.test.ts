import { beforeEach, describe, expect, it, vi } from 'vitest';
import YAML from 'yaml';
import type { SkillYamlData } from '$api/types';
import { clearLegacyEditorStorage, collectLegacyEditorData } from './editor-persistence-legacy';

vi.mock('$app/environment', () => ({
	browser: true
}));

vi.mock('$api/notification-service', () => ({
	notify: vi.fn()
}));

const skillData = (name: string): SkillYamlData =>
	({
		name,
		type: 'Dynamic',
		'max-level': 5,
		icon: 'stone',
		components: {}
	}) as SkillYamlData;

const yamlOf = (value: object) => YAML.stringify(value, { lineWidth: 0, aliasDuplicateObjects: false });

describe('legacy editor data collection', () => {
	beforeEach(() => {
		localStorage.clear();
	});

	it('collects the per-key sapi.* format and marks those keys consumed', () => {
		localStorage.setItem('skillNames', 'Meteor, Fireball');
		localStorage.setItem('sapi.skill.Meteor', yamlOf({ Meteor: skillData('Meteor') }));
		localStorage.setItem('sapi.skill.Fireball', yamlOf({ Fireball: skillData('Fireball') }));

		const collected = collectLegacyEditorData();

		expect(collected.skills.map((record) => record.name)).toEqual(['Meteor', 'Fireball']);
		expect(collected.consumedKeys).toEqual(
			expect.arrayContaining(['sapi.skill.Meteor', 'sapi.skill.Fireball'])
		);
	});

	it('collects the oldest monolithic skillData/classData format', () => {
		localStorage.setItem(
			'skillData',
			yamlOf({ loaded: false, Meteor: skillData('Meteor'), Fireball: skillData('Fireball') })
		);

		const collected = collectLegacyEditorData();

		expect(collected.skills.map((record) => record.name)).toEqual(['Meteor', 'Fireball']);
		expect(collected.consumedKeys).toContain('skillData');
	});

	it('expands the legacy comma-separated attribs list into default attribute data', () => {
		localStorage.setItem('attribs', 'strength, dexterity');

		const collected = collectLegacyEditorData();

		expect(collected.attributes.map((record) => record.name)).toEqual([
			'strength',
			'dexterity'
		]);
		expect(collected.attributes[0].data.display).toBe('strength');
		expect(collected.consumedKeys).toContain('attribs');
	});

	it('keeps other records when one entry is corrupt and does not consume the corrupt key', () => {
		localStorage.setItem('skillNames', 'Meteor, Broken');
		localStorage.setItem('sapi.skill.Meteor', yamlOf({ Meteor: skillData('Meteor') }));
		localStorage.setItem('sapi.skill.Broken', '{unclosed: [yaml');

		const collected = collectLegacyEditorData();

		expect(collected.skills.map((record) => record.name)).toEqual(['Meteor']);
		expect(collected.consumedKeys).toContain('sapi.skill.Meteor');
		expect(collected.consumedKeys).not.toContain('sapi.skill.Broken');
	});

	it('treats corrupt folder JSON as empty without consuming the key', () => {
		localStorage.setItem('skillFolders', '{not valid json');

		const collected = collectLegacyEditorData();

		expect(collected.skillFolders).toEqual([]);
		expect(collected.consumedKeys).not.toContain('skillFolders');
	});

	it('clears only consumed keys plus the name indexes', () => {
		localStorage.setItem('skillNames', 'Meteor, Broken');
		localStorage.setItem('sapi.skill.Meteor', yamlOf({ Meteor: skillData('Meteor') }));
		localStorage.setItem('sapi.skill.Broken', '{unclosed: [yaml');

		const collected = collectLegacyEditorData();
		clearLegacyEditorStorage(collected.consumedKeys);

		expect(localStorage.getItem('sapi.skill.Meteor')).toBeNull();
		expect(localStorage.getItem('skillNames')).toBeNull();
		expect(localStorage.getItem('sapi.skill.Broken')).not.toBeNull();
	});
});
