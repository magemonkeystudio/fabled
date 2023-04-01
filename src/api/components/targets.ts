import ProComponent from "./procomponent";
import { YAMLObject } from "../yaml";
import type ComponentOption from "../options/options";

export default class ProTarget extends ProComponent {
  public constructor(name: string, components?: ProComponent[], data?: any[]) {
    super("target", name, components, data);
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
}

class LinearTarget extends ProTarget {
  public constructor() {
    super("Linear",
      [],
      []);
  }
}

export const targets = {
  LINEAR: LinearTarget
};