import type { Icon, ProSkillData, Serializable, SkillYamlData, YamlComponentData } from './types';
import { ProAttribute }                                                            from './proattribute';
import { getSkill, isSaving }                                                      from '../data/skill-store';
import ProTrigger                                                                  from './components/triggers';
import type ProComponent                                                           from '$api/components/procomponent';
import Registry, { initialized }                                                   from '$api/components/registry';
import type { Unsubscriber }                                                       from 'svelte/store';
import { saveError }                                                               from '../data/store';
import YAML                                                                        from 'yaml';
import { toEditorCase }                                                            from '$api/api';

export default class ProSkill implements Serializable {
	dataType                     = 'skill';
	location: 'local' | 'server' = 'local';
	loaded                       = false;
	tooBig                       = false;
	acknowledged                 = false;

	isSkill                               = true;
	public key                            = {};
	name: string;
	previousName: string                  = '';
	type                                  = 'Dynamic';
	maxLevel                              = 5;
	skillReq?: ProSkill;
	skillReqLevel                         = 0;
	attributeRequirements: ProAttribute[] = [];
	permission: boolean                   = false;
	levelReq: ProAttribute                = new ProAttribute('level', 1, 0);
	cost: ProAttribute                    = new ProAttribute('cost', 1, 0);
	cooldown: ProAttribute                = new ProAttribute('cooldown', 0, 0);
	cooldownMessage: boolean              = true;
	mana: ProAttribute                    = new ProAttribute('mana', 0, 0);
	minSpent: ProAttribute                = new ProAttribute('points-spent-req', 0, 0);
	castMessage                           = '&6{player} &2has cast &6{skill}';
	combo                                 = '';
	icon: Icon                            = {
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
	incompatible: ProSkill[]              = [];
	triggers: ProTrigger[]                = [];

	private skillReqStr         = '';
	private incompStr: string[] = [];

	constructor(data?: ProSkillData) {
		this.name = data?.name || 'Skill';
		if (!data) return;
		if (data.location) this.location = data.location;
		if (data.type) this.type = data.type;
		if (data.maxLevel) this.maxLevel = data.maxLevel;
		if (data.skillReq) this.skillReq = data.skillReq;
		if (data.skillReqLevel) this.skillReqLevel = data.skillReqLevel;
		if (data.attributeRequirements) this.attributeRequirements = data.attributeRequirements.map(a => new ProAttribute(a.name, a.base, a.scale));
		if (data.permission) this.permission = data.permission;
		if (data.levelReq) this.levelReq = data.levelReq;
		if (data.cost) this.cost = data.cost;
		if (data.cooldown) this.cooldown = data.cooldown;
		if (data.cooldownMessage) this.cooldownMessage = data.cooldownMessage;
		if (data.mana) this.mana = data.mana;
		if (data.minSpent) this.minSpent = data.minSpent;
		if (data.castMessage) this.castMessage = data.castMessage;
		if (data.combo) this.combo = data.combo;
		if (data.icon) this.icon = data.icon;
		if (data.incompatible) this.incompatible = data.incompatible;
		if (data.triggers) this.triggers = data.triggers;
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

	private nextChar = (c: string) => {
		if (/z$/.test(c)) {
			return c.replaceAll(/z$/g, 'a') + 'a';
		}
		return c.substring(0, c.length - 1) + String.fromCharCode(c.charCodeAt(c.length - 1) + 1);
	};

	public serializeYaml = (): SkillYamlData => {
		const compData = <YamlComponentData>{};

		for (const comp of this.triggers) {
			const yamlData = comp.toYamlObj();
			let name       = comp.name;
			let suffix     = 'a';
			while (compData[name]) {
				suffix = this.nextChar(suffix);
				name   = comp.name + '-' + suffix;
			}
			compData[name] = yamlData;
		}
		const data = <SkillYamlData>{
			name:               this.name,
			type:               this.type,
			'max-level':        this.maxLevel,
			'skill-req':        this.skillReq?.name,
			'skill-req-lvl':    this.skillReqLevel,
			'needs-permission': this.permission,
			'cooldown-message': this.cooldownMessage,
			msg:                this.castMessage,
			combo:              this.combo,
			icon:               this.icon.material,
			'icon-data':        this.icon.customModelData,
			'icon-lore':        this.icon.lore,
			attributes:         {
				'level-base':             this.levelReq.base,
				'level-scale':            this.levelReq.scale,
				'cost-base':              this.cost.base,
				'cost-scale':             this.cost.scale,
				'cooldown-base':          this.cooldown.base,
				'cooldown-scale':         this.cooldown.scale,
				'mana-base':              this.mana.base,
				'mana-scale':             this.mana.scale,
				'points-spent-req-base':  this.minSpent.base,
				'points-spent-req-scale': this.minSpent.scale,
				incompatible:             this.incompatible.map(s => s.name)
			},
			components:         compData
		};

		this.attributeRequirements.forEach(attr => {
			data.attributes[`${attr.name.toLowerCase()}-base`]  = attr.base;
			data.attributes[`${attr.name.toLowerCase()}-scale`] = attr.scale;
		});

		return data;
	};

	public load = (yaml: SkillYamlData) => {
		this.name            = yaml.name;
		this.type            = yaml.type;
		this.maxLevel        = yaml['max-level'];
		this.skillReqStr     = yaml['skill-req'];
		this.skillReqLevel   = yaml['skill-req-lvl'];
		this.permission      = yaml['needs-permission'];
		this.cooldownMessage = yaml['cooldown-message'];
		this.castMessage     = yaml.msg;
		this.combo           = yaml.combo;

		const attributes = yaml.attributes;
		this.levelReq    = new ProAttribute('level', attributes['level-base'], attributes['level-scale']);
		this.cost        = new ProAttribute('cost', attributes['cost-base'], attributes['cost-scale']);
		this.cooldown    = new ProAttribute('cooldown', attributes['cooldown-base'], attributes['cooldown-scale']);
		this.mana        = new ProAttribute('mana', attributes['mana-base'], attributes['mana-scale']);
		this.minSpent    = new ProAttribute('points-spent-req', attributes['points-spent-req-base'], attributes['points-spent-req-scale']);
		this.incompStr   = attributes.incompatible;

		const reserved             = ['level', 'cost', 'cooldown', 'mana', 'points-spent-req', 'incompatible'];
		const names                = new Set(Object.keys(attributes).map(k => k.replace(/-(base|scale)/i, '')).filter(name => !reserved.includes(name)));
		this.attributeRequirements = [...names].map(name => new ProAttribute(name, attributes[`${name}-base`], attributes[`${name}-scale`]));

		this.icon.material        = toEditorCase(yaml.icon);
		this.icon.customModelData = yaml['icon-data'];
		this.icon.lore            = yaml['icon-lore'];

		let unsub: Unsubscriber | undefined = undefined;

		unsub = initialized.subscribe(init => {
			if (!init) return;
			this.triggers = <ProTrigger[]>Registry.deserializeComponents(yaml.components);

			if (unsub) {
				unsub();
			}
		});

		this.loaded = true;
	};

	public postLoad = () => {
		this.skillReq     = getSkill(this.skillReqStr);
		this.incompatible = <ProSkill[]>this.incompStr.map(s => getSkill(s)).filter(s => !!s);
	};

	public save = () => {
		if (!this.name || this.tooBig) return;

		if (this.tooBig && !this.acknowledged) {
			saveError.set(this);
			return;
		}

		isSaving.set(true);

		const yaml = YAML.stringify({ [this.name]: this.serializeYaml() }, { lineWidth: 0 });
		if (this.location === 'server') {

			return;
		}

		if (this.previousName && this.previousName !== this.name) {
			localStorage.removeItem('sapi.skill.' + this.previousName);
		}
		this.previousName = this.name;

		try {
			localStorage.setItem('sapi.skill.' + this.name, yaml);
			this.tooBig = false;
		} catch (e: any) {
			// If the data is too big
			if (!e?.message?.includes('quota')) {
				console.error(this.name + ' Save error', e);
			} else {
				localStorage.removeItem('sapi.skill.' + this.name);
				this.tooBig = true;
				saveError.set(this);
			}
		}

		isSaving.set(false);
	};
}