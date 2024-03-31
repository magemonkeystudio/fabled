import type { Writable }           from 'svelte/store';
import { get, writable }           from 'svelte/store';
import FabledFolder                from '$api/fabled-folder';
import { sort }                    from '$api/api';
import { browser }                 from '$app/environment';
import FabledSkill                 from '$api/fabled-skill';
import { active, rename }          from './store';
import { goto }                    from '$app/navigation';
import { base }                    from '$app/paths';
import { initialized }             from '$api/components/registry';
import YAML                        from 'yaml';
import type { MultiSkillYamlData } from '$api/types';

let isLegacy = false;

const loadSkillTextToArray = (text: string): FabledSkill[] => {
	const list: FabledSkill[] = [];
	// Load skills
	const data                = <MultiSkillYamlData>YAML.parse(text);
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
		skill.load(data[key]);
		list.push(skill);
		return list;
	}

	for (const key of Object.keys(data)) {
		if (key != 'loaded') {
			skill = new FabledSkill({ name: key });
			skill.load(data[key]);
			list.push(skill);
		}
	}
	return list;
};

const setupSkillStore = <T extends FabledSkill[] | FabledFolder[]>(key: string,
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

export const skills: Writable<FabledSkill[]> = setupSkillStore<FabledSkill[]>(
	browser && localStorage.getItem('skillNames') ? 'skillNames' : 'skillData',
	[],
	(data: string) => {
		if (localStorage.getItem('skillNames')) {
			return data.split(', ').map(name => {
				const skill = new FabledSkill({ name, location: 'local' });
				return skill;
			}).filter(sk => localStorage.getItem('sapi.skill.' + sk.name));
		} else {
			localStorage.removeItem('skillData');
			isLegacy = true;
			return sort<FabledSkill>(loadSkillTextToArray(data));
		}
	},
	(value: FabledSkill[]) => {
		persistSkills();
		return sort<FabledSkill>(value);
	});

export const getSkill = (name: string): FabledSkill | undefined => {
	for (const c of get(skills)) {
		if (c.name == name) return c;
	}

	return undefined;
};

export const skillFolders: Writable<FabledFolder[]> = setupSkillStore<FabledFolder[]>('skillFolders', [],
	(data: string) => {
		if (!data || data === 'null') return [];

		const parsedData = JSON.parse(data, (key: string, value) => {
			if (!value) return;
			if (/\d+/.test(key)) {
				if (typeof (value) === 'string') {
					return getSkill(value);
				}

				const folder = new FabledFolder(value.data);
				folder.name  = value.name;
				return folder;
			}
			return value;
		});

		return parsedData;
	},
	(value: FabledFolder[]) => {
		const data = JSON.stringify(value, (key, value: FabledFolder | FabledSkill | FabledSkill) => {
			if (value instanceof FabledSkill || value instanceof FabledSkill) return value.name;
			else if (key === 'parent') return undefined;
			return value;
		});
		localStorage.setItem('skillFolders', data);
		return sort<FabledFolder>(value);
	});

export const isSkillNameTaken = (name: string): boolean => !!getSkill(name);

export const addSkill = (name?: string): FabledSkill => {
	const allSkills = get(skills);
	let index       = allSkills.length + 1;
	while (!name && isSkillNameTaken(name || 'Skill ' + index)) {
		index++;
	}
	const skill = new FabledSkill({ name: (name || 'Skill ' + index) });
	allSkills.push(skill);

	skills.set(allSkills);
	skill.save();
	return skill;
};

export const loadSkill = (data: FabledSkill) => {
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

export const cloneSkill = (data: FabledSkill): FabledSkill => {
	if (!data.loaded) loadSkill(data);

	const sk: FabledSkill[] = get(skills);
	let name                = data.name + ' (Copy)';
	let i                   = 1;
	while (isSkillNameTaken(name)) {
		name = data.name + ' (Copy ' + i + ')';
		i++;
	}
	const skill    = new FabledSkill();
	const yamlData = data.serializeYaml();
	skill.load(yamlData);
	skill.name = name;
	sk.push(skill);

	skills.set(sk);
	skill.save();
	return skill;
};

export const addSkillFolder = (folder: FabledFolder) => {
	const folders = get(skillFolders);
	if (folders.includes(folder)) return;

	rename(folder, folders);

	folders.push(folder);
	folders.sort((a, b) => a.name.localeCompare(b.name));
	skillFolders.set(folders);
};


export const deleteSkillFolder = (folder: FabledFolder, deleteCheck?: (subfolder: FabledFolder) => boolean) => {
	const folders = get(skillFolders).filter(f => f != folder);

	// If there are any subfolders or skills, move them to the parent or root
	folder.data.forEach(d => {
		if (d instanceof FabledFolder) {
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

export const deleteSkill = (data: FabledSkill) => {
	const filtered = get(skills).filter(c => c != data);
	const act      = get(active);
	skills.set(filtered);
	localStorage.removeItem('sapi.skill.' + data.name);

	if (!(act instanceof FabledSkill)) return;

	if (filtered.length === 0) goto(`${base}/`);
	else if (!filtered.find(sk => sk === get(active))) goto(`${base}/skill/${filtered[0].name}`);
};

export const refreshSkills       = () => skills.set(sort<FabledSkill>(get(skills)));
export const refreshSkillFolders = () => {
	skillFolders.set(sort<FabledFolder>(get(skillFolders)));
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

	let skill: FabledSkill;
	// If we only have one skill, and it is the current YAML,
	// the structure is a bit different
	if (keys.length == 1) {
		const key: string = keys[0];
		skill             = (<FabledSkill>(isSkillNameTaken(key)
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
			skill = (<FabledSkill>(isSkillNameTaken(key)
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
export const persistSkills = (list?: FabledSkill[]) => {
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