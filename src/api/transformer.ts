import type FabledClass from '../data/class-store';
import type FabledSkill from '../data/skill-store';

export interface Transformer<T, V> {
	transform(value: T): V;
}

export class NameTransformer implements Transformer<FabledSkill | FabledClass | string, string> {
	transform(value: FabledSkill | FabledClass | string): string {
		if (typeof value === 'string') return value;
		return value.name;
	}
};