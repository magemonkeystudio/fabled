/**
 * yaml-worker.ts
 * Runs YAML parsing off the main thread so the UI never freezes.
 *
 * Messages IN  (main → worker):
 *   { type: 'parse',         id, text }
 *   { type: 'parseChunked',  id, text, batchSize? }
 *
 * Messages OUT (worker → main):
 *   { id, type: 'done',     result }          — full parse finished
 *   { id, type: 'batch',    data, processed, total } — chunk batch ready
 *   { id, type: 'progress', processed, total }       — progress ping
 *   { id, type: 'error',    error }
 */

import YAML from 'yaml';

// ---------------------------------------------------------------------------
// Top-level key splitter — pure string scan, zero YAML parsing overhead
// ---------------------------------------------------------------------------

/**
 * Split a multi-skill YAML text into per-skill chunks WITHOUT parsing YAML.
 * A top-level key is any line that:
 *   - starts at column 0 (no leading whitespace)
 *   - is not a comment (#)
 *   - is not blank
 *   - contains ':'
 *
 * Returns Map<skillName, rawYamlChunk>
 */
function splitTopLevel(text: string): Map<string, string> {
	const result: Map<string, string> = new Map();
	const lines                        = text.split('\n');

	let currentKey: string | null = null;
	let currentLines: string[]    = [];

	for (const line of lines) {
		const first = line.charCodeAt(0);

		// Top-level key: doesn't start with whitespace, tab, '#', or be empty
		if (
			line.length > 0 &&
			first !== 32 &&   // space
			first !== 9 &&    // tab
			first !== 35 &&   // '#'
			line.includes(':')
		) {
			if (currentKey !== null) {
				result.set(currentKey, currentLines.join('\n'));
			}
			currentKey   = line.slice(0, line.indexOf(':')).trim();
			currentLines = [line];
		} else if (currentKey !== null) {
			currentLines.push(line);
		}
	}

	if (currentKey !== null && currentLines.length > 0) {
		result.set(currentKey, currentLines.join('\n'));
	}

	return result;
}

// ---------------------------------------------------------------------------
// Message handler
// ---------------------------------------------------------------------------

self.onmessage = (e: MessageEvent) => {
	const { type, text, id, batchSize = 25 } = e.data as {
		type: 'parse' | 'parseChunked';
		text: string;
		id: string;
		batchSize?: number;
	};

	// ---- Plain parse (small files / single skill) -------------------------
	if (type === 'parse') {
		try {
			const result = YAML.parse(text);
			self.postMessage({ id, type: 'done', result });
		} catch (err: any) {
			self.postMessage({ id, type: 'error', error: err.message });
		}
		return;
	}

	// ---- Chunked parse (big all-in-one skills.yml / classes.yml) ----------
	if (type === 'parseChunked') {
		const chunks = splitTopLevel(text);
		const total  = chunks.size;
		let processed = 0;
		let batch: Record<string, unknown> = {};

		for (const [key, chunk] of chunks) {
			// Skip the 'loaded: false' header key
			if (key === 'loaded') {
				processed++;
				continue;
			}

			try {
				const parsed = YAML.parse(chunk);
				if (parsed && typeof parsed === 'object') {
					// parsed is { [key]: SkillYamlData }
					batch[key] = parsed[key] ?? parsed;
				}
			} catch {
				// Malformed chunk — skip silently, don't crash the whole import
			}

			processed++;

			// Flush batch every N items
			if (Object.keys(batch).length >= batchSize) {
				self.postMessage({ id, type: 'batch', data: { ...batch }, processed, total });
				batch = {};
			}
		}

		// Flush remaining
		if (Object.keys(batch).length > 0) {
			self.postMessage({ id, type: 'batch', data: { ...batch }, processed, total });
		}

		self.postMessage({ id, type: 'done', processed, total });
	}
};
