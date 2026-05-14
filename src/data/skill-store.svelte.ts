import type { Unsubscriber, Writable } from 'svelte/store';
import { get, writable }               from 'svelte/store';
import { sort, toEditorCase }          from '$api/api';
import { parseYaml }                   from '$api/yaml';
import { browser }                     from '$app/environment';
import { active, loadProgress, saveError } from './store';
import { goto }                        from '$app/navigation';
import { base }                        from '$app/paths';
import Registry, { initialized }       from '$api/components/registry';
import type {
	FabledSkillData,
	IAttribute,
	Icon,
	MultiSkillYamlData,
	Serializable,
	SkillYamlData,
	YamlComponentData
}                                      from '$api/types';
import { socketService }               from '$api/socket/socket-connector';
import { notify }                      from '$api/notification-service';
import FabledTrigger                   from '$api/components/triggers.svelte';
import type FabledComponent            from '$api/components/fabled-component.svelte';
import { FabledFolder, folderStore }   from './folder-store.svelte';
import YAML                            from 'yaml';
import { idbDelete, idbGet, idbGetAllKeys, idbSet, migrateFromLocalStorage } from '$api/idb';
import { workerParseChunked }          from '$api/worker-yaml';

export default class FabledSkill implements Serializable {
	dataType                     = 'skill';
	location: 'local' | 'server' = 'local';
	loaded                       = false;
	tooBig                       = false;
	acknowledged                 = false;

	isSkill                             = true;
	public key                          = {};
	name: string                        = $state('');
	previousName: string                = '';
	type                                = $state('Dynamic');
	maxLevel                            = $state(5);
	skillReq?: FabledSkill              = $state();
	skillReqLevel                       = $state(0);
	attributeRequirements: IAttribute[] = $state([]);
	permission: boolean                 = $state(false);
	levelReq: IAttribute                = $state({ name: 'level', base: 1, scale: 0 });
	cost: IAttribute                    = $state({ name: 'cost', base: 1, scale: 0 });
	cooldown: IAttribute                = $state({ name: 'cooldown', base: 1, scale: 0 });
	cooldownMessage: boolean            = $state(true);
	mana: IAttribute                    = $state({ name: 'mana', base: 0, scale: 0 });
	minSpent: IAttribute                = $state({ name: 'points-spent-req', base: 0, scale: 0 });
	castMessage                         = $state('&6{player} &2has cast &6{skill}');
	combo                               = $state('');
	icon: Icon                          = $state({
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
	});
	incompatible: FabledSkill[]         = $state([]);
	triggers: FabledTrigger[]           = $state([]);

	private skillReqStr         = '';
	private incompStr: string[] = [];

	constructor(data?: FabledSkillData) {
		this.name = data?.name || 'Skill';
		if (!data) return;
		if (data.location) this.location = data.location;
		if (data.type) this.type = data.type;
		if (data.maxLevel) this.maxLevel = data.maxLevel;
		if (data.skillReq) this.skillReq = data.skillReq;
		if (data.skillReqLevel) this.skillReqLevel = data.skillReqLevel;
		if (data.attributeRequirements) this.attributeRequirements = data.attributeRequirements.map(a => ({
			name:  a.name,
			base:  a.base,
			scale: a.scale
		}));
		if (data.permission !== undefined) this.permission = data.permission;
		if (data.levelReq) this.levelReq = data.levelReq;
		if (data.cost) this.cost = data.cost;
		if (data.cooldown) this.cooldown = data.cooldown;
		if (data.cooldownMessage !== undefined) this.cooldownMessage = data.cooldownMessage;
		if (data.mana) this.mana = data.mana;
		if (data.minSpent) this.minSpent = data.minSpent;
		if (data.castMessage) this.castMessage = data.castMessage;
		if (data.combo) this.combo = data.combo;
		if (data.icon) this.icon = data.icon;
		if (data.incompatible) this.incompatible = data.incompatible;
		if (data.triggers) this.triggers = data.triggers;
	}

	/**
	 * Reads all the reactive state elements to act as a chane detector
 	 */
	public changed = () => {
		return {
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
				'points-spent-req-scale': this.minSpent.scale
			},
			incompatible:       this.incompatible,
			components:         this.triggers
		};
	};

	public addComponent = (comp: FabledComponent) => {
		if (comp instanceof FabledTrigger) {
			this.triggers = [...this.triggers, comp];
			return;
		}

		if (this.triggers.length === 0) {
			this.triggers.push(<FabledTrigger>Registry.getTriggerByName('cast')?.new());
		}

		this.triggers[0].addComponent(comp);
		this.triggers = [...this.triggers];
	};

	public removeComponent = (comp: FabledComponent) => {
		if (comp instanceof FabledTrigger && this.triggers.includes(comp)) {
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
				'points-spent-req-scale': this.minSpent.scale
			},
			incompatible:       this.incompatible.map(s => s.name),
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
		if (yaml['needs-permission'] !== undefined) this.permission = yaml['needs-permission'];
		if (yaml['cooldown-message'] !== undefined) this.cooldownMessage = yaml['cooldown-message'];
		if (yaml.msg) this.castMessage = yaml.msg;
		if (yaml.combo) this.combo = yaml.combo;

		if (yaml.attributes) {
			const attributes = yaml.attributes;
			this.levelReq    = { name: 'level', base: attributes['level-base'], scale: attributes['level-scale'] };
			this.cost        = { name: 'cost', base: attributes['cost-base'], scale: attributes['cost-scale'] };
			this.cooldown    = { name: 'cooldown', base: attributes['cooldown-base'], scale: attributes['cooldown-scale'] };
			this.mana        = { name: 'mana', base: attributes['mana-base'], scale: attributes['mana-scale'] };
			this.minSpent    = {
				name:  'points-spent-req',
				base:  attributes['points-spent-req-base'],
				scale: attributes['points-spent-req-scale']
			};

			const reserved             = ['level', 'cost', 'cooldown', 'mana', 'points-spent-req', 'incompatible'];
			const names                = new Set(Object.keys(attributes).map(k => k.replace(/-(base|scale)/i, '')).filter(name => !reserved.includes(name)));
			this.attributeRequirements = [...names].map(name => ({
				name,
				base:  attributes[`${name}-base`],
				scale: attributes[`${name}-scale`]
			}));
		}

		if (yaml.incompatible) this.incompStr = yaml.incompatible;

		if (yaml.icon) this.icon.material = toEditorCase(yaml.icon);
		if (yaml['icon-data']) this.icon.customModelData = yaml['icon-data'];
		if (yaml['icon-lore']) this.icon.lore = yaml['icon-lore'];

		let unsub: Unsubscriber | undefined = undefined;

		return new Promise<void>((resolve) => {
			unsub = initialized.subscribe(init => {
				if (!init) return;
				if (yaml.components) this.triggers = <FabledTrigger[]>Registry.deserializeComponents(yaml.components);

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

	private saveDebounceTimeout: number | undefined;
	public save = () => {
		if (!this.name) return;
		if (this.location === 'server') return;

		if (this.saveDebounceTimeout) {
			window.clearTimeout(this.saveDebounceTimeout);
		}

		this.changed();
		this.saveDebounceTimeout = window.setTimeout(async () => {
			skillStore.isSaving.set(true);

			// Rename: remove old IDB entry
			if (this.previousName && this.previousName !== this.name) {
				await idbDelete('skills', this.previousName);
			}
			this.previousName = this.name;

			try {
				const yaml = YAML.stringify({ [this.name]: this.serializeYaml() }, {
					lineWidth:             0,
					aliasDuplicateObjects: false
				});
				await idbSet('skills', this.name, yaml);
				this.tooBig = false;
			} catch (e: any) {
				console.error(this.name + ' Save error', e);
				this.tooBig = true;
				saveError.set(this);
			}

			this.saveDebounceTimeout = undefined;
			skillStore.isSaving.set(false);
			console.log('Saved ' + this.name + ' 😎');
		}, 600);
	};
}

class SkillStore {
	isLegacy = false;

	// ---- O(1) name → skill lookup ----------------------------------------
	private skillMap = new Map<string, FabledSkill>();

	private syncMap(skills: FabledSkill[]) {
		this.skillMap.clear();
		for (const sk of skills) this.skillMap.set(sk.name, sk);
	}

	// ---- Server skills ----------------------------------------------------
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

		// Start async IDB init here — all arrow-function fields are guaranteed
		// to be initialised by the time the constructor body runs.
		if (browser) {
			this.initSkillsFromIdb();
		}

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

	skills: Writable<FabledSkill[]> = (() => {
		const { subscribe, set, update } = writable<FabledSkill[]>([]);

		return {
			subscribe,
			set: (value: FabledSkill[]) => {
				const sorted = sort<FabledSkill>(value);
				this.syncMap(sorted);
				this.persistSkills(sorted);
				return set(sorted);
			},
			update
		};
	})();

	getSkill = (name: string): FabledSkill | undefined => {
		return this.skillMap.get(name);
	};

	/**
	 * Load skill names from IndexedDB on startup (replaces localStorage init).
	 * Falls back to localStorage migration if IDB is empty.
	 */
	initSkillsFromIdb = async () => {
		let names = await idbGetAllKeys('skills');

		// --- one-time migration from localStorage ---
		if (names.length === 0) {
			const lsNames = localStorage.getItem('skillNames');
			if (lsNames) {
				// Legacy new-format: comma-separated names
				const legacyNames = lsNames.split(', ').filter(Boolean);
				await migrateFromLocalStorage(n => 'sapi.skill.' + n, 'skills', legacyNames);
				localStorage.removeItem('skillNames');
				names = await idbGetAllKeys('skills');
			} else {
				// Very old format: skillData key had full YAML
				const oldData = localStorage.getItem('skillData');
				if (oldData) {
					localStorage.removeItem('skillData');
					this.isLegacy = true;
					const legacy  = sort<FabledSkill>(this.loadSkillTextToArray(oldData));
					this.skills.set(legacy);
					legacy.forEach(sk => sk.save());
					return;
				}
			}
		}

		if (names.length === 0) return;

		const skills = names.map(name => new FabledSkill({ name, location: 'local' }));
		// Bypass the Writable.set wrapper to avoid double-persistSkills
		const sorted = sort<FabledSkill>(skills);
		this.syncMap(sorted);
		// Use the raw writable update to seed the store
		get(this.skills); // ensure writable is subscribed
		(this.skills as any).set(sorted);
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
			const raw = await idbGet('skills', data.name) ?? '';
			yamlData  = <MultiSkillYamlData>parseYaml(raw);
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
		idbDelete('skills', data.name); // fire & forget

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
	 * Loads skill data from a YAML string.
	 * Uses the Web Worker + chunked parsing for large files (all-in-one skills.yml).
	 * Falls back to synchronous parse for server-sourced single skills.
	 */
	loadSkillText = async (text: string, fromServer: boolean = false) => {
		// For server single-skill loads, stay synchronous (they're tiny)
		if (fromServer) {
			const data = <MultiSkillYamlData>parseYaml(text);
			if (!data || Object.keys(data).length === 0) return;
			const keys = Object.keys(data);
			const key  = keys[0];
			if (!key || key === 'loaded') return;
			const skill = <FabledSkill>(this.isSkillNameTaken(key)
				? this.getSkill(key)
				: this.addSkill(key));
			skill.location = 'server';
			await skill.load(data[key]);
			this.refreshSkills();
			return;
		}

		// --- Chunked loading via Web Worker ---
		const tempSkills: FabledSkill[] = get(this.skills).slice();

		await workerParseChunked(
			text,
			async (batch, processed, total) => {
				// Update progress bar
				loadProgress.set({ label: 'Loading skills…', processed, total });

				for (const [key, yamlData] of Object.entries(batch)) {
					if (key === 'loaded') continue;
					if (this.isSkillNameTaken(key)) continue; // skip duplicates

					const skill = new FabledSkill({ name: key });
					// load() resolves when Registry is initialized
					await skill.load(<SkillYamlData>yamlData);
					skill.save(); // persist to IDB (fire & forget)
					tempSkills.push(skill);
				}

				// Flush to store so sidebar updates incrementally
				const sorted = sort<FabledSkill>(tempSkills);
				this.syncMap(sorted);
				// Directly trigger the Writable without calling persistSkills
				(this.skills as any).set(sorted);
			},
			25 // batch size — 25 skills per message
		);

		loadProgress.set(null);
		this.refreshSkills();
	};

	loadSkills = async (e: ProgressEvent<FileReader>) => {
		const text: string = <string>e.target?.result;
		if (!text) return;

		await this.loadSkillText(text);
	};

	isSaving: Writable<boolean> = writable(false);
	saveTask: number            = 0;

	persistSkills = (_list?: FabledSkill[]) => {
		// Skill YAML is now persisted per-skill via idbSet() inside FabledSkill.save().
		// This method intentionally left as a no-op to maintain call-site compatibility.
	};
}

export const skillStore = new SkillStore();