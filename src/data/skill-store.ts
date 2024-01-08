import { parseYAML, YAMLObject } from '$api/yaml';
import type { Writable }         from 'svelte/store';
import { get, writable }         from 'svelte/store';
import ProFolder                 from '$api/profolder';
import { sort }                  from '$api/api';
import { browser }               from '$app/environment';
import ProSkill                  from '$api/proskill';
import { active, rename }        from './store';
import { goto }                  from '$app/navigation';
import { base }                  from '$app/paths';

let isLegacy = false;

const loadSkillTextToArray = (text: string): ProSkill[] => {
	const list: ProSkill[] = [];
	// Load skills
	const data: YAMLObject = parseYAML(text);
	let skill: ProSkill;
	// If we only have one skill, and it is the current YAML,
	// the structure is a bit different
	if (data.key && !data.data[data.key]) {
		const key: string = data.key;
		if (key === 'loaded') return list;
		skill = new ProSkill({ name: key });
		skill.load(data);
		list.push(skill);
		return list;
	}

	for (const key in data.data) {
		if (key != 'loaded' && data.data[key] instanceof YAMLObject) {
			skill = new ProSkill({ name: key });
			skill.load(data.data[key]);
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
		data.load(parseYAML(localStorage.getItem(`sapi.skill.${data.name}`) || ''));
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
	const skill = new ProSkill();
	skill.load(parseYAML(data.serializeYaml().toString()));
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
export const loadSkillText = (text: string, fromServer: boolean = false) => {
	// Load new skills
	const data: YAMLObject = parseYAML(text);
	console.log(data);
	let skill: ProSkill;
	// If we only have one skill, and it is the current YAML,
	// the structure is a bit different
	if (data.key && !data.data[data.key]) {
		const key: string = data.key;
		skill             = (<ProSkill>(isSkillNameTaken(key)
			? getSkill(key)
			: addSkill(key)));
		if (fromServer) skill.location = 'server';
		skill.load(data);
		refreshSkills();
		return;
	}

	for (const key in data.data) {
		if (key != 'loaded' && data.data[key] instanceof YAMLObject && !isSkillNameTaken(key)) {
			skill = (<ProSkill>(isSkillNameTaken(key)
				? getSkill(key)
				: addSkill(key)));
			skill.load(data.data[key]);
		}
	}
	refreshSkills();
};

export const loadSkills = (e: ProgressEvent<FileReader>) => {
	const text: string = <string>e.target?.result;
	if (!text) return;

	loadSkillText(text);
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
	get(skills).forEach(sk => {
		if (sk.location === 'local') sk.save();
	});
	persistSkills();
}