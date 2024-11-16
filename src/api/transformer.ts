import type FabledClass from '../data/class-store.svelte';
import type FabledSkill from '../data/skill-store.svelte';

export interface Transformer<T, V> {
	transform(value: T): V;
}

export class NameTransformer implements Transformer<FabledSkill | FabledClass | string, string> {
	transform(value: FabledSkill | FabledClass | string): string {
		if (typeof value === 'string') return value;
		return value.name;
	}
}