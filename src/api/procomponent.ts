import type { SkillComponent } from "./types";

export default class ProComponent {
  name: string;
  components: SkillComponent[] = [];
  data: any[] = [];

  constructor(name: string, components?: SkillComponent[], data?: any[]) {
    this.name = name;
    this.components = components || [];
    this.data = data || [];
  }
}