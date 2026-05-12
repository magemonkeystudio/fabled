// UI/state helpers for persistence failures. IndexedDB owns the actual editor storage,
// but the editor still needs a shared way to classify quota-like errors and drive the
// "memory only" warning/acknowledgement flow for oversized data.
export interface PersistenceSaveErrorTarget {
	name: string;
	acknowledged: boolean;
}

export interface PersistenceSaveState extends PersistenceSaveErrorTarget {
	tooBig: boolean;
}

export interface PersistenceWriteResult {
	ok: boolean;
	quotaExceeded: boolean;
	error?: unknown;
}

export interface PersistenceSaveDecision {
	shouldPersist: boolean;
	state: PersistenceSaveState;
	saveError?: PersistenceSaveErrorTarget;
	clearSaveError: boolean;
}

export interface PersistenceWarning {
	label: string;
	detail: string;
}

interface ActivePersistenceTarget {
	dataType?: 'class' | 'skill' | 'attribute' | string;
	name?: string;
	tooBig?: boolean;
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

export const beginPersistenceSave = (state: PersistenceSaveState): PersistenceSaveDecision => {
	if (!state.tooBig || state.acknowledged) {
		return {
			shouldPersist: true,
			state,
			clearSaveError: false
		};
	}

	return {
		shouldPersist: false,
		state,
		saveError: {
			name: state.name,
			acknowledged: state.acknowledged
		},
		clearSaveError: false
	};
};

export const finishPersistenceSave = (
	state: PersistenceSaveState,
	result: PersistenceWriteResult
): PersistenceSaveDecision => {
	if (result.ok) {
		return {
			shouldPersist: true,
			state: {
				...state,
				tooBig: false,
				acknowledged: false
			},
			clearSaveError: true
		};
	}

	if (result.quotaExceeded) {
		return {
			shouldPersist: false,
			state: {
				...state,
				tooBig: true,
				acknowledged: false
			},
			saveError: {
				name: state.name,
				acknowledged: false
			},
			clearSaveError: false
		};
	}

	return {
		shouldPersist: false,
		state,
		clearSaveError: false
	};
};

export const getPersistenceWarning = (
	active: ActivePersistenceTarget | undefined
): PersistenceWarning | undefined => {
	if (!active?.tooBig || (active.dataType !== 'skill' && active.dataType !== 'class')) {
		return undefined;
	}

	const itemType = active.dataType === 'skill' ? 'Skill' : 'Class';
	return {
		label: `${itemType} only in memory`,
		detail: `${active.name || itemType} is too large for browser storage. Export before refreshing or closing.`
	};
};
