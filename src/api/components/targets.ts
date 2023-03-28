import ProComponent from "./procomponent";
import MaterialSelect from "$api/options/materialselect";

export default class ProTarget extends ProComponent {
  public constructor(name: string, components?: ProComponent[], data?: any[]) {
    super(name, components, data);
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