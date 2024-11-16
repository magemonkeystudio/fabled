import type { Writable }                                                                           from 'svelte/store';
import { get, writable }                                                                           from 'svelte/store';
import { active }                                                                                  from './store';
import { parseBool, sort, toEditorCase, toProperCase }                                             from '$api/api';
import { parseYaml }                                                                               from '$api/yaml';
import {
	browser
}                                                                                                  from '$app/environment';
import {
	goto
}                                                                                                  from '$app/navigation';
import { base }                                                                                    from '$app/paths';
import type { ClassYamlData, FabledClassData, IAttribute, Icon, MultiClassYamlData, Serializable } from '$api/types';
import YAML                                                                                        from 'yaml';
import {
	socketService
}                                                                                                  from '$api/socket/socket-connector';
import {
	notify
}                                                                                                  from '$api/notification-service';
import type {
	SkillTree
}                                                                                                  from '$api/SkillTree';
import FabledSkill, {
	skillStore
}                                                                                                  from './skill-store.svelte';
import {
	FabledFolder,
	folderStore
}                                                                                                  from './folder-store.svelte';

export default class FabledClass implements Serializable {
	dataType                     = 'class';
	location: 'local' | 'server' = 'local';
	loaded                       = $state(false);

	isClass              = true;
	public key           = {};
	name: string         = $state('');
	previousName: string = '';
	prefix               = $state('');
	group                = $state('class');
	manaName             = $state('&2Mana');
	maxLevel             = $state(40);
	parent?: FabledClass = $state();
	parentStr            = $derived(this.parent?.name);

	permission               = $state(false);
	expSources               = $state(273);
	manaRegen                = $state(1);
	health: IAttribute       = $state({ name: 'health', base: 20, scale: 1 });
	mana: IAttribute         = $state({ name: 'mana', base: 20, scale: 1 });
	attributes: IAttribute[] = $state([]);
	skillTree: SkillTree     = $state('Requirement');
	skills: FabledSkill[]    = $state([]);
	icon: Icon               = $state({
		material:        'Pumpkin',
		customModelData: 0
	});
	unusableItems: string[]  = $state([]);
	actionBar                = $state('');

	lInverted  = $state(true);
	rInverted  = $state(true);
	lsInverted = $state(true);
	rsInverted = $state(true);
	sInverted  = $state(true);
	pInverted  = $state(true);
	qInverted  = $state(true);
	fInverted  = $state(true);

	lWhitelist: string[]  = $state([]);
	rWhitelist: string[]  = $state([]);
	lsWhitelist: string[] = $state([]);
	rsWhitelist: string[] = $state([]);
	sWhitelist: string[]  = $state([]);
	pWhitelist: string[]  = $state([]);
	qWhitelist: string[]  = $state([]);
	fWhitelist: string[]  = $state([]);

	constructor(data?: FabledClassData) {
		this.name   = data?.name || 'Class';
		this.prefix = data?.prefix || '&6' + this.name;
		if (!data) return;
		if (data?.location) this.location = data.location;
		if (data?.group) this.group = data.group;
		if (data?.manaName) this.manaName = data.manaName;
		if (data?.maxLevel) this.maxLevel = data.maxLevel;
		if (data?.parent) this.parent = data.parent;
		if (data?.permission !== undefined) this.permission = data.permission;
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
		if (data?.lInverted !== undefined) this.lInverted = data.lInverted;
		if (data?.rInverted !== undefined) this.rInverted = data.rInverted;
		if (data?.lsInverted !== undefined) this.lsInverted = data.lsInverted;
		if (data?.rsInverted !== undefined) this.rsInverted = data.rsInverted;
		if (data?.pInverted !== undefined) this.pInverted = data.pInverted;
		if (data?.qInverted !== undefined) this.qInverted = data.qInverted;
		if (data?.fInverted !== undefined) this.fInverted = data.fInverted;
		if (data?.lWhitelist) this.lWhitelist = data.lWhitelist;
		if (data?.rWhitelist) this.rWhitelist = data.rWhitelist;
		if (data?.lsWhitelist) this.lsWhitelist = data.lsWhitelist;
		if (data?.rsWhitelist) this.rsWhitelist = data.rsWhitelist;
		if (data?.pWhitelist) this.pWhitelist = data.pWhitelist;
		if (data?.qWhitelist) this.qWhitelist = data.qWhitelist;
		if (data?.fWhitelist) this.fWhitelist = data.fWhitelist;
	}

	/**
	 * Reads all the reactive state elements to act as a chane detector
	 */
	public changed = () => {
		return {
			name:          this.name,
			prefix:        this.prefix,
			group:         this.group,
			manaName:      this.manaName,
			maxLevel:      this.maxLevel,
			parent:        this.parent,
			permission:    this.permission,
			expSources:    this.expSources,
			health:        this.health,
			mana:          this.mana,
			attributes:    this.attributes,
			skillTree:     this.skillTree,
			skills:        this.skills,
			icon:          this.icon,
			unusableItems: this.unusableItems,
			actionBar:     this.actionBar,
			lInverted:     this.lInverted,
			rInverted:     this.rInverted,
			lsInverted:    this.lsInverted,
			rsInverted:    this.rsInverted,
			sInverted:     this.sInverted,
			pInverted:     this.pInverted,
			qInverted:     this.qInverted,
			fInverted:     this.fInverted,
			lWhitelist:    this.lWhitelist,
			rWhitelist:    this.rWhitelist,
			lsWhitelist:   this.lsWhitelist,
			rsWhitelist:   this.rsWhitelist,
			sWhitelist:    this.sWhitelist,
			pWhitelist:    this.pWhitelist,
			qWhitelist:    this.qWhitelist,
			fWhitelist:    this.fWhitelist
		};
	};

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
			this.attributes.push({ name: attrib, base: 0, scale: 0 });
		}
	};

	public serializeYaml = (): ClassYamlData => {
		const health = {
			base:  this.health.base,
			scale: this.health.scale
		};
		const mana   = {
			base:  this.mana.base,
			scale: this.mana.scale
		};

		// Attempt to convert health/mana base & scale to numbers, if applicable
		if (typeof (health.base) === 'string') {
			const base = parseInt(health.base);
			if (!isNaN(base)) {
				health.base = base;
			}
		}
		if (typeof (health.scale) === 'string') {
			const scale = parseInt(health.scale);
			if (!isNaN(scale)) {
				health.scale = scale;
			}
		}
		if (typeof (mana.base) === 'string') {
			const base = parseInt(mana.base);
			if (!isNaN(base)) {
				mana.base = base;
			}
		}
		if (typeof (mana.scale) === 'string') {
			const scale = parseInt(mana.scale);
			if (!isNaN(scale)) {
				mana.scale = scale;
			}
		}

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
				'health-base':  health.base ?? 20,
				'health-scale': health.scale ?? 0,
				'mana-base':    mana.base ?? 20,
				'mana-scale':   mana.scale ?? 0
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
			if (typeof attr.base === 'string') {
				const base = parseInt(attr.base);
				if (!isNaN(base)) {
					attr.base = base;
				}
			}
			if (typeof attr.scale === 'string') {
				const scale = parseInt(attr.scale);
				if (!isNaN(scale)) {
					attr.scale = scale;
				}
			}

			yaml.attributes[`${attr.name.toLowerCase()}-base`]  = attr.base || 0;
			yaml.attributes[`${attr.name.toLowerCase()}-scale`] = attr.scale || 0;
		});

		return yaml;
	};

	public updateParent = (classes: FabledClass[]) => {
		if (!this.parentStr) return;
		this.parent = classes.find(c => c.name === this.parentStr);
	};

	public load = (yaml: ClassYamlData) => {
		if (yaml.name) this.name = yaml.name;
		if (yaml['action-bar'] !== undefined) this.actionBar = yaml['action-bar'];
		if (yaml.mana !== undefined) this.manaName = yaml.mana;
		if (yaml.prefix !== undefined) this.prefix = yaml.prefix;
		if (yaml.group) this.group = yaml.group;
		if (yaml['max-level']) this.maxLevel = yaml['max-level'];
		if (yaml.parent) this.parentStr = yaml.parent;
		this.permission = parseBool(yaml['needs-permission']);

		if (yaml.attributes) {
			const attributes = yaml.attributes;
			this.health      = {
				name:  'health',
				base:  attributes['health-base'] ?? 20,
				scale: attributes['health-scale'] ?? 1
			};
			this.mana        = { name: 'mana', base: attributes['mana-base'] ?? 20, scale: attributes['mana-scale'] ?? 1 };

			const map: { [key: string]: IAttribute } = {};
			for (const attrId of Object.keys(attributes)) {
				const split = attrId.split('-');
				const name  = split[0];
				if (map[name] || name === 'health' || name === 'mana') continue;

				map[name] = { name, base: attributes[`${name}-base`], scale: attributes[`${name}-scale`] };
			}
			this.attributes = Object.values(map);
		}

		if (yaml['mana-regen']) this.manaRegen = yaml['mana-regen'];
		if (yaml['skill-tree']) this.skillTree = <SkillTree>toProperCase(yaml['skill-tree']);
		if (yaml.blacklist) this.unusableItems = yaml.blacklist;
		if (yaml.skills) this.skills = <FabledSkill[]>yaml.skills.map(s => skillStore.getSkill(s)).filter(s => !!s);
		if (yaml.icon) this.icon.material = toEditorCase(yaml.icon);
		if (yaml['icon-data']) this.icon.customModelData = yaml['icon-data'];
		if (yaml['icon-lore']) this.icon.lore = yaml['icon-lore'];
		if (yaml['exp-source'] !== null) this.expSources = yaml['exp-source'];

		if (yaml['combo-starters']) {
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
		}

		this.loaded = true;
		this.save();
	};

	public save = () => {
		if (!this.name) return;

		if (this.location === 'server') {
			return;
		}

		this.changed();

		const yaml = YAML.stringify({ [this.name]: this.serializeYaml() }, { lineWidth: 0, aliasDuplicateObjects: false });

		if (this.previousName && this.previousName !== this.name) {
			localStorage.removeItem('sapi.class.' + this.previousName);
		}
		this.previousName = this.name;
		localStorage.setItem('sapi.class.' + this.name, yaml);

		console.log('Saved ' + this.name + ' 😎');
	};
}

class ClassStoreSvelte {
	isLegacy = false;

	private loadClassesFromServer = async () => {
		let serverClasses: string[];
		try {
			serverClasses = await socketService.getClasses();
		} catch (_) {
			return;
		}

		const tempFolders = get(this.classFolders);
		const tempClasses = get(this.classes);
		serverClasses.forEach(c => {
			const parts = c.split('/');
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

			// If we already have this class, don't add it
			if (tempClasses.find(cl => cl.name === c)) return;

			const clazz = new FabledClass({ name, location: 'server' });
			if (folder) folder.add(clazz);
			tempClasses.push(clazz);
		});
		this.classes.set(tempClasses);
	};

	private removeServerClasses = () => {
		const tempClasses = get(this.classes);
		this.classes.set(tempClasses.filter(c => c.location !== 'server'));

		const tempFolders = get(this.classFolders);
		tempFolders.filter(f => f.location === 'server').forEach(f => this.deleteClassFolder(f, (sb) => sb.location === 'server'));
	};

	constructor() {
		socketService.onConnect(this.loadClassesFromServer);
		socketService.onDisconnect(this.removeServerClasses);

		if (this.isLegacy) {
			get(this.classes).forEach(clazz => {
				if (clazz.location === 'local') clazz.save();
			});
			this.persistClasses();
		}
	}

	private loadClassTextToArray = (text: string): FabledClass[] => {
		const list: FabledClass[] = [];
		// Load classes
		const data                = <MultiClassYamlData>parseYaml(text);
		const keys                = Object.keys(data);

		let clazz: FabledClass;
		// If we only have one class, and it is the current YAML,
		// the structure is a bit different
		if (keys.length == 1) {
			const key = keys[0];
			if (key === 'loaded') return list;
			clazz = new FabledClass({ name: key });
			clazz.load(data[key]);
			list.push(clazz);
			return list;
		}

		for (const key of Object.keys(data)) {
			if (key != 'loaded') {
				clazz = new FabledClass({ name: key });
				clazz.load(data[key]);
				list.push(clazz);
			}
		}
		return list;
	};

	private setupClassStore = <T>(key: string,
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

	classes: Writable<FabledClass[]> = this.setupClassStore<FabledClass[]>(
		browser && localStorage.getItem('classNames') ? 'classNames' : 'classData', [],
		(data: string) => {
			if (localStorage.getItem('classNames')) {
				return data.split(', ').map(name => new FabledClass({
					name,
					location: 'local'
				})).filter(cl => localStorage.getItem('sapi.class.' + cl.name));
			} else {
				localStorage.removeItem('classData');
				this.isLegacy = true;
				return sort<FabledClass>(this.loadClassTextToArray(data));
			}
		},
		(value: FabledClass[]) => {
			this.persistClasses(value);
			value.forEach(c => c.updateParent(value));
			return sort<FabledClass>(value);
		},
		(saved: FabledClass[]) => saved.forEach(c => c.updateParent(saved))); // This will be the gotcha here

	getClass = (name: string): FabledClass | undefined => {
		for (const c of get(this.classes)) {
			if (c.name == name) return c;
		}

		return undefined;
	};

	classFolders: Writable<FabledFolder[]> = this.setupClassStore<FabledFolder[]>('classFolders', [],
		(data: string) => {
			if (!data || data === 'null') return [];

			try {
				return JSON.parse(data, (key: string, value) => {
					if (!value) return;
					if (/\d+/.test(key)) {
						if (typeof (value) === 'string') {
							return this.getClass(value);
						}

						const folder = new FabledFolder(value.data);
						folder.name  = value.name;
						return folder;
					}
					return value;
				});
			} catch (e) {
				console.error('Error loading class folders. Folder data: ' + data, e);
				notify('Error loading class folders. ' + JSON.stringify(e) + '\nFolder data: ' + data);
				return [];
			}
		},
		(value: FabledFolder[]) => {
			const data = JSON.stringify(value, (key, value: FabledFolder | FabledClass | FabledSkill) => {
				if (value instanceof FabledClass || value instanceof FabledSkill) return value.name;
				else if (key === 'parent') return undefined;
				return value;
			});
			localStorage.setItem('classFolders', data);
			return sort<FabledFolder>(value);
		});

	updateAllAttributes = (attributes: string[]) =>
		get(this.classes).forEach(c => c.updateAttributes(attributes));

	isClassNameTaken = (name: string): boolean => !!this.getClass(name);

	addClass = (name?: string): FabledClass => {
		const cl  = get(this.classes);
		let index = cl.length + 1;
		while (!name && this.isClassNameTaken(name || 'Class ' + index)) {
			index++;
		}
		const clazz = new FabledClass({ name: (name || 'Class ' + index) });
		cl.push(clazz);

		this.classes.set(cl);
		clazz.save();
		return clazz;
	};

	loadClass = async (data: FabledClass) => {
		if (data.loaded) return;
		let yamlData: MultiClassYamlData;

		if (data.location === 'local') {
			yamlData = <MultiClassYamlData>parseYaml(localStorage.getItem(`sapi.class.${data.name}`) || '');
		} else {
			const yaml = await socketService.getClassYaml(data.name);
			if (!yaml) return;
			yamlData = <MultiClassYamlData>YAML.parse(yaml);
		}

		if (yamlData === null || Object.values(yamlData).length == 0) {
			console.warn(`Failed to parse yaml for class ${data.name}`, localStorage.getItem(`sapi.class.${data.name}`));
			return;
		}

		const clazz = Object.values(yamlData)[0];
		data.load(clazz);

		data.updateParent(get(this.classes));
		data.loaded = true;
	};

	cloneClass = async (data: FabledClass): Promise<FabledClass> => {
		if (!data.loaded) await this.loadClass(data);

		const cl: FabledClass[] = get(this.classes);
		let name                = data.name + ' (Copy)';
		let i                   = 1;
		while (this.isClassNameTaken(name)) {
			name = data.name + ' (Copy ' + i + ')';
			i++;
		}
		const clazz    = new FabledClass();
		const yamlData = data.serializeYaml();
		clazz.load(yamlData);
		clazz.name = name;
		cl.push(clazz);

		this.classes.set(cl);
		clazz.save();
		return clazz;
	};

	addClassFolder = (folder: FabledFolder) => {
		const folders = get(this.classFolders);
		if (folders.includes(folder)) return;

		folderStore.rename(folder, folders);

		folders.push(folder);
		folders.sort((a, b) => a.name.localeCompare(b.name));
		this.classFolders.set(folders);
	};

	deleteClassFolder = (folder: FabledFolder, deleteCheck?: (subfolder: FabledFolder) => boolean) => {
		const folders = get(this.classFolders).filter(f => f != folder);

		folder.data.forEach(d => {
			if (d instanceof FabledFolder) {
				if (deleteCheck && deleteCheck(d)) {
					this.deleteClassFolder(d, deleteCheck);
					return;
				}
				if (folder.parent) folder.parent.add(d);
				else {
					d.updateParent();
					folders.push(d);
				}
			} else if (folder.parent)
				folder.parent.add(d); // Add the class to the parent folder
		});

		this.classFolders.set(folders);
	};

	deleteClass = (data: FabledClass) => {
		const filtered = get(this.classes).filter(c => c != data);
		const act      = get(active);
		this.classes.set(filtered);
		localStorage.removeItem('sapi.class.' + data.name);

		if (!(act instanceof FabledClass)) return;

		if (filtered.length === 0) goto(`${base}/`);
		else if (!filtered.find(cl => cl === get(active))) goto(`${base}/class/${filtered[0].name}/edit`).then(() => {
		});
	};

	refreshClasses      = () => this.classes.set(sort<FabledClass>(get(this.classes)));
	refreshClassFolders = () => {
		this.classFolders.set(sort<FabledFolder>(get(this.classFolders)));
		this.refreshClasses();
	};


	/**
	 *  Loads class data from a string
	 */
	loadClassText = (text: string, fromServer: boolean = false) => {
		// Load new classes
		const data = <MultiClassYamlData>parseYaml(text);

		if (!data || Object.keys(data).length === 0) {
			// If there is no data or the object is empty... return
			return;
		}

		const keys = Object.keys(data);

		let clazz: FabledClass;
		// If we only have one class, and it is the current YAML,
		// the structure is a bit different
		if (keys.length == 1) {
			const key: string = keys[0];
			clazz             = (<FabledClass>(this.isClassNameTaken(key)
				? this.getClass(key)
				: this.addClass(key)));
			if (fromServer) clazz.location = 'server';
			clazz.load(data[key]);
			this.refreshClasses();
			return;
		}

		for (const key of Object.keys(data)) {
			if (key != 'loaded' && !this.isClassNameTaken(key)) {
				clazz = (<FabledClass>(this.isClassNameTaken(key)
					? this.getClass(key)
					: this.addClass(key)));
				clazz.load(data[key]);
			}
		}
		this.refreshClasses();
	};

	loadClasses = (e: ProgressEvent<FileReader>) => {
		const text: string = <string>e.target?.result;
		if (!text) return;

		this.loadClassText(text);
	};

	persistClasses = (list?: FabledClass[]) => {
		const classList = (list || get(this.classes)).filter(c => c.location === 'local');
		localStorage.setItem('classNames', classList.map(c => c.name).join(', '));
	};
}

export const classStore = new ClassStoreSvelte();
