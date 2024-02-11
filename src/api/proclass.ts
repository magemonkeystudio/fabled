import type { Icon, ProClassData, Serializable } from './types';
import type ProSkill                             from './proskill';
import { YAMLObject }                            from './yaml';
import { ProAttribute }                          from './proattribute';
import { getSkill }                              from '../data/skill-store';
import { toEditorCase, toProperCase }            from './api';
import type { SkillTree }                        from '$api/SkillTree';

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
	pInverted  = true;
	qInverted  = true;
	fInverted  = true;

	lWhitelist: string[]  = [];
	rWhitelist: string[]  = [];
	lsWhitelist: string[] = [];
	rsWhitelist: string[] = [];
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

	public serializeYaml = (): YAMLObject => {
		const yaml = new YAMLObject(this.name);
		const data = new YAMLObject();
		data.put('name', this.name);
		data.put('action-bar', this.actionBar);
		data.put('prefix', this.prefix);
		data.put('group', this.group);
		data.put('mana', this.manaName);
		data.put('max-level', this.maxLevel);
		data.put('parent', this.parent?.name || '');
		data.put('needs-permission', this.permission);
		data.put('attributes', [this.health, this.mana, ...this.attributes]);
		data.put('mana-regen', this.manaRegen);
		data.put('skill-tree', this.skillTree.toUpperCase().replace(/ /g, '_'));
		data.put('blacklist', this.unusableItems);
		data.put('skills', this.skills.map(s => s.name));
		data.put('icon', this.icon.material);
		data.put('icon-data', this.icon.customModelData);
		data.put('icon-lore', this.icon.lore);
		data.put('exp-source', this.expSources);

		// Combo starters
		const combos = new YAMLObject('combo-starters');
		const l      = new YAMLObject('L');
		l.put('inverted', this.lInverted);
		l.put('whitelist', this.lWhitelist);
		combos.put('L', l);
		const r = new YAMLObject('R');
		r.put('inverted', this.rInverted);
		r.put('whitelist', this.rWhitelist);
		combos.put('R', r);
		const ls = new YAMLObject('LS');
		ls.put('inverted', this.lsInverted);
		ls.put('whitelist', this.lsWhitelist);
		combos.put('LS', ls);
		const rs = new YAMLObject('RS');
		rs.put('inverted', this.rsInverted);
		rs.put('whitelist', this.rsWhitelist);
		combos.put('RS', rs);
		const p = new YAMLObject('P');
		p.put('inverted', this.pInverted);
		p.put('whitelist', this.pWhitelist);
		combos.put('P', p);
		const q = new YAMLObject('Q');
		q.put('inverted', this.qInverted);
		q.put('whitelist', this.qWhitelist);
		combos.put('Q', q);
		const f = new YAMLObject('F');
		f.put('inverted', this.fInverted);
		f.put('whitelist', this.fWhitelist);
		combos.put('F', f);
		data.put('combo-starters', combos);

		yaml.data = data.data;
		return yaml;
	};

	public updateParent = (classes: ProClass[]) => {
		if (!this.parentStr) return;
		this.parent = classes.find(c => c.name === this.parentStr);
	};

	public load = (yaml: YAMLObject) => {
		this.name                 = yaml.get('name', this.name);
		this.actionBar            = yaml.get('action-bar', this.actionBar);
		this.manaName             = yaml.get('mana', this.manaName);
		this.prefix               = yaml.get('prefix', this.prefix);
		this.group                = yaml.get('group', this.group);
		this.maxLevel             = yaml.get('max-level', this.maxLevel);
		this.parentStr            = yaml.get('parent', this.parentStr);
		this.permission           = yaml.get('needs-permission', this.permission);
		this.attributes           = yaml.get<YAMLObject, ProAttribute[]>('attributes', this.attributes,
			(obj: YAMLObject) => {
				const attrs: ProAttribute[] = [];

				const healthBase: number = obj.get<number, number>('health-base', 20);
				const healthMod: number  = obj.get<number, number>('health-scale', 1);
				const manaBase: number   = obj.get<number, number>('mana-base', 20);
				const manaMod: number    = obj.get<number, number>('mana-scale', 1);
				this.health              = new ProAttribute('health', healthBase, healthMod);
				this.mana                = new ProAttribute('mana', manaBase, manaMod);
				obj.remove('health-base');
				obj.remove('health-scale');
				obj.remove('mana-base');
				obj.remove('mana-scale');

				const map: { [key: string]: ProAttribute } = {};
				Object.keys(obj.data).forEach(key => {
					const split = key.split('-');
					const name  = split[0];
					if (!map[name]) map[name] = new ProAttribute(name, 0, 0);

					const attr = map[name];
					if (split[1] == 'base') attr.base = Number.parseFloat(obj.data[key]);
					if (split[1] == 'scale') attr.scale = Number.parseFloat(obj.data[key]);
				});

				attrs.push(...Object.values(map));

				return attrs;
			});
		this.manaRegen            = yaml.get('mana-regen', this.manaRegen);
		this.skillTree            = yaml.get('skill-tree', this.skillTree, toProperCase);
		this.unusableItems        = yaml.get('blacklist', this.unusableItems);
		this.skills               = yaml.get<string[], ProSkill[]>('skills', this.skills,
			(list: string[]) => list.map(s => getSkill(s)).filter(s => !!s));
		this.icon.material        = yaml.get<string, string>('icon', this.icon.material, toEditorCase);
		this.icon.customModelData = yaml.get('icon-data', this.icon.customModelData);
		this.icon.lore            = yaml.get('icon-lore', this.icon.lore);
		this.expSources           = yaml.get('exp-source', this.expSources);

		// Combo starters
		const combos     = yaml.get('combo-starters', new YAMLObject());
		this.lInverted   = combos.get('L', new YAMLObject()).get('inverted', this.lInverted);
		this.rInverted   = combos.get('R', new YAMLObject()).get('inverted', this.rInverted);
		this.lsInverted  = combos.get('LS', new YAMLObject()).get('inverted', this.lsInverted);
		this.rsInverted  = combos.get('RS', new YAMLObject()).get('inverted', this.rsInverted);
		this.pInverted   = combos.get('P', new YAMLObject()).get('inverted', this.pInverted);
		this.qInverted   = combos.get('Q', new YAMLObject()).get('inverted', this.qInverted);
		this.fInverted   = combos.get('F', new YAMLObject()).get('inverted', this.fInverted);
		this.lWhitelist  = combos.get('L', new YAMLObject()).get('whitelist', this.lWhitelist);
		this.rWhitelist  = combos.get('R', new YAMLObject()).get('whitelist', this.rWhitelist);
		this.lsWhitelist = combos.get('LS', new YAMLObject()).get('whitelist', this.lsWhitelist);
		this.rsWhitelist = combos.get('RS', new YAMLObject()).get('whitelist', this.rsWhitelist);
		this.pWhitelist  = combos.get('P', new YAMLObject()).get('whitelist', this.pWhitelist);
		this.qWhitelist  = combos.get('Q', new YAMLObject()).get('whitelist', this.qWhitelist);
		this.fWhitelist  = combos.get('F', new YAMLObject()).get('whitelist', this.fWhitelist);

		this.loaded = true;
	};

	public save = () => {
		if (!this.name) return;

		if (this.location === 'server') {

			return;
		}

		const yaml = this.serializeYaml();

		if (this.previousName && this.previousName !== this.name) {
			localStorage.removeItem('sapi.class.' + this.previousName);
		}
		this.previousName = this.name;
		localStorage.setItem('sapi.class.' + this.name, yaml.toString());

	};
}