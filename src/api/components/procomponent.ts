import type ComponentOption from "$api/options/options";
import { YAMLObject } from "../yaml";

export default abstract class ProComponent {
  type: "trigger" | "condition" | "mechanic" | "target";
  name: string;
  components: ProComponent[] = [];
  data: ComponentOption[] = [];
  isParent = true;

  protected constructor(type: "trigger" | "condition" | "mechanic" | "target",
                        name: string,
                        components?: ProComponent[],
                        data?: ComponentOption[]) {
    this.type = type;
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
  };

  public removeComponent = (comp: ProComponent) => {
    if (this.components.includes(comp)) {
      this.components.splice(this.components.indexOf(comp), 1);
      return;
    }

    for (const component of this.components) {
      if (component.contains(comp))
        component.removeComponent(comp);
    }
  };

  public toYamlObj(): YAMLObject {
    const data = new YAMLObject(this.name);
    data.put("type", this.type);

    return data;
  };

  public abstract getData(): YAMLObject;
}