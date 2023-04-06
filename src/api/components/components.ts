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
import Registry                              from "$api/components/registry";
import StringListSelect                      from "$api/options/stringlistselect";
import type ComponentOption                  from "$api/options/options";
import AttributeSelect                       from "$api/options/attributeselect";
import SectionMarker                         from "$api/options/sectionmarker";
import StringSelect                          from "$api/options/stringselect";

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
      description: "Applies skill effects when a player dies"
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
      name:        "Physical Damage",
      description: "Applies skill effects when a player deals physical (or non-skill) damage. This includes melee attacks and firing a bow",
      data:        [
        new BooleanSelect("Target Caster", "target", true)
          .setTooltip("True makes the children target the caster. False makes children target the damaged entity"),
        new DropdownSelect("Type", "type", ["Both", "Melee", "Projectile"], "Both")
          .setTooltip("The type of damage dealt"),
        new DoubleSelect("Min Damage", "dmg-min", 0)
          .setTooltip("The minimum damage that needs to be dealt")
      ]
    });
  }

  public static override new = () => new this();
}

class RightClickTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Right Click",
      description: "Applies skill effects upon performing a right-click (NOTE: When clicking in air, you have to have an item in your hand)",
      data:        [
        new DropdownSelect("Crouch", "crouch", ["Crouch", "Dont crouch", "Both"], "Crouch")
          .setTooltip("If the player has to be crouching in order for this trigger to function")
      ]
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
        new BooleanSelect("Cancel Cast", "cancel", false)
          .setTooltip("True cancels the skill cast. False allows the skill cast"),
        new StringListSelect("Classes", "allowed-classes")
          .setTooltip("The list of classes which will trigger this effect. Leave blank to allow all to trigger. Use '!xxx' to exclude"),
        new StringListSelect("Skills", "allowed-skills")
          .setTooltip("The list of skills which will trigger this effect. Leave blank to allow all to trigger. Use '!xxx' to exclude")
      ]
    });
  }

  public static override new = () => new this();
}

class SkillDamageTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Skill Damage",
      description: "Applies skill effects when a player deals damage with a skill",
      data:        [
        new BooleanSelect("Target Caster", "target", true)
          .setTooltip("True makes children target the caster. False makes children target the damaged entity"),
        new DoubleSelect("Min Damage", "dmg-min", 0)
          .setTooltip("The minimum damage that needs to be dealt"),
        new DoubleSelect("Max Damage", "dmg-max", 999)
          .setTooltip("The maximum damage that needs to be dealt"),
        new StringListSelect("Category", "category", ["default"])
          .setTooltip("The type of skill damage to apply for. Leave this empty to apply to all skill damage.")
      ]
    });
  }

  public static override new = () => new this();
}

class TookPhysicalTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Took Physical Damage",
      description: "Applies skill effects when a player takes physical (or non-skill) damage. This includes melee attacks and projectiles not fired by a skill",
      data:        [
        new BooleanSelect("Target Caster", "target", true)
          .setTooltip("True makes children target the caster. False makes children target the attacking entity"),
        new DropdownSelect("Type", "type", ["Both", "Melee", "Projectile"], "Both")
          .setTooltip("The type of damage dealt"),
        new DoubleSelect("Min Damage", "dmg-min", 0)
          .setTooltip("The minimum damage that needs to be dealt"),
        new DoubleSelect("Max Damage", "dmg-max", 999)
          .setTooltip("The maximum damage that needs to be dealt")
      ]
    });
  }

  public static override new = () => new this();
}

class TookSkillTrigger extends ProTrigger {
  public constructor() {
    super({
      name:        "Took Skill Damage",
      description: "Applies skill effects when a player takes damage from a skill other than their own",
      data:        [
        new BooleanSelect("Target Caster", "target", true)
          .setTooltip("True makes children target the caster. False makes children target the attacking entity"),
        new DoubleSelect("Min Damage", "dmg-min", 0)
          .setTooltip("The minimum damage that needs to be dealt"),
        new DoubleSelect("Max Damage", "dmg-max", 999)
          .setTooltip("The maximum damage that needs to be dealt"),
        new StringListSelect("Category", "category", ["default"])
          .setTooltip("The type of skill damage to apply for. Leave this empty to apply to all skill damage.")
      ]
    });
  }

  public static override new = () => new this();
}

// TARGETS

const targetOptions = (): ComponentOption[] => {
  const data: ComponentOption[] = [];
  data.push(new DropdownSelect("Group", "group", ["Ally", "Enemy", "Both"], "Enemy")
    .setTooltip("The alignment of targets to get")
  );
  data.push(new BooleanSelect("Through Wall", "wall", false)
    .setTooltip("Whether to allow targets to be on the other side of a wall")
  );
  data.push(new DropdownSelect("Include Caster", "caster", ["True", "False", "In area"], "False")
    .setTooltip("Whether to include the caster in the target list. \"True\" will always include them, \"False\" will never, and \"In area\" will only if they are within the targeted area")
  );
  data.push(new AttributeSelect("Max Targets", "max", 99, 0)
    .setTooltip("The max amount of targets to apply children to")
  );

  return data;
};

class AreaTarget extends ProTarget {
  public constructor() {
    super({
      name:        "Area",
      description: "Targets all units in a radius from the current target (the casting player is the default target)",
      data:        [
        new AttributeSelect("Radius", "radius", 3)
          .setTooltip("The radius of the area to target in blocks"),
        ...targetOptions(),
        new BooleanSelect("Random", "random", false)
          .setTooltip("Whether to randomize the targets selected")
      ]
    });
  }

  public static override new = () => new this();
}

class ConeTarget extends ProTarget {
  public constructor() {
    super({
      name:        "Cone",
      description: "Targets all units in a line in front of the current target (the casting player is the default target). If you include the caster, that counts towards the max amount",
      data:        [
        new AttributeSelect("Range", "range", 5)
          .setTooltip("The max distance away any target can be in blocks"),
        new AttributeSelect("Angle", "angle", 90)
          .setTooltip("The angle of the cone arc in degrees"),
        ...targetOptions()
      ]
    });
  }

  public static override new = () => new this();
}

class LinearTarget extends ProTarget {
  public constructor() {
    super({
      name:        "Linear",
      description: "Targets all units in a line in front of the current target (the casting player is the default target)",
      data:        [
        new AttributeSelect("Range", "range", 5)
          .setTooltip("The max distance away any target can be in blocks"),
        new AttributeSelect("Tolerance", "tolerance")
          .setTooltip("How much to expand the potential entity's hit-box in all directions, in blocks. This makes it easier to aim"),
        ...targetOptions()
      ]
    });
  }

  public static override new = () => new this();
}

class LocationTarget extends ProTarget {
  public constructor() {
    super({
      name:        "Location",
      description: "Targets the location the target or caster is looking at. Combine this with another targeting type for ranged area effects",
      data:        [
        new AttributeSelect("Range", "range", 5)
          .setTooltip("The max distance the location can be from the target's eyes"),
        new BooleanSelect("Entities", "entities", true)
          .setTooltip("True to account for entities, or false to pass through them"),
        new BooleanSelect("Fluids", "fluids", false)
          .setTooltip("True to account for fluids (water and lava), or false to pass through them"),
        new BooleanSelect("Passable blocks", "passable", true)
          .setTooltip("True to account for passable or non-collidable blocks (grass, saplings, etc), or false to pass through them"),
        new BooleanSelect("Center", "center", false)
          .setTooltip("Whether to move the hit location to the center of the block")
      ]
    });
  }

  public static override new = () => new this();
}

class NearestTarget extends ProTarget {
  public constructor() {
    super({
      name:        "Nearest",
      description: "Targets the closest unit(s) in a radius from the current target (the casting player is the default target). If you include the caster, that counts towards the max number",
      data:        [
        new AttributeSelect("Range", "range", 3)
          .setTooltip("The radius of the area to target in blocks"),
        ...targetOptions()
      ]
    });
  }

  public static override new = () => new this();
}

class OffsetTarget extends ProTarget {
  public constructor() {
    super({
      name:        "Offset",
      description: "Targets a location that is the given offset away from each target",
      data:        [
        new SectionMarker("Offset"),
        new AttributeSelect("Forward", "forward")
          .setTooltip("The offset from the target in the direction they are facing. Negative numbers go backwards."),
        new AttributeSelect("Upward", "upward", 2, 0.5)
          .setTooltip("The offset from the target upwards. Negative numbers go below them."),
        new AttributeSelect("Right", "right")
          .setTooltip("The offset from the target to their right. Negative numbers go to the left.")
      ]
    });
  }

  public static override new = () => new this();
}

class RememberTarget extends ProTarget {
  public constructor() {
    super({
      name:        "Remember",
      description: "Targets entities stored using the \"Remember Targets\" mechanic for the matching key. If it was never set, this will fail",
      data:        [
        new StringSelect("Key", "key", "target")
          .setTooltip("The unique key for the target group that should match that used by the \"Remember Targets\" skill")
      ]
    });
  }

  public static override new = () => new this();
}

class SelfTarget extends ProTarget {
  public constructor() {
    super({
      name:        "Self",
      description: "Returns the current target back to the caster"
    });
  }

  public static override new = () => new this();
}

class SingleTarget extends ProTarget {
  public constructor() {
    super({
      name:        "Single",
      description: "Targets a single unit in front of the current target (the casting player is the default target)",
      data:        [
        new AttributeSelect("Range", "range", 5)
          .setTooltip("The max distance away any target can be in blocks"),
        new AttributeSelect("Tolerance", "tolerance")
          .setTooltip("How much to expand the potential entity's hitbox in all directions, in blocks. This makes it easier to aim"),
        new DropdownSelect("Group", "group", ["Ally", "Enemy", "Both"], "Enemy")
          .setTooltip("The alignment of targets to get"),
        new BooleanSelect("Through Wall", "wall", false)
          .setTooltip("Whether to allow targets to be on the other side of a wall")
      ]
    });
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
    AREA:     { name: "Area", component: AreaTarget },
    CONE:     { name: "Cone", component: ConeTarget },
    LINEAR:   { name: "Linear", component: LinearTarget },
    LOCATION: { name: "Location", component: LocationTarget },
    NEAREST:  { name: "Nearest", component: NearestTarget },
    OFFSET:   { name: "Offset", component: OffsetTarget },
    REMEMBER: { name: "Remember", component: RememberTarget },
    SELF:     { name: "Self", component: SelfTarget },
    SINGLE:   { name: "Single", component: SingleTarget }
  });
  Registry.conditions.set({
    BLOCK: { name: "Block", component: BlockCondition }
  });
  Registry.mechanics.set({
    LAUNCH: { name: "Launch", component: LaunchMechanic }
  });
  Registry.initialized.set(true);
};