import { derived, get, writable, type Readable, type Writable } from "svelte/store";
import { saveAll } from "../data/attribute-store";
import { Attribute } from "./stat";
import type { AttributeYamlData, Icon, ProAttributeData, Serializable } from "./types";
import { conditions, mechanics, targets, type RegistryData, type RegistryEntry } from "./components/registry";
import type ProComponent from "./components/procomponent";
import AttributeSelect from "./options/attributeselect";

export default class FabledAttribute implements Serializable {
	dataType                     = 'attribute';
	location: 'local' | 'server' = 'local';
	loaded                       = false;

	name: string;
	display: string;
	max: number = 999;
	cost: Attribute = new Attribute('cost', 1, 0);
	icon: Icon = {
        material: 'Ink sac',
        customModelData: 0
    };
	public targets: AttributeSection = new AttributeSection(targets, this);
	public conditions: AttributeSection = new AttributeSection(conditions, this);
	public mechanics: AttributeSection = new AttributeSection(mechanics, this);
	public stats: AttributeStats = new AttributeStats(this);

	constructor(data?: ProAttributeData) {
        this.name = data ? data.name : "Attribute";
		this.display = data?.display ? data.display : this.name;
        if (!data) return;
		if (data.max) this.max = data.max;
		if (data.cost) this.cost = data.cost;
		if (data.icon) this.icon = data.icon;
		if (data.targets) this.targets = new AttributeSection(targets, this, data.targets);
		if (data.conditions) this.conditions = new AttributeSection(conditions, this, data.conditions);
		if (data.mechanics) this.mechanics = new AttributeSection(mechanics, this, data.mechanics);
		if (data.stats) this.stats = new AttributeStats(this, data.stats);
	}

    public serializeYaml = (): AttributeYamlData => {
        return <AttributeYamlData> {
			display: this.display,
	        max: this.max,
			cost_base: this.cost.base,
			cost_modifier: this.cost.scale,
	        icon: this.icon.material,
	        'icon-data': this.icon.customModelData,
	        'icon-lore': this.icon.lore,
			global: {
				target: this.targets.serializeYaml(),
				condition: this.conditions.serializeYaml(),
				mechanic: this.mechanics.serializeYaml()
			},
	        stats: this.stats.serializeYaml()
		};
    };

	public load = (yaml: AttributeYamlData) => {
        this.display = yaml.display;
		if (yaml.max) this.max = yaml.max;
		this.cost = new Attribute('cost', yaml.cost_base || 1, yaml.cost_modifier || 0);
        this.icon = <Icon> {
            material: yaml.icon || 'Ink sac',
            customModelData: yaml["icon-data"] || 0,
            lore: yaml["icon-lore"] || null
        }
        if (yaml.global?.target) this.targets.load(yaml.global.target);
        if (yaml.global?.condition) this.conditions.load(yaml.global.condition);
        if (yaml.global?.mechanic) this.mechanics.load(yaml.global.mechanic);
        if (yaml.stats) this.stats.load(yaml.stats);

		this.loaded = true;
	};

	public save = () => {
		if (!this.name) return;
		if (this.location === 'server') {
			return;
		}
		saveAll();
	};
}

export class AttributeSection {
	registry: Readable<RegistryData>;
	public attribute: FabledAttribute;
	public components: Writable<AttributeComponent[]> = writable([]);
	public availableComponents: Readable<string[]> = derived(this.components, components =>
		Object.values(get(this.registry)).map(entry => entry.name).filter(name => !components.some(comp => get(comp.name) === name)))
	
	constructor(registry: Readable<RegistryData>, attribute: FabledAttribute, data?: AttributeComponent[]) {
		this.registry = registry;
		this.attribute = attribute;
		if (data) this.components.set(data);
	}

	refresh = () => {
		this.components.set(get(this.components));
	}

	public addComponent = (component?: string) => {
		if (!component) return;
		let components = get(this.components);
		components.push(new AttributeComponent(this, component, this.registry));
		this.components.set(components)
	}

	public removeComponent = (component?: AttributeComponent) => {
		if (!component) return;
		this.components.set(get(this.components).filter(comp => comp != component));
	}

	public load = (data?: {[key: string]: string}) => {
		if (data) {
			let components: AttributeComponent[] = []
			for (const key of Object.keys(data)) {
				let split: string[] = key.split('-', 2);
				if (split.length !== 2) continue;
				let component: AttributeComponent | undefined = components.find(comp => get(comp.name) === split[0]);
				if (!component) {
					component = new AttributeComponent(this, split[0], this.registry);
					components.push(component);
				}
				let stats: AttributeStat[] = get(component.stats);
				stats.push(new AttributeStat(component, split[1], data[key]));
				component.stats.set(stats);
			}
			this.components.set(components);
		}
	}

    public serializeYaml = (): {[key: string]: string} => {
		let yaml: {[key: string]: string} = {};
		for (const component of get(this.components)) {
			for (const stat of get(component.stats)) {
				yaml[get(component.name)+'-'+get(stat.key)] = stat.formula;
			}
		}
		return yaml;
    };
}

export class AttributeComponent {
	section: AttributeSection;
	public name: Writable<string> = writable('');
	registry: Readable<RegistryData>;
	public component: Readable<ProComponent | undefined> = derived(this.name, (name: string): ProComponent | undefined => {
		let newComponent: ProComponent | undefined = Object.values(get(this.registry)).find(entry => entry.name === name)?.component.new();
		if (newComponent) this.stats.set(get(this.stats).filter(stat => newComponent.data.some(option => option.key === get(stat.key))));
		return newComponent;
	})
	public stats: Writable<AttributeStat[]> = writable([]);
	public availableStats: Readable<string[]> = derived([this.component, this.stats], ([component, stats]) => {
		if (component) {
			return component.data.filter(option => option instanceof AttributeSelect && !stats.some(stat => get(stat.key) === option.key)).map(option => option.key);
		}
		return [];
	})

	constructor(section: AttributeSection, name: string, registry: Readable<RegistryData>, stats?: AttributeStat[]) {
		this.section = section;
		this.name.set(name);
		this.name.subscribe(() => this.section.refresh());
		this.registry = registry;
		if (stats) this.stats.set(stats);
	}

	refresh = () => {
		this.stats.set(get(this.stats));
	}

	public addStat = (stat?: string) => {
		if (!stat) return;
		let stats: AttributeStat[] = get(this.stats);
		stats.push(new AttributeStat(this, stat));
		this.stats.set(stats);
	}

	public removeStat = (stat?: AttributeStat) => {
		if (!stat) return;
		this.stats.set(get(this.stats).filter(st => st != stat))
	}
}

export class AttributeStat {
	public key: Writable<string> = writable('');
	public formula: string = 'a*0.025+1*v';

	constructor(parent: AttributeComponent | AttributeStats, key?: string, formula?: string) {
		this.key.subscribe(() => parent.refresh());
		if (key) this.key.set(key);
		if (formula) this.formula = formula;
	}
}

export class AttributeStats {
	public attribute: FabledAttribute;
	public stats: Writable<AttributeStat[]> = writable([]);

	constructor(attribute: FabledAttribute, stats?: AttributeStat[]) {
		this.attribute = attribute;
		if (stats) this.stats.set(stats);
	}

	refresh = () => {
		this.stats.set(get(this.stats));
	}

	public addStat = () => {
		let stats: AttributeStat[] = get(this.stats);
		stats.push(new AttributeStat(this, ''));
		this.stats.set(stats);
	}

	public removeStat = (stat?: AttributeStat) => {
		if (!stat) return;
		this.stats.set(get(this.stats).filter(st => st != stat))
	}

	public load = (data?: {[key: string]: string}) => {
		if (data) {
			let stats: AttributeStat[] = []
			for (const key of Object.keys(data)) {
				stats.push(new AttributeStat(this, key, data[key]));
			}
			this.stats.set(stats);
		}
	}

    public serializeYaml = (): {[key: string]: string} => {
		let yaml: {[key: string]: string} = {};
		for (const stat of get(this.stats)) {
			yaml[get(stat.key)] = stat.formula;
		}
		return yaml;
    };
}