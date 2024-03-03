import type { ClassYamlData, Icon, ProClassData, Serializable } from './types';
import type ProSkill                                            from './proskill';
import { ProAttribute }                                         from './proattribute';
import { getSkill }                                             from '../data/skill-store';
import { parseBool, toEditorCase, toProperCase }                from './api';
import type { SkillTree }                                       from '$api/SkillTree';
import YAML                                                     from 'yaml';

export default class ProClass implements Serializable {
	dataType                     = 'class';
	location: 'local' | 'server' = 'local';
	loaded                       = false;

	isClass              = true;
	public key           = {};
	name: string;
	previousName: string = '';
	prefix               = '';
	group                = 'class';
	manaName             = '&2Mana';
	maxLevel             = 40;
	parentStr            = '';
	_parent?: ProClass;
	get parent() {
		return this._parent;
	}

	set parent(parent: ProClass | undefined) {
		this._parent   = parent;
		this.parentStr = parent ? parent.name : '';
	}

	permission                 = false;
	expSources                 = 273;
	manaRegen                  = 1;
	health: ProAttribute       = new ProAttribute('health', 20, 1);
	mana: ProAttribute         = new ProAttribute('mana', 20, 1);
	attributes: ProAttribute[] = [];
	skillTree: SkillTree       = 'Requirement';
	skills: ProSkill[]         = [];
	icon: Icon                 = {
		material:        'Pumpkin',
		customModelData: 0
	};
	unusableItems: string[]    = [];
	actionBar                  = '';

	lInverted  = true;
	rInverted  = true;
	lsInverted = true;
	rsInverted = true;
	sInverted  = true;
	pInverted  = true;
	qInverted  = true;
	fInverted  = true;

	lWhitelist: string[]  = [];
	rWhitelist: string[]  = [];
	lsWhitelist: string[] = [];
	rsWhitelist: string[] = [];
	sWhitelist: string[]  = [];
	pWhitelist: string[]  = [];
	qWhitelist: string[]  = [];
	fWhitelist: string[]  = [];

	constructor(data?: ProClassData) {
		this.name   = data ? data.name : 'Class';
		this.prefix = data?.prefix ? data.prefix : '&6' + this.name;
		if (!data) return;
		if (data?.location) this.location = data.location;
		if (data?.group) this.group = data.group;
		if (data?.manaName) this.manaName = data.manaName;
		if (data?.maxLevel) this.maxLevel = data.maxLevel;
		if (data?.parent) this.parent = data.parent;
		if (data?.permission) this.permission = data.permission;
		if (data?.expSources) this.expSources = data.expSources;
		if (data?.health) this.health = data.health;
		if (data?.mana) this.mana = data.mana;
		if (data?.manaRegen) this.manaRegen = data.manaRegen;
		if (data?.attributes) this.attributes = data.attributes;
		if (data?.skillTree) this.skillTree = data.skillTree;
		if (data?.skills) this.skills = data.skills;
		if (data?.icon) this.icon = data.icon;
		if (data?.unusableItems) this.unusableItems = data.unusableItems;
		if (data?.actionBar) this.actionBar = data.actionBar;

		// Combo starters
		if (data?.lInverted) this.lInverted = data.lInverted;
		if (data?.rInverted) this.rInverted = data.rInverted;
		if (data?.lsInverted) this.lsInverted = data.lsInverted;
		if (data?.rsInverted) this.rsInverted = data.rsInverted;
		if (data?.pInverted) this.pInverted = data.pInverted;
		if (data?.qInverted) this.qInverted = data.qInverted;
		if (data?.fInverted) this.fInverted = data.fInverted;
		if (data?.lWhitelist) this.lWhitelist = data.lWhitelist;
		if (data?.rWhitelist) this.rWhitelist = data.rWhitelist;
		if (data?.lsWhitelist) this.lsWhitelist = data.lsWhitelist;
		if (data?.rsWhitelist) this.rsWhitelist = data.rsWhitelist;
		if (data?.pWhitelist) this.pWhitelist = data.pWhitelist;
		if (data?.qWhitelist) this.qWhitelist = data.qWhitelist;
		if (data?.fWhitelist) this.fWhitelist = data.fWhitelist;
	}

	public updateAttributes = (attribs: string[]) => {
		const included: string[] = [];
		this.attributes          = this.attributes.filter(a => {
			if (attribs?.includes(a.name)) {
				included.push(a.name);
				return true;
			}
			return false;
		});

		attribs = attribs.filter(a => !included.includes(a));

		for (const attrib of attribs) {
			this.attributes.push(new ProAttribute(attrib, 0, 0));
		}
	};

	public serializeYaml = (): ClassYamlData => {
		const yaml = <ClassYamlData>{
			name:               this.name,
			'action-bar':       this.actionBar,
			prefix:             this.prefix,
			group:              this.group,
			mana:               this.manaName,
			'max-level':        this.maxLevel,
			parent:             this.parent?.name || '',
			'needs-permission': this.permission,
			attributes:         {
				'health-base':  this.health.base,
				'health-scale': this.health.scale,
				'mana-base':    this.mana.base,
				'mana-scale':   this.mana.scale
			},
			'mana-regen':       this.manaRegen,
			'skill-tree':       this.skillTree.toUpperCase().replace(/ /g, '_'),
			blacklist:          this.unusableItems,
			skills:             this.skills.map(s => s.name),
			icon:               this.icon.material,
			'icon-data':        this.icon.customModelData,
			'icon-lore':        this.icon.lore,
			'exp-source':       this.expSources,
			'combo-starters':   {
				L:  { inverted: this.lInverted, whitelist: this.lWhitelist },
				R:  { inverted: this.rInverted, whitelist: this.rWhitelist },
				LS: { inverted: this.lsInverted, whitelist: this.lsWhitelist },
				RS: { inverted: this.rsInverted, whitelist: this.rsWhitelist },
				S:  { inverted: this.sInverted, whitelist: this.sWhitelist },
				P:  { inverted: this.pInverted, whitelist: this.pWhitelist },
				Q:  { inverted: this.qInverted, whitelist: this.qWhitelist },
				F:  { inverted: this.fInverted, whitelist: this.fWhitelist }
			}
		};

		this.attributes.forEach(attr => {
			yaml.attributes[`${attr.name.toLowerCase()}-base`]  = attr.base;
			yaml.attributes[`${attr.name.toLowerCase()}-scale`] = attr.scale;
		});

		return yaml;
	};

	public updateParent = (classes: ProClass[]) => {
		if (!this.parentStr) return;
		this.parent = classes.find(c => c.name === this.parentStr);
	};

	public load = (yaml: ClassYamlData) => {
		this.name       = yaml.name;
		this.actionBar  = yaml['action-bar'];
		this.manaName   = yaml.mana;
		this.prefix     = yaml.prefix;
		this.group      = yaml.group;
		this.maxLevel   = yaml['max-level'];
		this.parentStr  = yaml.parent;
		this.permission = parseBool(yaml['needs-permission']);

		const attributes = yaml.attributes;
		this.health      = new ProAttribute('health', attributes['health-base'] || 20, attributes['health-scale'] || 1);
		this.mana        = new ProAttribute('mana', attributes['mana-base'] || 20, attributes['mana-scale'] || 1);

		const map: { [key: string]: ProAttribute } = {};
		for (const attrId of Object.keys(attributes)) {
			const split = attrId.split('-');
			const name  = split[0];
			if (map[name] || name === 'health' || name === 'mana') continue;

			const attr = new ProAttribute(name, 0, 0);
			attr.base  = attributes[`${name}-base`];
			attr.scale = attributes[`${name}-scale`];
			map[name]  = attr;
		}
		this.attributes = Object.values(map);

		this.manaRegen            = yaml['mana-regen'];
		this.skillTree            = <SkillTree>toProperCase(yaml['skill-tree']);
		this.unusableItems        = yaml.blacklist;
		this.skills               = <ProSkill[]>yaml.skills.map(s => getSkill(s)).filter(s => !!s);
		this.icon.material        = toEditorCase(yaml.icon);
		this.icon.customModelData = yaml['icon-data'];
		this.icon.lore            = yaml['icon-lore'];
		this.expSources           = yaml['exp-source'];

		// Combo starters
		const combos = yaml['combo-starters'];
		if (combos) {
			this.lInverted   = parseBool(combos.L?.inverted);
			this.rInverted   = parseBool(combos.R?.inverted);
			this.lsInverted  = parseBool(combos.LS?.inverted);
			this.rsInverted  = parseBool(combos.RS?.inverted);
			this.sInverted   = parseBool(combos.S?.inverted);
			this.pInverted   = parseBool(combos.P?.inverted);
			this.qInverted   = parseBool(combos.Q?.inverted);
			this.fInverted   = parseBool(combos.F?.inverted);
			this.lWhitelist  = combos.L?.whitelist || [];
			this.rWhitelist  = combos.R?.whitelist || [];
			this.lsWhitelist = combos.LS?.whitelist || [];
			this.rsWhitelist = combos.RS?.whitelist || [];
			this.sWhitelist  = combos.S?.whitelist || [];
			this.pWhitelist  = combos.P?.whitelist || [];
			this.qWhitelist  = combos.Q?.whitelist || [];
			this.fWhitelist  = combos.F?.whitelist || [];
		}

		this.loaded = true;
	};

	public save = () => {
		if (!this.name) return;

		if (this.location === 'server') {

			return;
		}

		const yaml = YAML.stringify({ [this.name]: this.serializeYaml() }, { lineWidth: 0 });

		if (this.previousName && this.previousName !== this.name) {
			localStorage.removeItem('sapi.class.' + this.previousName);
		}
		this.previousName = this.name;
		localStorage.setItem('sapi.class.' + this.name, yaml);
	};
}