import type { Readable, Writable } from 'svelte/store';
import { derived, get, writable }  from 'svelte/store';
import ProClass                    from '$api/proclass';
import ProSkill                    from '$api/proskill';
import ProFolder                   from '$api/profolder';
import {
	classes,
	classFolders,
	deleteClass,
	deleteClassFolder, loadClass,
	loadClasses,
	loadClassText,
	refreshClasses,
	refreshClassFolders
} from './class-store';
import { localStore }              from '$api/api';
import { loadAttributes }          from './attribute-store';
import {
	deleteSkill,
	deleteSkillFolder,
	loadSkill,
	loadSkills,
	loadSkillText,
	refreshSkillFolders,
	refreshSkills,
	skillFolders,
	skills
}                                  from './skill-store';
import type ProComponent           from '$api/components/procomponent';
import { YAMLObject }              from '$api/yaml';

export const active: Writable<ProClass | ProSkill | undefined>     = writable(undefined);
export const activeType: Readable<'class' | 'skill'>               = derived(
	active,
	$active => $active instanceof ProClass ? 'class' : 'skill'
);
export const dragging: Writable<ProClass | ProSkill | ProFolder>   = writable();
export const draggingComponent: Writable<ProComponent | undefined> = writable();
export const showSidebar: Writable<boolean>                        = localStore('sidebarOpen', true);
export const sidebarOpen: Writable<boolean>                        = writable(true);
export const isShowClasses: Writable<boolean>                      = writable(true);
export const importing: Writable<boolean>                          = writable(false);

export const updateSidebar = () => {
	if (!get(showSidebar)) return;
	if (get(activeType) == 'class') refreshClasses();
	else refreshSkills();
	updateFolders();
};
export const toggleSidebar = () => showSidebar.set(!get(showSidebar));
export const closeSidebar  = () => showSidebar.set(false);
export const setImporting  = (bool: boolean) => importing.set(bool);

export const rename = (folder: ProFolder, folders: Array<ProFolder | ProClass | ProSkill>) => {
	const origName = folder.name;
	let num        = 1;
	while (folders.filter(f => f instanceof ProFolder && f.name == folder.name).length >= 1) {
		folder.name = origName + ' (' + (num++) + ')';
	}
};

export const deleteFolder = (folder: ProFolder) => {
	if (folder.parent) {
		folder.parent.deleteFolder(folder);
		updateFolders();
	} else if (get(isShowClasses)) deleteClassFolder(folder, () => false);
	else deleteSkillFolder(folder, () => false);
};

export const deleteProData = (data: ProClass | ProSkill | undefined) => {
	if (!data) return;

	getFolder(data)?.remove(data);
	if (data instanceof ProClass) deleteClass(data);
	else deleteSkill(data);
	updateFolders();
};

export const updateFolders = () => {
	if (!get(showSidebar)) return;
	if (get(isShowClasses)) refreshClassFolders();
	else refreshSkillFolders();
};

export const removeFolder = (folder: ProFolder) => {
	const classF = get(classFolders);
	const skillF = get(skillFolders);
	if (classF.includes(folder)) classFolders.set(classF.filter(f => f != folder));
	if (skillF.includes(folder)) skillFolders.set(skillF.filter(f => f != folder));
};

export const getFolder = (data?: ProFolder | ProClass | ProSkill): (ProFolder | undefined) => {
	if (!data) return undefined;

	if (data instanceof ProFolder) return data.parent;
	const folders: ProFolder[] = data instanceof ProClass ? get(classFolders) : get(skillFolders);

	for (const folder of folders) {
		const containingFolder = folder.getContainingFolder(data);
		if (!containingFolder) continue;

		return containingFolder;
	}

	return undefined;
};

const skillFileRegex = /['"](components|skills|group|combo)['"]:/;
/**
 * Loads an individual skill or class file
 * @param e ProgressEvent
 */
export const loadIndividual = async (e: ProgressEvent<FileReader>) => {
	const text: string = <string>e.target?.result;
	if (!text) return;

	if (text.indexOf('global:') >= 0) {
		loadAttributes(text);
	} else if (skillFileRegex.test(text)) {
		await loadSkillText(text);
	} else {
		loadClassText(text);
	}
	(<HTMLElement>document.activeElement).blur();
};

export const loadRaw = async (text: string, fromServer: boolean = false) => {
	if (!text) return;

	if (text.indexOf('global:') >= 0) {
		loadAttributes(text);
	} else if (skillFileRegex.test(text)) {
		await loadSkillText(text.replace('loaded: false\n', ''), fromServer);
	} else {
		console.log('loading classes');
		loadClassText(text.replace('loaded: false\n', ''), fromServer);
	}
};

export const loadFile = (file: File) => {
	const reader = new FileReader();
	if (file.name.indexOf('skills') == 0) {
		reader.onload = loadSkills;
	} else if (file.name.indexOf('classes') == 0) {
		reader.onload = loadClasses;
	} else {
		reader.onload = loadIndividual;
	}
	reader.readAsText(file);
};

export const saveData = (data?: ProSkill | ProClass) => {
	const act = data || get(active);
	if (!act) return;

	saveToFile(act.name + '.yml', act.serializeYaml().toString());
};

export const getAllSkillYaml = (): YAMLObject => {
	const allSkills: ProSkill[] = get(skills);
	allSkills.sort((a, b) => {
		if (a.name > b.name) return 1;
		if (a.name < b.name) return -1;
		return 0;
	});

	const skillYaml = new YAMLObject();
	skillYaml.put('loaded', false);
	for (const skill of allSkills) {
		if (!skill.loaded) loadSkill(skill);
		skillYaml.put(skill.name, skill.serializeYaml());
	}

	return skillYaml;
};

export const getAllClassYaml = (): YAMLObject => {
	const allClasses: ProClass[] = get(classes);
	allClasses.sort((a, b) => {
		if (a.name > b.name) return 1;
		if (a.name < b.name) return -1;
		return 0;
	});

	const classYaml = new YAMLObject();
	classYaml.put('loaded', false);
	for (const cls of allClasses) {
		if (!cls.loaded) loadClass(cls);
		classYaml.put(cls.name, cls.serializeYaml());
	}

	return classYaml;
};

export const saveAll = async () => {
	const skillYaml = getAllSkillYaml();
	const classYaml = getAllClassYaml();

	saveToFile('skills.yml', skillYaml.toString());
	saveToFile('classes.yml', classYaml.toString());
};

/**
 * Saves text data to a file locally
 */
const saveToFile = (file: string, data: string) => {
	const textFileAsBlob = new Blob([data], { type: 'text/plain;charset=utf-8' });

	const element         = document.createElement('a');
	element.href          = URL.createObjectURL(textFileAsBlob);
	element.download      = file;
	element.style.display = 'none';

	document.body.appendChild(element);
	element.click();
	document.body.removeChild(element);
};

export const saveError: Writable<ProSkill> = writable();