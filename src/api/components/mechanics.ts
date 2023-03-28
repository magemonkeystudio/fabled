import ProComponent from "./procomponent";
import MaterialSelect from "$api/options/materialselect";

export default class ProMechanic extends ProComponent {
  public constructor(name: string, components?: ProComponent[], data?: any[]) {
    super(name, components, data);
  }

}

class LaunchTarget extends ProMechanic {
  public constructor() {
    super("Launch",
      [],
      []);
  }
}

export const mechanics = {
  LAUNCH: LaunchTarget
};