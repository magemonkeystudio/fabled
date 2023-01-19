import type { SkillComponent } from "./types";

export default class ProComponent {
  name: string;
    components: SkillComponent[] = [];

  constructor(name: string, components?: SkillComponent[]) {
    this.name = name;
    this.components = components || [];
  }
}