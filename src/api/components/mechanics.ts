import ProComponent           from "./procomponent";
import { YAMLObject }         from "../yaml";
import type ComponentOption   from "../options/options";
import type { ComponentData } from "$api/types";
import Registry               from "$api/components/registry";

export default class ProMechanic extends ProComponent {
  public constructor(data: ComponentData, isParent = false) {
    super("mechanic", data);
    super.isParent = isParent; // This should be false unless for specific mechanics like projectiles
  }

  public override toYamlObj(): YAMLObject {
    const parent: YAMLObject = super.toYamlObj();
    const data               = this.getData();
    if (data.getKeys().length > 0) parent.put("data", data);
    if (this.components.length > 0)
      parent.put("children", this.components);

    return parent;
  };

  public override getData(): YAMLObject {
    const data = new YAMLObject("data");

    this.data
      .filter(opt => opt.meetsRequirements(this))
      .forEach((opt: ComponentOption) => {
        const optData: { [key: string]: string } = opt.getData();
        Object.keys(optData).forEach(key => data.put(key, optData[key]));
      });

    return data;
  }

  public override getRawData(): YAMLObject {
    const data = new YAMLObject("data");

    this.data
      .forEach((opt: ComponentOption) => {
        const optData: { [key: string]: string } = opt.getData();
        Object.keys(optData).forEach(key => data.put(key, optData[key]));
      });

    return data;
  }

  deserialize(yaml: YAMLObject): void {
    const data = yaml.get<YAMLObject, YAMLObject>("data");

    if (data) this.data.forEach((opt: ComponentOption) => opt.deserialize(data));

    this.components = yaml.get<YAMLObject, ProComponent[]>("children", [], (obj) => Registry.deserializeComponents(obj));
  }
}