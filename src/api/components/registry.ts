import type { Readable, Writable } from 'svelte/store';
import { derived, get, writable }  from 'svelte/store';
import type ProComponent           from '$api/components/procomponent';
import type ProTrigger             from '$api/components/triggers';
import type ProTarget              from '$api/components/targets';
import type ProCondition           from '$api/components/conditions';
import type ProMechanic            from '$api/components/mechanics';
import type { YamlComponentData }  from '$api/types';

export type RegistryEntry = {
	name: string,
	alias?: string,
	component: typeof ProComponent,
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
		.sort((a, b) => (triggers[a].component.new().isDeprecated ? 0 : -1) - (triggers[b].component.new().isDeprecated ? 0 : -1))
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
		.sort((a, b) => (targets[a].component.new().isDeprecated ? 0 : -1) - (targets[b].component.new().isDeprecated ? 0 : -1))
		.map(key => targets[key]);

	return filtered;
});
export const filteredConditions: Readable<Array<RegistryEntry>> = derived([conditions, filterParams], ([conditions, filterParams]) => {
	const filtered = Object.keys(conditions)
		.filter(key => key.toLowerCase().replace('_', ' ').includes(filterParams.toLowerCase()))
		.sort((a, b) => (conditions[a].component.new().isDeprecated ? 0 : -1) - (conditions[b].component.new().isDeprecated ? 0 : -1))
		.map(key => conditions[key]);

	return filtered;
});
export const filteredMechanics: Readable<Array<RegistryEntry>>  = derived([mechanics, filterParams], ([mechanics, filterParams]) => {
	const filtered = Object.keys(mechanics)
		.filter(key => key.toLowerCase().replace('_', ' ').includes(filterParams.toLowerCase()))
		.sort((a, b) => (mechanics[a].component.new().isDeprecated ? 0 : -1) - (mechanics[b].component.new().isDeprecated ? 0 : -1))
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
	public static getTriggerByName = (name: string): typeof ProTrigger | undefined => {
		const val = Object.values(get(triggers))
			.find(trig => trig.name.toLowerCase() === name.toLowerCase() || trig.alias?.toLowerCase() === name.toLowerCase())?.component;
		return <typeof ProTrigger><unknown>val;
	};

	public static getTargetByName = (name: string): typeof ProTarget | undefined => {
		const val = Object.values(get(targets))
			.find(target => target.name.toLowerCase() === name.toLowerCase() || target.alias?.toLowerCase() === name.toLowerCase())?.component;
		return <typeof ProTarget><unknown>val;
	};

	public static getConditionByName = (name: string): typeof ProCondition | undefined => {
		const val = Object.values(get(conditions))
			.find(condition => condition.name.toLowerCase() === name.toLowerCase() || condition.alias?.toLowerCase() === name.toLowerCase())?.component;
		return <typeof ProCondition><unknown>val;
	};

	public static getMechanicByName = (name: string): typeof ProMechanic | undefined => {
		const val = Object.values(get(mechanics))
			.find(mechanic => mechanic.name.toLowerCase() === name.toLowerCase() || mechanic.alias?.toLowerCase() === name.toLowerCase())?.component;
		return <typeof ProMechanic><unknown>val;
	};

	public static deserializeComponents = (yaml: YamlComponentData): ProComponent[] => {
		if (!yaml) return [];
		const comps: ProComponent[] = [];

		const keys: string[] = Object.keys(yaml);
		for (const key of keys) {
			let comp: ProComponent | undefined = undefined;
			const data                         = yaml[key];
			const type                         = data.type;

			if (type === 'trigger') {
				const trigger: typeof ProTrigger | undefined = Registry.getTriggerByName(key.split('-')[0]);
				if (trigger) {
					comp = trigger.new();
				}
			} else if (type === 'target') {
				const target: typeof ProTarget | undefined = Registry.getTargetByName(key.split('-')[0]);
				if (target) {
					comp = target.new();
				}
			} else if (type === 'condition') {
				const condition: typeof ProCondition | undefined = Registry.getConditionByName(key.split('-')[0]);
				if (condition) {
					comp = condition.new();
				}
			} else if (type === 'mechanic') {
				const mechanic: typeof ProMechanic | undefined = Registry.getMechanicByName(key.split('-')[0]);
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