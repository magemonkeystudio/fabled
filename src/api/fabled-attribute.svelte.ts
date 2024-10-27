import { get, type Readable }                                                       from 'svelte/store';
import type { AttributeYamlData, IAttribute, Icon, ProAttributeData, Serializable } from './types';
import { conditions, mechanics, type RegistryData, targets }                        from './components/registry';
import type FabledComponent
																																										from './components/fabled-component.svelte';
import AttributeSelect
																																										from './options/attributeselect.svelte';
import type { ComponentOption }                                                     from '$api/options/options';
import { attributeStore }                                                           from '../data/attribute-store';
import { untrack }                                                                  from 'svelte';

export default class FabledAttribute implements Serializable {
	dataType                     = 'attribute';
	location: 'local' | 'server' = 'local';
	loaded                       = $state(false);

	name: string                        = $state('');
	display: string                     = $state('');
	max: number                         = $state(999);
	cost: IAttribute                    = $state({ name: 'cost', base: 1, scale: 0 });
	icon: Icon                          = $state({
		material:        'Ink sac',
		customModelData: 0
	});
	public targets: AttributeSection    = new AttributeSection(targets, this);
	public conditions: AttributeSection = new AttributeSection(conditions, this);
	public mechanics: AttributeSection  = new AttributeSection(mechanics, this);
	public stats: AttributeStats        = new AttributeStats(this);

	key = {};

	constructor(data?: ProAttributeData) {
		this.name    = data ? data.name : 'Attribute';
		this.display = data?.display ? data.display : this.name;
		if (!data) return;
		if (data.location) this.location = data.location;
		if (data.max) this.max = data.max;
		if (data.cost) this.cost = data.cost;
		if (data.icon) this.icon = data.icon;
		if (data.targets) this.targets = new AttributeSection(targets, this, data.targets);
		if (data.conditions) this.conditions = new AttributeSection(conditions, this, data.conditions);
		if (data.mechanics) this.mechanics = new AttributeSection(mechanics, this, data.mechanics);
		if (data.stats) this.stats = new AttributeStats(this, data.stats);
	}

	public serializeYaml = (): AttributeYamlData => {
		return <AttributeYamlData>{
			display:       this.display,
			max:           this.max,
			cost_base:     this.cost.base,
			cost_modifier: this.cost.scale,
			icon:          this.icon.material,
			'icon-data':   this.icon.customModelData,
			'icon-lore':   this.icon.lore,
			global:        {
				target:    this.targets.serializeYaml(),
				condition: this.conditions.serializeYaml(),
				mechanic:  this.mechanics.serializeYaml()
			},
			stats:         this.stats.serializeYaml()
		};
	};

	public load = (yaml: AttributeYamlData) => {
		this.display = yaml.display;
		if (yaml.max) this.max = yaml.max;
		this.cost = { name: 'cost', base: yaml.cost_base || 1, scale: yaml.cost_modifier || 0 };
		this.icon = <Icon>{
			material:        yaml.icon || 'Ink sac',
			customModelData: yaml['icon-data'] || 0,
			lore:            yaml['icon-lore'] || null
		};
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
		attributeStore.saveAll();
	};
}

export class AttributeSection {
	registry?: Readable<RegistryData>       = $state();
	public attribute: FabledAttribute;
	public components: AttributeComponent[] = $state([]);
	public availableComponents: string[]    = $derived(
		this.registry ?
			Object.values(get(this.registry))
				.filter(entry => untrack(() => entry.component.new()).data.some((option: ComponentOption) => option instanceof AttributeSelect))
				.map(entry => entry.name)
				.filter(name => !this.components.some(comp => comp.name === name))
			: []);

	constructor(registry: Readable<RegistryData>, attribute: FabledAttribute, data?: AttributeComponent[]) {
		this.registry  = registry;
		this.attribute = attribute;
		if (data) this.components = data;
	}

	public addComponent = (component?: string) => {
		if (!component || !this.registry) return;
		this.components.push(new AttributeComponent(this, component, this.registry));
	};

	public removeComponent = (component?: AttributeComponent) => {
		if (!component) return;
		this.components = this.components.filter(comp => comp.id != component.id);
	};

	public load = (data?: { [key: string]: string }) => {
		if (data) {
			for (const key of Object.keys(data)) {
				const split: string[] = key.split('-', 2);
				if (split.length !== 2) continue;
				let component: AttributeComponent | undefined = this.components.find(comp => comp.name === split[0]);
				if (!component) {
					component = new AttributeComponent(this, split[0], <Readable<RegistryData>>this.registry);
					this.components.push(component);
				}
				component.stats.push(new AttributeStat(split[1], data[key]));
			}
		}
	};

	public serializeYaml = (): { [key: string]: string } => {
		const yaml: { [key: string]: string } = {};
		for (const component of this.components) {
			for (const stat of component.stats) {
				yaml[component.name + '-' + stat.key] = stat.formula;
			}
		}
		return yaml;
	};
}

export class AttributeComponent {
	id                                = {};
	section: AttributeSection;
	public name: string               = $state('');
	registry?: Readable<RegistryData> = $state();

	public component: FabledComponent | undefined = $derived(this.registry ? untrack(() => Object.values(get(<Readable<RegistryData>>this.registry)).find(entry => entry.name === this.name)?.component.new()) : undefined);
	public stats: AttributeStat[]                 = $state([]);
	public availableStats: string[]               =
					 $derived(
						 this.component
							 ? this.component.data.filter(option => option instanceof AttributeSelect && !this.stats.some(stat => stat.key === option.key)).map(option => option.key)
							 : []
					 );

	// 	$effect(() => {
	// 	if (this.component) this.stats.filter(stat => this.component?.data.some(option => option.key === stat.key));
	// });

	constructor(section: AttributeSection, name: string, registry: Readable<RegistryData>, stats?: AttributeStat[]) {
		this.section  = section;
		this.name     = name;
		this.registry = registry;
		if (stats) this.stats.push(...stats);
	}

	public addStat = (stat?: string) => {
		if (!stat) return;
		this.stats.push(new AttributeStat(stat));
	};

	public removeStat = (stat?: AttributeStat) => {
		if (!stat) return;
		this.stats = this.stats.filter(st => st != stat);
	};
}

export class AttributeStat {
	public key: string     = $state('');
	public formula: string = $state('a*0.025+1*v');

	constructor(key?: string, formula?: string) {
		if (key) this.key = key;
		if (formula) this.formula = formula;
	}
}

export class AttributeStats {
	public attribute: FabledAttribute;
	public stats: AttributeStat[] = $state([]);

	constructor(attribute: FabledAttribute, stats?: AttributeStat[]) {
		this.attribute = attribute;
		if (stats) this.stats = stats;
	}

	public addStat = () => {
		this.stats.push(new AttributeStat(''));
	};

	public removeStat = (stat?: AttributeStat) => {
		if (!stat) return;
		this.stats = this.stats.filter(st => st != stat);
	};

	public load = (data?: { [key: string]: string }) => {
		if (data) {
			for (const key of Object.keys(data)) {
				this.stats.push(new AttributeStat(key, data[key]));
			}
		}
	};

	public serializeYaml = (): { [key: string]: string } => {
		const yaml: { [key: string]: string } = {};
		for (const stat of this.stats) {
			yaml[stat.key] = stat.formula;
		}
		return yaml;
	};
}