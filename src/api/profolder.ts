import type { ProSkill } from "./proskill";
import type { ProClass } from "./proclass";

export class ProFolder {
  public key = {};
  public isFolder = true;
  public open = false;
  public name = "Folder";
  public data: Array<ProFolder | ProClass | ProSkill> = [];
  public parent?: ProFolder;

  constructor(data?: Array<ProFolder | ProClass | ProSkill>) {
    if (data) {
      this.data = data;
      data.forEach(d => {
        if (d instanceof ProFolder && d.isFolder) d.updateParent(this);
      });
    }
  }

  public updateParent = (folder: ProFolder) => {
    this.parent = folder;
  };

  public addFolder = () => {
    const folder = new ProFolder();
    this.data.push(folder);
    this.data = this.data.sort((a, b) => a instanceof ProFolder ? -1 : 0);
    folder.updateParent(this);
  };

  public deleteFolder = (folder: ProFolder) => {
    this.data = this.data.filter(f => f != folder);
  };
}