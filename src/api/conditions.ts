import ProComponent from "$api/procomponent";
import type { SkillComponent } from "$api/types";

export default class ProCondition extends ProComponent {
  public constructor(name: string, components?: SkillComponent[], data?: any[]) {
    super(name, components, data);
  }

}

class BlockCondition extends ProCondition {
  public constructor() {
    super("Block");
  }
}

export const conditions = {
  BLOCK: BlockCondition
};