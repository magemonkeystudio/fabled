import type { Unsubscriber, Writable } from 'svelte/store';
import {
	get,
	writable
}                                      from 'svelte/store';
import { sort, toEditorCase }          from '$api/api';
import { parseYaml }                   from '$api/yaml';
import {
	browser
}                                      from '$app/environment';
import { active, saveError }           from './store';
import {
	goto
}                                      from '$app/navigation';
import {
	base
}                                      from '$app/paths';
import Registry, {
	initialized
}                                      from '$api/components/registry';
import type {
	Icon,
	MultiSkillYamlData,
	ProSkillData,
	Serializable,
	SkillYamlData,
	YamlComponentData
}                                      from '$api/types';
import {
	socketService
}                                      from '$api/socket/socket-connector';
import {
	notify
}                                      from '$api/notification-service';
import { Attribute }                   from '$api/stat';
import ProTrigger                      from '$api/components/triggers';
import type ProComponent               from '$api/components/procomponent';
import YAML                            from 'yaml';
import {
	FabledFolder,
	folderStore
}                                      from './folder-store';

export default class FabledSkill implements Serializable {
	dataType                     = 'skill';
	location: 'local' | 'server' = 'local';
	loaded                       = false;
	tooBig                       = false;
	acknowledged                 = false;

	isSkill                            = true;
	public key                         = {};
	name: string;
	previousName: string               = '';
	type                               = 'Dynamic';
	maxLevel                           = 5;
	skillReq?: FabledSkill;
	skillReqLevel                      = 0;
	attributeRequirements: Attribute[] = [];
	permission: boolean                = false;
	levelReq: Attribute                = new Attribute('level', 1, 0);
	cost: Attribute                    = new Attribute('cost', 1, 0);
	cooldown: Attribute                = new Attribute('cooldown', 0, 0);
	cooldownMessage: boolean           = true;
	mana: Attribute                    = new Attribute('mana', 0, 0);
	minSpent: Attribute                = new Attribute('points-spent-req', 0, 0);
	castMessage                        = '&6{player} &2has cast &6{skill}';
	combo                              = '';
	icon: Icon                         = {
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
	incompatible: FabledSkill[]        = [];
	triggers: ProTrigger[]             = [];

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
		if (data.attributeRequirements) this.attributeRequirements = data.attributeRequirements.map(a => new Attribute(a.name, a.base, a.scale));
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

	public load = async (yaml: SkillYamlData) => {
		if (yaml.name) this.name = yaml.name;
		if (yaml.type) this.type = yaml.type;
		if (yaml['max-level']) this.maxLevel = yaml['max-level'];
		if (yaml['skill-req']) this.skillReqStr = yaml['skill-req'];
		if (yaml['skill-req-lvl']) this.skillReqLevel = yaml['skill-req-lvl'];
		if (yaml['needs-permission']) this.permission = yaml['needs-permission'];
		if (yaml['cooldown-message']) this.cooldownMessage = yaml['cooldown-message'];
		if (yaml.msg) this.castMessage = yaml.msg;
		if (yaml.combo) this.combo = yaml.combo;

		if (yaml.attributes) {
			const attributes = yaml.attributes;
			this.levelReq    = new Attribute('level', attributes['level-base'], attributes['level-scale']);
			this.cost        = new Attribute('cost', attributes['cost-base'], attributes['cost-scale']);
			this.cooldown    = new Attribute('cooldown', attributes['cooldown-base'], attributes['cooldown-scale']);
			this.mana        = new Attribute('mana', attributes['mana-base'], attributes['mana-scale']);
			this.minSpent    = new Attribute('points-spent-req', attributes['points-spent-req-base'], attributes['points-spent-req-scale']);
			this.incompStr   = attributes.incompatible;

			const reserved             = ['level', 'cost', 'cooldown', 'mana', 'points-spent-req', 'incompatible'];
			const names                = new Set(Object.keys(attributes).map(k => k.replace(/-(base|scale)/i, '')).filter(name => !reserved.includes(name)));
			this.attributeRequirements = [...names].map(name => new Attribute(name, attributes[`${name}-base`], attributes[`${name}-scale`]));
		}

		if (yaml.icon) this.icon.material = toEditorCase(yaml.icon);
		if (yaml['icon-data']) this.icon.customModelData = yaml['icon-data'];
		if (yaml['icon-lore']) this.icon.lore = yaml['icon-lore'];

		let unsub: Unsubscriber | undefined = undefined;

		return new Promise<void>((resolve) => {
			unsub = initialized.subscribe(init => {
				if (!init) return;
				if (yaml.components) this.triggers = <ProTrigger[]>Registry.deserializeComponents(yaml.components);

				if (unsub) {
					unsub();
				}

				this.loaded = true;
				resolve();
			});
		});
	};

	public postLoad = () => {
		this.skillReq     = skillStore.getSkill(this.skillReqStr);
		this.incompatible = <FabledSkill[]>this.incompStr.map(s => skillStore.getSkill(s)).filter(s => !!s);
	};

	public save = () => {
		if (!this.name || this.tooBig) return;

		if (this.tooBig && !this.acknowledged) {
			saveError.set(this);
			return;
		}

		skillStore.isSaving.set(true);

		if (this.location === 'server') {

			return;
		}

		if (this.previousName && this.previousName !== this.name) {
			localStorage.removeItem('sapi.skill.' + this.previousName);
		}
		this.previousName = this.name;

		try {
			const yaml = YAML.stringify({ [this.name]: this.serializeYaml() });
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

		skillStore.isSaving.set(false);
	};
}

class SkillStore {
	isLegacy                     = false;
	private loadSkillsFromServer = async () => {
		let serverSkills: string[];
		try {
			serverSkills = await socketService.getSkills();
		} catch (_) {
			return;
		}

		const tempFolders = get(this.skillFolders);
		const tempSkills  = get(this.skills);
		// Skills come through with some sort of path before their name A/B/C/Skill
		// We need to create folders for each of these
		serverSkills.forEach(sk => {
			const parts = sk.split('/');
			const name  = parts.pop();
			if (!name) return;

			let previous: FabledFolder | undefined;
			let folder: FabledFolder | undefined;
			parts.forEach(part => {
				folder = previous ? previous.getSubfolder(part) : tempFolders.find(f => f.name === part);
				if (!folder) {
					folder          = new FabledFolder();
					folder.name     = part;
					folder.location = 'server';
					if (previous) {
						previous.add(folder);
						folder.updateParent(previous);
					}
				}
				if (!previous && !tempFolders.includes(folder)) tempFolders.push(folder);
				previous = folder;
			});

			// If we already have this skill, don't add it
			if (tempSkills.find(sk => sk.name === name)) return;

			const skill = new FabledSkill({ name, location: 'server' });
			if (folder) folder.add(skill);
			tempSkills.push(skill);
		});

		this.skills.set(tempSkills);
		this.skillFolders.set(tempFolders);
	};

	private removeServerSkills = () => {
		const tempSkills = get(this.skills);
		this.skills.set(tempSkills.filter(c => c.location !== 'server'));

		const tempFolders = get(this.skillFolders);
		tempFolders.filter(f => f.location === 'server').forEach(f => this.deleteSkillFolder(f, (sb) => sb.location === 'server'));
	};

	constructor() {
		socketService.onConnect(this.loadSkillsFromServer);
		socketService.onDisconnect(this.removeServerSkills);

		get(this.skills).forEach(sk => {
			if (sk.loaded) {
				sk.postLoad();
			}
		});

		if (this.isLegacy) {
			const sub = initialized.subscribe(init => {
				if (!init) return;
				get(this.skills).forEach(sk => {
					if (sk.location === 'local') sk.save();
				});
				this.persistSkills();
				if (sub) sub();
			});
		}
	}

	private loadSkillTextToArray = (text: string): FabledSkill[] => {
		const list: FabledSkill[] = [];
		// Load skills
		const data                = <MultiSkillYamlData>parseYaml(text);
		if (!data || Object.keys(data).length === 0) {
			// If there is no data or the object is empty... return
			return list;
		}

		const keys = Object.keys(data);

		let skill: FabledSkill;
		// If we only have one skill, and it is the current YAML,
		// the structure is a bit different
		if (keys.length == 1) {
			const key = keys[0];
			if (key === 'loaded') return list;
			skill = new FabledSkill({ name: key });
			skill.load(data[key]).then(() => {
			});
			list.push(skill);
			return list;
		}

		for (const key of Object.keys(data)) {
			if (key != 'loaded') {
				skill = new FabledSkill({ name: key });
				skill.load(data[key]).then(() => {
				});
				list.push(skill);
			}
		}
		return list;
	};

	private setupSkillStore = <T extends FabledSkill[] | FabledFolder[]>(key: string,
																																			 def: T,
																																			 mapper: (data: string) => T,
																																			 setAction: (data: T) => T,
																																			 postLoad?: (saved: T) => void): Writable<T> => {
		let saved: T = def;
		if (browser) {
			const stored = localStorage.getItem(key);
			if (stored) {
				saved = mapper(stored);
				if (postLoad) postLoad(saved);
			}
		}

		const {
						subscribe,
						set,
						update
					} = writable<T>(saved);
		return {
			subscribe,
			set: (value: T) => {
				if (setAction) value = setAction(value);
				return set(value);
			},
			update
		};
	};

	skills: Writable<FabledSkill[]> = this.setupSkillStore<FabledSkill[]>(
		browser && localStorage.getItem('skillNames') ? 'skillNames' : 'skillData',
		[],
		(data: string) => {
			if (localStorage.getItem('skillNames')) {
				return data.split(', ').map(name => new FabledSkill({
					name,
					location: 'local'
				})).filter(sk => localStorage.getItem('sapi.skill.' + sk.name));
			} else {
				localStorage.removeItem('skillData');
				this.isLegacy = true;
				return sort<FabledSkill>(this.loadSkillTextToArray(data));
			}
		},
		(value: FabledSkill[]) => {
			this.persistSkills();
			return sort<FabledSkill>(value);
		});

	getSkill = (name: string): FabledSkill | undefined => {
		for (const c of get(this.skills)) {
			if (c.name == name) return c;
		}

		return undefined;
	};

	skillFolders: Writable<FabledFolder[]> = this.setupSkillStore<FabledFolder[]>('skillFolders', [],
		(data: string) => {
			if (!data || data === 'null') return [];

			try {
				return JSON.parse(data, (key: string, value) => {
					if (!value) return;
					if (/\d+/.test(key)) {
						if (typeof (value) === 'string') {
							return this.getSkill(value);
						}

						const folder = new FabledFolder(value.data);
						folder.name  = value.name;
						return folder;
					}
					return value;
				});
			} catch (e) {
				console.error('Error loading skill folders. Folder data: ' + data, e);
				notify('Error loading skill folders. ' + JSON.stringify(e) + '\nFolder data: ' + data);
				return [];
			}
		},
		(value: FabledFolder[]) => {
			const data = JSON.stringify(value, (key, value: FabledFolder | FabledSkill) => {
				if (value instanceof FabledSkill) return value.name;
				else if (key === 'parent') return undefined;
				return value;
			});
			localStorage.setItem('skillFolders', data);
			return sort<FabledFolder>(value);
		});

	isSkillNameTaken = (name: string): boolean => !!this.getSkill(name);

	addSkill = (name?: string): FabledSkill => {
		const allSkills = get(this.skills);
		let index       = allSkills.length + 1;
		while (!name && this.isSkillNameTaken(name || 'Skill ' + index)) {
			index++;
		}
		const skill = new FabledSkill({ name: (name || 'Skill ' + index) });
		allSkills.push(skill);

		this.skills.set(allSkills);
		skill.save();
		return skill;
	};

	loadSkill = async (data: FabledSkill) => {
		if (data.loaded) return;
		let yamlData: MultiSkillYamlData;

		if (data.location === 'local') {
			yamlData = <MultiSkillYamlData>parseYaml(localStorage.getItem(`sapi.skill.${data.name}`) || '');
		} else {
			const yaml = await socketService.getSkillYaml(data.name);
			if (!yaml) return;

			yamlData = <MultiSkillYamlData>parseYaml(yaml);
		}

		// Get the first entry in the object
		const skill = Object.values(yamlData)[0];
		await data.load(skill);

		data.postLoad();
	};

	cloneSkill = async (data: FabledSkill): Promise<FabledSkill> => {
		if (!data.loaded) await this.loadSkill(data);

		const sk: FabledSkill[] = get(this.skills);
		let name                = data.name + ' (Copy)';
		let i                   = 1;
		while (this.isSkillNameTaken(name)) {
			name = data.name + ' (Copy ' + i + ')';
			i++;
		}
		const skill    = new FabledSkill();
		const yamlData = data.serializeYaml();
		await skill.load(yamlData);
		skill.name = name;
		sk.push(skill);

		this.skills.set(sk);
		skill.save();
		return skill;
	};

	addSkillFolder = (folder: FabledFolder) => {
		const folders = get(this.skillFolders);
		if (folders.includes(folder)) return;

		folderStore.rename(folder, folders);

		folders.push(folder);
		folders.sort((a, b) => a.name.localeCompare(b.name));
		this.skillFolders.set(folders);
	};


	deleteSkillFolder = (folder: FabledFolder, deleteCheck?: (subfolder: FabledFolder) => boolean) => {
		const folders = get(this.skillFolders).filter(f => f != folder);

		// If there are any subfolders or skills, move them to the parent or root
		folder.data.forEach(d => {
			if (d instanceof FabledFolder) {
				if (deleteCheck && deleteCheck(d)) {
					this.deleteSkillFolder(d, deleteCheck);
					return;
				}
				if (folder.parent) folder.parent.add(d);
				else {
					d.updateParent();
					folders.push(d);
				}
			} else if (folder.parent)
				folder.parent.add(d); // Add the skill to the parent folder
		});

		this.skillFolders.set(folders);
	};

	deleteSkill = (data: FabledSkill) => {
		const filtered = get(this.skills).filter(c => c != data);
		const act      = get(active);
		this.skills.set(filtered);
		localStorage.removeItem('sapi.skill.' + data.name);

		if (!(act instanceof FabledSkill)) return;

		if (filtered.length === 0) goto(`${base}/`).then(() => {
		});
		else if (!filtered.find(sk => sk === get(active))) goto(`${base}/skill/${filtered[0].name}`).then(() => {
		});
	};

	refreshSkills       = () => this.skills.set(sort<FabledSkill>(get(this.skills)));
	refreshSkillFolders = () => {
		this.skillFolders.set(sort<FabledFolder>(get(this.skillFolders)));
		this.refreshSkills();
	};


	/**
	 *  Loads skill data from a string
	 */
	loadSkillText = async (text: string, fromServer: boolean = false) => {
		// Load new skills
		const data = <MultiSkillYamlData>parseYaml(text);

		if (!data || Object.keys(data).length === 0) {
			// If there is no data or the object is empty... return
			return;
		}

		const keys = Object.keys(data);

		let skill: FabledSkill;
		// If we only have one skill, and it is the current YAML,
		// the structure is a bit different
		if (keys.length == 1) {
			const key: string = keys[0];
			skill             = (<FabledSkill>(this.isSkillNameTaken(key)
				? this.getSkill(key)
				: this.addSkill(key)));
			if (fromServer) skill.location = 'server';
			await skill.load(data[key]);
			skill.save();
			this.refreshSkills();
			return;
		}

		for (const key of Object.keys(data)) {
			if (key != 'loaded' && !this.isSkillNameTaken(key)) {
				skill = (<FabledSkill>(this.isSkillNameTaken(key)
					? this.getSkill(key)
					: this.addSkill(key)));
				await skill.load(data[key]);
				skill.save();
			}
		}
		this.refreshSkills();
	};

	loadSkills = async (e: ProgressEvent<FileReader>) => {
		const text: string = <string>e.target?.result;
		if (!text) return;

		await this.loadSkillText(text);
	};

	isSaving: Writable<boolean> = writable(false);
	saveTask: number            = 0;

	persistSkills = (list?: FabledSkill[]) => {
		if (get(this.isSaving) && this.saveTask) {
			clearTimeout(this.saveTask);
		}

		this.isSaving.set(true);

		this.saveTask = window.setTimeout(() => {
			const skillList = (list || get(this.skills)).filter(sk => sk.location === 'local');
			localStorage.setItem('skillNames', skillList.map(sk => sk.name).join(', '));
			this.isSaving.set(false);
		});
	};
}

export const skillStore = new SkillStore();