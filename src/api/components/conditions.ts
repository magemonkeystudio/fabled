import ProComponent from "./procomponent";
import MaterialSelect from "$api/options/materialselect";
import { YAMLObject } from "../yaml";
import type ComponentOption from "../options/options";
import DropdownSelect from "../options/dropdownselect";

export default class ProCondition extends ProComponent {
  public constructor(name: string, components?: ProComponent[], data?: any[]) {
    super("condition", name, components, data);
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

class BlockCondition extends ProCondition {
  public constructor() {
    super("Block",
      [],
      [
        new DropdownSelect("standing", ["On Block", "Not On Block", "In Block", "Not In Block"]),
        new MaterialSelect()
      ]);
  }
}

export const conditions = {
  BLOCK: BlockCondition
};