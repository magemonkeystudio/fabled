import type { Icon, ProSkillData, Serializable } from './types';
import { YAMLObject }                            from './yaml';
import { ProAttribute }                          from './proattribute';
import { toEditorCase }                          from './api';
import { getSkill }                              from '../data/skill-store';
import ProTrigger                                from './components/triggers';
import type ProComponent         from '$api/components/procomponent';
import Registry, { initialized } from '$api/components/registry';
import type { Unsubscriber }     from 'svelte/types/runtime/store';

export default class ProSkill implements Serializable {
	isSkill                  = true;
	public key               = {};
	name: string;
	type                     = 'Dynamic';
	maxLevel                 = 5;
	skillReq?: ProSkill;
	skillReqLevel            = 0;
	permission: boolean      = false;
	levelReq: ProAttribute   = new ProAttribute('level', 1, 0);
	cost: ProAttribute       = new ProAttribute('cost', 1, 0);
	cooldown: ProAttribute   = new ProAttribute('cooldown', 0, 0);
	cooldownMessage: boolean = true;
	mana: ProAttribute       = new ProAttribute('mana', 0, 0);
	minSpent: ProAttribute   = new ProAttribute('points-spent-req', 0, 0);
	castMessage              = '&6{player} &2has cast &6{skill}';
	combo                    = '';
	icon: Icon               = {
		material:        'Pumpkin',
		customModelData: 0,
		lore:            [
			'&d{name} &7({level}/{max})',
			'&2Type: &6{type}',
			'',
			'{req:level}Level: {attr:level}',
			'{req:cost}Cost: {attr:cost}',
			'',
			'&2Mana: {attr:mana}',
			'&2Cooldown: {attr:cooldown}'
		]
	};
	incompatible: ProSkill[] = [];
	triggers: ProTrigger[]   = [];

	private skillReqStr         = '';
	private incompStr: string[] = [];

	constructor(data?: ProSkillData) {
		this.name = data ? data.name : 'Skill';
		if (data?.type) this.type = data.type;
		if (data?.maxLevel) this.maxLevel = data.maxLevel;
		if (data?.skillReq) this.skillReq = data.skillReq;
		if (data?.skillReqLevel) this.skillReqLevel = data.skillReqLevel;
		if (data?.permission) this.permission = data.permission;
		if (data?.levelReq) this.levelReq = data.levelReq;
		if (data?.cost) this.cost = data.cost;
		if (data?.cooldown) this.cooldown = data.cooldown;
		if (data?.cooldownMessage) this.cooldownMessage = data.cooldownMessage;
		if (data?.mana) this.mana = data.mana;
		if (data?.minSpent) this.minSpent = data.minSpent;
		if (data?.castMessage) this.castMessage = data.castMessage;
		if (data?.combo) this.combo = data.combo;
		if (data?.icon) this.icon = data.icon;
		if (data?.incompatible) this.incompatible = data.incompatible;
		if (data?.triggers) this.triggers = data.triggers;
	}

	public addComponent = (comp: ProComponent) => {
		if (comp instanceof ProTrigger) {
			this.triggers = [...this.triggers, comp];
			return;
		}

		if (this.triggers.length === 0) {
			this.triggers.push(<ProTrigger>Registry.getTriggerByName('cast')?.new());
		}

		this.triggers[0].addComponent(comp);
		this.triggers = [...this.triggers];
	};

	public removeComponent = (comp: ProComponent) => {
		if (comp instanceof ProTrigger && this.triggers.includes(comp)) {
			this.triggers.splice(this.triggers.indexOf(comp), 1);
			return;
		}

		for (const trigger of this.triggers) {
			if (trigger.contains(comp))
				trigger.removeComponent(comp);
		}

		this.triggers = [...this.triggers];
	};

	public serializeYaml = (): YAMLObject => {
		const yaml = new YAMLObject(this.name);
		const data = new YAMLObject();
		data.put('name', this.name);
		data.put('type', this.type);
		data.put('max-level', this.maxLevel);
		data.put('skill-req', this.skillReq?.name);
		data.put('skill-req-lvl', this.skillReqLevel);
		data.put('needs-permission', this.permission);
		data.put('cooldown-message', this.cooldownMessage);
		data.put('msg', this.castMessage);
		data.put('combo', this.combo);
		data.put('icon', this.icon.material);
		data.put('icon-data', this.icon.customModelData);
		data.put('icon-lore', this.icon.lore);
		const attributes = new YAMLObject('attributes');
		attributes.put('level', this.levelReq);
		attributes.put('cost', this.cost);
		attributes.put('cooldown', this.cooldown);
		attributes.put('mana', this.mana);
		attributes.put('points-spent-req', this.minSpent);
		attributes.put('incompatible', this.incompatible.map(s => s.name));
		data.put('attributes', attributes);
		data.put('components', this.triggers);

		yaml.data = data.data;
		return yaml;
	};

	public load = (yaml: YAMLObject) => {
		this.name            = yaml.get('name', this.name);
		this.type            = yaml.get('type', this.type);
		this.maxLevel        = yaml.get('max-level', this.maxLevel);
		this.skillReqStr     = yaml.get('skill-req', this.skillReqStr);
		this.skillReqLevel   = yaml.get('skill-req-lvl', this.skillReqLevel);
		this.permission      = yaml.get('needs-permission', this.permission);
		this.cooldownMessage = yaml.get('cooldown-message', this.cooldownMessage);
		this.castMessage     = yaml.get('msg', this.castMessage);
		this.combo           = yaml.get('combo', this.combo);

		const attributes: YAMLObject = yaml.get('attributes');
		this.levelReq                = new ProAttribute('level', attributes.get('level-base'), attributes.get('level-scale'));
		this.cost                    = new ProAttribute('cost', attributes.get('cost-base'), attributes.get('cost-scale'));
		this.cooldown                = new ProAttribute('cooldown', attributes.get('cooldown-base'), attributes.get('cooldown-scale'));
		this.mana                    = new ProAttribute('mana', attributes.get('mana-base'), attributes.get('mana-scale'));
		this.minSpent                = new ProAttribute('points-spent-req', attributes.get('points-spent-req-base'), attributes.get('points-spent-req-scale'));
		this.incompStr               = attributes.get('incompatible', this.incompStr);

		this.icon.material        = yaml.get<string, string>('icon', this.icon.material, toEditorCase);
		this.icon.customModelData = yaml.get('icon-data', this.icon.customModelData);
		this.icon.lore            = yaml.get('icon-lore', this.icon.lore);

		let unsub: Unsubscriber | undefined = undefined;

		unsub = initialized.subscribe(init => {
			if (!init) return;
			this.triggers = yaml.get<YAMLObject, ProTrigger[]>('components', this.triggers, (list: YAMLObject) => Registry.deserializeComponents(list));

			if (unsub) {
				unsub();
			}
		});
	};

	public postLoad = () => {
		this.skillReq     = getSkill(this.skillReqStr);
		this.incompatible = <ProSkill[]>this.incompStr.map(s => getSkill(s)).filter(s => !!s);
	};
}