import type { ComponentOption } from "$api/options/options";
import { YAMLObject }           from "../yaml";
import { Constructable }        from "$api/components/constructable";
import type { ComponentData }   from "$api/types";
import type { Writable }        from "svelte/store";
import { get, writable }        from "svelte/store";

export default abstract class ProComponent extends Constructable {
  type: "trigger" | "condition" | "mechanic" | "target";
  name: string;
  description: string;
  components: Writable<ProComponent[]> = writable([]);
  data: ComponentOption[]              = [];
  isParent                             = true;
  isDeprecated                         = false;
  id                                   = {};
  _defaultOpen                         = false;

  protected constructor(type: "trigger" | "condition" | "mechanic" | "target", data: ComponentData) {
    super();
    this.type        = type;
    this.name        = data.name;
    this.description = data.description ?? "";
    this.setComponents(data.components || []);
    this.data = data.data || [];
  }

  public setComponents = (comps: ProComponent[]) => {
    this.components.set([...comps]);
  };

  public contains = (comp: ProComponent): boolean => {
    const comps = get(this.components);
    if (comps.includes(comp)) return true;

    for (const component of comps) {
      if (component.contains(comp)) return true;
    }

    return false;
  };

  public addComponent = (comp: ProComponent, index = -1) => {
    const comps = get(this.components);
    if (index == -1)
      comps.push(comp);
    else {
      comps.splice(index, 0, comp);
    }
    this.setComponents(comps);
  };

  public removeComponent = (comp: ProComponent) => {
    const comps = get(this.components);
    if (comps.includes(comp)) {
      comps.splice(comps.indexOf(comp), 1);
      this.setComponents(comps);
      return;
    }

    for (const component of comps) {
      if (component.contains(comp))
        component.removeComponent(comp);
    }
  };

  public defaultOpen = () => {
    this._defaultOpen = true;
    return this;
  }

  public toYamlObj(): YAMLObject {
    const data = new YAMLObject(this.name);
    data.put("type", this.type);

    return data;
  };

  public abstract getData(): YAMLObject;

  public abstract getRawData(): YAMLObject;

  public abstract deserialize(yaml: YAMLObject): void;
}