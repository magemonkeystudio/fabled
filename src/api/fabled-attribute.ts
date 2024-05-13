import { saveAll } from "../data/attribute-store";
import { Attribute } from "./stat";
import type { AttributeYamlData, Icon, ProAttributeData, Serializable, SkillYamlData } from "./types";

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
	condition: { [key: string]: string } = {};
	mechanic: { [key: string]: string } = {};
	target: { [key: string]: string } = {};
	stats: { [key: string]: string } = {};

	constructor(data?: ProAttributeData) {
        this.name = data ? data.name : "Attribute";
		this.display = data?.display ? data.display : this.name;
        if (!data) return;
		if (data.max) this.max = data.max;
		if (data.cost) this.cost = data.cost;
		if (data.icon) this.icon = data.icon;
		if (data.condition) this.condition = data.condition;
		if (data.mechanic) this.mechanic = data.mechanic;
		if (data.target) this.target = data.target;
		if (data.stats) this.stats = data.stats;
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
	        	condition: this.condition,
	        	mechanic: this.mechanic,
	        	target: this.target
	        },
	        stats: this.stats
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
        if (yaml.global?.condition) this.condition = yaml.global.condition;
        if (yaml.global?.mechanic) this.mechanic = yaml.global.mechanic;
        if (yaml.global?.target) this.target = yaml.global.target;
        if (yaml.stats) this.stats = yaml.stats;

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