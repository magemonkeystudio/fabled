import type { Writable }                          from 'svelte/store';
import { get, writable }                          from 'svelte/store';
import ProFolder                                  from '$api/profolder';
import { sort }                                   from '$api/api';
import { browser }                                from '$app/environment';
import ProSkill                                   from '$api/proskill';
import { active, rename }                         from './store';
import { goto }                                   from '$app/navigation';
import { base }                                   from '$app/paths';
import { initialized }                            from '$api/components/registry';
import YAML                                       from 'yaml';
import type { MultiSkillYamlData, SkillYamlData } from '$api/types';

let isLegacy = false;

const loadSkillTextToArray = (text: string): ProSkill[] => {
	const list: ProSkill[] = [];
	// Load skills
	const data             = <MultiSkillYamlData>YAML.parse(text);
	if (!data || Object.keys(data).length === 0) {
		// If there is no data or the object is empty... return
		return list;
	}

	const keys = Object.keys(data);

	let skill: ProSkill;
	// If we only have one skill, and it is the current YAML,
	// the structure is a bit different
	if (keys.length == 1) {
		const key = keys[0];
		if (key === 'loaded') return list;
		skill = new ProSkill({ name: key });
		skill.load(data[key]);
		list.push(skill);
		return list;
	}

	for (const key of Object.keys(data)) {
		if (key != 'loaded') {
			skill = new ProSkill({ name: key });
			skill.load(data[key]);
			list.push(skill);
		}
	}
	return list;
};

const setupSkillStore = <T extends ProSkill[] | ProFolder[]>(key: string,
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

export const skills: Writable<ProSkill[]> = setupSkillStore<ProSkill[]>(
	browser && localStorage.getItem('skillNames') ? 'skillNames' : 'skillData',
	[],
	(data: string) => {
		if (localStorage.getItem('skillNames')) {
			return data.split(', ').map(name => {
				const skill = new ProSkill({ name, location: 'local' });
				return skill;
			}).filter(sk => localStorage.getItem('sapi.skill.' + sk.name));
		} else {
			localStorage.removeItem('skillData');
			isLegacy = true;
			return sort<ProSkill>(loadSkillTextToArray(data));
		}
	},
	(value: ProSkill[]) => {
		persistSkills();
		return sort<ProSkill>(value);
	});

export const getSkill = (name: string): ProSkill | undefined => {
	for (const c of get(skills)) {
		if (c.name == name) return c;
	}

	return undefined;
};

export const skillFolders: Writable<ProFolder[]> = setupSkillStore<ProFolder[]>('skillFolders', [],
	(data: string) => {
		if (!data || data === 'null') return [];

		const parsedData = JSON.parse(data, (key: string, value) => {
			if (!value) return;
			if (/\d+/.test(key)) {
				if (typeof (value) === 'string') {
					return getSkill(value);
				}

				const folder = new ProFolder(value.data);
				folder.name  = value.name;
				return folder;
			}
			return value;
		});

		return parsedData;
	},
	(value: ProFolder[]) => {
		const data = JSON.stringify(value, (key, value: ProFolder | ProSkill | ProSkill) => {
			if (value instanceof ProSkill || value instanceof ProSkill) return value.name;
			else if (key === 'parent') return undefined;
			return value;
		});
		localStorage.setItem('skillFolders', data);
		return sort<ProFolder>(value);
	});

export const isSkillNameTaken = (name: string): boolean => !!getSkill(name);

export const addSkill = (name?: string): ProSkill => {
	const allSkills = get(skills);
	let index       = allSkills.length + 1;
	while (!name && isSkillNameTaken(name || 'Skill ' + index)) {
		index++;
	}
	const skill = new ProSkill({ name: (name || 'Skill ' + index) });
	allSkills.push(skill);

	skills.set(allSkills);
	skill.save();
	return skill;
};

export const loadSkill = (data: ProSkill) => {
	if (data.loaded) return;

	if (data.location === 'local') {
		const yamlData = <MultiSkillYamlData>YAML.parse(localStorage.getItem(`sapi.skill.${data.name}`) || '');
		// Get the first entry in the object
		const skill    = Object.values(yamlData)[0];
		data.load(skill);
	} else {
		// TODO Load data from server
	}
	data.postLoad();
};

export const cloneSkill = (data: ProSkill): ProSkill => {
	if (!data.loaded) loadSkill(data);

	const sk: ProSkill[] = get(skills);
	let name             = data.name + ' (Copy)';
	let i                = 1;
	while (isSkillNameTaken(name)) {
		name = data.name + ' (Copy ' + i + ')';
		i++;
	}
	const skill     = new ProSkill();
	const yamlData  = data.serializeYaml();
	skill.load(yamlData);
	skill.name = name;
	sk.push(skill);

	skills.set(sk);
	skill.save();
	return skill;
};

export const addSkillFolder = (folder: ProFolder) => {
	const folders = get(skillFolders);
	if (folders.includes(folder)) return;

	rename(folder, folders);

	folders.push(folder);
	folders.sort((a, b) => a.name.localeCompare(b.name));
	skillFolders.set(folders);
};


export const deleteSkillFolder = (folder: ProFolder, deleteCheck?: (subfolder: ProFolder) => boolean) => {
	const folders = get(skillFolders).filter(f => f != folder);

	// If there are any subfolders or skills, move them to the parent or root
	folder.data.forEach(d => {
		if (d instanceof ProFolder) {
			if (deleteCheck && deleteCheck(d)) {
				deleteSkillFolder(d, deleteCheck);
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

	skillFolders.set(folders);
};

export const deleteSkill = (data: ProSkill) => {
	const filtered = get(skills).filter(c => c != data);
	const act      = get(active);
	skills.set(filtered);
	localStorage.removeItem('sapi.skill.' + data.name);

	if (!(act instanceof ProSkill)) return;

	if (filtered.length === 0) goto(`${base}/`);
	else if (!filtered.find(sk => sk === get(active))) goto(`${base}/skill/${filtered[0].name}`);
};

export const refreshSkills       = () => skills.set(sort<ProSkill>(get(skills)));
export const refreshSkillFolders = () => {
	skillFolders.set(sort<ProFolder>(get(skillFolders)));
	refreshSkills();
};


/**
 *  Loads skill data from a string
 */
export const loadSkillText = async (text: string, fromServer: boolean = false) => {
	// Load new skills
	const data = <MultiSkillYamlData>YAML.parse(text);

	if (!data || Object.keys(data).length === 0) {
		// If there is no data or the object is empty... return
		return;
	}

	const keys = Object.keys(data);

	let skill: ProSkill;
	// If we only have one skill, and it is the current YAML,
	// the structure is a bit different
	if (keys.length == 1) {
		const key: string = keys[0];
		skill             = (<ProSkill>(isSkillNameTaken(key)
			? getSkill(key)
			: addSkill(key)));
		if (fromServer) skill.location = 'server';
		skill.load(data[key]);
		skill.save();
		refreshSkills();
		return;
	}

	for (const key of Object.keys(data)) {
		if (key != 'loaded' && !isSkillNameTaken(key)) {
			skill = (<ProSkill>(isSkillNameTaken(key)
				? getSkill(key)
				: addSkill(key)));
			skill.load(data[key]);
			skill.save();
		}
	}
	refreshSkills();
};

export const loadSkills = async (e: ProgressEvent<FileReader>) => {
	const text: string = <string>e.target?.result;
	if (!text) return;

	await loadSkillText(text);
};

export const isSaving: Writable<boolean> = writable(false);

let saveTask: NodeJS.Timeout;
export const persistSkills = (list?: ProSkill[]) => {
	if (get(isSaving) && saveTask) {
		clearTimeout(saveTask);
	}

	isSaving.set(true);

	saveTask = setTimeout(() => {
		const skillList = (list || get(skills)).filter(sk => sk.location === 'local');
		localStorage.setItem('skillNames', skillList.map(sk => sk.name).join(', '));
		isSaving.set(false);
	});
};

get(skills).forEach(sk => {
	if (sk.loaded) {
		sk.postLoad();
	}
});

if (isLegacy) {
	const sub = initialized.subscribe(init => {
		if (!init) return;
		get(skills).forEach(sk => {
			if (sk.location === 'local') sk.save();
		});
		persistSkills();
		if (sub) sub();
	});
}