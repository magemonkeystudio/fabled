import { describe, expect, it } from 'vitest';
import {
	beginPersistenceSave,
	finishPersistenceSave,
	getPersistenceWarning,
	isStorageQuotaError
} from './persistence-state';

describe('storage helpers', () => {
	it('recognizes quota exceeded errors by name and message', () => {
		expect(isStorageQuotaError(new DOMException('Quota exceeded', 'QuotaExceededError'))).toBe(
			true
		);
		expect(isStorageQuotaError({ message: 'The quota has been exceeded.' })).toBe(true);
		expect(isStorageQuotaError(new Error('disk full'))).toBe(false);
	});
});

describe('storage save state machine', () => {
	it('blocks repeated oversized saves until the warning is acknowledged', () => {
		const decision = beginPersistenceSave({
			name: 'HugeSkill',
			tooBig: true,
			acknowledged: false
		});

		expect(decision.shouldPersist).toBe(false);
		expect(decision.saveError).toEqual({ name: 'HugeSkill', acknowledged: false });
	});

	it('allows acknowledged oversized saves to retry persistence', () => {
		const decision = beginPersistenceSave({
			name: 'HugeSkill',
			tooBig: true,
			acknowledged: true
		});

		expect(decision.shouldPersist).toBe(true);
		expect(decision.saveError).toBeUndefined();
	});

	it('marks quota failures as recoverable oversized saves', () => {
		const decision = finishPersistenceSave(
			{
				name: 'HugeSkill',
				tooBig: false,
				acknowledged: true
			},
			{
				ok: false,
				quotaExceeded: true
			}
		);

		expect(decision.shouldPersist).toBe(false);
		expect(decision.state).toEqual({
			name: 'HugeSkill',
			tooBig: true,
			acknowledged: false
		});
		expect(decision.saveError).toEqual({ name: 'HugeSkill', acknowledged: false });
	});

	it('clears oversized state after a successful retry', () => {
		const decision = finishPersistenceSave(
			{
				name: 'HugeSkill',
				tooBig: true,
				acknowledged: true
			},
			{
				ok: true,
				quotaExceeded: false
			}
		);

		expect(decision.shouldPersist).toBe(true);
		expect(decision.state).toEqual({
			name: 'HugeSkill',
			tooBig: false,
			acknowledged: false
		});
		expect(decision.clearSaveError).toBe(true);
	});

	it('builds an active skill warning for memory-only data', () => {
		expect(
			getPersistenceWarning(
				{
					dataType: 'skill',
					name: 'Meteor',
					tooBig: true
				},
				false
			)
		).toEqual({
			label: 'Skill only in memory',
			detail: 'Meteor is too large for browser storage. Export before refreshing or closing.'
		});
	});

	it('builds an attribute warning when the attribute dataset is too large', () => {
		expect(
			getPersistenceWarning(
				{
					dataType: 'attribute',
					name: 'Strength'
				},
				true
			)
		).toEqual({
			label: 'Attributes only in memory',
			detail:
				'Your attributes are too large for browser storage. Export before refreshing or closing.'
		});
	});
});
