import ProMechanic                           from "$api/components/mechanics";
import BlockSelect                           from "$api/options/blockselect";
import ProCondition                          from "$api/components/conditions";
import DropdownSelect                        from "$api/options/dropdownselect";
import ProTrigger                            from "$api/components/triggers";
import ProTarget                             from "$api/components/targets";
import MaterialSelect                        from "$api/options/materialselect";
import { getAnyProjectiles, getDamageTypes } from "../../version/data";
import BooleanSelect                         from "$api/options/booleanselect";
import DoubleSelect                          from "$api/options/doubleselect";
import SkillSelect                           from "$api/options/skillselect";
import ClassSelect                           from "$api/options/classselect";
import Registry                              from "$api/components/registry";

// TRIGGERS

class BlockBreakTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Block Break",
      description: "Applies skill effects when a player breaks a block matching the given details",
      data:        [new BlockSelect(
        "The type of block expected to be broken",
        "The expected data value of the block (-1 for any data value)"
      )]
    });
  }

  public static override new = () => new this();
}

class BlockPlaceTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Block Place",
      description: "Applies skill effects when a player places a block matching the given details",
      data:        [new BlockSelect(
        "The type of block expected to be placed",
        "The expected data value of the block (-1 for any data value)"
      )]
    });
  }

  public static override new = () => new this();
}

class CastTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Cast",
      description: "Applies skill effects when a player casts the skill using either the cast command, the skill bar, or click combos"
    });
  }

  public static override new = () => new this();
}

class CleanupTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Cleanup",
      description: "Applies skill effects when the player disconnects or unlearns the skill. This is always applied with a skill level of 1 just for the sake of math"
    });
  }

  public static override new = () => new this();
}

class CrouchTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Crouch",
      description: "Applies skill effects when a player starts or stops crouching using the shift key",
      data:        [
        new DropdownSelect("Crouching", "type", ["Start Crouching", "Stop Crouching", "Both"])
      ]
    });
  }

  public static override new = () => new this();
}

class DeathTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Death",
      description: "Applies skill effects when a player dies",
      data:        []
    });
  }

  public static override new = () => new this();
}

class DropItemTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Drop Item",
      description: "Applies skill effects upon dropping an item",
      data:        [
        new DropdownSelect("Drop multiple", "drop multiple", ["True", "False", "Ignore"], "Ignore")
          .setTooltip("Whether the player has to drop multiple items or a single item")
      ]
    });
  }

  public static override new = () => new this();
}

class EnvironmentDamageTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Environment Damage",
      description: "Applies skill effects when a player takes environmental damage",
      data:        [
        new DropdownSelect("Type", "type", getDamageTypes, "Fall")
          .setTooltip("The source of damage to apply for")
      ]
    });
  }

  public static override new = () => new this();
}

class FishingTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Fishing",
      description: "Applies skill effects upon right-clicking with a fishing rod"
    });
  }

  public static override new = () => new this();
}

class FishingBiteTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Fishing Bite",
      description: "Applies skill effects when a fish bites the fishing rod of a player"
    });
  }

  public static override new = () => new this();
}

class FishingFailTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Fishing Fail",
      description: "Applies skill effects when a player fails to catch a fish due to poor timing"
    });
  }

  public static override new = () => new this();
}

class FishingGrabTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Fishing Grab",
      description: "Applies skill effects when a player catches a fish"
    });
  }

  public static override new = () => new this();
}

class FishingGroundTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Fishing Ground",
      description: "Applies skill effects when the bobber of a fishing rod hits the ground"
    });
  }

  public static override new = () => new this();
}

class FishingReelTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Fishing Reel",
      description: "Applies skill effects when a player reels in a fishing rod out of water or air with no fish on the rod"
    });
  }

  public static override new = () => new this();
}

class InitializeTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Initialize",
      description: "Applies skill effects immediately. This can be used for passive abilities"
    });
  }

  public static override new = () => new this();
}

class ItemSwapTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Item Swap",
      description: "Applies skill effects upon pressing the swap-key on your keyboard",
      data:        [
        new BooleanSelect("Cancel Swap", "cancel", true)
          .setTooltip("True cancels the item swap. False allows the item swap")
      ]
    });
  }

  public static override new = () => new this();
}

class KillTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Kill",
      description: "Applies skill effects upon killing something"
    });
  }

  public static override new = () => new this();
}

class LandTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Land",
      description: "Applies skill effects when a player lands on the ground",
      data:        [new DoubleSelect("Min Distance", "min-distance")
        .setTooltip("The minimum distance the player should fall before effects activate")]
    });
  }

  public static override new = () => new this();
}

class LaunchTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Launch",
      description: "Applies skill effects when a player launches a projectile",
      data:        [new DropdownSelect("Type", "type", getAnyProjectiles, "Any")
        .setTooltip("The type of projectile that should be launched")]
    });
  }

  public static override new = () => new this();
}

class LeftClickTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Left Click",
      description: "Applies skill effects upon performing a left-click",
      data:        [new DropdownSelect("Crouch", "crouch", ["Crouch", "Dont crouch", "Both"], "Crouch")
        .setTooltip("If the player has to be crouching in order for this trigger to function")]
    });
  }

  public static override new = () => new this();
}

class MoveTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Move",
      description: "Applies skill effects when a player moves around. This triggers every tick the player is moving, so use this sparingly. Use the 'api-moved' value to check/use the distance traveled"
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
      name:        "Skill Cast",
      description: "Applies skill effects when a player casts a skill",
      data:        [
        new BooleanSelect("Cancel Cast", "cancel", false),
        new ClassSelect("Classes", "allowed-classes"),
        new SkillSelect("Skills", "allowed-skills")
      ]
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

// TARGETS

class LinearTarget extends ProTarget {
  public constructor() {
    super({ name: "Linear" });
  }

  public static override new = () => new this();
}

// CONDITIONS

class BlockCondition extends ProCondition {
  public constructor() {
    super({
      name: "Block",
      data: [
        new DropdownSelect("Standing", "standing", ["On Block", "Not On Block", "In Block", "Not In Block"]),
        new MaterialSelect()
      ]
    });
  }

  public static override new = () => new this();
}

// MECHANICS
class LaunchMechanic extends ProMechanic {
  public constructor() {
    super({ name: "Launch" });
  }

  public static override new = () => new this();
}

export const initComponents = () => {
  Registry.triggers.set({
    BLOCK_BREAK:    { name: "Block Break", component: BlockBreakTrigger },
    BLOCK_PLACE:    { name: "Block Place", component: BlockPlaceTrigger },
    CAST:           { name: "Cast", component: CastTrigger },
    CLEANUP:        { name: "Cleanup", component: CleanupTrigger },
    CROUCH:         { name: "Crouch", component: CrouchTrigger },
    DEATH:          { name: "Death", component: DeathTrigger },
    DROP_ITEM:      { name: "Drop Item", component: DropItemTrigger },
    ENV_DAMAGE:     { name: "Environment Damage", component: EnvironmentDamageTrigger },
    FISHING:        { name: "Fishing", component: FishingTrigger },
    FISHING_BITE:   { name: "Fishing Bite", component: FishingBiteTrigger },
    FISHING_FAIL:   { name: "Fishing Fail", component: FishingFailTrigger },
    FISHING_GRAB:   { name: "Fishing Grab", component: FishingGrabTrigger },
    FISHING_GROUND: { name: "Fishing Ground", component: FishingGroundTrigger },
    FISHING_REEL:   { name: "Fishing Reel", component: FishingReelTrigger },
    INIT:           { name: "Initialize", component: InitializeTrigger },
    ITEM_SWAP:      { name: "Item Swap", component: ItemSwapTrigger },
    KILL:           { name: "Kill", component: KillTrigger },
    LAND:           { name: "Land", component: LandTrigger },
    LAUNCH:         { name: "Launch", component: LaunchTrigger },
    LEFT_CLICK:     { name: "Left Click", component: LeftClickTrigger },
    RIGHT_CLICK:    { name: "Right Click", component: RightClickTrigger },
    MOVE:           { name: "Move", component: MoveTrigger },
    PHYS_DAMAGE:    { name: "Physical Damage", component: PhysicalDamageTrigger },
    SKILL_DAMAGE:   { name: "Skill Damage", component: SkillDamageTrigger },
    SKILL_CAST:     { name: "Skill Cast", component: SkillCastTrigger },
    TOOK_PHYS:      { name: "Took Physical Damage", component: TookPhysicalTrigger },
    TOOK_SKILL:     { name: "Took Skill Damage", component: TookSkillTrigger }
  });
  Registry.targets.set({
    LINEAR: { name: "Linear", component: LinearTarget }
  });
  Registry.conditions.set({
    BLOCK: { name: "Block", component: BlockCondition }
  });
  Registry.mechanics.set({
    LAUNCH: { name: "Launch", component: LaunchMechanic }
  });
  Registry.initialized.set(true);
};