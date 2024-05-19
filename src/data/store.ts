import type { Readable, Writable }                                                from 'svelte/store';
import { derived, get, writable }                                                 from 'svelte/store';
import FabledClass                                                                from '$api/fabled-class';
import FabledSkill                                                                from '$api/fabled-skill';
import FabledFolder                                                               from '$api/fabled-folder';
import {
	classes,
	classFolders,
	deleteClass,
	deleteClassFolder,
	loadClass,
	loadClasses,
	loadClassText,
	refreshClasses,
	refreshClassFolders
}                                                                                 from './class-store';
import { localStore }                                                                         from '$api/api';
import { attributes, deleteAttribute, loadAttributes, loadAttributesText, refreshAttributes } from './attribute-store';
import {
	deleteSkill,
	deleteSkillFolder,
	isSaving,
	loadSkill,
	loadSkills,
	loadSkillText,
	refreshSkillFolders,
	refreshSkills,
	skillFolders,
	skills
}                                                                                             from './skill-store';
import type ProComponent                                                       from '$api/components/procomponent';
import type { MultiAttributeYamlData, MultiClassYamlData, MultiSkillYamlData } from '$api/types';
import { socketService }                                                       from '$api/socket/socket-connector';
import YAML                                                                       from 'yaml';
import FabledAttribute                                                            from '$api/fabled-attribute';
import { parseYaml }                                                              from '$api/yaml';
import { Tab }                                                                    from '$api/tab';

export const active: Writable<FabledClass | FabledSkill | FabledAttribute | undefined>      = writable(undefined);
export const activeType: Readable<'class' | 'skill' | 'attribute' | ''>                     = derived(
	active,
	$active => {
		if ($active?.dataType === 'class') {
			return 'class';
		} else if ($active?.dataType === 'skill') {
			return 'skill';
		} else if ($active?.dataType === 'attribute') {
			return 'attribute';
		} else {
			return '';
		}
	}
);
export const dragging: Writable<FabledClass | FabledSkill | FabledAttribute | FabledFolder> = writable();
export const draggingComponent: Writable<ProComponent | undefined>                          = writable();
export const showSidebar: Writable<boolean>                                                 = localStore('sidebarOpen', true);
export const sidebarOpen: Writable<boolean>                                                 = writable(true);
export const shownTab: Writable<Tab>                                                        = writable(Tab.CLASSES);
export const importing: Writable<boolean>                                                   = writable(false);

export const updateSidebar = () => {
	if (!get(showSidebar)) return;
	switch (get(activeType)) {
		case 'class': {
			refreshClasses();
			break;
		}
		case 'skill': {
			refreshSkills();
			break;
		}
		case 'attribute': {
			refreshAttributes();
			break;
		}
	}
	updateFolders();
};
export const toggleSidebar = () => showSidebar.set(!get(showSidebar));
export const closeSidebar  = () => showSidebar.set(false);
export const setImporting  = (bool: boolean) => importing.set(bool);

export const rename = (folder: FabledFolder, folders: Array<FabledFolder | FabledClass | FabledSkill | FabledAttribute>) => {
	const origName = folder.name;
	let num        = 1;
	while (folders.filter(f => f instanceof FabledFolder && f.name == folder.name).length >= 1) {
		folder.name = origName + ' (' + (num++) + ')';
	}
};

export const deleteFolder = (folder: FabledFolder) => {
	if (folder.parent) {
		folder.parent.deleteFolder(folder);
		updateFolders();
	} else {
		switch (get(shownTab)) {
			case Tab.CLASSES: {
				deleteClassFolder(folder, () => false);
				break;
			}
			case Tab.SKILLS: {
				deleteSkillFolder(folder, () => false);
				break;
			}
		}
	}
};

export const deleteProData = (data: FabledClass | FabledSkill | FabledAttribute | undefined) => {
	if (!data) return;

	getFolder(data)?.remove(data);
	if (data instanceof FabledClass) deleteClass(data);
	else if (data instanceof FabledSkill) deleteSkill(data);
	else if (data instanceof FabledAttribute) deleteAttribute(data);
	updateFolders();
};

export const updateFolders = () => {
	if (!get(showSidebar)) return;
	switch (get(shownTab)) {
		case Tab.CLASSES: {
			refreshClassFolders();
			break;
		}
		case Tab.SKILLS: {
			refreshSkillFolders();
			break;
		}
	}
};

export const removeFolder = (folder: FabledFolder) => {
	const classF = get(classFolders);
	const skillF = get(skillFolders);
	if (classF.includes(folder)) classFolders.set(classF.filter(f => f != folder));
	if (skillF.includes(folder)) skillFolders.set(skillF.filter(f => f != folder));
};

export const getFolder = (data?: FabledFolder | FabledClass | FabledSkill | FabledAttribute): (FabledFolder | undefined) => {
	if (!data) return undefined;

	if (data instanceof FabledFolder) return data.parent;
	const folders: FabledFolder[] = data instanceof FabledClass ? get(classFolders) : get(skillFolders);

	for (const folder of folders) {
		const containingFolder = folder.getContainingFolder(data);
		if (!containingFolder) continue;

		return containingFolder;
	}

	return undefined;
};

const skillFileRegex        = /['"]?(components|combo)['"]?:/;
/**
 * Loads an individual skill or class file
 * @param e ProgressEvent
 */
export const loadIndividual = async (e: ProgressEvent<FileReader>) => {
	const text: string = <string>e.target?.result;
	if (!text) return;

	if (skillFileRegex.test(text)) {
		await loadSkillText(text);
	} else {
		loadClassText(text);
	}
	(<HTMLElement>document.activeElement).blur();
};

export const loadRaw = async (text: string, fromServer: boolean = false) => {
	if (!text) return;

	if (text.indexOf('global:') >= 0) {
		loadAttributesText(text);
	} else if (skillFileRegex.test(text)) {
		await loadSkillText(text.replace('loaded: false\n', ''), fromServer);
	} else {
		loadClassText(text.replace('loaded: false\n', ''), fromServer);
	}
};

export const loadFile = (file: File) => {
	const reader = new FileReader();
	if (file.name.indexOf('skills') == 0) {
		reader.onload = loadSkills;
	} else if (file.name.indexOf('classes') == 0) {
		reader.onload = loadClasses;
	} else if (file.name.indexOf('attributes') == 0) {
		reader.onload = loadAttributes;
	} else {
		reader.onload = loadIndividual;
	}
	reader.readAsText(file);
};

export const saveData = (data?: FabledSkill | FabledClass | FabledAttribute) => {
	const act = data || get(active);
	if (!act) return;
	if (act instanceof FabledAttribute) {
		// If it's an attribute, we should export the whole attributes.yml
		saveAttributes().then(() => {
		});
		return;
	}

	saveToFile(act.name + '.yml', YAML.stringify({ [act.name]: act.serializeYaml() }, { lineWidth: 0 }));
};

export const getAttributeYaml = async () => {
	let text = '';
	for (const line of (await fetch('https://raw.githubusercontent.com/promcteam/fabled/dev/src/main/resources/attributes.yml').then(r => r.text())).split('\n')) {
		if (line.startsWith('#') || line.length === 0) {
			text = text + line + '\n';
		} else {
			break;
		}
	}

	const attributeYaml: MultiAttributeYamlData = {};
	for (const attr of get(attributes)) {
		attributeYaml[attr.name] = attr.serializeYaml();
	}
	const yaml = YAML.stringify(attributeYaml, { lineWidth: 0 });

	text += yaml;
	return text;
};

export const saveAttributes = async () => {
	saveToFile('attributes.yml', await getAttributeYaml());
};

export const saveDataToServer = async (data?: FabledSkill | FabledClass | FabledAttribute) => {
	const act = data || get(active);
	if (!act) return false;

	if (act instanceof FabledAttribute) {
		return await socketService.saveAttributesToServer(await getAttributeYaml());
	}

	const yaml = YAML.stringify({ [act.name]: act.serializeYaml() });

	const folder = getFolder(act);
	let path     = '';
	if (folder) {
		path = folder.name + '/';
	}

	if (act instanceof FabledSkill) {
		return await socketService.saveSkillToServer(path + act.name, yaml);
	} else if (act instanceof FabledClass) {
		return await socketService.saveClassToServer(path + act.name, yaml);
	}
};

export const getAllSkillYaml = async (): Promise<MultiSkillYamlData> => {
	const allSkills: FabledSkill[] = get(skills);
	allSkills.sort((a, b) => {
		if (a.name > b.name) return 1;
		if (a.name < b.name) return -1;
		return 0;
	});

	const skillYaml: MultiSkillYamlData = {};
	skillYaml.loaded                    = false;

	const loadedPromise = allSkills.map(async skill => {
		if (!skill.loaded) await loadSkill(skill);
		skillYaml[skill.name] = skill.serializeYaml();
	});
	await Promise.all(loadedPromise);

	return skillYaml;
};

export const getAllClassYaml = async (): Promise<MultiClassYamlData> => {
	const allClasses: FabledClass[] = get(classes);
	allClasses.sort((a, b) => {
		if (a.name > b.name) return 1;
		if (a.name < b.name) return -1;
		return 0;
	});

	const classYaml: MultiClassYamlData = {};
	classYaml.loaded                    = false;
	for (const cls of allClasses) {
		if (!cls.loaded) await loadClass(cls);
		classYaml[cls.name] = cls.serializeYaml();
	}

	return classYaml;
};

export const saveAllToServer = async () => {
	const skillYaml = await getAllSkillYaml();
	const classYaml = await getAllClassYaml();
	const attributeYaml = await getAttributeYaml();

	return await socketService.exportAll(classYaml.toString(), skillYaml.toString(), attributeYaml.toString());
};

export const saveAll = async () => {
	isSaving.set(true);
	const skillYaml = await getAllSkillYaml();
	const classYaml = await getAllClassYaml();

	saveToFile('skills.yml', YAML.stringify(skillYaml, { lineWidth: 0 }));
	saveToFile('classes.yml', YAML.stringify(classYaml, { lineWidth: 0 }));
	await saveAttributes();
	isSaving.set(false);
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

export const saveError: Writable<FabledSkill | undefined> = writable();