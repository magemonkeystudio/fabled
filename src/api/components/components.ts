import ProMechanic          from "$api/components/mechanics";
import BlockSelect          from "$api/options/blockselect";
import ProCondition         from "$api/components/conditions";
import DropdownSelect       from "$api/options/dropdownselect";
import ProTrigger           from "$api/components/triggers";
import ProTarget            from "$api/components/targets";
import MaterialSelect       from "$api/options/materialselect";
import {
  getAnyEntities,
  getAnyPotion,
  getAnyProjectiles,
  getBiomes,
  getDamageableMaterials,
  getDamageTypes,
  getEntities,
  getPotionTypes
}                           from "../../version/data";
import BooleanSelect        from "$api/options/booleanselect";
import DoubleSelect         from "$api/options/doubleselect";
import Registry             from "$api/components/registry";
import StringListSelect     from "$api/options/stringlistselect";
import type ComponentOption from "$api/options/options";
import AttributeSelect      from "$api/options/attributeselect";
import SectionMarker        from "$api/options/sectionmarker";
import StringSelect         from "$api/options/stringselect";
import ClassSelect          from "$api/options/classselect";
import SkillSelect          from "$api/options/skillselect";
import IntSelect            from "$api/options/intselect";
import ColorSelect          from "$api/options/colorselect";

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
          .setTooltip("The type of skill damage to apply for. Leave this empty to apply to all skill damage")
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
          .setTooltip("The type of skill damage to apply for. Leave this empty to apply to all skill damage")
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
          .setTooltip("The offset from the target in the direction they are facing. Negative numbers go backwards"),
        new AttributeSelect("Upward", "upward", 2, 0.5)
          .setTooltip("The offset from the target upwards. Negative numbers go below them"),
        new AttributeSelect("Right", "right")
          .setTooltip("The offset from the target to their right. Negative numbers go to the left")
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

/**
 * Adds the options for item-check related effects to the component
 */
const itemConditionOptions = (): ComponentOption[] => {
  const data: ComponentOption[] = [
    new BooleanSelect("Check Material", "check-mat", true)
      .setTooltip("Whether the item needs to be a certain type"),
    new MaterialSelect(true, "Arrow")
      .requireValue("check-mat", [true])
      .setTooltip("The type the item needs to be"),
    new BooleanSelect("Check Data", "check-data", false)
      .setTooltip("Whether the item needs to have a certain data value"),
    new IntSelect("Data", "data")
      .requireValue("check-data", [true])
      .setTooltip("The data value the item must have"),
    new BooleanSelect("Check Lore", "check-lore", false)
      .setTooltip("Whether the item requires a bit of text in its lore"),
    new StringSelect("Lore", "lore", "text")
      .requireValue("check-lore", [true])
      .setTooltip("The text the item requires in its lore"),
    new BooleanSelect("Check Name", "check-name", false)
      .setTooltip("Whether the item needs to have a bit of text in its display name"),
    new StringSelect("Name", "name", "name")
      .requireValue("check-name", [true])
      .setTooltip("The text the item requires in its display name"),
    new BooleanSelect("Regex", "regex", false)
      .setTooltip("Whether the name and lore checks are regex strings. If you do not know what regex is, leave this option alone.")
  ];

  return data;
};

class AltitudeCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Altitude",
      description: "Applies child components whenever the player is on a certain height-level",
      data:        [
        new AttributeSelect("Min", "min")
          .setTooltip("The minimum height a player has to be on"),
        new AttributeSelect("Max", "max")
          .setTooltip("The maximum height a player can be on")
      ]
    });
  }

  public static override new = () => new this();
}

class ArmorCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Armor",
      description: "Applies child components when the target is wearing an armor item matching the given details",
      data:        [
        new DropdownSelect("Armor", "armor", ["Any", "Helmet", "Chestplate", "Leggings", "Boots"])
          .setTooltip("The type of armor to check"),
        ...itemConditionOptions()
      ]
    });
  }

  public static override new = () => new this();
}

class AttributeCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Attribute",
      description: "Requires the target to have a given number of attributes",
      data:        [
        new StringSelect("Attribute", "attribute", "Vitality")
          .setTooltip("The name of the attribute you are checking the value of"),
        new AttributeSelect("Min", "min")
          .setTooltip("The minimum amount of the attribute the target requires"),
        new AttributeSelect("Max", "max", 999)
          .setTooltip("The maximum amount of the attribute the target requires")
      ]
    });
  }

  public static override new = () => new this();
}

class BiomeCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Biome",
      description: "Applies child components when in a specified biome",
      data:        [
        new DropdownSelect("Type", "type", ["In Biome", "Not In Biome"], "In Biome")
          .setTooltip("Whether the target should be in the biome. If checking for in the biome, they must be in any one of the checked biomes. If checking for the opposite, they must not be in any of the checked biomes"),
        new DropdownSelect("Biome", "biome", getBiomes, ["Forest"], true)
          .setTooltip("The biomes to check for. The expectation would be any of the selected biomes need to match")
      ]
    });
  }


  public static override new = () => new this();
}

class BlockCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Block",
      description: "Applies child components if the target is currently standing on a block of the given type",
      data:        [
        new DropdownSelect("Type", "standing", ["On Block", "Not On Block", "In Block", "Not In Block"])
          .setTooltip("Specifies which block to check and whether it should match the selected material. \"On Block\" is directly below the player while \"In Block\" is the block a player's feet are in"),
        new MaterialSelect()
          .setTooltip("The type of the block to require the targets to stand on")
      ]
    });
  }


  public static override new = () => new this();
}

class BurningCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Burning",
      description: "Applies child components if the caster burns or not",
      data:        [
        new DropdownSelect("Type", "burn", ["Burn", "Dont burn"], "Burn")
          .setTooltip("Specifies whether the player has to be burning for this skill to be performed")
      ]
    });
  }

  public static override new = () => new this();
}

class CeilingCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Ceiling",
      description: "Checks the height of the ceiling above each target",
      data:        [
        new AttributeSelect("Distance", "distance", 5)
          .setTooltip("How high to check for the ceiling"),
        new BooleanSelect("At least", "at-least", true)
          .setTooltip("When true, the ceiling must be at least the give number of blocks high. If false, the ceiling must be lower than the given number of blocks")
      ]
    });
  }

  public static override new = () => new this();
}

class ChanceCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Chance",
      description: "Rolls a chance to apply child components",
      data:        [
        new AttributeSelect("Chance", "chance", 25)
          .setTooltip("The chance to execute children as a percentage. \"25\" would be 25%")
      ]
    });
  }

  public static override new = () => new this();
}

class ClassCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Class",
      description: "Applies child components when the target is the given class or optionally a profession of that class. For example, if you check for \"Fighter\" which professes into \"Warrior\", a \"Warrior\" will pass the check if you do not enable \"exact\"",
      data:        [
        new ClassSelect("Class", "class", false)
          .setTooltip("The class the player should be"),
        new BooleanSelect("Exact", "exact", false)
          .setTooltip("Whether the player must be exactly the given class. If false, they can be a later profession of the class")
      ]
    });
  }

  public static override new = () => new this();
}

class ClassLevelCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Class Level",
      description: "Applies child components when the level of the class with this skill is within the range. This only checks the level of the caster, not the targets",
      data:        [
        new IntSelect("Min Level", "min-level", 2)
          .setTooltip("The minimum class level the player should be. If the player has multiple classes, this will be of their main class"),
        new IntSelect("Max Level", "max-level", 99)
          .setTooltip("The maximum class level the player should be. If the player has multiple classes, this will be of their main class")
      ]
    });
  }

  public static override new = () => new this();
}

class CombatCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Combat",
      description: "Applies child components to targets that are in/out of combat, depending on the settings",
      data:        [
        new BooleanSelect("In Combat", "combat", true)
          .setTooltip("Whether the target should be in or out of combat"),
        new DoubleSelect("Seconds", "seconds", 10)
          .setTooltip("The time in seconds since the last combat activity before something is considered not in combat")
      ]
    });
  }

  public static override new = () => new this();
}

class CrouchCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Crouch",
      description: "Applies child components if the target player(s) are crouching",
      data:        [
        new BooleanSelect("Crouching", "crouch", true)
          .setTooltip("Whether the player should be crouching")
      ]
    });
  }

  public static override new = () => new this();
}

class DirectionCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Direction",
      description: "Applies child components when the target or caster is facing the correct direction relative to the other",
      data:        [
        new DropdownSelect("Type", "type", ["Target", "Caster"])
          .setTooltip("The entity to check the direction of"),
        new DropdownSelect("Direction", "direction", ["Away", "Towards"])
          .setTooltip("The direction the chosen entity needs to be looking relative to the other")
      ]
    });
  }

  public static override new = () => new this();
}

class ElevationCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Elevation",
      description: "Applies child components when the elevation of the target matches the settings",
      data:        [
        new DropdownSelect("Type", "type", ["Normal", "Difference"])
          .setTooltip("The type of comparison to make. Normal is just their Y-coordinate. Difference would be the difference between that the caster's Y-coordinate"),
        new AttributeSelect("Min Value", "min-value")
          .setTooltip("The minimum value for the elevation required. A positive minimum value with a \"Difference\" type would be for when the target is higher up than the caster"),
        new AttributeSelect("Max Value", "max-value", 255)
          .setTooltip("The maximum value for the elevation required. A negative maximum value with a \"Difference\" type would be for when the target is below the caster")
      ]
    });
  }

  public static override new = () => new this();
}

class ElseCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Else",
      description: "Applies child elements if the previous component failed to execute. This not only applies for conditions not passing, but mechanics failing due to no target or other cases"
    });
  }

  public static override new = () => new this();
}

class EntityTypeCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Entity Type",
      description: "Applies child elements if the target matches one of the selected entity types",
      data:        [
        new DropdownSelect("Types", "types", getEntities, [], true)
          .setTooltip("The entity types to target")
      ]
    });
  }

  public static override new = () => new this();
}

class FireCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Fire",
      description: "Applies child components when the target is on fire",
      data:        [
        new DropdownSelect("Type", "type", ["On Fire", "Not On Fire"], "On Fire")
          .setTooltip("Whether the target should be on fire")
      ]
    });
  }

  public static override new = () => new this();
}

class FlagCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Flag",
      description: "Applies child components when the target is marked by the appropriate flag",
      data:        [
        new DropdownSelect("Type", "type", ["Set", "Not Set"], "Set")
          .setTooltip("Whether the flag should be set"),
        new StringSelect("Key", "key", "key")
          .setTooltip("The unique key representing the flag. This should match the key for when you set it using the Flag mechanic or the Flat Toggle mechanic")
      ]
    });
  }

  public static override new = () => new this();
}

class FoodCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Food",
      description: "Applies child components when the target's food level matches the settings",
      data:        [
        new DropdownSelect("Type", "type", ["Food", "Percent", "Difference", "Difference Percent"])
          .setTooltip("The type of measurement to use for the food. Food level is their flat food left. Percent is the percentage of food they have left. Difference is the difference between the target's flat food and the caster's. Difference percent is the difference between the target's percentage food left and the caster's"),
        new AttributeSelect("Min Value", "min-value")
          .setTooltip("The minimum food required. A positive minimum with one of the \"Difference\" types would be for when the target has more food"),
        new AttributeSelect("Max Value", "max-value", 10, 2)
          .setTooltip("The maximum food required. A negative maximum with one of the \"Difference\" types would be for when the target has less food")
      ]
    });
  }

  public static override new = () => new this();
}

class GroundCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Ground",
      description: "Applies child components when the target is on the ground",
      data:        [
        new DropdownSelect("Type", "type", ["On Ground", "Not On Ground"])
          .setTooltip("Whether the target should be on the ground")
      ]
    });
  }

  public static override new = () => new this();
}

class HealthCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Health",
      description: "Applies child components when the target's health matches the settings",
      data:        [
        new DropdownSelect("Type", "type", ["Health", "Percent", "Difference", "Difference Percent"])
          .setTooltip("The type of measurement to use for the health. Health is their flat health left. Percent is the percentage of health they have left. Difference is the difference between the target's flat health and the caster's. Difference percent is the difference between the target's percentage health left and the caster's"),
        new AttributeSelect("Min Value", "min-value")
          .setTooltip("The minimum health required. A positive minimum with one of the \"Difference\" types would be for when the target has more health"),
        new AttributeSelect("Max Value", "max-value", 10, 2)
          .setTooltip("The maximum health required. A negative maximum with one of the \"Difference\" types would be for when the target has less health")
      ]
    });
  }

  public static override new = () => new this();
}

class ItemCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Item",
      description: "Applies child components when the target is wielding an item matching the given material",
      data:        [...itemConditionOptions()]
    });
  }

  public static override new = () => new this();
}

class InventoryCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Inventory",
      description: "Applies child components when the target player contains the given item in their inventory. This does not work on mobs",
      data:        [
        new AttributeSelect("Amount", "amount", 1, 0)
          .setTooltip("The amount of the item needed in the player's inventory"),
        ...itemConditionOptions()
      ]
    });
  }

  public static override new = () => new this();
}

class LightCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Light",
      description: "Applies child components when the light level at the target's location matches the settings",
      data:        [
        new AttributeSelect("Min Light", "min-light")
          .setTooltip("The minimum light level needed. 16 is full brightness while 0 is complete darkness"),
        new AttributeSelect("Max Light", "max-light", 16, 16)
          .setTooltip("The maximum light level needed. 16 is full brightness while 0 is complete darkness")
      ]
    });
  }

  public static override new = () => new this();
}

class ManaCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Mana",
      description: "Applies child components when the target's mana matches the settings",
      data:        [
        new DropdownSelect("Type", "type", ["Mana", "Percent", "Difference", "Difference Percent"], "Mana")
          .setTooltip("The type of measurement to use for the mana. Mana is their flat mana left. Percent is the percentage of mana they have left. Difference is the difference between the target's flat mana and the caster's. Difference percent is the difference between the target's percentage mana left and the caster's"),
        new AttributeSelect("Min Value", "min-value")
          .setTooltip("The minimum amount of mana needed"),
        new AttributeSelect("Max Value", "max-value", 10, 2)
          .setTooltip("The maximum amount of mana needed")
      ]
    });
  }

  public static override new = () => new this();
}

class MoneyCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Money",
      description: "Applies child components when the target's balance matches the settings (requires Vault and an economy plugin). Always is false for non-player targets",
      data:        [
        new DropdownSelect("Type", "type", ["Min", "Max", "Between"], "Min")
          .setTooltip("The type of comparison to make"),
        new AttributeSelect("Min Value", "min-value", 10)
          .requireValue("type", ["Min", "Between"])
          .setTooltip("The minimum balance the target must have, inclusive"),
        new AttributeSelect("Max Value", "max-value", 100)
          .requireValue("type", ["Max", "Between"])
          .setTooltip("The maximum balance the target can have, inclusive")
      ]
    });
  }

  public static override new = () => new this();
}

class MountedCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Mounted",
      description: "Applies child elements if the target is being mounted by one of the selected entity types",
      data:        [
        new DropdownSelect("Types", "types", getAnyEntities, ["Any"], true)
          .setTooltip("The entity types that can be mounting the target")
      ]
    });
  }

  public static override new = () => new this();
}

class MountingCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Mounting",
      description: "Applies child elements if the target is mounting one of the selected entity types",
      data:        [
        new DropdownSelect("Types", "types", getAnyEntities, ["Any"], true)
          .setTooltip("The entity types the target can be mounting")
      ]
    });
  }

  public static override new = () => new this();
}

class MythicMobTypeCondition extends ProCondition {
  public constructor() {
    super({
      name:        "MythicMob Type",
      description: "Applies child elements if the target corresponds to one of the entered MythicMob types, or is not a MythicMob if left empty",
      data:        [
        new StringListSelect("MythicMob Types", "types")
          .setTooltip("The MythicMob types to target")
      ]
    });
  }

  public static override new = () => new this();
}

class NameCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Name",
      description: "Applies child components when the target has a name matching the settings",
      data:        [
        new BooleanSelect("Contains Text", "contains", true)
          .setTooltip("Whether the target should have a name containing the text"),
        new BooleanSelect("Regex", "regex", false)
          .setTooltip("Whether the text is formatted as regex. If you do not know what regex is, ignore this option"),
        new StringSelect("Text", "text", "text")
          .setTooltip("The text to look for in the target's name")
      ]
    });
  }

  public static override new = () => new this();
}

class OffhandCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Offhand",
      description: "Applies child components when the target is wielding an item matching the given material as an offhand item. This is for v1.9+ servers only",
      data:        [...itemConditionOptions()]
    });
  }

  public static override new = () => new this();
}

class PermissionCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Permission",
      description: "Applies child components if the caster has the required permission",
      data:        [
        new StringSelect("Permission", "perm", "some.permission")
          .setTooltip("The permission the player needs to have")
      ]
    });
  }

  public static override new = () => new this();
}

class PotionCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Potion",
      description: "Applies child components when the target has the potion effect",
      data:        [
        new DropdownSelect("Type", "type", ["Active", "Not Active"], "Active")
          .setTooltip("Whether the potion should be active"),
        new DropdownSelect("Potion", "potion", getAnyPotion, "Any")
          .setTooltip("The type of potion to look for"),
        new AttributeSelect("Min Rank", "min-rank")
          .setTooltip("The minimum rank the potion effect can be"),
        new AttributeSelect("Max Rank", "max-rank", 999)
          .setTooltip("The maximum rank the potion effect can be")
      ]
    });
  }

  public static override new = () => new this();
}

class SkillLevelCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Skill Level",
      description: "Applies child components when the skill level is with the range. This checks the skill level of the caster, not the targets",
      data:        [
        new SkillSelect("Skill", "skill", false)
          .setTooltip("The name of the skill to check the level of. If you want to check the current skill, enter the current skill's name anyway"),
        new IntSelect("Min Level", "min-level", 2)
          .setTooltip("The minimum level of the skill needed"),
        new IntSelect("Max Level", "max-level", 99)
          .setTooltip("The maximum level of the skill needed")
      ]
    });
  }

  public static override new = () => new this();
}

class SlotCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Slot",
      description: "Applies child components when the target player has a matching item in the given slot",
      data:        [
        new StringListSelect("Slots (one per line)", "slot", ["9"])
          .setTooltip("The slots to look at. Slots 0-8 are the hot bar, 9-35 are the main inventory, 36-39 are armor, and 40 is the offhand slot. Multiple slots will check if any of the slots match"),
        ...itemConditionOptions()
      ]
    });
  }

  public static override new = () => new this();
}

class StatusCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Status",
      description: "Applies child components when the target has the status condition",
      data:        [
        new DropdownSelect("Type", "type", ["Active", "Not Active"])
          .setTooltip("Whether the status should be active"),
        new DropdownSelect("Status", "status",
          [
            "Any",
            "Absorb",
            "Curse",
            "Disarm",
            "Invincible",
            "Root",
            "Silence",
            "Stun"
          ])
          .setTooltip("The status to look for")
      ]
    });
  }

  public static override new = () => new this();
}

class TimeCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Time",
      description: "Applies child components when the server time matches the settings",
      data:        [
        new DropdownSelect("Time", "time", ["Day", "Night"], "Day")
          .setTooltip("The time to check for in the current world")
      ]
    });
  }

  public static override new = () => new this();
}

class ToolCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Tool",
      description: "Applies child components when the target is wielding a matching tool",
      data:        [
        new DropdownSelect("Material", "material", ["Any",
          "Wood",
          "Stone",
          "Iron",
          "Gold",
          "Diamond",
          "Netherite"])
          .setTooltip("The material the held tool needs to be made out of"),
        new DropdownSelect("Tool", "tool", ["Any", "Axe", "Hoe", "Pickaxe", "Shovel", "Sword"])
          .setTooltip("The type of tool it needs to be")
      ]
    });
  }

  public static override new = () => new this();
}

class ValueCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Value",
      description: "Applies child components if a stored value is within the given range",
      data:        [
        new StringSelect("Key", "key", "value")
          .setTooltip("The unique string used for the value set by the Value mechanics"),
        new AttributeSelect("Min Value", "min-value", 1)
          .setTooltip("The lower bound of the required value"),
        new AttributeSelect("Max Value", "max-value", 999)
          .setTooltip("The upper bound of the required value")
      ]
    });
  }

  public static override new = () => new this();
}

class WaterCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Water",
      description: "Applies child components when the target is in or out of water, depending on the settings",
      data:        [
        new DropdownSelect("State", "state", ["In Water", "Out Of Water"])
          .setTooltip("Whether the target needs to be in the water")
      ]
    });
  }

  public static override new = () => new this();
}

class WeatherCondition extends ProCondition {
  public constructor() {
    super({
      name:        "Weather",
      description: "Applies child components when the target's location has the given weather condition",
      data:        [
        new DropdownSelect("Type", "type", ["Rain", "None", "Snow", "Thunder"])
          .setTooltip("Whether the target needs to be in the water")
      ]
    });
  }

  public static override new = () => new this();
}

class WorldCondition extends ProCondition {
  public constructor() {
    super({
      name:        "World",
      description: "Applies child components when the target is in a specific world",
      data:        [
        new BooleanSelect("Blacklist", "blacklist", false)
          .setTooltip("Whether the list should be seen as a blacklist"),
        new StringListSelect("Worlds", "worlds")
          .setTooltip("Which worlds should be taken into consideration")
      ]
    });
  }

  public static override new = () => new this();
}

// MECHANICS

/**
 * Adds the options for item related effects to the component
 *
 * @param {Component} component - the component to add to
 */
const itemOptions = (): ComponentOption[] => {
  const data: ComponentOption[] = [
    new SectionMarker("Item Options"),
    new MaterialSelect(true, "Arrow")
      .setTooltip("The type of item to give to the player"),
    new IntSelect("Amount", "amount", 1)
      .setTooltip("The quantity of the item to give to the player"),
    new IntSelect("Durability", "data")
      .requireValue("material", getDamageableMaterials())
      .setTooltip("The durability to reduce from the item"),
    new BooleanSelect("Unbreakable", "unbreakable", false)
      .requireValue("material", getDamageableMaterials())
      .setTooltip("Whether to make the item unbreakable"),
    new IntSelect("CustomModelData", "byte", 0)
      .setTooltip("The CustomModelData of the item"),
    new DropdownSelect("Hide Flags", "hide-flags", ["Enchants", "Attributes", "Unbreakable", "Destroys", "Placed on", "Potion effects", "Dye"], [], true)
      .setTooltip("Flags to hide from the item"),
    new BooleanSelect("Custom Name/Lore", "custom", false)
      .setTooltip("Whether to apply a custom name/lore to the item"),

    new StringSelect("Name", "name", "Name")
      .requireValue("custom", [true])
      .setTooltip("The name of the item"),
    new StringListSelect("Lore", "lore")
      .requireValue("custom", [true])
      .setTooltip("The lore text for the item (the text below the name)"),
    new ColorSelect("Potion Color", "potion_color", "#385dc6")
      .requireValue("material", ["Potion", "Splash potion", "Lingering potion"])
      .setTooltip("The potion color in hex RGB"),
    new DropdownSelect("Potion Type", "potion_type", getPotionTypes, "Speed")
      .requireValue("material", ["Potion", "Splash potion", "Lingering potion"])
      .setTooltip("The type of potion"),
    new IntSelect("Potion Level", "potion_level")
      .requireValue("material", ["Potion", "Splash potion", "Lingering potion"])
      .setTooltip("The potion level"),
    new IntSelect("Potion Duration", "potion_duration", 30)
      .requireValue("material", ["Potion", "Splash potion", "Lingering potion"])
      .setTooltip("The potion duration (seconds)"),
    new ColorSelect("Armor Color", "armor_color", "#a06540")
      .requireValue("material", ["Leather helmet", "Leather chestplate", "Leather leggings", "Leather boots"])
      .setTooltip("The armor color in hex RGB")
  ];

  return data;
};

class ArmorMechanic extends ProMechanic {
  public constructor() {
    super({
      name:        "Armor",
      description: "Sets the specified armor slot of the target to the item defined by the settings",
      data:        [
        new DropdownSelect("Slot", "slot", ["Hand", "Off Hand", "Feet", "Legs", "Chest", "Head"])
          .setTooltip("The slot number to set the item to"),
        new BooleanSelect("Overwrite", "overwrite", false)
          .setTooltip("USE WITH CAUTION. Whether to overwrite an existing item in the slot. If true, will permanently delete the existing iem"),
        ...itemOptions()
      ]
    });
  }

  public static override new = () => new this();
}

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
    ALTITUDE:       { name: "Altitude", component: AltitudeCondition },
    ARMOR:          { name: "Armor", component: ArmorCondition },
    ATTRIBUTE:      { name: "Attribute", component: AttributeCondition },
    BIOME:          { name: "Biome", component: BiomeCondition },
    BLOCK:          { name: "Block", component: BlockCondition },
    BURNING:        { name: "Burning", component: BurningCondition },
    CEILING:        { name: "Ceiling", component: CeilingCondition },
    CHANCE:         { name: "Chance", component: ChanceCondition },
    CLASS:          { name: "Class", component: ClassCondition },
    CLASS_LEVEL:    { name: "Class Level", component: ClassLevelCondition },
    COMBAT:         { name: "Combat", component: CombatCondition },
    CROUCH:         { name: "Crouch", component: CrouchCondition },
    DIRECTION:      { name: "Direction", component: DirectionCondition },
    ELEVATION:      { name: "Elevation", component: ElevationCondition },
    ELSE:           { name: "Else", component: ElseCondition },
    ENTITY_TYPE:    { name: "Entity Type", component: EntityTypeCondition },
    FIRE:           { name: "Fire", component: FireCondition },
    FLAG:           { name: "Flag", component: FlagCondition },
    FOOD:           { name: "Food", component: FoodCondition },
    GROUND:         { name: "Ground", component: GroundCondition },
    HEALTH:         { name: "Health", component: HealthCondition },
    INVENTORY:      { name: "Inventory", component: InventoryCondition },
    ITEM:           { name: "Item", component: ItemCondition },
    LIGHT:          { name: "Light", component: LightCondition },
    MANA:           { name: "Mana", component: ManaCondition },
    MONEY:          { name: "Money", component: MoneyCondition },
    MOUNTED:        { name: "Mounted", component: MountedCondition },
    MOUNTING:       { name: "Mounting", component: MountingCondition },
    MYTHICMOB_TYPE: { name: "MythicMob Type", component: MythicMobTypeCondition },
    NAME:           { name: "Name", component: NameCondition },
    OFFHAND:        { name: "Offhand", component: OffhandCondition },
    PERMISSION:     { name: "Permission", component: PermissionCondition },
    POTION:         { name: "Potion", component: PotionCondition },
    SKILL_LEVEL:    { name: "Skill Level", component: SkillLevelCondition },
    SLOT:           { name: "Slot", component: SlotCondition },
    STATUS:         { name: "Status", component: StatusCondition },
    TIME:           { name: "Time", component: TimeCondition },
    TOOL:           { name: "Tool", component: ToolCondition },
    VALUE:          { name: "Value", component: ValueCondition },
    WATER:          { name: "Water", component: WaterCondition },
    WEATHER:        { name: "Weather", component: WeatherCondition },
    WORLD:          { name: "World", component: WorldCondition }
  });
  Registry.mechanics.set({
    ARMOR:               { name: "Armor", component: ArmorMechanic },
    // ARMOR_STAND:         { name: "Armor Stand", component: ArmorStandMechanic },
    // ARMOR_STAND_POSE:    { name: "Armor Stand Pose", component: ArmorStandPoseMechanic },
    // ATTRIBUTE:           { name: "Attribute", component: AttributeMechanic },
    // BLOCK:               { name: "Block", component: BlockMechanic },
    // BUFF:                { name: "Buff", component: BuffMechanic },
    // CANCEL:              { name: "Cancel", component: CancelMechanic },
    // CHANNEL:             { name: "Channel", component: ChannelMechanic },
    // CLEANSE:             { name: "Cleanse", component: CleanseMechanic },
    // COMMAND:             { name: "Command", component: CommandMechanic },
    // COOLDOWN:            { name: "Cooldown", component: CooldownMechanic },
    // DAMAGE:              { name: "Damage", component: DamageMechanic },
    // DAMAGE_BUFF:         { name: "Damage Buff", component: DamageBuffMechanic },
    // DAMAGE_LORE:         { name: "Damage Lore", component: DamageLoreMechanic },
    // DEFENSE_BUFF:        { name: "Defense Buff", component: DefenseBuffMechanic },
    // DELAY:               { name: "Delay", component: DelayMechanic },
    // DISGUISE:            { name: "Disguise", component: DisguiseMechanic },
    // DURABILITY:          { name: "Durability", component: DurabilityMechanic },
    // EXPLOSION:           { name: "Explosion", component: ExplosionMechanic },
    // FIRE:                { name: "Fire", component: FireMechanic },
    // FLAG:                { name: "Flag", component: FlagMechanic },
    // FLAG_CLEAR:          { name: "Flag Clear", component: FlagClearMechanic },
    // FLAG_TOGGLE:         { name: "Flag Toggle", component: FlagToggleMechanic },
    // FOOD:                { name: "Food", component: FoodMechanic },
    // FORGET_TARGETS:      { name: "Forget Targets", component: ForgetTargetsMechanic },
    // HEAL:                { name: "Heal", component: HealMechanic },
    // HEALTH_SET:          { name: "Health Set", component: HealthSetMechanic },
    // HELD_ITEM:           { name: "Held Item", component: HeldItemMechanic },
    // IMMUNITY:            { name: "Immunity", component: ImmunityMechanic },
    // INTERRUPT:           { name: "Interrupt", component: InterruptMechanic },
    // ITEM:                { name: "Item", component: ItemMechanic },
    // ITEM_DROP:           { name: "Item Drop", component: ItemDropMechanic },
    // ITEM_PROJECTILE:     { name: "Item Projectile", component: ItemProjectileMechanic },
    // ITEM_REMOVE:         { name: "Item Remove", component: ItemRemoveMechanic },
    LAUNCH:              { name: "Launch", component: LaunchMechanic },
    // LIGHTNING:           { name: "Lightning", component: LightningMechanic },
    // MANA:                { name: "Mana", component: ManaMechanic },
    // MESSAGE:             { name: "Message", component: MessageMechanic },
    // MINE:                { name: "Mine", component: MineMechanic },
    // MONEY:               { name: "Money", component: MoneyMechanic },
    // PARTICLE:            { name: "Particle", component: ParticleMechanic },
    // PARTICLE_ANIMATION:  { name: "Particle Animation", component: ParticleAnimationMechanic },
    // PARTICLE_EFFECT:     { name: "Particle Effect", component: ParticleEffectMechanic },
    // CANCEL_EFFECT:       { name: "Cancel Effect", component: CancelEffectMechanic },
    // PARTICLE_PROJECTILE: { name: "Particle Projectile", component: ParticleProjectileMechanic },
    // PASSIVE:             { name: "Passive", component: PassiveMechanic },
    // PERMISSION:          { name: "Permission", component: PermissionMechanic },
    // POTION:              { name: "Potion", component: PotionMechanic },
    // POTION_PROJECTILE:   { name: "Potion Projectile", component: PotionProjectileMechanic },
    // PROJECTILE:          { name: "Projectile", component: ProjectileMechanic },
    // PURGE:               { name: "Purge", component: PurgeMechanic },
    // PUSH:                { name: "Push", component: PushMechanic },
    // REMEMBER_TARGETS:    { name: "Remember Targets", component: RememberTargetsMechanic },
    // REPEAT:              { name: "Repeat", component: RepeatMechanic },
    // SOUND:               { name: "Sound", component: SoundMechanic },
    // Stat:                { name: "Stat", component: StatMechanic },
    // STATUS:              { name: "Status", component: StatusMechanic },
    // TAUNT:               { name: "Taunt", component: TauntMechanic },
    // TRIGGER:             { name: "Trigger", component: TriggerMechanic },
    // VALUE_ADD:           { name: "Value Add", component: ValueAddMechanic },
    // VALUE_ATTRIBUTE:     { name: "Value Attribute", component: ValueAttributeMechanic },
    // VALUE_COPY:          { name: "Value Copy", component: ValueCopyMechanic },
    // VALUE_DISTANCE:      { name: "Value Distance", component: ValueDistanceMechanic },
    // VALUE_HEALTH:        { name: "Value Health", component: ValueHealthMechanic },
    // VALUE_LOCATION:      { name: "Value Location", component: ValueLocationMechanic },
    // VALUE_LORE:          { name: "Value Lore", component: ValueLoreMechanic },
    // VALUE_LORE_SLOT:     { name: "Value Lore Slot", component: ValueLoreSlotMechanic },
    // VALUE_MANA:          { name: "Value Mana", component: ValueManaMechanic },
    // VALUE_MULTIPLY:      { name: "Value Multiply", component: ValueMultiplyMechanic },
    // VALUE_PLACEHOLDER:   { name: "Value Placeholder", component: ValuePlaceholderMechanic },
    // VALUE_RANDOM:        { name: "Value Random", component: ValueRandomMechanic },
    // VALUE_SET:           { name: "Value Set", component: ValueSetMechanic },
    // WARP:                { name: "Warp", component: WarpMechanic },
    // WARP_LOC:            { name: "Warp Location", component: WarpLocMechanic },
    // WARP_RANDOM:         { name: "Warp Random", component: WarpRandomMechanic },
    // WARP_SWAP:           { name: "Warp Swap", component: WarpSwapMechanic },
    // WARP_TARGET:         { name: "Warp Target", component: WarpTargetMechanic },
    // WARP_VALUE:          { name: "Warp Value", component: WarpValueMechanic },
    // WOLF:                { name: "Wolf", component: WolfMechanic }
  });
  Registry.initialized.set(true);
};