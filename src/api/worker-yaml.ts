/**
 * worker-yaml.ts
 * Promise-based wrapper around yaml-worker.ts.
 *
 * Usage:
 *   import { workerParse, workerParseChunked } from '$api/worker-yaml';
 *
 *   // Small file — full parse, returns parsed object
 *   const data = await workerParse(text);
 *
 *   // Large file — chunked parse with progress callback
 *   await workerParseChunked(text, (batch, processed, total) => {
 *     // batch: Record<string, SkillYamlData>
 *     loadBatch(batch);
 *     showProgress(processed / total);
 *   });
 */

import { browser } from '$app/environment';

// Lazy-initialize the worker (only in browser, and only once)
let _worker: Worker | null = null;

function getWorker(): Worker {
	if (!_worker) {
		_worker = new Worker(
			new URL('../workers/yaml-worker.ts', import.meta.url),
			{ type: 'module' }
		);
	}
	return _worker;
}

let _nextId = 0;
function nextId(): string {
	return String(_nextId++);
}

// ---------------------------------------------------------------------------
// Full parse (small files)
// ---------------------------------------------------------------------------

export function workerParse(text: string): Promise<Record<string, unknown>> {
	if (!browser) return Promise.resolve({});

	return new Promise((resolve, reject) => {
		const id     = nextId();
		const worker = getWorker();

		const handler = (e: MessageEvent) => {
			if (e.data.id !== id) return;
			worker.removeEventListener('message', handler);

			if (e.data.type === 'done')  resolve(e.data.result ?? {});
			if (e.data.type === 'error') reject(new Error(e.data.error));
		};

		worker.addEventListener('message', handler);
		worker.postMessage({ type: 'parse', id, text });
	});
}

// ---------------------------------------------------------------------------
// Chunked parse (large all-in-one files)
// ---------------------------------------------------------------------------

export type ChunkBatchCallback = (
	batch: Record<string, unknown>,
	processed: number,
	total: number
) => void | Promise<void>;

export function workerParseChunked(
	text: string,
	onBatch: ChunkBatchCallback,
	batchSize = 25
): Promise<void> {
	if (!browser) return Promise.resolve();

	return new Promise((resolve, reject) => {
		const id     = nextId();
		const worker = getWorker();

		const handler = async (e: MessageEvent) => {
			if (e.data.id !== id) return;

			if (e.data.type === 'batch') {
				await onBatch(e.data.data, e.data.processed, e.data.total);
			}

			if (e.data.type === 'done') {
				worker.removeEventListener('message', handler);
				resolve();
			}

			if (e.data.type === 'error') {
				worker.removeEventListener('message', handler);
				reject(new Error(e.data.error));
			}
		};

		worker.addEventListener('message', handler);
		worker.postMessage({ type: 'parseChunked', id, text, batchSize });
	});
}
