import type { Readable, Writable }                                             from 'svelte/store';
import { derived, get, writable }                                              from 'svelte/store';
import { localStore }                                                          from '$api/api';
import { attributeStore }                                                      from './attribute-store';
import type ProComponent                                                       from '$api/components/procomponent';
import type { MultiAttributeYamlData, MultiClassYamlData, MultiSkillYamlData } from '$api/types';
import { socketService }                                                       from '$api/socket/socket-connector';
import YAML                                                                    from 'yaml';
import FabledAttribute                                                         from '$api/fabled-attribute';
import { Tab }                                                                 from '$api/tab';
import FabledSkill, { skillStore }                                             from './skill-store';
import FabledClass, { classStore }                                             from './class-store';
import { FabledFolder, folderStore }                                           from './folder-store';

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
			classStore.refreshClasses();
			break;
		}
		case 'skill': {
			skillStore.refreshSkills();
			break;
		}
		case 'attribute': {
			attributeStore.refreshAttributes();
			break;
		}
	}
	folderStore.updateFolders();
};
export const toggleSidebar = () => showSidebar.set(!get(showSidebar));
export const closeSidebar  = () => showSidebar.set(false);
export const setImporting  = (bool: boolean) => importing.set(bool);

export const deleteProData = (data: FabledClass | FabledSkill | FabledAttribute | undefined) => {
	if (!data) return;

	folderStore.getFolder(data)?.remove(data);
	if (data instanceof FabledClass) classStore.deleteClass(data);
	else if (data instanceof FabledSkill) skillStore.deleteSkill(data);
	else if (data instanceof FabledAttribute) attributeStore.deleteAttribute(data);
	folderStore.updateFolders();
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
		await skillStore.loadSkillText(text);
	} else {
		classStore.loadClassText(text);
	}
	(<HTMLElement>document.activeElement).blur();
};

export const loadRaw = async (text: string, fromServer: boolean = false) => {
	if (!text) return;

	if (text.indexOf('global:') >= 0) {
		attributeStore.loadAttributesText(text);
	} else if (skillFileRegex.test(text)) {
		await skillStore.loadSkillText(text.replace('loaded: false\n', ''), fromServer);
	} else {
		classStore.loadClassText(text.replace('loaded: false\n', ''), fromServer);
	}
};

export const loadFile = (file: File) => {
	const reader = new FileReader();
	if (file.name.indexOf('skills') == 0) {
		reader.onload = skillStore.loadSkills;
	} else if (file.name.indexOf('classes') == 0) {
		reader.onload = classStore.loadClasses;
	} else if (file.name.indexOf('attributes') == 0) {
		reader.onload = attributeStore.loadAttributes;
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

	saveToFile(act.name + '.yml', YAML.stringify({ [act.name]: act.serializeYaml() }, {
		lineWidth:             0,
		aliasDuplicateObjects: false
	}));
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
	for (const attr of get(attributeStore.attributes)) {
		attributeYaml[attr.name] = attr.serializeYaml();
	}
	const yaml = YAML.stringify(attributeYaml, { lineWidth: 0, aliasDuplicateObjects: false });

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

	const yaml = YAML.stringify({ [act.name]: act.serializeYaml() }, { lineWidth: 0, aliasDuplicateObjects: false });

	const folder = folderStore.getFolder(act);
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
	const allSkills: FabledSkill[] = get(skillStore.skills);
	allSkills.sort((a, b) => {
		if (a.name > b.name) return 1;
		if (a.name < b.name) return -1;
		return 0;
	});

	const skillYaml: MultiSkillYamlData = {};
	skillYaml.loaded                    = false;

	const loadedPromise = allSkills.map(async skill => {
		if (!skill.loaded) await skillStore.loadSkill(skill);
		skillYaml[skill.name] = skill.serializeYaml();
	});
	await Promise.all(loadedPromise);

	return skillYaml;
};

export const getAllClassYaml = async (): Promise<MultiClassYamlData> => {
	const allClasses: FabledClass[] = get(classStore.classes);
	allClasses.sort((a, b) => {
		if (a.name > b.name) return 1;
		if (a.name < b.name) return -1;
		return 0;
	});

	const classYaml: MultiClassYamlData = {};
	classYaml.loaded                    = false;
	for (const cls of allClasses) {
		if (!cls.loaded) await classStore.loadClass(cls);
		classYaml[cls.name] = cls.serializeYaml();
	}

	return classYaml;
};

export const saveAllToServer = async () => {
	const skillYaml     = await getAllSkillYaml();
	const classYaml     = await getAllClassYaml();
	const attributeYaml = await getAttributeYaml();

	return await socketService.exportAll(
		YAML.stringify(classYaml, { lineWidth: 0, aliasDuplicateObjects: false }),
		YAML.stringify(skillYaml, { lineWidth: 0, aliasDuplicateObjects: false }),
		attributeYaml.toString());
};

export const saveAll = async () => {
	skillStore.isSaving.set(true);
	const skillYaml = await getAllSkillYaml();
	const classYaml = await getAllClassYaml();

	saveToFile('skills.yml', YAML.stringify(skillYaml, { lineWidth: 0, aliasDuplicateObjects: false }));
	saveToFile('classes.yml', YAML.stringify(classYaml, { lineWidth: 0, aliasDuplicateObjects: false }));
	await saveAttributes();
	skillStore.isSaving.set(false);
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

export const saveError: Writable<{ name: string, acknowledged: boolean } | undefined> = writable();