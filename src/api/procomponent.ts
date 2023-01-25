import type ComponentOption from "$api/options/options";

export default class ProComponent {
  name: string;
  components: ProComponent[] = [];
  data: ComponentOption[] = [];

  constructor(name: string, components?: ProComponent[], data?: ComponentOption[]) {
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