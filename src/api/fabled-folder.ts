import type FabledSkill from './fabled-skill';
import type FabledClass from './fabled-class';
import { removeFolder, rename, updateFolders } from '../data/store';

export default class FabledFolder {
	public location: 'local' | 'server'                          = 'local';
	public dataType                                              = 'folder';
	public key                                                   = {};
	public open                                                  = false;
	public name                                                  = 'Folder';
	public data: Array<FabledFolder | FabledClass | FabledSkill> = [];
	public parent?: FabledFolder;

	constructor(data?: Array<FabledFolder | FabledClass | FabledSkill>) {
		if (data) {
			this.data = data;
			data
				.forEach(d => {
					if (d instanceof FabledFolder) d.updateParent(this);
				});
		}
	}

	public updateParent = (parentFolder?: FabledFolder) => {
		this.parent = parentFolder;
	};

	public add = (data: FabledClass | FabledSkill | FabledFolder) => {
		if (this.data.includes(data)) return;
		if (data instanceof FabledFolder) {
			rename(data, this.data);
			removeFolder(data);
			data.parent = this;
		}
		this.data.push(data);
		this.sort();
	};

	public remove = (data: FabledClass | FabledSkill | FabledFolder) => {
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
		updateFolders();
	};

	public createFolder = () => {
		const folder = new FabledFolder();
		rename(folder, this.data);
		this.data.push(folder);
		this.sort();
		folder.updateParent(this);
	};

	public deleteFolder = (folder: FabledFolder) => {
		this.data = this.data.filter(f => f != folder);
	};

	public contains = (data: FabledFolder | FabledClass | FabledSkill): boolean => {
		if (this.data.includes(data)) return true;

		for (const folder of <FabledFolder[]>this.data.filter(d => d instanceof FabledFolder)) {
			if (folder.contains(data)) return true;
		}

		return false;
	};

	public getContainingFolder = (data: FabledFolder | FabledClass | FabledSkill): FabledFolder | undefined => {
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