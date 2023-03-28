import ProComponent from "./procomponent";
import MaterialSelect from "$api/options/materialselect";

export default class ProCondition extends ProComponent {
  public constructor(name: string, components?: ProComponent[], data?: any[]) {
    super(name, components, data);
  }

}

class BlockCondition extends ProCondition {
  public constructor() {
    super("Block",
      [],
      [
        new MaterialSelect()
      ]);
  }
}

export const conditions = {
  BLOCK: BlockCondition
};