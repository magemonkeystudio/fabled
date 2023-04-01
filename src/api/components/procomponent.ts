import type ComponentOption from "$api/options/options";
import { YAMLObject } from "../yaml";
import ProTrigger, { Triggers } from "$api/components/triggers";

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

  public static deserialize = (yaml: YAMLObject): ProComponent[] => {
    if (!yaml || !(yaml instanceof YAMLObject)) return [];
    const comps: ProComponent[] = [];

    const keys: string[] = yaml.getKeys();
    for (const key of keys) {
      let comp: ProComponent | undefined = undefined;
      const data = yaml.get<YAMLObject, YAMLObject>(key);
      const type = data.get("type");

      if (type === "trigger") {
        const trigger: ProTrigger | undefined = Triggers.byName(key.split("-")[0]);
        if (trigger) {
          comp = trigger;
          // TODO Further processing..
        }
      } else if (type === "condition") {

      } else if (type === "mechanic") {

      } else if (type === "target") {

      }

      if (comp) {
        comps.push(comp);
      }
    }

    return comps;
  };
}