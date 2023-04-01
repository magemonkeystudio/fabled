import type { TriggerData } from "../types";
import ProComponent from "./procomponent";
import BlockSelect from "$api/options/blockselect";
import DropdownSelect from "../options/dropdownselect";
import { YAMLObject } from "../yaml";
import type ComponentOption from "../options/options";
import { conditions } from "./conditions";

export default class ProTrigger extends ProComponent {
  mana = false;
  cooldown = false;

  protected constructor(data: TriggerData) {
    super("trigger", data.name, data.components, data.data);
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
    });
  };

  public override toYamlObj(): YAMLObject {
    const parent: YAMLObject = super.toYamlObj();
    parent.put("data", this.getData());
    if (this.components.length > 0)
      parent.put("children", this.components);

    return parent;
  };

  public override getData(): YAMLObject {
    const data = new YAMLObject("data");
    data.put("mana", this.mana);
    data.put("cooldown", this.cooldown);

    this.data.forEach((opt: ComponentOption) => {
      const optData: { [key: string]: string } = opt.getData();
      Object.keys(optData).forEach(key => data.put(key, optData[key]));
    });

    return data;
  }
}

class BlockBreakTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Block Break",
      data: [
        new BlockSelect()
      ],
      components: [
        new conditions.BLOCK()
      ]
    });
  }
}

class BlockPlaceTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Block Place",
      data: [
        new BlockSelect()
      ]
    });
  }
}

class CastTrigger extends ProTrigger {
  public constructor() {
    super({ name: "Cast" });
  }
}

class CleanupTrigger extends ProTrigger {
  public constructor() {
    super({ name: "Cleanup" });
  }
}

class CrouchTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Crouch",
      data: [
        new DropdownSelect("crouching", ["Start Crouching", "Stop Crouching", "Both"])
      ]
    });
  }
}

class DeathTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Death",
      data: []
    });
  }
}

class DropItemTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Drop Item",
      data: []
    });
  }
}

class EnvironmentDamageTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Environment Damage",
      data: []
    });
  }
}

class FishingTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Fishing",
      data: []
    });
  }
}

class FishingBiteTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Fishing Bite",
      data: []
    });
  }
}

class FishingFailTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Fishing Fail",
      data: []
    });
  }
}

class FishingGrabTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Fishing Grab",
      data: []
    });
  }
}

class FishingGroundTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Fishing Ground",
      data: []
    });
  }
}

class FishingReelTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Fishing Reel",
      data: []
    });
  }
}

class InitializeTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Initialize",
      data: []
    });
  }
}

class ItemSwapTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Item Swap",
      data: []
    });
  }
}

class KillTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Kill",
      data: []
    });
  }
}

class LandTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Land",
      data: []
    });
  }
}

class LaunchTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Launch",
      data: []
    });
  }
}

class LeftClickTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Left Click",
      data: []
    });
  }
}

class MoveTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Move",
      data: []
    });
  }
}

class PhysicalDamageTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Physical Damage",
      data: []
    });
  }
}

class RightClickTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Right Click",
      data: []
    });
  }
}

class SkillCastTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Skill Cast",
      data: []
    });
  }
}

class SkillDamageTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Skill Damage",
      data: []
    });
  }
}

class TookPhysicalTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Took Physical Damage",
      data: []
    });
  }
}

class TookSkillTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Took Skill Damage",
      data: []
    });
  }
}

export const triggers = {
  BLOCK_BREAK: BlockBreakTrigger,
  BLOCK_PLACE: BlockPlaceTrigger,
  CAST: CastTrigger,
  CLEANUP: CleanupTrigger,
  CROUCH: CrouchTrigger,
  DEATH: DeathTrigger,
  DROP_ITEM: DropItemTrigger,
  ENV_DAMAGE: EnvironmentDamageTrigger,
  FISHING: FishingTrigger,
  FISHING_BITE: FishingBiteTrigger,
  FISHING_FAIL: FishingFailTrigger,
  FISHING_GRAB: FishingGrabTrigger,
  FISHING_GROUND: FishingGroundTrigger,
  FISHING_REEL: FishingReelTrigger,
  INIT: InitializeTrigger,
  ITEM_SWAP: ItemSwapTrigger,
  KILL: KillTrigger,
  LAND: LandTrigger,
  LAUNCH: LaunchTrigger,
  LEFT_CLICK: LeftClickTrigger,
  RIGHT_CLICK: RightClickTrigger,
  MOVE: MoveTrigger,
  PHYS_DAMAGE: PhysicalDamageTrigger,
  SKILL_DAMAGE: SkillDamageTrigger,
  SKILL_CAST: SkillCastTrigger,
  TOOK_PHYS: TookPhysicalTrigger,
  TOOK_SKILL: TookSkillTrigger
};