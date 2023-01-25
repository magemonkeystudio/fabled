import type { SkillComponent } from "./types";

export default class ProComponent {
  name: string;
  components: ProComponent[] = [];
  data: any[] = [];

  constructor(name: string, components?: ProComponent[], data?: any[]) {
    this.name = name;
    this.components = components || [];
    this.data = data || [];
  }

  public contains = (comp: ProComponent): boolean => {
    if (this.components.includes(comp)) return true;

    for (const component of this.components) {
      if (component.contains(comp)) return true;
    }

    return false;
  }

  public removeComponent = (comp: ProComponent) => {
    if(this.components.includes(comp)) {
      this.components.splice(this.components.indexOf(comp), 1);
      return;
    }

    for (const component of this.components) {
      if (component.contains(comp))
        component.removeComponent(comp);
    }
  }
}