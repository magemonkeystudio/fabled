// Shared helpers for browser-persistence failures. IndexedDB owns editor storage, but
// the UI still needs a consistent way to classify failures and explain them to users.
export interface PersistenceSaveError {
	name: string;
	message: string;
}

export interface PersistenceWriteResult {
	ok: boolean;
	quotaExceeded: boolean;
	error?: unknown;
}

const storageQuotaNames = new Set(['QuotaExceededError', 'NS_ERROR_DOM_QUOTA_REACHED']);
const storageQuotaCodes = new Set([22, 1014]);

export const isStorageQuotaError = (error: unknown): boolean => {
	if (!error || typeof error !== 'object') return false;

	const maybeDomException = error as { name?: string; code?: number; message?: string };
	if (maybeDomException.name && storageQuotaNames.has(maybeDomException.name)) {
		return true;
	}

	if (typeof maybeDomException.code === 'number' && storageQuotaCodes.has(maybeDomException.code)) {
		return true;
	}

	if (typeof maybeDomException.message !== 'string') return false;

	return maybeDomException.message.toLowerCase().includes('quota');
};

export const getPersistenceFailureMessage = (result: PersistenceWriteResult): string =>
	result.quotaExceeded
		? 'Browser storage is full. Export before refreshing or closing this page.'
		: "The editor couldn't persist this change to browser storage. Your latest edits remain only in memory until you refresh or close this page.";
