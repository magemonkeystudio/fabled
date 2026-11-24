import { describe, expect, it } from 'vitest';
import { getPersistenceFailureMessage, isStorageQuotaError } from './persistence-state';

describe('storage helpers', () => {
  it('recognizes quota exceeded errors by name and message', () => {
    expect(isStorageQuotaError(new DOMException('Quota exceeded', 'QuotaExceededError'))).toBe(
      true
    );
    expect(isStorageQuotaError({ message: 'The quota has been exceeded.' })).toBe(true);
    expect(isStorageQuotaError(new Error('disk full'))).toBe(false);
  });
});

describe('persistence failure messaging', () => {
  it('describes quota failures in user-facing terms', () => {
    expect(
      getPersistenceFailureMessage({
        ok: false,
        quotaExceeded: true
      })
    ).toBe('Browser storage is full. Export before refreshing or closing this page.');
  });

  it('describes non-quota failures generically', () => {
    expect(
      getPersistenceFailureMessage({
        ok: false,
        quotaExceeded: false
      })
    ).toBe(
      "The editor couldn't persist this change to browser storage. Your latest edits remain only in memory until you refresh or close this page."
    );
  });
});
