import ProComponent from "./procomponent";
import { YAMLObject } from "../yaml";
import type ComponentOption from "../options/options";

export default class ProMechanic extends ProComponent {
  public constructor(name: string, components?: ProComponent[], data?: any[]) {
    super("mechanic", name, components, data);
    super.isParent = false; // This should be false unless for specific mechanics like projectiles
  }

  public override toYamlObj(): YAMLObject {
    const parent: YAMLObject = super.toYamlObj();
    parent.put("data", this.getData());
    if (this.components.length > 0)
      parent.put("children", this.components);

    return parent;
  };

  public override getData(): YAMLObject {
    const data = new YAMLObject("data");

    this.data.forEach((opt: ComponentOption) => {
      const optData: { [key: string]: string } = opt.getData();
      Object.keys(optData).forEach(key => data.put(key, optData[key]));
    });

    return data;
  }

  deserialize(yaml: YAMLObject): void {
    const data = yaml.get<YAMLObject, YAMLObject>("data");

    this.data.forEach((opt: ComponentOption) => opt.deserialize(data));
    this.components = yaml.get<YAMLObject, ProComponent[]>("children", [], (obj) => YAMLObject.deserializeComponent(obj));
  }
}