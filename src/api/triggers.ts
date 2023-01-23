import type { TriggerData } from "./types";
import ProComponent from "./procomponent";
import BlockSelect from "$api/options/blockselect";
import { conditions } from "$api/conditions";

export default class ProTrigger extends ProComponent{
  mana = false;
  cooldown = false;

  public constructor(data: TriggerData) {
    super(data.name, data.components, data.data);
    this.mana = data.mana || false;
    this.cooldown = data.cooldown || false;
  }

  public clone = (): ProTrigger => {
    return new ProTrigger({
      name: this.name,
      components: [...this.components],
      mana: this.mana,
      cooldown: this.cooldown,
      data: [...this.data]
    })
  }
}

class BlockBreakTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Block Break",
      components: [new conditions.BLOCK()],
      data: [
        new BlockSelect()
      ]
    });
  }
}

export const triggers = {
  BLOCK_BREAK: BlockBreakTrigger
}