import { describe, expect, it, vi } from 'vitest';
import { parseYaml }                from './yaml';
import { notify }                   from '$api/notification-service';

describe('yaml test', () => {
	it('notifies when parsing fails', () => {
		vi.mock('./notification-service', () => ({ notify: vi.fn() }));
		const result = parseYaml('invalid yaml: adsf: asdf');
		expect(result).toEqual({});
		expect(notify).toHaveBeenCalled();
	});
});