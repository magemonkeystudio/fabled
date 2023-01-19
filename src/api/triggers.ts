import type { SkillComponent, TriggerData } from "./types";
import ProComponent from "./procomponent";

export default class ProTrigger extends ProComponent{
  mana = false;
  cooldown = false;
  data: any[] = [];

  public constructor(data: TriggerData) {
    super(data.name, data.components);
    this.mana = data.mana || false;
    this.cooldown = data.cooldown || false;
    this.data = data.data || [];
  }
}

export const triggers = {
  BLOCK_BREAK: new ProTrigger({
    name: "Block Break",
    data: [
      // new BlockSelect()
    ]
  })
}