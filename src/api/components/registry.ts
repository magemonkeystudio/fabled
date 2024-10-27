import type { Readable, Writable } from 'svelte/store';
import { derived, get, writable }  from 'svelte/store';
import type FabledComponent        from '$api/components/fabled-component.svelte';
import type FabledTrigger          from '$api/components/triggers.svelte';
import type FabledTarget           from '$api/components/targets.svelte';
import type FabledCondition        from '$api/components/conditions.svelte';
import type FabledMechanic         from '$api/components/mechanics.svelte';
import type { YamlComponentData }  from '$api/types';
import { deprecated }              from '$api/components/components.svelte';

export type RegistryEntry = {
	name: string,
	alias?: string,
	component: typeof FabledComponent,
	section?: string
};
export type RegistryData = {
	[key: string]: RegistryEntry
};

export const triggers: Writable<RegistryData>   = writable({});
export const targets: Writable<RegistryData>    = writable({});
export const conditions: Writable<RegistryData> = writable({});
export const mechanics: Writable<RegistryData>  = writable({});
export const initialized: Writable<boolean>     = writable(false);

export const filterParams: Writable<string> = writable('');
// Create a derived store that filters the registry based on the filterParams
// The filter params should filter the entries by their name
export const filteredTriggers: Readable<Array<RegistryEntry>> = derived([triggers, filterParams], ([triggers, filterParams]) => {
	const filtered = Object.keys(triggers)
		.filter(key => key.toLowerCase().replace('_', ' ').includes(filterParams.toLowerCase()))
		.sort((a, b) => (deprecated.includes(triggers[a].component) ? 0 : -1) - (deprecated.includes(triggers[b].component) ? 0 : -1))
		.map(key => triggers[key]);

	return filtered;
});

export const triggerSections = derived(filteredTriggers, (triggers) => {
	const sections: { [key: string]: RegistryEntry[] } = {};
	triggers.forEach(trigger => {
		let section = 'General';
		if (trigger.section) section = trigger.section;
		if (!sections[section]) sections[section] = [];
		sections[section].push(trigger);
	});

	return sections;
});

export const filteredTargets: Readable<Array<RegistryEntry>>    = derived([targets, filterParams], ([targets, filterParams]) => {
	const filtered = Object.keys(targets)
		.filter(key => key.toLowerCase().replace('_', ' ').includes(filterParams.toLowerCase()))
		.sort((a, b) => (deprecated.includes(targets[a].component) ? 0 : -1) - (deprecated.includes(targets[b].component) ? 0 : -1))
		.map(key => targets[key]);

	return filtered;
});
export const filteredConditions: Readable<Array<RegistryEntry>> = derived([conditions, filterParams], ([conditions, filterParams]) => {
	const filtered = Object.keys(conditions)
		.filter(key => key.toLowerCase().replace('_', ' ').includes(filterParams.toLowerCase()))
		.sort((a, b) => (deprecated.includes(conditions[a].component) ? 0 : -1) - (deprecated.includes(conditions[b].component) ? 0 : -1))
		.map(key => conditions[key]);

	return filtered;
});
export const filteredMechanics: Readable<Array<RegistryEntry>>  = derived([mechanics, filterParams], ([mechanics, filterParams]) => {
	const filtered = Object.keys(mechanics)
		.filter(key => key.toLowerCase().replace('_', ' ').includes(filterParams.toLowerCase()))
		.sort((a, b) => (deprecated.includes(mechanics[a].component) ? 0 : -1) - (deprecated.includes(mechanics[b].component) ? 0 : -1))
		.map(key => mechanics[key]);

	return filtered;
});

export const mechanicSections = derived(filteredMechanics, (mechanics) => {
	const sections: { [key: string]: RegistryEntry[] } = {};
	mechanics.forEach(mechanic => {
		let section = 'General';
		if (mechanic.section) section = mechanic.section;
		if (!sections[section]) sections[section] = [];
		sections[section].push(mechanic);
	});

	return sections;
});

export default class Registry {
	public static getTriggerByName = (name: string): typeof FabledTrigger | undefined => {
		const val = Object.values(get(triggers))
			.find(trig => trig.name.toLowerCase() === name.toLowerCase() || trig.alias?.toLowerCase() === name.toLowerCase())?.component;
		return <typeof FabledTrigger><unknown>val;
	};

	public static getTargetByName = (name: string): typeof FabledTarget | undefined => {
		const val = Object.values(get(targets))
			.find(target => target.name.toLowerCase() === name.toLowerCase() || target.alias?.toLowerCase() === name.toLowerCase())?.component;
		return <typeof FabledTarget><unknown>val;
	};

	public static getConditionByName = (name: string): typeof FabledCondition | undefined => {
		const val = Object.values(get(conditions))
			.find(condition => condition.name.toLowerCase() === name.toLowerCase() || condition.alias?.toLowerCase() === name.toLowerCase())?.component;
		return <typeof FabledCondition><unknown>val;
	};

	public static getMechanicByName = (name: string): typeof FabledMechanic | undefined => {
		const val = Object.values(get(mechanics))
			.find(mechanic => mechanic.name.toLowerCase() === name.toLowerCase() || mechanic.alias?.toLowerCase() === name.toLowerCase())?.component;
		return <typeof FabledMechanic><unknown>val;
	};

	public static deserializeComponents = (yaml: YamlComponentData): FabledComponent[] => {
		if (!yaml) return [];
		const comps: FabledComponent[] = [];

		const keys: string[] = Object.keys(yaml);
		for (const key of keys) {
			let comp: FabledComponent | undefined = undefined;
			const data                            = yaml[key];
			const type                            = data.type;

			if (type === 'trigger') {
				const trigger: typeof FabledTrigger | undefined = Registry.getTriggerByName(key.split('-')[0]);
				if (trigger) {
					comp = trigger.new();
				}
			} else if (type === 'target') {
				const target: typeof FabledTarget | undefined = Registry.getTargetByName(key.split('-')[0]);
				if (target) {
					comp = target.new();
				}
			} else if (type === 'condition') {
				const condition: typeof FabledCondition | undefined = Registry.getConditionByName(key.split('-')[0]);
				if (condition) {
					comp = condition.new();
				}
			} else if (type === 'mechanic') {
				const mechanic: typeof FabledMechanic | undefined = Registry.getMechanicByName(key.split('-')[0]);
				if (mechanic) {
					comp = mechanic.new();
				}
			}

			if (comp) {
				comp.deserialize(data);
				comps.push(comp);
			}
		}

		return comps;
	};
}