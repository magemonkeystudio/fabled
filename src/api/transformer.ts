import type FabledSkill from '$api/fabled-skill';
import type FabledClass from '$api/fabled-class';

export interface Transformer<T, V> {
    transform(value: T): V;
}

export class NameTransformer implements Transformer<FabledSkill | FabledClass | string, string> {
    transform(value: FabledSkill | FabledClass | string): string {
        if (typeof value === 'string') return value;
        return value.name;
    }
};