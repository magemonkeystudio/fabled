import { get }                     from 'svelte/store';
import { Tab }                     from '$api/tab';
import FabledClass, { classStore } from './class-store.svelte';
import FabledSkill, { skillStore } from './skill-store.svelte';
import FabledAttribute             from '$api/fabled-attribute.svelte';
import { shownTab, showSidebar }   from './store';

export interface FolderProperties {
	location: 'local' | 'server';
	dataType: 'folder';
	name: string;
	data: (string | FolderProperties)[];
	open: boolean;
}

export class FabledFolder {
	public key = {};

	public location: 'local' | 'server' = 'local';
	public dataType                     = 'folder' as const;
	public open                         = $state(false);
	public name                         = $state('Folder');

	public data: Array<FabledFolder | FabledClass | FabledSkill | FabledAttribute> = $state([]);
	public parent?: FabledFolder;

	constructor(data?: Array<FabledFolder | FabledClass | FabledSkill | FabledAttribute>) {
		if (data) {
			this.data = data;
			data
				.forEach(d => {
					if (d instanceof FabledFolder) d.updateParent(this);
				});
		}
	}

	public toJSON = (): FolderProperties => {
		return {
			location: this.location,
			dataType: this.dataType,
			name:     this.name,
			data:     this.data
									.map(d => {
										if (d instanceof FabledFolder) return d.toJSON();

										return d.name;
									})
									.filter(d => !!d),
			open:     this.open
		};
	};

	public updateParent = (parentFolder?: FabledFolder) => {
		this.parent = parentFolder;
	};

	public add = (data: FabledClass | FabledSkill | FabledFolder | FabledAttribute) => {
		if (this.data.includes(data)) return;
		if (data instanceof FabledFolder) {
			folderStore.rename(data, this.data);
			folderStore.removeFolder(data);
			data.parent = this;
		}
		this.data.push(data);
		this.sort();
	};

	public remove = (data: FabledClass | FabledSkill | FabledFolder | FabledAttribute) => {
		this.data = this.data.filter(d => d != data);
		this.sort();
	};

	public sort = () => {
		this.data = this.data.sort((a, b) => {
			let folder = 0;
			if (a instanceof FabledFolder) {
				if (!(b instanceof FabledFolder))
					folder = -100;
			}
			return a.name.localeCompare(b.name) + folder;
		});
		folderStore.updateFolders();
	};

	public createFolder = () => {
		const folder = new FabledFolder();
		folderStore.rename(folder, this.data);
		this.data.push(folder);
		this.sort();
		folder.updateParent(this);
	};

	public deleteFolder = (folder: FabledFolder) => {
		this.data = this.data.filter(f => f != folder);
	};

	/**
	 * Gets all the non-folder data in this folder and sub folders
	 */
	public getAllData = (): Array<FabledClass | FabledSkill | FabledAttribute> => {
		const data: Array<FabledClass | FabledSkill | FabledAttribute> = [];
		for (const d of this.data) {
			if (d instanceof FabledFolder) data.push(...d.getAllData());
			else data.push(d);
		}
		return data;
	};

	/**
	 * Gets all the classes in this folder and sub folders
	 */
	public getAllClasses = (): FabledClass[] => {
		const data: FabledClass[] = [];
		for (const d of this.data) {
			if (d instanceof FabledFolder) data.push(...d.getAllClasses());
			else if (d instanceof FabledClass) data.push(d);
		}
		return data;
	};

	/**
	 * Gets all the skills in this folder and sub folders
	 */
	public getAllSkills = (): FabledSkill[] => {
		const data: FabledSkill[] = [];
		for (const d of this.data) {
			if (d instanceof FabledFolder) data.push(...d.getAllSkills());
			else if (d instanceof FabledSkill) data.push(d);
		}
		return data;
	};

	public contains = (data: FabledFolder | FabledClass | FabledSkill): boolean => {
		if (this.data.includes(data)) return true;

		for (const folder of <FabledFolder[]>this.data.filter(d => d instanceof FabledFolder)) {
			if (folder.contains(data)) return true;
		}

		return false;
	};

	public getContainingFolder = (data: FabledFolder | FabledClass | FabledSkill | FabledAttribute): FabledFolder | undefined => {
		if (this.data.includes(data)) return this;

		for (const folder of <FabledFolder[]>this.data.filter(d => d instanceof FabledFolder)) {
			if (folder.data.includes(data)) return folder;
			else {
				const subFolder = folder.getContainingFolder(data);
				if (!subFolder) continue;
				return subFolder;
			}
		}

		return undefined;
	};

	public getSubfolder = (name: string, recursive = false): FabledFolder | undefined => {
		for (const folder of <FabledFolder[]>this.data.filter(d => d instanceof FabledFolder)) {
			if (folder.name === name) return folder;
			else if (recursive) {
				const subFolder = folder.getSubfolder(name);
				if (!subFolder) continue;
				return subFolder;
			}
		}

		return undefined;
	};
}

class FolderStoreSvelte {
	rename = (folder: FabledFolder, folders: Array<FabledFolder | FabledClass | FabledSkill | FabledAttribute>) => {
		const origName = folder.name;
		let num        = 1;
		while (folders.filter(f => f instanceof FabledFolder && f.name == folder.name).length >= 1) {
			folder.name = origName + ' (' + (num++) + ')';
		}
	};

	deleteFolder = (folder: FabledFolder) => {
		if (folder.parent) {
			folder.parent.deleteFolder(folder);
			this.updateFolders();
		} else {
			switch (get(shownTab)) {
				case Tab.CLASSES: {
					classStore.deleteClassFolder(folder, () => false);
					break;
				}
				case Tab.SKILLS: {
					skillStore.deleteSkillFolder(folder, () => false);
					break;
				}
			}
		}
	};

	updateFolders = () => {
		if (!get(showSidebar)) return;
		switch (get(shownTab)) {
			case Tab.CLASSES: {
				classStore.refreshClassFolders();
				break;
			}
			case Tab.SKILLS: {
				skillStore.refreshSkillFolders();
				break;
			}
		}
	};

	removeFolder = (folder: FabledFolder) => {
		const classF = get(classStore.classFolders);
		const skillF = get(skillStore.skillFolders);
		if (classF.includes(folder)) classStore.classFolders.set(classF.filter(f => f != folder));
		if (skillF.includes(folder)) skillStore.skillFolders.set(skillF.filter(f => f != folder));
	};

	getFolder = (data?: FabledFolder | FabledClass | FabledSkill | FabledAttribute): (FabledFolder | undefined) => {
		if (!data) return undefined;

		if (data instanceof FabledFolder) return data.parent;
		const folders: FabledFolder[] = data instanceof FabledClass ? get(classStore.classFolders) : get(skillStore.skillFolders);

		for (const folder of folders) {
			const containingFolder = folder.getContainingFolder(data);
			if (!containingFolder) continue;

			return containingFolder;
		}

		return undefined;
	};
}

export const folderStore = new FolderStoreSvelte();