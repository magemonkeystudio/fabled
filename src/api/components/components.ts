import ProMechanic from "$api/components/mechanics";
import BlockSelect from "$api/options/blockselect";
import ProCondition from "$api/components/conditions";
import DropdownSelect from "$api/options/dropdownselect";
import ProTrigger from "$api/components/triggers";
import ProTarget from "$api/components/targets";
import MaterialSelect from "$api/options/materialselect";

// TRIGGERS

class BlockBreakTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Block Break",
      data: [new BlockSelect()]
    });
  }

  public static override new = () => new this();
}

class BlockPlaceTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Block Place",
      data: [new BlockSelect()]
    });
  }

  public static override new = () => new this();
}

class CastTrigger extends ProTrigger {
  public constructor() {
    super({ name: "Cast" });
  }

  public static override new = () => new this();
}

class CleanupTrigger extends ProTrigger {
  public constructor() {
    super({ name: "Cleanup" });
  }

  public static override new = () => new this();
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

  public static override new = () => new this();
}

class DeathTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Death",
      data: []
    });
  }

  public static override new = () => new this();
}

class DropItemTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Drop Item",
      data: []
    });
  }

  public static override new = () => new this();
}

class EnvironmentDamageTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Environment Damage",
      data: []
    });
  }

  public static override new = () => new this();
}

class FishingTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Fishing",
      data: []
    });
  }

  public static override new = () => new this();
}

class FishingBiteTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Fishing Bite",
      data: []
    });
  }

  public static override new = () => new this();
}

class FishingFailTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Fishing Fail",
      data: []
    });
  }

  public static override new = () => new this();
}

class FishingGrabTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Fishing Grab",
      data: []
    });
  }

  public static override new = () => new this();
}

class FishingGroundTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Fishing Ground",
      data: []
    });
  }

  public static override new = () => new this();
}

class FishingReelTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Fishing Reel",
      data: []
    });
  }

  public static override new = () => new this();
}

class InitializeTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Initialize",
      data: []
    });
  }

  public static override new = () => new this();
}

class ItemSwapTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Item Swap",
      data: []
    });
  }

  public static override new = () => new this();
}

class KillTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Kill",
      data: []
    });
  }

  public static override new = () => new this();
}

class LandTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Land",
      data: []
    });
  }

  public static override new = () => new this();
}

class LaunchTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Launch",
      data: []
    });
  }

  public static override new = () => new this();
}

class LeftClickTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Left Click",
      data: []
    });
  }

  public static override new = () => new this();
}

class MoveTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Move",
      data: []
    });
  }

  public static override new = () => new this();
}

class PhysicalDamageTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Physical Damage",
      data: []
    });
  }

  public static override new = () => new this();
}

class RightClickTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Right Click",
      data: []
    });
  }

  public static override new = () => new this();
}

class SkillCastTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Skill Cast",
      data: []
    });
  }

  public static override new = () => new this();
}

class SkillDamageTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Skill Damage",
      data: []
    });
  }

  public static override new = () => new this();
}

class TookPhysicalTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Took Physical Damage",
      data: []
    });
  }

  public static override new = () => new this();
}

class TookSkillTrigger extends ProTrigger {
  public constructor() {
    super({
      name: "Took Skill Damage",
      data: []
    });
  }

  public static override new = () => new this();
}

export class Triggers {
  public static BLOCK_BREAK = BlockBreakTrigger;
  public static BLOCK_PLACE = BlockPlaceTrigger;
  public static CAST = CastTrigger;
  public static CLEANUP = CleanupTrigger;
  public static CROUCH = CrouchTrigger;
  public static DEATH = DeathTrigger;
  public static DROP_ITEM = DropItemTrigger;
  public static ENV_DAMAGE = EnvironmentDamageTrigger;
  public static FISHING = FishingTrigger;
  public static FISHING_BITE = FishingBiteTrigger;
  public static FISHING_FAIL = FishingFailTrigger;
  public static FISHING_GRAB = FishingGrabTrigger;
  public static FISHING_GROUND = FishingGroundTrigger;
  public static FISHING_REEL = FishingReelTrigger;
  public static INIT = InitializeTrigger;
  public static ITEM_SWAP = ItemSwapTrigger;
  public static KILL = KillTrigger;
  public static LAND = LandTrigger;
  public static LAUNCH = LaunchTrigger;
  public static LEFT_CLICK = LeftClickTrigger;
  public static RIGHT_CLICK = RightClickTrigger;
  public static MOVE = MoveTrigger;
  public static PHYS_DAMAGE = PhysicalDamageTrigger;
  public static SKILL_DAMAGE = SkillDamageTrigger;
  public static SKILL_CAST = SkillCastTrigger;
  public static TOOK_PHYS = TookPhysicalTrigger;
  public static TOOK_SKILL = TookSkillTrigger;
  public static MAP: { [key: string]: typeof ProTrigger } = {
    "Block Break": BlockBreakTrigger,
    "Block Place": BlockPlaceTrigger,
    "Cast": CastTrigger,
    "Cleanup": CleanupTrigger,
    "Crouch": CrouchTrigger,
    "Death": DeathTrigger,
    "Drop Item": DropItemTrigger,
    "Environment Damage": EnvironmentDamageTrigger,
    "Fishing": FishingTrigger,
    "Fishing Bite": FishingBiteTrigger,
    "Fishing Fail": FishingFailTrigger,
    "Fishing Grab": FishingGrabTrigger,
    "Fishing Ground": FishingGroundTrigger,
    "Fishing Reel": FishingReelTrigger,
    "Initialize": InitializeTrigger,
    "Item Swap": ItemSwapTrigger,
    "Kill": KillTrigger,
    "Land": LandTrigger,
    "Launch": LaunchTrigger,
    "Left Click": LeftClickTrigger,
    "Right Click": RightClickTrigger,
    "Move": MoveTrigger,
    "Physical Damage": PhysicalDamageTrigger,
    "Skill Damage": SkillDamageTrigger,
    "Skill Cast": SkillCastTrigger,
    "Took Physical Damage": TookPhysicalTrigger,
    "Took Skill Damage": TookSkillTrigger
  };

  public static byName = (name: string): ProTrigger | undefined => {
    const triggers = Object.keys(this.MAP);
    if (!triggers.includes(name)) return undefined;

    return Triggers.MAP[name].new();
  };
}

// TARGETS

class LinearTarget extends ProTarget {
  public constructor() {
    super("Linear",
      [],
      []);
  }

  public static override new = () => new this();
}

export class Targets {
  public static LINEAR = LinearTarget;
  public static MAP: { [key: string]: typeof ProMechanic } = {
    "Linear": LinearTarget
  };

  public static byName = (name: string): ProMechanic | undefined => {
    const conditions = Object.keys(this.MAP);
    if (!conditions.includes(name)) return undefined;

    return this.MAP[name].new();
  };
}

// CONDITIONS

class BlockCondition extends ProCondition {
  public constructor() {
    super("Block",
      [],
      [
        new DropdownSelect("standing", ["On Block", "Not On Block", "In Block", "Not In Block"]),
        new MaterialSelect()
      ]);
  }

  public static override new = () => new this();
}

export class Conditions {
  public static BLOCK = BlockCondition;
  public static MAP: { [key: string]: typeof ProCondition } = {
    "Block": BlockCondition
  };

  public static byName = (name: string): ProCondition | undefined => {
    const conditions = Object.keys(this.MAP);
    if (!conditions.includes(name)) return undefined;

    return Conditions.MAP[name].new();
  };
}

// MECHANICS
class LaunchMechanic extends ProMechanic {
  public constructor() {
    super("Launch",
      [],
      []);
  }

  public static override new = () => new this();
}

export class Mechanics {
  public static LAUNCH = LaunchMechanic;
  public static MAP: { [key: string]: typeof ProMechanic } = {
    "Launch": LaunchMechanic
  };

  public static byName = (name: string): ProMechanic | undefined => {
    const conditions = Object.keys(this.MAP);
    if (!conditions.includes(name)) return undefined;

    return this.MAP[name].new();
  };
}