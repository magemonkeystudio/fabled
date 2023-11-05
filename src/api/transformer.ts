import type ProSkill from '$api/proskill';
import type ProClass from '$api/proclass';

export interface Transformer<T, V> {
    transform(value: T): V;
}

export class NameTransformer implements Transformer<ProSkill | ProClass | string, string> {
    transform(value: ProSkill | ProClass | string): string {
        if (typeof value === 'string') return value;
        return value.name;
    }
};