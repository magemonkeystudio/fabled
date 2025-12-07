import {
	getAnyConsumable,
	getAnyDamageTypes,
	getAnyEntities,
	getAnyMaterials,
	getAnyPotion,
	getAnyProjectiles,
	getBadPotions,
	getBiomes,
	getBlocks,
	getDamageableMaterials,
	getDamageTypes,
	getDyes,
	getEntities,
	getGoodPotions,
	getMaterials,
	getMiscDisguises,
	getMobDisguises,
	getParticles,
	getPotionTypes,
	getProjectiles,
	getSounds
}                                                                from '../../version/data';
import type FabledComponent                                      from '$api/components/fabled-component.svelte';
import FabledMechanic                                            from '$api/components/mechanics.svelte';
import FabledCondition                                           from '$api/components/conditions.svelte';
import FabledTrigger                                             from '$api/components/triggers.svelte';
import FabledTarget                                              from '$api/components/targets.svelte';
import { conditions, initialized, mechanics, targets, triggers } from '$api/components/registry';
import AttributeSelect                                           from '$api/options/attributeselect.svelte';
import BlockSelect                                               from '$api/options/blockselect.svelte';
import BooleanSelect                                             from '$api/options/booleanselect.svelte';
import ClassSelect                                               from '$api/options/classselect.svelte';
import ColorSelect                                               from '$api/options/colorselect.svelte';
import DoubleSelect                                              from '$api/options/doubleselect.svelte';
import DropdownSelect                                            from '$api/options/dropdownselect.svelte';
import EnchantSelect                                             from '$api/options/enchantselect.svelte';
import IntSelect                                                 from '$api/options/intselect.svelte';
import MaterialSelect                                            from '$api/options/materialselect.svelte';
import SectionMarker                                             from '$api/options/sectionmarker.svelte';
import SkillSelect                                               from '$api/options/skillselect.svelte';
import StringListSelect                                          from '$api/options/stringlistselect.svelte';
import StringSelect                                              from '$api/options/stringselect.svelte';
import type { ComponentOption, Requirements }                    from '$api/options/options';
import { get }                                                   from 'svelte/store';
import { attributeStore }                                        from '../../data/attribute-store';

// This file holds all the component configurations and setup. Please add new components/options here

// TRIGGERS

class AirTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Air',
			description:  'Applies skill effects when an entity loses or gains air, firing every tick the air value changes. This is useful for abilities that activate upon drowning, surfacing, or when underwater.',
			keywords:     'air, oxygen, breathe, drown, underwater, suffocate, breath',
			data:         [
				new DropdownSelect('Type', 'type', ['Increasing', 'Decreasing'])
					.setTooltip('Whether the entity needs to be gaining or losing air to trigger')
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class ArmorEquipTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Armor Equip',
			description:  'Applies skill effects when a player equips or unequips an item in any armor slot or a hand slot. This can be used for abilities that activate when changing gear.',
			keywords:     'armor, equip, unequip, gear, change, helmet, chestplate, leggings, boots, main hand, offhand',
			data:         [new DropdownSelect('Slots', 'slots', ['Any', 'Helmet', 'Chestplate', 'Leggings', 'Boots', 'Main hand', 'Offhand'], ['Any'], true)
				.setTooltip('The armor slots to check for')],
			summaryItems: ['slots']
		});
	}

	public static override new = () => new this();
}

class AttributeChangeTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Attribute Change',
			description:  'Applies skill effects when a player\'s attribute changes, such as strength, dexterity, or custom attributes. The placeholders <code>api-attribute</code>, <code>api-change</code>, and <code>api-value</code> provide details about the change.',
			keywords:     'attribute, stats, strength, dexterity, intelligence, health, mana, change, modify',
			data:         [new DropdownSelect('Attribute', 'attr', () => ['Any', ...attributeStore.getAttributeNames()], ['Any'], true)
				.setTooltip('The attribute to check for')],
			summaryItems: ['attr']
		});
	}

	public static override new = () => new this();
}

class BlockBreakTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Block Break',
			description:  'Applies skill effects when a player successfully breaks a specific type of block. This can be used for abilities that activate upon mining or destroying environmental elements.',
			keywords:     'block, break, destroy, mine, mining, chop, digging',
			data:         [new BlockSelect(
				'The type of block expected to be broken',
				'The expected data value of the block (-1 for any data value)'
			)],
			summaryItems: ['block']
		});
	}

	public static override new = () => new this();
}

class BlockPlaceTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Block Place',
			description:  'Applies skill effects when a player places a specific type of block. This trigger is useful for abilities related to construction, building, or strategic placement of blocks.',
			keywords:     'block, place, build, building, construction, lay, set, create',
			data:         [new BlockSelect(
				'The type of block expected to be placed',
				'The expected data value of the block (-1 for any data value)'
			)],
			summaryItems: ['block']
		});
	}

	public static override new = () => new this();
}

class CastTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Cast',
			description: 'Applies skill effects when a player actively casts a skill, whether through a command, the skill bar interface, or specific click combinations. This is the primary trigger for most active abilities.',
			keywords:    'cast, skill, command, hotbar, ability, activate, use, execute'
		});
	}

	public static override new = () => new this();
}

class ChatTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Chat',
			description:  'Applies skill effects when a player sends a chat message that matches a specified format or regular expression. This can be used to create abilities that respond to spoken commands or keywords.',
			keywords:     'chat, message, speak, talk, command, keyword, regex, communication',
			data:         [
				new BooleanSelect('Cancel', 'cancel', false)
					.setTooltip('Whether to cancel message or not'),
				new BooleanSelect('Regex', 'regex', false)
					.setTooltip('Whether to use format value as regex or check if message contains format value'),
				new StringSelect('Format', 'format', '')
					.setTooltip('Format of message (if message contains format or regex)')
			],
			summaryItems: ['format', 'cancel']
		});
	}

	public static override new = () => new this();
}

class CleanupTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Cleanup',
			description: 'Applies skill effects when a player disconnects from the server or unlearns the skill. This is useful for removing temporary effects, resetting states, or performing other actions when an ability is no longer active or the player leaves.',
			keywords:    'cleanup, disconnect, logout, unlearn, remove, end, finish, state, reset'
		});
	}

	public static override new = () => new this();
}

class ConsumeTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Consume',
			description:  'Applies skill effects when a player consumes any item, such as food, potions, or other consumables. This can be used for abilities that activate upon eating or drinking.',
			keywords:     'consume, eat, drink, food, potion, consumable, item, use',
			data:         [
				...itemConditionOptions(new DropdownSelect('Material', 'material', getAnyConsumable, 'Any', true)
					.setTooltip('The type of item that the player has consumed.')
					.requireValue('check-mat', [true]))
			],
			summaryItems: ['material', 'potion']
		});
	}

	public static override new = () => new this();
}

class CrouchTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Crouch',
			description:  'Applies skill effects when a player starts or stops crouching (sneaking) using the shift key. This can be used for stealth abilities or actions that require a lowered stance.',
			keywords:     'crouch, sneak, shift, stealth, low stance, defensive, hide',
			data:         [
				new DropdownSelect('Crouching', 'type', ['Start Crouching', 'Stop Crouching', 'Both'])
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class DeathTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Death',
			description: 'Applies skill effects when a player dies. This is useful for abilities that trigger upon death, such as a final explosion, a resurrection effect, or dropping special items.',
			keywords:    'death, die, dead, perish, killed, respawn, revive, sacrifice'
		});
	}

	public static override new = () => new this();
}

class DropItemTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Drop Item',
			description:  'Applies skill effects when a player intentionally drops an item from their inventory. This can be used for abilities that trigger when discarding items or transferring them to the ground.',
			keywords:     'drop, item, discard, throw, inventory, remove',
			data:         [
				new DropdownSelect('Drop multiple', 'drop multiple', ['True', 'False', 'Ignore'], 'Ignore')
					.setTooltip('Whether the player has to drop multiple items or a single item')
			],
			summaryItems: ['drop multiple']
		});
	}

	public static override new = () => new this();
}

class EntityResurrectTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Entity Resurrect',
			description: 'Applies skill effects when an entity, including players or mobs, is resurrected. This is useful for abilities that trigger upon a successful revival, such as granting temporary buffs or creating a visual effect.',
			keywords:    'resurrect, revive, come back, afterlife, reborn, entity, mob, player'
		});
	}

	public static override new = () => new this();
}

class EntityTargetTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Entity Target',
			description:  'Applies skill effects when an entity, typically a mob, targets the skill\'s caster. This is useful for abilities that activate when drawing aggro or becoming the focus of an enemy.',
			keywords:     'entity, target, aggro, focus, mob, enemy, attention, attack, threat',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes children target the caster. False makes children target the attacking entity'),
				new DropdownSelect('Types', 'types', ['Any', ...getEntities()], ['Any'], true)
					.setTooltip('The entity types to target'),
				new BooleanSelect('Blacklist', 'blacklist', false)
					.setTooltip('Whether to consider the listed types as a blacklist, meaning only entities that do NOT match one of them will trigger.')
			],
			summaryItems: ['target', 'types', 'blacklist']
		});
	}

	public static override new = () => new this();
}

class EnvironmentDamageTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Environment Damage',
			description:  'Applies skill effects when a player takes damage from environmental sources, such as falling, drowning, fire, or suffocation. This is useful for abilities that mitigate or react to hazards.',
			keywords:     'environment, damage, fall, drowning, fire, suffocation, lava, hazard, natural damage',
			data:         [
				new DropdownSelect('Type', 'type', getAnyDamageTypes, ['Fall'], true)
					.setTooltip('The source of damage to apply for')
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class ExperienceTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Experience',
			description:  'Applies skill effects when a player earns vanilla experience from natural sources, such as killing mobs or mining. The placeholder {api-experience} can be used to reference the amount of experience collected. This is useful for abilities that scale with or react to experience gain.',
			keywords:     'experience, exp, level, gain, earn, mob, mine, farm, vanilla experience',
			data:         [new DoubleSelect('Min Experience', 'min-experience')
				.setTooltip('The minimum amount of experience to collect to trigger the skill.')],
			summaryItems: ['min-experience']
		});
	}

	public static override new = () => new this();
}

class FishingTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Fishing',
			description: 'Applies skill effects specifically when a player initiates the fishing action by right-clicking with a fishing rod. This trigger is distinct from events related to catching a fish.',
			keywords:    'fishing, fish, rod, cast, right-click, initiate, start'
		});
	}

	public static override new = () => new this();
}

class FishingBiteTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Fishing Bite',
			description: 'Applies skill effects when a fish or other catchable item bites a player\'s fishing rod. This occurs before the player reels in the catch.',
			keywords:    'fishing, bite, fish, catch, bobber, tug, hook, reel'
		});
	}

	public static override new = () => new this();
}

class FishingFailTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Fishing Fail',
			description: 'Applies skill effects when a player fails to catch a fish, typically due to poor timing or reeling too early/late. This can be used for abilities that trigger on fishing mishaps.',
			keywords:    'fishing, fail, missed, miss, timing, too early, too late, reel, mishap'
		});
	}

	public static override new = () => new this();
}

class FishingGrabTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Fishing Grab',
			description: 'Applies skill effects when a player successfully catches a fish or other item with their fishing rod. This is ideal for abilities that reward successful fishing attempts.',
			keywords:    'fishing, grab, catch, hooked, success, loot, item, fish'
		});
	}

	public static override new = () => new this();
}

class FishingGroundTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Fishing Ground',
			description: 'Applies skill effects when a player\'s fishing bobber hits a solid block or the ground instead of water. This can be used for abilities that trigger on casting a line onto land.',
			keywords:    'fishing, ground, bobber, land, hit, block, missed water, cast'
		});
	}

	public static override new = () => new this();
}

class FishingReelTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Fishing Reel',
			description: 'Applies skill effects when a player reels in their fishing rod without catching anything, typically when the bobber is in the air or out of water. This is useful for abilities that trigger on failed retrieval attempts.',
			keywords:    'fishing, reel, retrieve, pull, empty, fail, no catch'
		});
	}

	public static override new = () => new this();
}

class FlagTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Flag',
			description:  'Applies skill effects when a player receives a specific flag from a mechanic. Flags are custom markers that can be used to track states or conditions on a player, enabling complex interactions between skills.',
			keywords:     'flag, status, marker, condition, state, apply, receive, buff, debuff',
			data:         [
				new StringListSelect('Flags', 'flags', ['Any'])
					.setTooltip('The flags to check for, "Any" will trigger regardless of flag name'),
				new IntSelect('Min Duration', 'min-duration', 0)
					.setTooltip('The minimum duration the specified flags must be set for'),
				new BooleanSelect('Inverse', 'inverse', false)
					.setTooltip('Whether to trigger when NOT applying the specified flags')
			],
			summaryItems: ['flags', 'min-duration', 'inverse']
		});
	}

	public static override new = () => new this();
}

class FlagExpireTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Flag Expire',
			description:  'Applies skill effects when a specific flag, previously applied to a player, expires. This is useful for cleaning up effects, resetting temporary states, or triggering follow-up actions after a timed condition ends.',
			keywords:     'flag, expire, end, duration, cleanup, remove, status, condition, time out',
			data:         [
				new StringListSelect('Flags', 'flags', ['Any'])
					.setTooltip('The flags to check for, "Any" will trigger regardless of flag name'),
				new BooleanSelect('Inverse', 'inverse', false)
					.setTooltip('Whether to trigger when NOT applying the specified flags')
			],
			summaryItems: ['flags', 'inverse']
		});
	}

	public static override new = () => new this();
}

class FlightToggleTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Flight Toggle',
			description:  'Applies skill effects when a player starts or stops flying, either through creative mode, abilities, or other game mechanics. This is useful for triggering actions related to changes in a player\'s aerial mobility.',
			keywords:     'flight, fly, toggle, start flying, stop flying, creative, wings, aerial, mobility',
			data:         [
				new DropdownSelect('Flying', 'type', ['Start Flying', 'Stop Flying', 'Both'])
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class GlideTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Glide',
			description:  'Applies skill effects when a player starts or stops gliding with an elytra. This is useful for abilities that enhance aerial movement, provide buffs during flight, or react to a player initiating or ending a glide.',
			keywords:     'glide, elytra, fly, aerial, mobility, parachute, wingsuit, fall, slow fall',
			data:         [
				new DropdownSelect('Gliding', 'type', ['Start Gliding', 'Stop Gliding', 'Both'])
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class HarvestTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Harvest',
			description:  'Applies skill effects when a player harvests a specific block, typically crops like wheat, carrots, potatoes, or Glow Berries. This is useful for farming-related abilities or automated collection systems.',
			keywords:     'harvest, farm, farming, crop, gather, collect, break, grow, glow berries, wheat, carrots, potatoes',
			data:         [new BlockSelect(
				'The type of block expected to be harvested',
				'The expected data value of the block (-1 for any data value)'
			)],
			summaryItems: ['block', 'data']
		});
	}

	public static override new = () => new this();
}

class HealTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Heal',
			description:  'Applies skill effects when a player receives healing from any source, including potions, regeneration, or other abilities. The {api-heal} placeholder provides the healing amount. This is useful for abilities that react to health restoration.',
			keywords:     'heal, healing, health, restore, regeneration, recover, hp, medic',
			data:         [
				new DoubleSelect('Min Heal', 'heal-min', 0)
					.setTooltip('The minimum health that needs to be received'),
				new DoubleSelect('Max Heal', 'heal-max', 999)
					.setTooltip('The maximum health that needs to be received')
			],
			summaryItems: ['heal-min', 'heal-max']
		});
	}

	public static override new = () => new this();
}

class InitializeTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Initialize',
			description: 'Applies skill effects immediately upon a skill being learned or enabled. This is ideal for setting up passive abilities, permanent buffs, or initial conditions that persist throughout the skill\'s duration.',
			keywords:    'initialize, init, start, passive, buff, permanent, setup, load, enable'
		});
	}

	public static override new = () => new this();
}

class ItemSwapTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Item Swap',
			description:  'Applies skill effects when a player presses the item swap key (typically off-hand swap). This can be used for abilities that quickly switch modes, apply temporary buffs, or react to combat stance changes.',
			keywords:     'item, swap, off-hand, quick switch, combat stance, mode change, keybind',
			data:         [
				new BooleanSelect('Cancel Swap', 'cancel', true)
					.setTooltip('True cancels the item swap. False allows the item swap')
			],
			summaryItems: ['cancel']
		});
	}

	public static override new = () => new this();
}

class JumpTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Jump',
			description: 'Applies skill effects whenever a player jumps. This can be used for abilities like double jumps, high jumps, or effects that trigger upon leaving the ground.',
			keywords:    'jump, leap, hop, airborne, leave ground, double jump, high jump',
			data:        []
		});
	}

	public static override new = () => new this();
}

class KillTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Kill',
			description:  'Applies skill effects when a player successfully defeats (kills) another entity. This is useful for abilities that provide rewards, buffs, or special effects upon securing a kill.',
			keywords:     'kill, death, defeat, vanquish, slain, eliminate, execute, bounty',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes children target the caster. False makes children target the attacking entity')
			],
			summaryItems: ['target']
		});
	}

	public static override new = () => new this();
}

class LandTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Land',
			description:  'Applies skill effects when a player lands on the ground after being airborne. This is useful for abilities that trigger on impact, such as a ground pound or a safe landing effect.',
			keywords:     'land, fall, ground, impact, airborne, descent, shockwave, ground pound',
			data:         [new DoubleSelect('Min Distance', 'min-distance')
				.setTooltip('The minimum distance the player should fall before effects activate')],
			summaryItems: ['min-distance']
		});
	}

	public static override new = () => new this();
}

class LaunchTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Launch',
			description:  'Applies skill effects when a player launches any projectile, such as an arrow, snowball, or fire charge. This is useful for abilities that modify projectiles, apply effects to them, or trigger actions upon their firing.',
			keywords:     'launch, projectile, fire, shoot, arrow, snowball, fire charge, throw, cast, ranged',
			data:         [new DropdownSelect('Types', 'types', getAnyProjectiles, 'Any', true)
				.setTooltip('The type of projectile that should be launched')],
			summaryItems: ['types']
		});
	}

	public static override new = () => new this();
}

class LeftClickTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Left Click',
			description:  'Applies skill effects when a player performs a left-click action, typically an attack or interaction. This is a fundamental trigger for many combat abilities or quick actions.',
			keywords:     'left click, attack, melee, punch, action, interact, primary action',
			data:         [new DropdownSelect('Crouch', 'crouch', ['Crouch', 'Dont crouch', 'Both'], 'Crouch')
				.setTooltip('If the player has to be crouching in order for this trigger to function')],
			summaryItems: ['crouch']
		});
	}

	public static override new = () => new this();
}

class MoveTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Move',
			description: 'Applies skill effects every tick a player moves. Due to its high frequency, use this trigger sparingly for performance-intensive abilities. The \'api-moved\' placeholder can be used to access the distance the player has traveled.',
			keywords:    'move, walk, run, sprint, travel, displacement, relocate, tick, constantly'
		});
	}

	public static override new = () => new this();
}

class PhysicalDamageTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Physical Damage',
			description:  'Applies skill effects when a player deals physical damage through conventional means, such as melee attacks, unarmed strikes, or firing a bow. This trigger is distinct from skill-based damage and can be used for abilities that enhance basic attacks.',
			keywords:     'physical, damage, melee, attack, bow, projectile, unarmed, conventional damage, basic attack',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes the children target the caster. False makes children target the damaged entity'),
				new DropdownSelect('Type', 'type', ['Both', 'Melee', 'Projectile'], 'Both')
					.setTooltip('The type of damage dealt'),
				new DoubleSelect('Min Damage', 'dmg-min', 0)
					.setTooltip('The minimum damage that needs to be dealt'),
				new DoubleSelect('Max Damage', 'dmg-max', 999)
					.setTooltip('The maximum damage that needs to be dealt')
			],
			summaryItems: ['target', 'type', 'dmg-min', 'dmg-max']
		});
	}

	public static override new = () => new this();
}

class ProjectileHitTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Projectile Hit',
			description:  'Applies skill effects when a projectile, launched by a player or skill, makes contact with either an entity or a block. This is useful for abilities that trigger on successful hits or environmental interactions with projectiles.',
			keywords:     'projectile, hit, impact, collision, entity, block, arrow, magic missile, shot',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes the children target the caster. False makes children target the damaged entity'),
				new DropdownSelect('Type', 'type', ['Both', 'Entity', 'Block'], 'Both')
					.setTooltip('The type of what projectile hits'),
				new DropdownSelect('Projectile', 'projectile', getAnyProjectiles, ['Any'], true)
					.setTooltip('The type of projectile shot')
			],
			summaryItems: ['target', 'type', 'projectile']
		});
	}

	public static override new = () => new this();
}

class ProjectileTickTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Projectile Tick',
			description:  'Applies skill effects at regular intervals while a projectile is in mid-air. This is useful for creating dynamic projectile effects, such as a trail of particles, area-of-effect pulses, or homing behaviors.',
			keywords:     'projectile, tick, in air, flight, interval, trail, particle, homing, dynamic, mid-air',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes the children target the caster. False makes children target the damaged entity'),
				new IntSelect('Interval', 'interval', 1)
					.setTooltip('Interval between trigger executions'),
				new IntSelect('Delay', 'delay', 0)
					.setTooltip('Delay before executing trigger for the first time'),
				new DropdownSelect('Projectile', 'projectile', getAnyProjectiles, ['Any'], true)
					.setTooltip('The type of projectile shot')
			],
			summaryItems: ['target', 'interval', 'delay', 'projectile']
		});
	}

	public static override new = () => new this();
}

class RightClickTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Right Click',
			description:  'Applies skill effects when a player performs a right-click action, commonly used for interacting with blocks, using items, or activating secondary abilities. Note that when clicking in the air, an item must be held in hand for this to trigger.',
			keywords:     'right click, interact, use item, secondary action, activate, cast, block interaction',
			data:         [
				new DropdownSelect('Crouch', 'crouch', ['Crouch', 'Dont crouch', 'Both'], 'Crouch')
					.setTooltip('If the player has to be crouching in order for this trigger to function')
			],
			summaryItems: ['crouch']
		});
	}

	public static override new = () => new this();
}

class RiptideTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:        'Riptide',
			description: 'Applies skill effects when a player successfully uses a trident with the Riptide enchantment, launching themselves forward in water or during rain. This is useful for abilities that enhance mobility or trigger effects during this unique movement.',
			keywords:    'riptide, trident, enchantment, launch, water, rain, mobility, movement, dash'
		});
	}

	public static override new = () => new this();
}

class ShearTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Shear',
			description:  'Applies skill effects when a player uses shears on an entity, such as sheep, mooshrooms, or snow golems. This is useful for abilities that interact with resource gathering from mobs or special entity interactions.',
			keywords:     'shear, sheep, mooshroom, snow golem, resource, gather, interact, mob, entity',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes children target the caster. False makes children target the sheared entity'),
				new DropdownSelect('Types', 'types', ['Any', ...getEntities()], ['Any'], true)
					.setTooltip('The entity types to target'),
				new BooleanSelect('Blacklist', 'blacklist', false)
					.setTooltip('Whether to consider the listed types as a blacklist, meaning only entities that do NOT match one of them will trigger.')
			],
			summaryItems: ['target', 'types', 'blacklist']
		});
	}

	public static override new = () => new this();
}

class SkillCastTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Skill Cast',
			description:  'Applies skill effects when a player casts *any* skill. This can be used to create meta-abilities that react to skill usage, such as consuming mana, applying a global cooldown, or triggering a combined effect.',
			keywords:     'skill, cast, ability, spell, activate, use, global cooldown, mana, combo',
			data:         [
				new BooleanSelect('Cancel Cast', 'cancel', false)
					.setTooltip('True cancels the skill cast. False allows the skill cast'),
				new StringListSelect('Classes', 'allowed-classes')
					.setTooltip('The list of classes which will trigger this effect. Leave blank to allow all to trigger. Use \'!xxx\' to exclude'),
				new StringListSelect('Skills', 'allowed-skills')
					.setTooltip('The list of skills which will trigger this effect. Leave blank to allow all to trigger. Use \'!xxx\' to exclude')
			],
			summaryItems: ['cancel', 'allowed-classes', 'allowed-skills']
		});
	}

	public static override new = () => new this();
}

class SkillDamageTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Skill Damage',
			description:  'Applies skill effects when a player deals damage using a skill or ability. This is useful for abilities that trigger on successful offensive skill use, such as applying secondary effects or generating resources.',
			keywords:     'skill, damage, ability, spell, offensive, attack, hit, deal damage',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes children target the caster. False makes children target the damaged entity'),
				new DoubleSelect('Min Damage', 'dmg-min', 0)
					.setTooltip('The minimum damage that needs to be dealt'),
				new DoubleSelect('Max Damage', 'dmg-max', 999)
					.setTooltip('The maximum damage that needs to be dealt'),
				new StringListSelect('Category', 'category', ['default'])
					.setTooltip('The type of skill damage to apply for. Leave this empty to apply to all skill damage')
			],
			summaryItems: ['target', 'dmg-min', 'dmg-max', 'category']
		});
	}

	public static override new = () => new this();
}

class SprintTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Sprint',
			description:  'Applies skill effects when a player starts or stops sprinting. This is useful for abilities that grant bonuses during high-speed movement or trigger effects upon entering/exiting a sprint.',
			keywords:     'sprint, run, fast, speed, movement, toggle, start sprinting, stop sprinting',
			data:         [
				new DropdownSelect('Sprinting', 'type', ['Start Sprinting', 'Stop Sprinting', 'Both'])
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class StripLogTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Strip Log',
			description:  'Applies skill effects when a player strips a log block (e.g., using an axe). This is useful for abilities related to logging, woodworking, or specialized resource gathering.',
			keywords:     'strip, log, axe, wood, logging, woodworking, resource, gather, craft',
			data:         [new BlockSelect(
				'The type of block expected to be broken',
				'The expected data value of the block (-1 for any data value)'
			)],
			summaryItems: ['block']
		});
	}

	public static override new = () => new this();
}

class TookPhysicalTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Took Physical Damage',
			description:  'Applies skill effects when a player takes physical damage from conventional sources, such as melee attacks from mobs or non-skill projectiles. This is useful for abilities that trigger defensively, like counter-attacks or temporary invulnerability.',
			keywords:     'took damage, physical, melee, projectile, defensive, hit, injured, combat',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes children target the caster. False makes children target the attacking entity'),
				new DropdownSelect('Type', 'type', ['Both', 'Melee', 'Projectile'], 'Both')
					.setTooltip('The type of damage dealt'),
				new DoubleSelect('Min Damage', 'dmg-min', 0)
					.setTooltip('The minimum damage that needs to be dealt'),
				new DoubleSelect('Max Damage', 'dmg-max', 999)
					.setTooltip('The maximum damage that needs to be dealt')
			],
			summaryItems: ['target', 'type', 'dmg-min', 'dmg-max']
		});
	}

	public static override new = () => new this();
}

class TookSkillTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Took Skill Damage',
			description:  'Applies skill effects when a player takes damage from a skill or ability originating from another source (not their own skill). This is useful for abilities that trigger in response to enemy skill attacks, such as defensive counter-measures or debuffs.',
			keywords:     'took damage, skill, ability, spell, defensive, hit, injured, combat, magic damage',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes children target the caster. False makes children target the attacking entity'),
				new DoubleSelect('Min Damage', 'dmg-min', 0)
					.setTooltip('The minimum damage that needs to be dealt'),
				new DoubleSelect('Max Damage', 'dmg-max', 999)
					.setTooltip('The maximum damage that needs to be dealt'),
				new StringListSelect('Category', 'category', ['default'])
					.setTooltip('The type of skill damage to apply for. Leave this empty to apply to all skill damage')
			],
			summaryItems: ['target', 'dmg-min', 'dmg-max', 'category']
		});
	}

	public static override new = () => new this();
}

class ShieldTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Shield',
			description:  'Applies skill effects when a player successfully blocks incoming damage using a shield. The {api-blocked} placeholder provides the amount of damage absorbed. This is useful for abilities that trigger defensive responses, such as a counter-attack or a temporary buff upon successful block.',
			keywords:     'shield, block, guard, defend, absorb, damage reduction, counter, defensive',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes children target the caster. False makes children target the attacking entity'),
				new DropdownSelect('Type', 'type', ['Both', 'Melee', 'Projectile'], 'Both')
					.setTooltip('The type of damage dealt'),
				new DoubleSelect('Damage Blocked', 'dmg-min', 0)
					.setTooltip('The minimum damage that needs to be blocked'),
				new DoubleSelect('Damage Blocked', 'dmg-max', 999)
					.setTooltip('The maximum damage that needs to be blocked')
			],
			summaryItems: ['target', 'type', 'dmg-min', 'dmg-max']
		});
	}

	public static override new = () => new this();
}

class SignalTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'Signal',
			description:  'Applies skill effects when a player receives a custom signal emitted from a "Signal Emit" mechanic. This enables complex inter-skill communication and custom event handling.',
			keywords:     'signal, custom event, emit, receive, communicate, trigger, mechanic, inter-skill',
			data:         [
				new BooleanSelect('Target Receiver', 'target', true)
					.setTooltip('True makes children target the receiver. False makes children target the emitter'),
				new StringSelect('Signal', 'signal')
					.setTooltip('Name of signal want to receive.')
			],
			summaryItems: ['target', 'signal']
		});
	}

	public static override new = () => new this();
}

class WorldChangeTrigger extends FabledTrigger {
	public constructor() {
		super({
			name:         'World Change',
			description:  'Applies skill effects when a player transitions between different worlds (e.g., Overworld, Nether, End). This is useful for abilities that provide buffs/debuffs or special effects specific to certain dimensions.',
			keywords:     'world, change, dimension, travel, portal, nether, end, overworld, transition, environment',
			data:         [
				new StringListSelect('Worlds', 'worlds', ['Any'])
					.setTooltip('The worlds to check for, "Any" will trigger regardless of world'),
				new BooleanSelect('Inverse', 'inverse', false)
					.setTooltip('Whether to trigger when NOT changing to the specified worlds'),
				new DropdownSelect('Direction', 'direction', ['To', 'From', 'Both'], 'To')
					.setTooltip('The direction of the world change to be considered')
			],
			summaryItems: ['worlds', 'inverse', 'direction']
		});
	}

	public static override new = () => new this();
}

// TARGETS

const targetOptions = (includeMax = true): ComponentOption[] => {
	const options: ComponentOption[] = [
		new DropdownSelect('Group', 'group', ['Ally', 'Enemy', 'Both'], 'Enemy')
			.setTooltip('The alignment of targets to get'),
		new BooleanSelect('Through Wall', 'wall', false)
			.setTooltip('Whether to allow targets to be on the other side of a wall'),
		new BooleanSelect('Include Invulnerable', 'invulnerable', false)
			.setTooltip('Whether to target on invulnerable entities'),
		new DropdownSelect('Include Caster', 'caster', ['True', 'False', 'In area'], 'False')
			.setTooltip('Whether to include the caster in the target list. "True" will always include them, "False" will never, and "In area" will only if they are within the targeted area')
	];

	if (includeMax) {
		options.push(
			new AttributeSelect('Max Targets', 'max', 99)
				.setTooltip('The max amount of targets to apply children to')
		);
	}

	return options;
};

const particlesAtTargetPreviewOptions = (): ComponentOption[] => {
	return [
		new SectionMarker('Particles at target'),
		new BooleanSelect('Particles at target', 'per-target', false)
			.setTooltip('Displays particles at the location of the current targets'),
		...particlePreviewOptions('per-target'),
		new DropdownSelect('Arrangement', 'per-target-arrangement', ['Sphere', 'Circle', 'Hemisphere'], 'Sphere')
			.requireValue('per-target', [true])
			.setTooltip('The arrangement to use for the particles. Circle is a 2D circle, Hemisphere is half a 3D sphere, and Sphere is a 3D sphere'),
		new DropdownSelect('Circle Direction', 'per-target-direction', ['XY', 'XZ', 'YZ'], 'XZ')
			.requireValue('per-target' + '-arrangement', ['Circle'])
			.requireValue('per-target', [true])
			.setTooltip('The orientation of the circle. XY and YZ are vertical circles while XZ is a horizontal circle'),
		new AttributeSelect('Radius', 'per-target-radius', 0.5)
			.requireValue('per-target', [true])
			.setTooltip('The radius of the arrangement in blocks'),
		new BooleanSelect('Increase size by hitbox', 'per-target-hitbox', true)
			.requireValue('per-target', [true])
			.setTooltip('Increases the \'radius\' parameter by the size of the target\'s hitbox'),
		new AttributeSelect('Points', 'per-target-particles', 20)
			.requireValue('per-target', [true])
			.setTooltip('The amount of points that conform the chosen arrangement')
	];
};

class AreaTarget extends FabledTarget {
	public constructor() {
		super({
			name:         'Area',
			description:  'Targets all eligible units within a specified radius around the current target (which is typically the casting player by default). This is useful for area-of-effect (AoE) abilities.',
			keywords:     'area, aoe, radius, nearby, proximity, multiple targets, splash, zone',
			data:         [
				new AttributeSelect('Radius', 'radius', 3)
					.setTooltip('The radius of the area to target in blocks'),
				...targetOptions(),
				new BooleanSelect('Random', 'random', false)
					.setTooltip('Whether to randomize the targets selected')
			],
			preview:      [
				...particlesAtTargetPreviewOptions(),
				new SectionMarker('Circle Preview'),
				new BooleanSelect('Circle Preview', 'circle', false)
					.setTooltip('Displays particles as a circle around the targeted area'),
				new DoubleSelect('Density', 'circle-density', 1)
					.setTooltip('The minimum amount of points to display per meter')
					.requireValue('circle', [true]),
				...particlePreviewOptions('circle'),
				new SectionMarker('Sphere Preview'),
				new BooleanSelect('Sphere Preview', 'sphere', false)
					.setTooltip('Displays particles as a sphere around the targeted area'),
				new DoubleSelect('Density', 'sphere-density', 1)
					.setTooltip('The minimum amount of points to display per meter')
					.requireValue('sphere', [true]),
				...particlePreviewOptions('sphere')
			],
			summaryItems: ['radius', 'group', 'wall', 'caster', 'max', 'random']
		});
	}

	public static override new = () => new this();
}

class ConeTarget extends FabledTarget {
	public constructor() {
		super({
			name:         'Cone',
			description:  'Targets all eligible units within a specified cone-shaped area extending from the current target (typically the casting player by default). This is ideal for frontal area-of-effect abilities or directed attacks.',
			keywords:     'cone, frontal aoe, directed, area, multiple targets, forward, fan, sweep',
			data:         [
				new AttributeSelect('Range', 'range', 5)
					.setTooltip('The max distance away any target can be in blocks'),
				new AttributeSelect('Angle', 'angle', 90)
					.setTooltip('The angle of the cone arc in degrees'),
				new AttributeSelect('Rotation', 'rotation', 0)
					.setTooltip('The rotation of the cone in degrees'),
				new BooleanSelect('Reset Y', 'reset-y', true)
					.setTooltip('Whether to remove the Y component of the caster/target when determining targets'),
				...targetOptions()
			],
			preview:      [
				...particlesAtTargetPreviewOptions(),
				new SectionMarker('Triangle Preview'),
				new BooleanSelect('Triangle Preview', 'triangle', false)
					.setTooltip('Displays particles as a two lines on both sides around the targeted area'),
				new DoubleSelect('Density', 'triangle-density', 1)
					.setTooltip('The minimum amount of points to display per meter')
					.requireValue('triangle', [true]),
				new DoubleSelect('Start distance', 'triangle-start-distance', 2)
					.setTooltip('How far from the target\'s face to start drawing the preview, in meters')
					.requireValue('triangle', [true]),
				...particlePreviewOptions('triangle'),
				new SectionMarker('Cone Preview'),
				new BooleanSelect('Cone Preview', 'cone', false)
					.setTooltip('Displays particles as a cone around the targeted area'),
				new DoubleSelect('Density', 'cone-density', 1)
					.setTooltip('The minimum amount of points to display per meter')
					.requireValue('cone', [true]),
				new DoubleSelect('Start distance', 'cone-start-distance', 2)
					.setTooltip('How far from the target\'s face to start drawing the preview, in meters')
					.requireValue('cone', [true]),
				...particlePreviewOptions('cone')
			],
			summaryItems: ['range', 'angle', 'group', 'wall', 'caster', 'max']
		});
	}

	public static override new = () => new this();
}

class LinearTarget extends FabledTarget {
	public constructor() {
		super({
			name:         'Linear',
			description:  'Targets all eligible units in a straight line extending forward from the current target (typically the casting player by default). This is useful for beam-like abilities or piercing attacks.',
			keywords:     'linear, line, straight, beam, pierce, pierce through, column, row, forward',
			data:         [
				new AttributeSelect('Range', 'range', 5)
					.setTooltip('The max distance away any target can be in blocks'),
				new AttributeSelect('Tolerance', 'tolerance')
					.setTooltip('How much to expand the potential entity\'s hit-box in all directions, in blocks. This makes it easier to aim'),
				...targetOptions()
			],
			preview:      [
				...particlesAtTargetPreviewOptions(),
				new SectionMarker('Line Preview'),
				new BooleanSelect('Line Preview', 'line', false)
					.setTooltip('Displays particles as a line of particles in front of the caster'),
				new DoubleSelect('Density', 'line-density', 1)
					.setTooltip('The minimum amount of points to display per meter')
					.requireValue('line', [true]),
				new DoubleSelect('Start distance', 'line-start-distance', 2)
					.setTooltip('How far from the target\'s face to start drawing the preview, in meters')
					.requireValue('line', [true]),
				...particlePreviewOptions('line'),
				new SectionMarker('Cylinder Preview'),
				new BooleanSelect('Cylinder Preview', 'cylinder', false)
					.setTooltip('Displays particles as a cylinder of particles in front of the caster, showing the component\'s tolerance'),
				new DoubleSelect('Density', 'cylinder-density', 1)
					.setTooltip('The minimum amount of points to display per meter')
					.requireValue('cylinder', [true]),
				new DoubleSelect('Start distance', 'cylinder-start-distance', 2)
					.setTooltip('How far from the target\'s face to start drawing the preview, in meters')
					.requireValue('cylinder', [true]),
				...particlePreviewOptions('cylinder')
			],
			summaryItems: ['range', 'tolerance', 'group', 'wall', 'caster', 'max']
		});
	}

	public static override new = () => new this();
}

class LocationTarget extends FabledTarget {
	public constructor() {
		super({
			name:         'Location',
			description:  'Targets a specific block location that the target or caster is currently looking at. This can be combined with other targeting types to create ranged area effects, ground-targeted abilities, or precision strikes.',
			keywords:     'location, look, gaze, point, ground target, precision, ranged, area effect, block, position',
			data:         [
				new AttributeSelect('Range', 'range', 5)
					.setTooltip('The max distance the location can be from the target\'s eyes'),
				new BooleanSelect('Entities', 'entities', true)
					.setTooltip('True to account for entities, or false to pass through them'),
				new BooleanSelect('Fluids', 'fluids', false)
					.setTooltip('True to account for fluids (water and lava), or false to pass through them'),
				new BooleanSelect('Passable blocks', 'passable', true)
					.setTooltip('True to account for passable or non-collidable blocks (grass, saplings, etc), or false to pass through them'),
				new BooleanSelect('Center', 'center', false)
					.setTooltip('Whether to move the hit location to the center of the block')
			],
			preview:      [
				...particlesAtTargetPreviewOptions()
			],
			summaryItems: ['range', 'entities', 'fluids', 'passable']
		});
	}

	public static override new = () => new this();
}

class NearestTarget extends FabledTarget {
	public constructor() {
		super({
			name:         'Nearest',
			description:  'Targets the closest eligible unit(s) within a specified radius from the current target (typically the casting player by default). This is useful for auto-targeting abilities or effects that prioritize nearby threats/allies.',
			keywords:     'nearest, closest, proximity, auto target, single target, nearby, ally, enemy',
			data:         [
				new AttributeSelect('Range', 'range', 3)
					.setTooltip('The radius of the area to target in blocks'),
				...targetOptions(false)
			],
			preview:      [
				...particlesAtTargetPreviewOptions(),
				new SectionMarker('Circle Preview'),
				new BooleanSelect('Circle Preview', 'circle', false)
					.setTooltip('Displays particles as a circle around the targeted area'),
				new DoubleSelect('Density', 'circle-density', 1)
					.setTooltip('The minimum amount of points to display per meter')
					.requireValue('circle', [true]),
				...particlePreviewOptions('circle'),
				new SectionMarker('Sphere Preview'),
				new BooleanSelect('Sphere Preview', 'sphere', false)
					.setTooltip('Displays particles as a sphere around the targeted area'),
				new DoubleSelect('Density', 'sphere-density', 1)
					.setTooltip('The minimum amount of points to display per meter')
					.requireValue('sphere', [true]),
				...particlePreviewOptions('sphere')
			],
			summaryItems: ['range', 'group', 'wall', 'caster']
		});
	}

	public static override new = () => new this();
}

class OffsetTarget extends FabledTarget {
	public constructor() {
		super({
			name:         'Offset',
			description:  'Targets a specific location or entity by applying a directional offset relative to each existing target. This is useful for placing effects, summoning entities, or targeting areas adjacent to current targets.',
			keywords:     'offset, relative, position, location, spawn, summon, displace, shift, move, custom position',
			data:         [
				new SectionMarker('Offset'),
				new AttributeSelect('Forward', 'forward')
					.setTooltip('The offset from the target in the direction they are facing. Negative numbers go backwards'),
				new AttributeSelect('Upward', 'upward', 2, 0.5)
					.setTooltip('The offset from the target upwards. Negative numbers go below them'),
				new AttributeSelect('Right', 'right')
					.setTooltip('The offset from the target to their right. Negative numbers go to the left')
			],
			preview:      [
				...particlesAtTargetPreviewOptions()
			],
			summaryItems: ['forward', 'upward', 'right']
		});
	}

	public static override new = () => new this();
}

class PartyTarget extends FabledTarget {
	public constructor() {
		super({
			name:         'Party',
			description:  'Targets all players who are currently in the caster\'s party. This component requires the "FabledParties" plugin to be installed. It is useful for cooperative abilities that affect allies.',
			keywords:     'party, allies, cooperative, team, group, FabledParties, friends',
			data:         [
				new AttributeSelect('Range', 'range', 5)
					.setTooltip('The max distance the location can be from the target\'s eyes'),
				...targetOptions().splice(1)
			],
			preview:      [
				...particlesAtTargetPreviewOptions()
			],
			summaryItems: ['range', 'max']
		});
	}

	public static override new = () => new this();
}

class RememberTarget extends FabledTarget {
	public constructor() {
		super({
			name:         'Remember',
			description:  'Targets entities that were previously stored using the "Remember Targets" mechanic, identified by a unique key. This allows for complex multi-stage abilities that track specific entities over time.',
			keywords:     'remember, stored, recalled, saved, tracked, custom, key, multi-stage',
			data:         [
				new StringSelect('Key', 'key', 'target')
					.setTooltip('The unique key for the target group that should match that used by the "Remember Targets" skill')
			],
			preview:      [
				...particlesAtTargetPreviewOptions()
			],
			summaryItems: ['key']
		});
	}

	public static override new = () => new this();
}

class SelfTarget extends FabledTarget {
	public constructor() {
		super({
			name:        'Self',
			description: 'Ensures that the skill\'s effects are applied directly to the caster, regardless of any previous targeting. This is useful for self-buffs, personal healing, or abilities that only affect the origin.',
			keywords:    'self, caster, personal, individual, origin, buff, heal, personal effect'
		});
	}

	public static override new = () => new this();
}

class SingleTarget extends FabledTarget {
	public constructor() {
		super({
			name:         'Single',
			description:  'Targets a single eligible unit directly in front of the current target (typically the casting player by default). This is the most common targeting type for single-target offensive or supportive abilities.',
			keywords:     'single target, direct, frontal, precision, one target, offensive, supportive, point and click',
			data:         [
				new AttributeSelect('Range', 'range', 5)
					.setTooltip('The max distance away any target can be in blocks'),
				new AttributeSelect('Tolerance', 'tolerance')
					.setTooltip('How much to expand the potential entity\'s hitbox in all directions, in blocks. This makes it easier to aim'),
				new DropdownSelect('Group', 'group', ['Ally', 'Enemy', 'Both'], 'Enemy')
					.setTooltip('The alignment of targets to get'),
				new BooleanSelect('Through Wall', 'wall', false)
					.setTooltip('Whether to allow targets to be on the other side of a wall')
			],
			preview:      [
				...particlesAtTargetPreviewOptions(),
				new SectionMarker('Line Preview'),
				new BooleanSelect('Line Preview', 'line', false)
					.setTooltip('Displays particles as a line of particles in front of the caster'),
				new DoubleSelect('Density', 'line-density', 1)
					.setTooltip('The minimum amount of points to display per meter')
					.requireValue('line', [true]),
				new DoubleSelect('Start distance', 'line-start-distance', 2)
					.setTooltip('How far from the target\'s face to start drawing the preview, in meters')
					.requireValue('line', [true]),
				...particlePreviewOptions('line'),
				new SectionMarker('Cylinder Preview'),
				new BooleanSelect('Cylinder Preview', 'cylinder', false)
					.setTooltip('Displays particles as a cylinder of particles in front of the caster, showing the component\'s tolerance'),
				new DoubleSelect('Density', 'cylinder-density', 1)
					.setTooltip('The minimum amount of points to display per meter')
					.requireValue('cylinder', [true]),
				new DoubleSelect('Start distance', 'cylinder-start-distance', 2)
					.setTooltip('How far from the target\'s face to start drawing the preview, in meters')
					.requireValue('cylinder', [true]),
				...particlePreviewOptions('cylinder')
			],
			summaryItems: ['range', 'tolerance', 'group', 'wall']
		});
	}

	public static override new = () => new this();
}

class WorldTarget extends FabledTarget {
	public constructor() {
		super({
			name:         'World',
			description:  'Targets all eligible entities throughout the caster\'s current world. This is primarily used for global effects, world-wide events, or abilities that affect every entity in the dimension.',
			keywords:     'world, global, all entities, all mobs, dimension, wide, area',
			data:         [
				...targetOptions()
			],
			summaryItems: ['group', 'wall', 'caster', 'max']
		});
	}

	public static override new = () => new this();
}

// CONDITIONS

/**
 * Adds the options for item-check related effects to the component
 */
const itemConditionOptions = (matOption: ComponentOption = new MaterialSelect(false, 'Arrow')
	.requireValue('check-mat', [true])
	.setTooltip('The type the item needs to be')): ComponentOption[] => {
	return [
		new BooleanSelect('Check Material', 'check-mat', true)
			.setTooltip('Whether the item needs to be a certain type'),
		matOption,
		new DropdownSelect('Potion', 'potion', getAnyPotion, 'Any')
			.requireValue('material', ['Potion', 'Lingering potion', 'Splash potion'])
			.setTooltip('The type of potion being consumed'),
		new BooleanSelect('Check Data', 'check-data', false)
			.setTooltip('Whether the item needs to have a certain data value'),
		new IntSelect('Data', 'data')
			.requireValue('check-data', [true])
			.setTooltip('The data value the item must have'),
		new BooleanSelect('Check Custom Data', 'check-custom-data', false)
			.setTooltip('Whether the item needs to have a certain custom model data value'),
		new IntSelect('Custom Data', 'custom-data')
			.requireValue('check-custom-data', [true])
			.setTooltip('The custom model data value the item must have'),
		new BooleanSelect('Check Lore', 'check-lore', false)
			.setTooltip('Whether the item requires a bit of text in its lore'),
		new StringSelect('Lore', 'lore', 'text')
			.requireValue('check-lore', [true])
			.setTooltip('The text the item requires in its lore'),
		new BooleanSelect('Check Name', 'check-name', false)
			.setTooltip('Whether the item needs to have a bit of text in its display name'),
		new StringSelect('Name', 'name', 'name')
			.requireValue('check-name', [true])
			.setTooltip('The text the item requires in its display name'),
		new BooleanSelect('Regex', 'regex', false)
			.setTooltip('Whether the name and lore checks are regex strings. If you do not know what regex is, leave this option alone')
	];
};

class ActionBarCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Action Bar',
			description:  'Applies child components based on whether the action bar (the bar above the hotbar) is currently displaying a message or not. This is useful for conditional effects tied to UI feedback.',
			keywords:     'action bar, ui, display, message, hud, feedback, status, cast bar',
			data:         [
				new BooleanSelect('Casting', 'casting', true)
					.setTooltip('Whether the Action Bar should be showing or not. True for yes, False for no.')
			],
			summaryItems: ['casting']
		});
	}

	public static override new = () => new this();
}

class AirCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Air',
			description:  'Applies child components based on the entity\'s remaining air supply, measured in seconds, within a specified range. This is useful for abilities that are restricted or empowered when an entity is underwater or suffocating.',
			keywords:     'air, oxygen, breath, underwater, drown, suffocate, remaining air, time',
			data:         [
				new AttributeSelect('Min', 'min')
					.setTooltip('The minimum number of seconds of remaining air the entity has.'),
				new AttributeSelect('Max', 'max')
					.setTooltip('The maximum number of seconds of remaining air the entity has.')
			],
			summaryItems: ['min', 'max']
		});
	}

	public static override new = () => new this();
}

class AltitudeCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Altitude',
			description:  'Applies child components based on the player\'s current height level (Y-coordinate) within a specified range. This is useful for abilities that are restricted to certain elevations, like mountain-top spells or underwater specializations.',
			keywords:     'altitude, height, elevation, y-coordinate, level, high, low, vertical',
			data:         [
				new AttributeSelect('Min', 'min')
					.setTooltip('The minimum height a player has to be on'),
				new AttributeSelect('Max', 'max')
					.setTooltip('The maximum height a player can be on')
			],
			summaryItems: ['min', 'max']
		});
	}

	public static override new = () => new this();
}

class ArmorCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Armor',
			description:  'Applies child components based on whether the target is wearing a specific type of armor or an armor item matching detailed criteria. This is useful for abilities that require certain gear, provide bonuses for wearing full sets, or react to an opponent\'s equipment.',
			keywords:     'armor, gear, equipment, helmet, chestplate, leggings, boots, protection, set bonus, item check',
			data:         [
				new DropdownSelect('Armor', 'armor', ['Any', 'Helmet', 'Chestplate', 'Leggings', 'Boots'])
					.setTooltip('The type of armor to check'),
				...itemConditionOptions()
			],
			summaryItems: ['armor', 'material', 'data', 'lore', 'name']
		});
	}

	public static override new = () => new this();
}

class AttackIndicatorCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Attack Indicator',
			description:  'Applies child components based on whether the target\'s attack indicator (charge bar) is filled to a certain extent. This is particularly useful for abilities that depend on the timing or power of a melee attack.',
			keywords:     'attack indicator, charge, attack power, melee, combat, timing, full charge',
			data:         [
				new AttributeSelect('Min', 'min', 0)
					.setTooltip('The minimum amount of charge the target requires (0-1)'),
				new AttributeSelect('Max', 'max', 1)
					.setTooltip('The maximum amount of charge the target requires (0-1)')
			],
			summaryItems: ['min', 'max']
		});
	}

	public static override new = () => new this();
}

class AttributeCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Attribute',
			description:  'Applies child components based on whether the target possesses a specific attribute (e.g., strength, vitality) within a defined numerical range. This is useful for abilities that scale with or require certain character statistics.',
			keywords:     'attribute, stats, strength, vitality, dexterity, intelligence, health, mana, check, requirement',
			data:         [
				new StringSelect('Attribute', 'attribute', 'Vitality')
					.setTooltip('The name of the attribute you are checking the value of'),
				new AttributeSelect('Min', 'min')
					.setTooltip('The minimum amount of the attribute the target requires'),
				new AttributeSelect('Max', 'max', 999)
					.setTooltip('The maximum amount of the attribute the target requires')
			],
			summaryItems: ['attribute', 'min', 'max']
		});
	}

	public static override new = () => new this();
}

class BiomeCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Biome',
			description:  'Applies child components based on whether the target is currently located within a specified biome. This is useful for abilities that are biome-specific, such as buffs in a desert or debuffs in a cold biome.',
			keywords:     'biome, environment, location, weather, desert, forest, ocean, plains, mountain, specific area',
			data:         [
				new DropdownSelect('Type', 'type', ['In Biome', 'Not In Biome'], 'In Biome')
					.setTooltip('Whether the target should be in the biome. If checking for in the biome, they must be in any one of the checked biomes. If checking for the opposite, they must not be in any of the checked biomes'),
				new DropdownSelect('Biome', 'biome', getBiomes, ['Forest'], true)
					.setTooltip('The biomes to check for. The expectation would be any of the selected biomes need to match')
			],
			summaryItems: ['type', 'biome']
		});
	}


	public static override new = () => new this();
}

class BlockCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Block',
			description:  'Applies child components based on the type of block the target is currently standing on or within. This is useful for abilities that interact with specific terrain types or block interactions.',
			keywords:     'block, terrain, ground, material, standing on, in block, specific block, interaction',
			data:         [
				new DropdownSelect('Type', 'standing', ['On Block', 'Not On Block', 'In Block', 'Not In Block'])
					.setTooltip('Specifies which block to check and whether it should match the selected material. "On Block" is directly below the player while "In Block" is the block a player\'s feet are in'),
				new MaterialSelect()
					.setTooltip('The type of the block to require the targets to stand on')
			],
			summaryItems: ['standing', 'material']
		});
	}


	public static override new = () => new this();
}

class BlockingCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Blocking',
			description:  'Applies child components based on whether the target player is actively blocking with a shield. This is useful for abilities that interact with defensive stances, such as breaking a block or empowering attacks against blocking targets.',
			keywords:     'blocking, shield, defend, guard, defensive stance, parry, absorb, counter',
			data:         [
				new BooleanSelect('Blocking', 'blocking', true)
					.setTooltip('Whether the player should be blocking')
			],
			summaryItems: ['blocking']
		});
	}

	public static override new = () => new this();
}

class BurningCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Burning',
			description:  'Applies child components based on whether the caster is currently burning (on fire). This is useful for abilities that are empowered by fire, grant immunity to it, or react to the caster\'s fiery status.',
			keywords:     'burning, fire, on fire, flame, ignite, hot, status, debuff',
			data:         [
				new DropdownSelect('Type', 'burn', ['Burn', 'Dont burn'], 'Burn')
					.setTooltip('Specifies whether the player has to be burning for this skill to be performed')
			],
			summaryItems: ['burn']
		});
	}

	public static override new = () => new this();
}

class CeilingCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Ceiling',
			description:  'Applies child components based on the height of the clear space (ceiling) directly above each target. This is useful for abilities that are restricted by overhead obstacles or require open vertical space.',
			keywords:     'ceiling, overhead, height, obstacle, vertical space, clear space, above, block',
			data:         [
				new AttributeSelect('Distance', 'distance', 5)
					.setTooltip('How high to check for the ceiling'),
				new BooleanSelect('At least', 'at-least', true)
					.setTooltip('When true, the ceiling must be at least the give number of blocks high. If false, the ceiling must be lower than the given number of blocks')
			],
			summaryItems: ['distance', 'at-least']
		});
	}

	public static override new = () => new this();
}

class ChanceCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Chance',
			description:  'Applies child components based on a percentage chance roll. This is useful for abilities that have a random element, such as proc effects, critical strikes, or 확률-based outcomes.',
			keywords:     'chance, probability, random, percentage, proc, critical, luck, rng',
			data:         [
				new AttributeSelect('Chance', 'chance', 25)
					.setTooltip('The chance to execute children as a percentage. "25" would be 25%')
			],
			summaryItems: ['chance']
		});
	}

	public static override new = () => new this();
}

class ClassCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Class',
			description:  'Applies child components based on whether the target belongs to a specified class or a profession of that class. This is crucial for class-specific abilities, skill trees, or tier-based progressions.',
			keywords:     'class, profession, subclass, skill tree, tier, progression, role, archetype',
			data:         [
				new ClassSelect('Class', 'class', false)
					.setTooltip('The class the player should be'),
				new BooleanSelect('Exact', 'exact', false)
					.setTooltip('Whether the player must be exactly the given class. If false, they can be a later profession of the class')
			],
			summaryItems: ['class', 'exact']
		});
	}

	public static override new = () => new this();
}

class ClassLevelCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Class Level',
			description:  'Applies child components based on whether the caster\'s class level for the skill\'s associated class falls within a specified range. This is useful for abilities that unlock or scale with progression in a specific class.',
			keywords:     'class level, progression, skill level, rank, tier, experience, character level, requirement',
			data:         [
				new IntSelect('Min Level', 'min-level', 2)
					.setTooltip('The minimum class level the player should be. If the player has multiple classes, this will be of their main class'),
				new IntSelect('Max Level', 'max-level', 99)
					.setTooltip('The maximum class level the player should be. If the player has multiple classes, this will be of their main class'),
				new StringSelect('Group', 'group', 'main')
					.setTooltip('The specified group to check the class level for. If set to main will choose the main class group.')
			],
			summaryItems: ['min-level', 'max-level', 'group']
		});
	}

	public static override new = () => new this();
}

class ColorCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Color',
			description:  'Applies child components based on the color of the target entity. Currently, this condition is primarily effective for color-changing mobs like sheep and shulkers. Useful for abilities that interact with or are specific to certain colored entities.',
			keywords:     'color, dye, sheep, shulker, entity, mob, specific color, visual',
			data:         [
				new DropdownSelect('Color', 'color', [
					...getDyes()
				], ['WHITE'], true)
					.setTooltip('The color the entity must be')
			],
			summaryItems: ['color']
		});
	}

	public static override new = () => new this();
}

class CombatCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Combat',
			description:  'Applies child components based on whether the target is currently in or out of combat. This is useful for abilities that have different effects during combat engagements versus peaceful exploration, such as combat-only buffs or out-of-combat regeneration.',
			keywords:     'combat, in combat, out of combat, fight, battle, engagement, aggression, pvp, pve',
			data:         [
				new BooleanSelect('In Combat', 'combat', true)
					.setTooltip('Whether the target should be in or out of combat'),
				new DoubleSelect('Seconds', 'seconds', 10)
					.setTooltip('The time in seconds since the last combat activity before something is considered not in combat')
			],
			summaryItems: ['combat', 'seconds']
		});
	}

	public static override new = () => new this();
}

class CrouchCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Crouch',
			description:  'Applies child components based on whether the target player is currently crouching (sneaking). This is useful for abilities that are restricted to stealthy movement or provide bonuses while in a lowered stance.',
			keywords:     'crouch, sneak, shift, stealth, low stance, defensive, hide, movement',
			data:         [
				new BooleanSelect('Crouching', 'crouch', true)
					.setTooltip('Whether the player should be crouching')
			],
			summaryItems: ['crouch']
		});
	}

	public static override new = () => new this();
}

class DirectionCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Direction',
			description:  'Applies child components based on the relative facing direction between the caster and the target. This is useful for abilities that require precise positioning, backstabs, or frontal attacks.',
			keywords:     'direction, facing, orientation, relative, position, backstab, frontal, precise, angle',
			data:         [
				new DropdownSelect('Type', 'type', ['Target', 'Caster'])
					.setTooltip('The entity to check the direction of'),
				new DropdownSelect('Direction', 'direction', ['Away', 'Towards'])
					.setTooltip('The direction the chosen entity needs to be looking relative to the other')
			],
			summaryItems: ['type', 'direction']
		});
	}

	public static override new = () => new this();
}

class DistanceCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Distance',
			description:  'Applies child components based on the physical distance between the caster and the target. This is useful for abilities with range limitations, proximity-based effects, or for determining optimal engagement distances.',
			keywords:     'distance, range, proximity, close, far, within range, out of range, measurement, spatial',
			data:         [
				new AttributeSelect('Min Value', 'min-value')
					.setTooltip('The minimum value for the distance required. This should be >= 0'),
				new AttributeSelect('Max Value', 'max-value', 50)
					.setTooltip('The maximum value for the distance required. This should be larger than the minimum value')
			],
			summaryItems: ['min-value', 'max-value']
		});
	}

	public static override new = () => new this();
}

class ElevationCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Elevation',
			description:  'Applies child components based on the target\'s vertical position (elevation or Y-coordinate), either absolutely or relative to the caster. This is useful for abilities that are restricted to certain altitudes or take advantage of height differences.',
			keywords:     'elevation, altitude, height, y-coordinate, vertical, up, down, above, below, relative, absolute',
			data:         [
				new DropdownSelect('Type', 'type', ['Normal', 'Difference'])
					.setTooltip('The type of comparison to make. Normal is just their Y-coordinate. Difference would be the difference between that the caster\'s Y-coordinate'),
				new AttributeSelect('Min Value', 'min-value')
					.setTooltip('The minimum value for the elevation required. A positive minimum value with a "Difference" type would be for when the target is higher up than the caster'),
				new AttributeSelect('Max Value', 'max-value', 255)
					.setTooltip('The maximum value for the elevation required. A negative maximum value with a "Difference" type would be for when the target is below the caster')
			],
			summaryItems: ['type', 'min-value', 'max-value']
		});
	}

	public static override new = () => new this();
}

class ElseCondition extends FabledCondition {
	public constructor() {
		super({
			name:        'Else',
			description: 'Applies child components if the preceding condition or mechanic in the skill chain fails to execute or return a valid result. This acts as a fallback or alternative path, allowing for complex conditional logic.',
			keywords:    'else, fallback, alternative, conditional, logic, failure, default, otherwise, if-else'
		});
	}

	public static override new = () => new this();
}

class EntityTypeCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Entity Type',
			description:  'Applies child components based on whether the target matches one or more specified entity types (e.g., Player, Zombie, Skeleton). This is useful for abilities that are restricted to certain mob categories or interact differently with various entities.',
			keywords:     'entity, mob, type, category, filter, player, zombie, skeleton, creature, monster, animal',
			data:         [
				new DropdownSelect('Types', 'types', ['Location', ...getEntities()], [], true)
					.setTooltip('The entity types to target'),
				new BooleanSelect('Blacklist', 'blacklist', false)
					.setTooltip('Whether to consider the listed types as a blacklist, meaning only entities that do NOT match one of them will pass the condition.')
			],
			summaryItems: ['types']
		});
	}

	public static override new = () => new this();
}

class FireCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Fire',
			description:  'Applies child components based on whether the target is currently on fire. This is useful for abilities that deal bonus damage to burning targets, extinguish flames, or have special effects when interacting with fiery entities.',
			keywords:     'fire, burning, flame, ignite, status, debuff, on fire, ablaze',
			data:         [
				new DropdownSelect('Type', 'type', ['On Fire', 'Not On Fire'], 'On Fire')
					.setTooltip('Whether the target should be on fire')
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class FlagCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Flag',
			description:  'Applies child components based on whether the target is currently marked with a specific custom flag. Flags are versatile markers used to track temporary states, buffs, debuffs, or unique conditions on entities. This is useful for complex conditional logic and inter-skill dependencies.',
			keywords:     'flag, status, marker, custom condition, buff, debuff, temporary state, check, key',
			data:         [
				new DropdownSelect('Type', 'type', ['Set', 'Not Set'], 'Set')
					.setTooltip('Whether the flag should be set'),
				new StringSelect('Key', 'key', 'key')
					.setTooltip('The unique key representing the flag. This should match the key for when you set it using the Flag mechanic or the Flat Toggle mechanic')
			],
			summaryItems: ['type', 'key']
		});
	}

	public static override new = () => new this();
}

class FoodCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Food',
			description:  'Applies child components based on the target\'s current food level and saturation. This is useful for abilities that are restricted by hunger, empowered by being well-fed, or interact with food mechanics.',
			keywords:     'food, hunger, saturation, well-fed, starved, appetite, sustenance',
			data:         [
				new DropdownSelect('Type', 'type', ['Food', 'Percent', 'Difference', 'Difference Percent'])
					.setTooltip('The type of measurement to use for the food. Food level is their flat food left. Percent is the percentage of food they have left. Difference is the difference between the target\'s flat food and the caster\'s. Difference percent is the difference between the target\'s percentage food left and the caster\'s'),
				new AttributeSelect('Min Value', 'min-value')
					.setTooltip('The minimum food required. A positive minimum with one of the "Difference" types would be for when the target has more food'),
				new AttributeSelect('Max Value', 'max-value', 10, 2)
					.setTooltip('The maximum food required. A negative maximum with one of the "Difference" types would be for when the target has less food')
			],
			summaryItems: ['type', 'min-value', 'max-value']
		});
	}

	public static override new = () => new this();
}

class GroundCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Ground',
			description:  'Applies child components based on whether the target is currently standing on the ground. This is useful for abilities that are restricted to ground-based movement, require solid footing, or interact with aerial units differently.',
			keywords:     'ground, on ground, airborne, flying, solid footing, movement, stability',
			data:         [
				new DropdownSelect('Type', 'type', ['On Ground', 'Not On Ground'])
					.setTooltip('Whether the target should be on the ground')
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}


class GlideCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Glide',
			description:  'Applies child components based on whether the target player is currently gliding with an elytra. This is useful for abilities that enhance aerial movement, provide buffs during flight, or react to a player\'s gliding state.',
			keywords:     'glide, elytra, flying, aerial, movement, mobility, control, momentum',
			data:         [
				new BooleanSelect('Gliding', 'glide', true)
					.setTooltip('Whether the player should be gliding with an elytra')
			],
			summaryItems: ['glide']
		});
	}

	public static override new = () => new this();
}

class HealthCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Health',
			description:  'Applies child components based on the target\'s current health, either as a raw value, a percentage, or a difference relative to the caster. This is useful for abilities that trigger at low health, execute enemies, or provide emergency healing.',
			keywords:     'health, hp, life, hit points, percent, low health, full health, missing health, damaged',
			data:         [
				new DropdownSelect('Type', 'type', ['Health', 'Percent', 'Difference', 'Difference Percent'])
					.setTooltip('The type of measurement to use for the health. Health is their flat health left. Percent is the percentage of health they have left. Difference is the difference between the target\'s flat health and the caster\'s. Difference percent is the difference between the target\'s percentage health left and the caster\'s'),
				new AttributeSelect('Min Value', 'min-value')
					.setTooltip('The minimum health required. A positive minimum with one of the "Difference" types would be for when the target has more health'),
				new AttributeSelect('Max Value', 'max-value', 10, 2)
					.setTooltip('The maximum health required. A negative maximum with one of the "Difference" types would be for when the target has less health')
			],
			summaryItems: ['type', 'min-value', 'max-value']
		});
	}

	public static override new = () => new this();
}

class ItemCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Item',
			description:  'Applies child components based on whether the target is wielding a specific item, or an item matching detailed criteria such as material, name, or lore. This is useful for abilities that require certain tools, weapons, or have special effects when certain items are equipped.',
			keywords:     'item, equip, wield, hold, weapon, tool, gear, material, name, lore, check, requirement',
			data:         [...itemConditionOptions()],
			summaryItems: ['material', 'data', 'lore', 'name']
		});
	}

	public static override new = () => new this();
}

class InventoryCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Inventory',
			description:  'Applies child components based on whether the target player\'s inventory contains a specific item, or an item matching detailed criteria and a minimum quantity. This is useful for abilities that require resources, consume items, or interact with a player\'s carried possessions.',
			keywords:     'inventory, item, check, consume, resource, craft, material, quantity, storage, player only',
			data:         [
				new AttributeSelect('Amount', 'amount', 1)
					.setTooltip('The amount of the item needed in the player\'s inventory'),
				...itemConditionOptions()
			],
			summaryItems: ['material', 'amount', 'data', 'lore', 'name']
		});
	}

	public static override new = () => new this();
}

class LightCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Light',
			description:  'Applies child components based on the light level at the target\'s current location. This is useful for abilities that are stronger in darkness, weaker in light, or have special effects during day/night cycles or in specific environments.',
			keywords:     'light, darkness, brightness, environment, day, night, shadow, illumination',
			data:         [
				new AttributeSelect('Min Light', 'min-light')
					.setTooltip('The minimum light level needed. 16 is full brightness while 0 is complete darkness'),
				new AttributeSelect('Max Light', 'max-light', 16, 16)
					.setTooltip('The maximum light level needed. 16 is full brightness while 0 is complete darkness')
			],
			summaryItems: ['min-light', 'max-light']
		});
	}

	public static override new = () => new this();
}

class ManaCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Mana',
			description:  'Applies child components based on the target\'s current mana, either as a raw value, a percentage, or a difference relative to the caster. This is useful for abilities that are restricted by mana costs, scale with mana, or provide mana regeneration effects.',
			keywords:     'mana, energy, resource, mp, spell points, magic, resource management, regeneration',
			data:         [
				new DropdownSelect('Type', 'type', ['Mana', 'Percent', 'Difference', 'Difference Percent'], 'Mana')
					.setTooltip('The type of measurement to use for the mana. Mana is their flat mana left. Percent is the percentage of mana they have left. Difference is the difference between the target\'s flat mana and the caster\'s. Difference percent is the difference between the target\'s percentage mana left and the caster\'s'),
				new AttributeSelect('Min Value', 'min-value')
					.setTooltip('The minimum amount of mana needed'),
				new AttributeSelect('Max Value', 'max-value', 10, 2)
					.setTooltip('The maximum amount of mana needed')
			],
			summaryItems: ['type', 'min-value', 'max-value']
		});
	}

	public static override new = () => new this();
}

class MoneyCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Money',
			description:  'Applies child components based on the target\'s current money balance. This condition requires a Vault-compatible economy plugin to be installed. It is useful for abilities with economic costs, conditional purchases, or wealth-based effects.',
			keywords:     'money, economy, vault, balance, coins, gold, currency, wealth, cost, purchase',
			data:         [
				new DropdownSelect('Type', 'type', ['Min', 'Max', 'Between'], 'Min')
					.setTooltip('The type of comparison to make'),
				new AttributeSelect('Min Value', 'min-value', 10)
					.requireValue('type', ['Min', 'Between'])
					.setTooltip('The minimum balance the target must have, inclusive'),
				new AttributeSelect('Max Value', 'max-value', 100)
					.requireValue('type', ['Max', 'Between'])
					.setTooltip('The maximum balance the target can have, inclusive')
			],
			summaryItems: ['type', 'min-value', 'max-value']
		});
	}

	public static override new = () => new this();
}

class MoonCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Moon',
			description:  'Applies child components based on the current moon phase in the target\'s world. This is useful for abilities that are tied to lunar cycles, such as werewolf transformations, nocturnal buffs, or spells that draw power from the moon.',
			keywords:     'moon, lunar, phase, full moon, new moon, waxing, waning, night, cycle, werewolf, nocturnal',
			data:         [
				new DropdownSelect('Phases', 'phases', ['Full Moon', 'Waning Gibbous', 'Last Quarter', 'Waning Crescent', 'New Moon', 'Waxing Crescent', 'First Quarter', 'Waxing Gibbous'], [], true)
					.setTooltip('The entity types to target'),
				new BooleanSelect('Blacklist', 'blacklist', false)
					.setTooltip('Whether to consider the listed types as a blacklist, meaning only phases that do NOT match one of them will trigger.')
			],
			summaryItems: ['phases', 'blacklist']
		});
	}

	public static override new = () => new this();
}

class MountedCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Mounted',
			description:  'Applies child components based on whether the target is currently being ridden (mounted) by a specific type of entity. This is useful for abilities that trigger when a player is on a horse, or when a mob is being controlled.',
			keywords:     'mounted, ridden, ride, horse, entity, mob, player, controlled',
			data:         [
				new DropdownSelect('Types', 'types', getAnyEntities, ['Any'], true)
					.setTooltip('The entity types that can be mounting the target')
			],
			summaryItems: ['types']
		});
	}

	public static override new = () => new this();
}

class MountingCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Mounting',
			description:  'Applies child components based on whether the target is currently riding (mounting) a specific type of entity. This is useful for abilities that trigger when a player mounts a creature, or when a mob is riding another mob.',
			keywords:     'mounting, riding, on mount, creature, entity, vehicle, control, passenger',
			data:         [
				new DropdownSelect('Types', 'types', getAnyEntities, ['Any'], true)
					.setTooltip('The entity types the target can be mounting')
			],
			summaryItems: ['types']
		});
	}

	public static override new = () => new this();
}

class MythicMobTypeCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'MythicMob Type',
			description:  'Applies child components based on whether the target is a specific MythicMob type, or a generic mob if the field is left empty. This is useful for abilities that interact exclusively with custom MythicMobs.',
			keywords:     'mythicmob, custom mob, monster, entity, type, specific mob, boss, creature, unique mob',
			data:         [
				new StringListSelect('MythicMob Types', 'types')
					.setTooltip('The MythicMob types to target')
			],
			summaryItems: ['types']
		});
	}

	public static override new = () => new this();
}

class NameCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Name',
			description:  'Applies child components based on whether the target\'s displayed name matches a specified text string or regular expression. This is useful for abilities that target named mobs, specific players, or entities with custom names.',
			keywords:     'name, custom name, player name, mob name, regex, text, identifier, target by name',
			data:         [
				new BooleanSelect('Contains Text', 'contains', true)
					.setTooltip('Whether the target should have a name containing the text'),
				new BooleanSelect('Regex', 'regex', false)
					.setTooltip('Whether the text is formatted as regex. If you do not know what regex is, ignore this option'),
				new StringSelect('Text', 'text', 'text')
					.setTooltip('The text to look for in the target\'s name')
			],
			summaryItems: ['contains', 'text']
		});
	}

	public static override new = () => new this();
}

class OffhandCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Offhand',
			description:  'Applies child components based on whether the target is wielding a specific item in their offhand slot. This condition is primarily for Minecraft versions 1.9 and above, allowing for abilities that leverage secondary equipment.',
			keywords:     'offhand, secondary hand, shield, item, equipment, dual wield, 1.9+, inventory slot',
			data:         [...itemConditionOptions()],
			summaryItems: ['material', 'data', 'lore', 'name']
		});
	}

	public static override new = () => new this();
}

class PermissionCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Permission',
			description:  'Applies child components based on whether the caster possesses a specific Minecraft permission. This is highly useful for abilities that are tied to player ranks, roles, or administrative privileges, ensuring only authorized users can activate them.',
			keywords:     'permission, access, authorization, rank, role, privilege, whitelist, op',
			data:         [
				new StringSelect('Permission', 'perm', 'some.permission')
					.setTooltip('The permission the player needs to have'),
				new BooleanSelect('Negate', 'negate', false)
					.setTooltip('Whether to negate the permission check. If true, the player must NOT have the permission to pass the condition')
			],
			summaryItems: ['perm', 'negate']
		});
	}

	public static override new = () => new this();
}

class PotionCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Potion',
			description:  'Applies child components based on whether the target currently has a specific potion effect applied, optionally checking its rank. This is useful for abilities that interact with status effects, such as cleansing, amplifying, or reacting to specific buffs/debuffs.',
			keywords:     'potion, effect, status effect, buff, debuff, cleanse, amplify, rank, strength, active, not active',
			data:         [
				new DropdownSelect('Type', 'type', ['Active', 'Not Active'], 'Active')
					.setTooltip('Whether the potion should be active'),
				new DropdownSelect('Potion', 'potion', getAnyPotion, 'Any')
					.setTooltip('The type of potion to look for'),
				new AttributeSelect('Min Rank', 'min-rank')
					.setTooltip('The minimum rank the potion effect can be'),
				new AttributeSelect('Max Rank', 'max-rank', 999)
					.setTooltip('The maximum rank the potion effect can be')
			],
			summaryItems: ['type', 'potion', 'min-rank', 'max-rank']
		});
	}

	public static override new = () => new this();
}

class SkillLevelCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Skill Level',
			description:  'Applies child components based on the caster\'s level in a specific skill. This is useful for abilities that unlock or improve as the caster gains proficiency in a particular skill, ensuring progressive gameplay.',
			keywords:     'skill level, proficiency, mastery, rank, progression, experience, unlock, improve',
			data:         [
				new SkillSelect('Skill', 'skill', false)
					.setTooltip('The name of the skill to check the level of. If you want to check the current skill, enter the current skill\'s name anyway'),
				new IntSelect('Min Level', 'min-level', 2)
					.setTooltip('The minimum level of the skill needed'),
				new IntSelect('Max Level', 'max-level', 99)
					.setTooltip('The maximum level of the skill needed')
			],
			summaryItems: ['skill', 'min-level', 'max-level']
		});
	}

	public static override new = () => new this();
}

class SlotCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Slot',
			description:  'Applies child components based on whether the target player has a specific item in a designated inventory slot. This is useful for abilities that require certain equipment to be in particular places, such as a tool in the hotbar or a specific armor piece.',
			keywords:     'slot, inventory, item, equipment, hotbar, armor, offhand, check, position, gear',
			data:         [
				new StringListSelect('Slots (one per line)', 'slot', ['9'])
					.setTooltip('The slots to look at. Slots 0-8 are the hot bar, 9-35 are the main inventory, 36-39 are armor, and 40 is the offhand slot. Multiple slots will check if any of the slots match'),
				...itemConditionOptions()
			],
			summaryItems: ['slot', 'material', 'data', 'lore', 'name']
		});
	}

	public static override new = () => new this();
}

class SprintCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Sprint',
			description:  'Applies child components based on whether the target player is currently sprinting. This is useful for abilities that grant bonuses during high-speed movement, provide stealth when not sprinting, or react to changes in a player\'s movement state.',
			keywords:     'sprint, running, fast, speed, movement, toggle, high speed, dash, run',
			data:         [
				new BooleanSelect('Sprinting', 'sprint', true)
					.setTooltip('Whether the player should be sprinting')
			],
			summaryItems: ['sprint']
		});
	}

	public static override new = () => new this();
}

class StatusCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Status',
			description:  'Applies child components based on whether the target currently has a specific custom status effect applied (e.g., Stun, Root, Silence). This is useful for abilities that interact with crowd control, buffs, or debuffs from other skills.',
			keywords:     'status, effect, custom status, buff, debuff, crowd control, stun, root, silence, invincible, invulnerable',
			data:         [
				new DropdownSelect('Type', 'type', ['Active', 'Not Active'])
					.setTooltip('Whether the status should be active'),
				new DropdownSelect('Status', 'status',
					[
						'Any',
						'Absorb',
						'Curse',
						'Disarm',
						'Invincible',
						'Invulnerable',
						'Root',
						'Silence',
						'Stun'
					])
					.setTooltip('The status to look for')
			],
			summaryItems: ['type', 'status']
		});
	}

	public static override new = () => new this();
}

class TimeCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Time',
			description:  'Applies child components based on the current server time (day or night). This is useful for abilities that are restricted to certain times of day, or have different effects depending on whether it is day or night.',
			keywords:     'time, day, night, clock, cycle, day-night cycle, schedule, light level',
			data:         [
				new DropdownSelect('Time', 'time', ['Day', 'Night'], 'Day')
					.setTooltip('The time to check for in the current world')
			],
			summaryItems: ['time']
		});
	}

	public static override new = () => new this();
}

class ToolCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Tool',
			description:  'Applies child components based on whether the target is currently wielding a tool of a specific type or material. This is useful for abilities that require certain tools for activation, provide bonuses when using a particular tool, or interact with crafting/gathering mechanics.',
			keywords:     'tool, weapon, axe, pickaxe, shovel, hoe, sword, material, craft, gather, equipment, wield, hold',
			data:         [
				new DropdownSelect('Material', 'material', ['Any',
					'Wood',
					'Stone',
					'Iron',
					'Gold',
					'Diamond',
					'Netherite'])
					.setTooltip('The material the held tool needs to be made out of'),
				new DropdownSelect('Tool', 'tool', ['Any', 'Axe', 'Hoe', 'Pickaxe', 'Shovel', 'Sword'])
					.setTooltip('The type of tool it needs to be')
			],
			summaryItems: ['material', 'tool']
		});
	}

	public static override new = () => new this();
}

class ValueCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Value',
			description:  'Applies child components based on whether a dynamically stored numerical value (set by "Value" mechanics) falls within a specified range. This is useful for abilities that track custom statistics, counters, or game-state variables.',
			keywords:     'value, stored value, custom stat, counter, game state, variable, numerical, range check',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique string used for the value set by the Value mechanics'),
				new AttributeSelect('Min Value', 'min-value', 1)
					.setTooltip('The lower bound of the required value'),
				new AttributeSelect('Max Value', 'max-value', 999)
					.setTooltip('The upper bound of the required value')
			],
			summaryItems: ['key', 'min-value', 'max-value']
		});
	}

	public static override new = () => new this();
}

class ValueTextCondition extends FabledCondition {
	public constructor() {
		super({
			name:        'Value Text',
			description: 'Applies child components based on whether a dynamically stored text value (set by "Value" mechanics) matches a specified string using various comparison modes (e.g., regex, exact match, contains). This is useful for abilities that react to custom text-based states or variables.',
			keywords:    'value, stored text, string, regex, exact match, contains, start, end, custom variable, text comparison',
			data:        [
				new DropdownSelect('Mode', 'mode', ['REGEX', 'EXACTLY', 'CONTAIN', 'START', 'END'], 'EXACTLY')
					.setTooltip('The comparison mode should be conditioned.'),
				new StringSelect('Key', 'value', '')
					.setTooltip('Key of the value to be compared.'),
				new StringSelect('Expect', 'expect', '')
					.setTooltip('Strings used for comparison.')
			]
		});
	}

	public static override new = () => new this();
}

class WaterCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Water',
			description:  'Applies child components based on whether the target is currently in water. This is useful for abilities that are restricted to aquatic environments, provide buffs underwater, or react to a player entering/exiting water.',
			keywords:     'water, in water, underwater, aquatic, swim, drown, wet, environment',
			data:         [
				new DropdownSelect('State', 'state', ['In Water', 'Out Of Water'])
					.setTooltip('Whether the target needs to be in the water')
			],
			summaryItems: ['state']
		});
	}

	public static override new = () => new this();
}

class WeatherCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Weather',
			description:  'Applies child components based on the current weather condition at the target\'s location (e.g., Rain, Snow, Thunder). This is useful for abilities that are influenced by environmental factors, such as spells that are stronger in a storm or provide shelter.',
			keywords:     'weather, rain, snow, thunder, clear, storm, environment, climate, atmosphere',
			data:         [
				new DropdownSelect('Type', 'type', ['Rain', 'None', 'Snow', 'Thunder'])
					.setTooltip('Whether the target needs to be in the water')
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class WorldCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'World',
			description:  'Applies child components based on whether the target is currently located within a specific world (e.g., Overworld, Nether, End). This is useful for abilities that are exclusive to certain dimensions or have different effects depending on the world.',
			keywords:     'world, dimension, overworld, nether, end, custom world, specific world, environment',
			data:         [
				new BooleanSelect('Blacklist', 'blacklist', false)
					.setTooltip('Whether the list should be seen as a blacklist'),
				new StringListSelect('Worlds', 'worlds')
					.setTooltip('Which worlds should be taken into consideration')
			],
			summaryItems: ['blacklist', 'worlds']
		});
	}

	public static override new = () => new this();
}

class YawCondition extends FabledCondition {
	public constructor() {
		super({
			name:         'Yaw',
			description:  'Applies child components based on the target\'s horizontal facing direction (yaw), measured in degrees (0-360). This is useful for abilities that require the target to be looking in a specific direction, such as puzzles, directional attacks, or stealth mechanics.',
			keywords:     'yaw, direction, facing, orientation, angle, horizontal, stealth, puzzle, view',
			data:         [
				new DoubleSelect('Min Yaw', 'min-yaw', 0)
					.setTooltip('The minimum yaw the target should be facing'),
				new DoubleSelect('Max Yaw', 'max-yaw', 60)
					.setTooltip('The maximum yaw the target should be facing')
			],
			summaryItems: ['min-yaw', 'max-yaw']
		});
	}

	public static override new = () => new this();
}

// MECHANICS

/**
 * Adds the options for item related effects to the component
 */
const itemOptions = (): ComponentOption[] => {
	return [
		new SectionMarker('Item Options'),
		new MaterialSelect(false, 'Arrow')
			.setTooltip('The type of item to give to the player'),
		new IntSelect('Amount', 'amount', 1)
			.setTooltip('The quantity of the item to give to the player'),
		new IntSelect('Durability', 'data')
			.requireValue('material', getDamageableMaterials())
			.setTooltip('The durability to reduce from the item'),
		new BooleanSelect('Unbreakable', 'unbreakable', false)
			.requireValue('material', getDamageableMaterials())
			.setTooltip('Whether to make the item unbreakable'),
		new IntSelect('CustomModelData', 'byte', 0)
			.setTooltip('The CustomModelData of the item'),
		new DropdownSelect('Hide Flags', 'hide-flags', ['Enchants', 'Attributes', 'Unbreakable', 'Destroys', 'Placed on', 'Potion effects', 'Dye'], [], true)
			.setTooltip('Flags to hide from the item'),
		new BooleanSelect('Custom Name/Lore', 'custom', false)
			.setTooltip('Whether to apply a custom name/lore to the item'),

		new StringSelect('Name', 'name', 'Name')
			.requireValue('custom', [true])
			.setTooltip('The name of the item'),
		new StringListSelect('Lore', 'lore')
			.requireValue('custom', [true])
			.setTooltip('The lore text for the item (the text below the name)'),
		new ColorSelect('Potion Color', 'potion_color', '#385dc6')
			.requireValue('material', ['Potion', 'Splash potion', 'Lingering potion'])
			.setTooltip('The potion color in hex RGB'),
		new DropdownSelect('Potion Type', 'potion_type', getPotionTypes, 'Speed')
			.requireValue('material', ['Potion', 'Splash potion', 'Lingering potion'])
			.setTooltip('The type of potion'),
		new IntSelect('Potion Level', 'potion_level')
			.requireValue('material', ['Potion', 'Splash potion', 'Lingering potion'])
			.setTooltip('The potion level'),
		new IntSelect('Potion Duration', 'potion_duration', 30)
			.requireValue('material', ['Potion', 'Splash potion', 'Lingering potion'])
			.setTooltip('The potion duration (seconds)'),
		new ColorSelect('Armor Color', 'armor_color', '#a06540')
			.requireValue('material', ['Leather helmet', 'Leather chestplate', 'Leather leggings', 'Leather boots'])
			.setTooltip('The armor color in hex RGB'),
		new EnchantSelect()
			.setTooltip('The enchantment to apply to the item')
	];
};

const warpOptions = (): ComponentOption[] => {
	return [
		// General data
		new BooleanSelect('Preserve Velocity', 'preserve')
			.setTooltip('Whether to preserve the target\'s velocity post-warp'),
		new BooleanSelect('Set Yaw', 'setYaw', false)
			.setTooltip('Whether to set the target\'s yaw on teleport'),
		new BooleanSelect('Relative', 'relative-yaw', false)
			.requireValue('setYaw', [true])
			.setTooltip('Whether to set the yaw relative to the target\'s current yaw'),
		new AttributeSelect('Yaw', 'yaw', 0)
			.requireValue('setYaw', [true])
			.setTooltip('The Yaw of the desired position (left/right orientation)'),
		new BooleanSelect('Set Pitch', 'setPitch', false)
			.setTooltip('Whether to set the target\'s pitch on teleport'),
		new BooleanSelect('Relative', 'relative-pitch', false)
			.requireValue('setPitch', [true])
			.setTooltip('Whether to set the pitch relative to the target\'s current pitch'),
		new AttributeSelect('Pitch', 'pitch', 0)
			.requireValue('setPitch', [true])
			.setTooltip('The Pitch of the desired position (up/down orientation)')
	];
};

/**
 * Adds the options for particle effects to the components
 */
const particleOptions = (): ComponentOption[] => {
	return [
		new SectionMarker('Particle Options'),
		new DropdownSelect('Particle', 'particle', getParticles, getParticles()[0])
			.setTooltip('The type of particle to display'),

		new DropdownSelect('Material', 'material', (() => [...getMaterials()]), 'Arrow')
			.requireValue('particle', ['Item crack', 'Item'])
			.setTooltip('The material to use for the particles'),
		new DropdownSelect('Material', 'material', (() => [...getBlocks()]), 'Dirt')
			.requireValue('particle', [
				'Block crack',
				'Block dust',
				'Block',
				'Falling dust',
				'Block marker'])
			.setTooltip('The block to use for the particles'),
		new IntSelect('Durability', 'durability', 0)
			.requireValue('particle', ['Item crack', 'Item'])
			.setTooltip('The durability to be reduced from the item used to make the particles'),
		new IntSelect('CustomModelData', 'type', 0)
			.requireValue('particle', ['Item crack', 'item'])
			.setTooltip('The CustomModelData of the item used to make the particles'),
		new ColorSelect('Dust Color', 'dust-color', '#FF0000')
			.requireValue('particle', ['Redstone', 'Dust', 'Dust color transition'])
			.setTooltip('The color of the dust particles in hex RGB'),
		new ColorSelect('Final Dust Color', 'final-dust-color', '#FF0000')
			.requireValue('particle', ['Dust color transition'])
			.setTooltip('The color to transition to, in hex RGB'),
		new DoubleSelect('Dust Size', 'dust-size', 1)
			.requireValue('particle', ['Redstone', 'Dust', 'Dust color transition'])
			.setTooltip('The size of the dust particles'),

		new DropdownSelect('Arrangement', 'arrangement', ['Sphere', 'Circle', 'Hemisphere'], 'Sphere')
			.setTooltip('The arrangement to use for the particles. Circle is a 2D circle, Hemisphere is half a 3D sphere, and Sphere is a 3D sphere'),
		// Circle arrangement direction
		new DropdownSelect('Circle Direction', 'direction', ['XY', 'XZ', 'YZ'], 'XZ')
			.requireValue('arrangement', ['Circle'])
			.setTooltip('The orientation of the circle. XY and YZ are vertical circles while XZ is a horizontal circle'),
		new AttributeSelect('Radius', 'radius', 1)
			.setTooltip('The radius of the arrangement in blocks'),
		new AttributeSelect('Points', 'particles', 20)
			.setTooltip('The amount of points that conform the chosen arrangement'),

		// Bukkit particle data value
		new IntSelect('Effect Data', 'data')
			.requireValue('particle',
				[
					'Smoke',
					'Ender Signal',
					'Mobspawner Flames',
					'Potion Break',
					'Sculk charge'
				])
			.setTooltip('The data value to use for the particle. The effect changes between particles such as the orientation for smoke particles or the color for potion break'),
		new IntSelect('Visible Radius', 'visible-radius', 25)
			.setTooltip('How far away players can see the particles from in blocks'),
		new DoubleSelect('DX', 'dx')
			.setTooltip('Offset in the X direction, used as the Red value for some particles'),
		new DoubleSelect('DY', 'dy')
			.setTooltip('Offset in the Y direction, used as the Green value for some particles'),
		new DoubleSelect('DZ', 'dz')
			.setTooltip('Offset in the Z direction, used as the Blue value for some particles'),
		new DoubleSelect('Amount', 'amount', 1)
			.setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color'),
		new DoubleSelect('Speed', 'speed', 0.1)
			.setTooltip('Speed of the particle. For some particles controls other parameters, such as size')
	];
};

const projectileOptions = (): ComponentOption[] => {
	return [
		new SectionMarker('Projectile Options'),

		// General data
		new AttributeSelect('Velocity', 'velocity', 3)
			.setTooltip('How fast the projectile is launched, in meters per second. A negative value fires it in the opposite direction.'),
		new AttributeSelect('Lifespan', 'lifespan', 5)
			.setTooltip('How long in seconds before the projectile will expire in case it doesn\'t hit anything.'),
		new AttributeSelect('Distance', 'distance', 50)
			.setTooltip('How far in blocks before the projectile will expire in case it doesn\'t hit anything.'),
		new BooleanSelect('On Expire', 'on-expire', false)
			.setTooltip('Whether to add the projectile\'s expire location as one of the targets. You can filter out this target with EntityTypeCondition: Location'),
		new DropdownSelect('Spread', 'spread', ['Cone', 'Horizontal Cone', 'Rain'], 'Cone')
			.setTooltip('The orientation for firing projectiles. Cone will fire arrows in a cone centered on your reticle. Horizontal cone does the same as cone, just locked to the XZ axis (parallel to the ground). Rain drops the projectiles from above the target. For firing one arrow straight, use "Cone"'),
		new AttributeSelect('Amount', 'amount', 1)
			.setTooltip('The number of projectiles to fire'),

		// Cone values
		new AttributeSelect('Angle', 'angle', 30)
			.requireValue('spread', ['Cone', 'Horizontal Cone'])
			.setTooltip('The angle in degrees of the cone arc to spread projectiles over. If you are only firing one projectile, this does not matter.'),

		// Rain values
		new AttributeSelect('Height', 'height', 8)
			.requireValue('spread', ['Rain'])
			.setTooltip('The distance in blocks over the target to rain the projectiles from'),
		new AttributeSelect('Radius', 'rain-radius', 2)
			.requireValue('spread', ['Rain'])
			.setTooltip('The radius of the rain emission area in blocks'),

		// Offsets
		new SectionMarker('Offset'),
		new AttributeSelect('Forward Offset', 'forward')
			.setTooltip('How far forward in front of the target the projectile should fire from in blocks. A negative value will put it behind.'),
		new AttributeSelect('Upward Offset', 'upward')
			.setTooltip('How far above the target the projectile should fire from in blocks. A negative value will put it below.'),
		new AttributeSelect('Right Offset', 'right')
			.setTooltip('How far to the right of the target the projectile should fire from. A negative value will put it to the left.')
	];
};

const homingOptions = (): ComponentOption[] => {
	return [
		new SectionMarker('Homing'),
		new BooleanSelect('Homing', 'homing', false)
			.setTooltip('Whether to make this a homing projectile'),
		new DropdownSelect('Target', 'target', ['Nearest', 'Remember Target'], 'Nearest')
			.setTooltip('What target to home into. "Nearest" will track the nearest valid target each tick. "Remember Target" tracks a target saved through a Remember Targets Mechanic.')
			.requireValue('homing', [true]),
		new AttributeSelect('Homing distance', 'homing-distance', 10)
			.setTooltip('Maximum distance at which the projectile can target an entity, in meters.')
			.requireValue('homing', [true])
			.requireValue('target', ['Nearest']),
		new StringSelect('Remember key', 'remember-key', 'target')
			.requireValue('homing', [true])
			.requireValue('target', ['Remember Target']),
		new AttributeSelect('Correction', 'correction', 0.5)
			.setTooltip('Maximum corrective acceleration of the projectile, in meters per squared tick. Higher values mean the projectile can make more tight turns.')
			.requireValue('homing', [true]),
		new BooleanSelect('Through Wall', 'wall', false)
			.setTooltip('Whether to allow targets to be on the other side of a wall')
			.requireValue('homing', [true])
	];
};

const appendOptional = (value: ComponentOption & Requirements) => {
	value.requireValue('use-effect', [true]);
	return value;
};

const appendNone = (value: ComponentOption & Requirements) => {
	return value;
};

const effectOptions = (optional: boolean): ComponentOption[] => {
	let opt = appendNone;
	if (optional) {
		opt = appendOptional;
	}
	return [new SectionMarker('Particle Effect Options'),

		new BooleanSelect('Use Effect', 'use-effect')
			.setTooltip('Whether to use a particle effect.'),

		opt(new StringSelect('Effect Key', 'effect-key', 'default')
			.setTooltip('The key to refer to the effect by. Only one effect of each key can be active at a time.')),
		opt(new AttributeSelect('Duration', 'duration', 1)
			.setTooltip('The time to play the effect for in seconds')),

		opt(new StringSelect('Shape', '-shape', 'hexagon')
			.setTooltip('Key of a formula for deciding where particles are played each iteration. View "effects.yml" for a list of defined formulas and their keys.')),
		opt(new DropdownSelect('Shape Direction', '-shape-dir', ['XY', 'YZ', 'XZ'], 'XY')
			.setTooltip('The plane the shape formula applies to. XZ would be flat, the other two are vertical.')),
		opt(new StringSelect('Shape Size', '-shape-size', '1')
			.setTooltip('Formula for deciding the size of the shape. This can be any sort of formula using the operations defined in the wiki.')),
		opt(new StringSelect('Animation', '-animation', 'one-circle')
			.setTooltip('Key of a formula for deciding where the particle effect moves relative to the target. View "effects.yml" for a list of defined formulas and their keys.')),
		opt(new DropdownSelect('Animation Direction', '-anim-dir', ['XY', 'YZ', 'XZ'], 'XZ')
			.setTooltip('The plane the animation motion moves through. XZ would be flat, the other two are vertical.')),
		opt(new StringSelect('Animation Size', '-anim-size', '1')
			.setTooltip('Formula for deciding the multiplier of the animation distance. This can be any sort of formula using the operations defined in the wiki.')),
		opt(new IntSelect('Interval', '-interval', 1)
			.setTooltip('Number of ticks between playing particles.')),
		opt(new IntSelect('View Range', '-view-range', 25)
			.setTooltip('How far away the effect can be seen from.')),

		opt(new DropdownSelect('Particle', '-particle-type', getParticles, getParticles()[0])
			.setTooltip('The type of particle to use.')),
		opt(new DropdownSelect('Material', '-particle-material', getMaterials, 'Dirt')
			.requireValue('-particle-type', ['Item crack', 'Item'])
			.setTooltip('The material to use for the particle.')),
		opt(new DropdownSelect('Material', '-particle-material', getBlocks, 'Dirt')
			.requireValue('-particle-type', ['Block crack', 'Block dust', 'Block', 'Falling dust', 'Block marker'])
			.setTooltip('The block to use for the particle.')),
		opt(new IntSelect('Durability', '-particle-durability')
			.requireValue('particle', ['Item crack', 'Item'])
			.setTooltip('The durability to be reduced from the item used to make the particles')),
		opt(new IntSelect('CustomModelData', '-particle-data')
			.requireValue('-particle-type', ['Item crack', 'Item'])
			.setTooltip('The data value for the material used by the particle. For 1.14+ determines the CustomModelData of the item.')),
		new ColorSelect('Dust Color', '-particle-dust-color', '#FF0000').requireValue('-particle-type', ['Redstone', 'Dust', 'Dust color transition'])
			.setTooltip('The color of the dust particles in hex RGB'),
		new ColorSelect('Final Dust Color', '-particle-final-dust-color', '#FF0000').requireValue('-particle-type', ['Dust color transition'])
			.setTooltip('The color to transition to, in hex RGB'),
		new DoubleSelect('Dust Size', '-particle-dust-size', 1).requireValue('-particle-type', ['Redstone', 'Dust', 'Dust color transition'])
			.setTooltip('The size of the dust particles'),
		opt(new IntSelect('Amount', '-particle-amount', 1)
			.setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color.')),
		opt(new DoubleSelect('DX', '-particle-dx')
			.setTooltip('Offset in the X direction, used as the Red value for some particles.')),
		opt(new DoubleSelect('DY', '-particle-dy')
			.setTooltip('Offset in the Y direction, used as the Green value for some particles.')),
		opt(new DoubleSelect('DZ', '-particle-dz')
			.setTooltip('Offset in the Z direction, used as the Blue value for some particles.')),
		opt(new DoubleSelect('Speed', '-particle-speed', 0.1)
			.setTooltip('Speed of the particle. For some particles controls other parameters, such as size.')),
		opt(new DoubleSelect('Initial Rotation', '-initial-rotation')
			.setTooltip('The amount to rotate the effect (useful for effects like the square).')),
		opt(new BooleanSelect('Rotate w/ Player', '-with-rotation', true)
			.setTooltip('Whether to follow the rotation of the player for the effect.'))
	];
};

class AirModify extends FabledMechanic {
	public constructor() {
		super({
			name:         'Air Modify',
			description:  'Modifies an entity\'s remaining air supply by a specified number of seconds. Positive values add air, while negative values deplete it. This can be used to simulate drowning, grant temporary underwater breathing, or react to environmental hazards.',
			keywords:     'air, oxygen, breathe, drown, underwater, suffocate, modify, change, add, remove, seconds',
			data:         [
				new AttributeSelect('Air', 'air', 3)
					.setTooltip('The amount of air, in seconds, to add/subtract.')],
			summaryItems: []
		}, false);
	}

	public static override new = () => new this();
}

class AirSet extends FabledMechanic {
	public constructor() {
		super({
			name:         'Air Set',
			description:  'Sets an entity\'s remaining air supply to a specific, absolute number of seconds. This can be used to instantly grant full breath, fully deplete air for immediate drowning, or control underwater duration precisely.',
			keywords:     'air, oxygen, breathe, drown, underwater, suffocate, set, absolute, seconds, control',
			data:         [
				new AttributeSelect('Air', 'air', 3)
					.setTooltip('The amount of air, in seconds, to set to.')],
			summaryItems: []
		}, false);
	}

	public static override new = () => new this();
}

class AbortSkillMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Abort Skill',
			description:  'Immediately cancels all currently active skill mechanics for the target. This is useful for stopping channeling abilities, breaking ongoing effects, or preventing further execution of a skill chain.',
			keywords:     'abort, cancel, stop, terminate, interrupt, skill, ability, cease, halt',
			data:         [],
			summaryItems: []
		}, false);
	}

	public static override new = () => new this();
}

class ArmorMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Armor',
			description:  'Sets or replaces an item in a specific armor or hand slot of the target, based on detailed item settings. This is useful for dynamically equipping gear, providing temporary armor, or transforming a target\'s appearance. Use with caution when overwriting existing items.',
			keywords:     'armor, equip, gear, item, slot, hand, head, chest, legs, feet, replace, change, outfit',
			data:         [
				new DropdownSelect('Slot', 'slot', ['Hand', 'Off Hand', 'Feet', 'Legs', 'Chest', 'Head'])
					.setTooltip('The slot number to set the item to'),
				new BooleanSelect('Overwrite', 'overwrite', false)
					.setTooltip('USE WITH CAUTION. Whether to overwrite an existing item in the slot. If true, will permanently delete the existing iem'),
				...itemOptions()
			],
			summaryItems: ['slot', 'material', 'data', 'lore', 'name']
		});
	}

	public static override new = () => new this();
}

class ArmorStandMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Armor Stand',
			description:  'Summons a customizable armor stand at the target\'s location, which can serve as a visual marker, a temporary entity, or for displaying items. Child components will be applied to this summoned armor stand.',
			keywords:     'armor stand, summon, marker, display, visual, temporary entity, hologram, item display',
			data:         [
				new StringSelect('Armor Stand Key', 'key', 'default')
					.setTooltip('The key to refer to the armor stand by. Only one armor stand of each key can be active per target at a time'),
				new AttributeSelect('Duration', 'duration', 5)
					.setTooltip('How long the armor stand lasts before being deleted'),
				new StringSelect('Name', 'name', 'Armor Stand')
					.setTooltip('The name the armor stand displays'),
				new BooleanSelect('Name visible', 'name-visible', false)
					.setTooltip('Whether the armor stand\'s name should be visible from afar'),
				new BooleanSelect('Follow target', 'follow', false)
					.setTooltip('Whether the armor stand should follow the target'),
				new BooleanSelect('Marker', 'marker', false)
					.setTooltip('Setting this to true will remove the armor stand\'s hit-box but will also disable gravity'),
				new BooleanSelect('Apply gravity', 'gravity', true)
					.setTooltip('Whether the armor stand should be affected by gravity')
					.requireValue('marker', [false]),
				new BooleanSelect('Small', 'tiny', false)
					.setTooltip('Whether the armor stand should be small'),
				new BooleanSelect('Show arms', 'arms', false)
					.setTooltip('Whether the armor stand should display its arms'),
				new BooleanSelect('Show base plate', 'base', false)
					.setTooltip('Whether the armor stand should display its base plate'),
				new BooleanSelect('Visible', 'visible', true)
					.setTooltip('Whether the armor stand should be visible'),
				new SectionMarker('Offset'),
				new AttributeSelect('Forward Offset', 'forward')
					.setTooltip('How far forward in front of the target the armor stand should be in blocks. A negative value will put it behind'),
				new AttributeSelect('Upward Offset', 'upward')
					.setTooltip('How far above the target the armor stand should be in blocks. A negative value will put it below'),
				new AttributeSelect('Right Offset', 'right')
					.setTooltip('How far to the right the armor stand should be of the target. A negative value will put it to the left')
			],
			summaryItems: ['duration', 'name', 'follow', 'gravity', 'visible']
		}, true);
	}

	public static override new = () => new this();
}

class ArmorStandPoseMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:        'Armor Stand Pose',
			description: 'Adjusts the rotational pose of an existing armor stand. Each body part (head, body, arms, legs) can be set with specific X, Y, Z rotation values in degrees, allowing for highly customized and dynamic visual effects.',
			keywords:    'armor stand, pose, rotation, animate, visual, custom, articulate, head, body, arm, leg',
			data:        [
				new StringSelect('Head', 'head', '').setTooltip('The pose values of the head. Leave empty if should be ignored'),
				new StringSelect('Body', 'body', '').setTooltip('The pose values of the body. Leave empty if should be ignored'),
				new StringSelect('Left Arm', 'left-arm', '').setTooltip('The pose values of the left arm. Leave empty if should be ignored'),
				new StringSelect('Right Arm', 'right-arm', '').setTooltip('The pose values of the right arm. Leave empty if should be ignored'),
				new StringSelect('Left Leg', 'left-leg', '').setTooltip('The pose values of the left leg. Leave empty if should be ignored'),
				new StringSelect('Right Leg', 'right-leg', '').setTooltip('The pose values of the right leg. Leave empty if should be ignored')
			]
		});
	}

	public static override new = () => new this();
}

class ArmorStandRemoveMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Armor Stand Remove',
			description:  'Removes an existing armor stand that was previously summoned and identified by a unique key. This is useful for cleaning up temporary visual effects or entities.',
			keywords:     'armor stand, remove, delete, despawn, clear, cleanup, temporary entity, destroy',
			data:         [
				new StringSelect('Armor Stand Key', 'key', 'default')
					.setTooltip('The key to refer to the armor stand by. Only one armor stand of each key can be active per target at a time')
			],
			summaryItems: ['key']
		});
	}

	public static override new = () => new this();
}

class AttributeMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Attribute',
			description:  'Temporarily modifies a player\'s attributes (e.g., strength, intelligence) by adding a flat bonus or a percentage multiplier. This is useful for applying buffs or debuffs to player stats for a limited duration.',
			keywords:     'attribute, stats, strength, intelligence, vitality, dexterity, buff, debuff, modify, bonus, temporary, enhance',
			data:         [
				new DropdownSelect('Attribute', 'key', () => attributeStore.getAttributeNames(), ['Intelligence'], true)
					.setTooltip('The attribute to add to'),
				new DropdownSelect('Operation', 'operation', ['ADD_NUMBER', 'MULTIPLY_PERCENTAGE'], 'ADD_NUMBER')
					.setTooltip('The operation on the original value by amount, ADD_NUMBER: Scalar adding, MULTIPLY_PERCENTAGE: Multiply the value by amount'),
				new AttributeSelect('Amount', 'amount', 5, 2)
					.setTooltip('The amount to use with the operation'),
				new AttributeSelect('Seconds', 'seconds', 3)
					.setTooltip('How long in seconds to give the attributes to the player'),
				new BooleanSelect('Stackable', 'stackable', false)
					.setTooltip('Whether applying multiple times stacks the effects')
			],
			summaryItems: ['key', 'operation', 'amount', 'seconds']
		});
	}

	public static override new = () => new this();
}

class BlockMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Block',
			description:  'Transforms blocks within a defined area (sphere or cuboid) to a specified type for a limited duration or permanently. This is useful for environmental manipulation, creating temporary barriers, or altering terrain.',
			keywords:     'block, terrain, manipulate, transform, create, destroy, barrier, environment, sphere, cuboid, shape',
			data:         [
				new DropdownSelect('Shape', 'shape', ['Sphere', 'Cuboid'], 'Sphere')
					.setTooltip('The shape of the region to change the blocks for'),
				new DropdownSelect('Type', 'type', (() => ['Air', 'Any', 'Solid', ...getBlocks()]), 'Solid')
					.setTooltip('The type of blocks to replace. Air or any would be for making obstacles while solid would change the environment'),
				new DropdownSelect('Block', 'block', getBlocks, 'Ice')
					.setTooltip('The type of block to turn the region into'),
				new IntSelect('Block Data', 'data')
					.setTooltip('The block data to apply, mostly applicable for things like signs, woods, steps, or the similar'),
				new BooleanSelect('Reset Yaw', 'reset-yaw', false)
					.setTooltip('Whether the target\'s yaw should be reset, effectively making the offsets cardinally aligned'),
				new BooleanSelect('Permanent', 'permanent', false)
					.setTooltip('Whether the blocks should stay changed indefinitely'),
				new AttributeSelect('Seconds', 'seconds', 5)
					.setTooltip('How long the blocks should be replaced for')
					.requireValue('permanent', [false]),

				// Sphere options
				new AttributeSelect('Radius', 'radius', 3)
					.requireValue('shape', ['Sphere'])
					.setTooltip('The radius of the sphere region in blocks'),

				// Cuboid options
				new AttributeSelect('Width (X)', 'width', 5)
					.requireValue('shape', ['Cuboid'])
					.setTooltip('The width of the cuboid in blocks'),
				new AttributeSelect('Height (Y)', 'height', 5)
					.requireValue('shape', ['Cuboid'])
					.setTooltip('The height of the cuboid in blocks'),
				new AttributeSelect('Depth (Z)', 'depth', 5)
					.requireValue('shape', ['Cuboid'])
					.setTooltip('The depth of the cuboid in blocks'),

				new SectionMarker('Offset'),
				new AttributeSelect('Forward Offset', 'forward')
					.setTooltip('How far forward in front of the target the region should be in blocks. A negative value will put it behind'),
				new AttributeSelect('Upward Offset', 'upward')
					.setTooltip('How far above the target the region should be in blocks. A negative value will put it below'),
				new AttributeSelect('Right Offset', 'right')
					.setTooltip('How far to the right the region should be of the target. A negative value will put it to the left')
			],
			preview:      [
				...particlesAtTargetPreviewOptions(),
				new BooleanSelect('Center only', 'per-target-center-only', true)
					.setTooltip('Whether to display particles only at the center of the affected area, or at each affected block')
					.requireValue('per-target', [true])

			],
			summaryItems: ['shape', 'type', 'block', 'seconds']
		});
	}

	public static override new = () => new this();
}

class BuffMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Buff',
			description:  'Applies temporary buffs or debuffs to a target\'s combat statistics, affecting their damage dealt, damage taken, or healing received. This can be applied immediately with a damage trigger or persist for a duration.',
			keywords:     'buff, debuff, combat stats, damage, defense, healing, modify, enhance, weaken, temporary effect',
			data:         [
				new BooleanSelect('Immediate', 'immediate', false)
					.setTooltip('Whether to apply the buff to the current damage trigger'),
				new StringSelect('Type', 'type', 'DAMAGE')
					.requireValue('immediate', [false])
					.setTooltip('What type of buff to apply. DAMAGE/DEFENSE is for regular attacks, SKILL_DAMAGE/SKILL_DEFENSE are for damage from abilities, and HEALING is for healing from abilities. ' +
						'You can also use <code>DIVINITY_damage_&lt;classifer&gt;</code>' +
						' or <code>DIVINITY_defense_&lt;classifier&gt;</code> to apply a buff to a specific damage type from Divinity.'),
				new DropdownSelect('Modifier', 'modifier', ['Flat', 'Multiplier'], 'Flat')
					.setTooltip('The sort of scaling for the buff. Flat will increase/reduce incoming damage by a fixed amount where Multiplier does it by a percentage of the damage. Multipliers above 1 will increase damage taken while multipliers below 1 reduce damage taken'),
				new StringSelect('Category', 'category', '')
					.requireValue('type', ['SKILL_DAMAGE', 'SKILL_DEFENSE'])
					.setTooltip('What kind of skill damage to affect. If left empty, this will affect all skill damage'),
				new AttributeSelect('Value', 'value', 1)
					.setTooltip('The amount to increase/decrease incoming damage by'),
				new AttributeSelect('Seconds', 'seconds', 3)
					.requireValue('immediate', [false])
					.setTooltip('The duration of the buff in seconds')
			],
			summaryItems: ['type', 'modifier', 'value', 'seconds']
		});
	}

	public static override new = () => new this();
}

class CancelMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:        'Cancel',
			description: 'Cancels the event that initiated the current trigger. For example, using this with a damage-based trigger would prevent the damage from being dealt, or with a Launch trigger would stop a projectile from firing. This is useful for defensive abilities, intercepts, or modifying game events.',
			keywords:    'cancel, prevent, stop, interrupt, negate, block, event, trigger, damage, projectile'
		});
	}

	public static override new = () => new this();
}

class CancelEffectMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Cancel Effect',
			description:  'Prematurely stops an active particle effect, identified by its unique key. This is useful for dynamic visual effects, allowing an effect to be stopped before its natural duration ends.',
			keywords:     'cancel, stop, effect, particle, visual, prematurely, remove, clear, cleanup',
			data:         [
				new StringSelect('Effect Key', 'effect-key', 'default')
					.setTooltip('The key used when setting up the effect')
			],
			summaryItems: ['effect-key']
		}, false);
	}

	public static override new = () => new this();
}

class ChannelMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Channel',
			description:  'Initiates a channeling period during which the player is typically immobilized and unable to perform other actions. After a set duration, child effects are applied. This channel can be interrupted, making it suitable for powerful, cast-time abilities.',
			keywords:     'channel, cast time, interrupt, concentrate, hold position, casting, spell, ability, delay, duration',
			data:         [
				new BooleanSelect('Still', 'still', true)
					.setTooltip('Whether to hold the player in place while channeling'),
				new AttributeSelect('Time', 'time', 3)
					.setTooltip('The amount of time, in seconds, to channel for')
			],
			summaryItems: ['still', 'time']
		}, true);
	}

	public static override new = () => new this();
}

class CleanseMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Cleanse',
			description:  'Removes specified negative potion effects or custom status effects from the targets. This is useful for support abilities, purifying allies, or counteracting enemy debuffs.',
			keywords:     'cleanse, purify, remove effect, cure, dispel, debuff, potion, status, support, healing',
			data:         [
				new DropdownSelect('Potion', 'potion', getBadPotions, undefined, true)
					.setTooltip('The type of potion effect to remove from the target'),
				new DropdownSelect('Status', 'status', ['All', 'Curse', 'Disarm', 'Root', 'Silence', 'Stun'], undefined, true)
					.setTooltip('The status to remove from the target')
			],
			summaryItems: ['potion', 'status']
		}, false);
	}

	public static override new = () => new this();
}

class CommandMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Command',
			description:  'Executes a specified server command for each targeted entity, allowing for integration with other plugins or direct server actions. Commands can be executed by the console or by an OP-level player, and support placeholders for dynamic values.',
			keywords:     'command, server command, console, op, execute, custom action, plugin integration, dynamic',
			data:         [
				new StringSelect('Command', 'command', '')
					.setTooltip('The command to execute. {player} = caster\'s name, {target} = target\'s name, {targetUUID} = target\'s UUID (useful if targets are non players), &lc: "{", &rc: "}", &sq: "\'"'),
				new DropdownSelect('Execute Type', 'type', ['Console', 'OP'], 'OP')
					.setTooltip('Console: executes the command from the console. OP: Only if the target is a player, will have them execute it while given a temporary OP permission (If server closes in the meantime, the permission might stay, not recommended!!)')
			],
			summaryItems: ['command', 'type']
		}, false);
	}

	public static override new = () => new this();
}

class CooldownMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Cooldown',
			description:  'Modifies the cooldown of a specific skill (or all skills) for the target. Positive values reduce cooldown, while negative values increase it. This is useful for abilities that refresh skills, apply global cooldowns, or penalize frequent ability use.',
			keywords:     'cooldown, skill, ability, refresh, reduce, increase, global cooldown, reset, timer',
			data:         [
				new StringSelect('Skill (or "all")', 'skill', 'all')
					.setTooltip('The skill to modify the cooldown for'),
				new DropdownSelect('Type', 'type', ['Seconds', 'Percent'], 'Seconds')
					.setTooltip('The modification unit to use. Seconds will add/subtract seconds from the cooldown while Percent will add/subtract a percentage of its full cooldown'),
				new AttributeSelect('Value', 'value', -1)
					.setTooltip('The amount to add/subtract from the skill\'s cooldown')
			],
			summaryItems: ['skill', 'type', 'value']
		}, false);
	}

	public static override new = () => new this();
}

class DamageMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Damage',
			description:  'Inflicts skill-based damage to each target, with options for flat, percentage-based, or true damage. This mechanic allows for various damage types (e.g., elemental, magical) and configurable effects like knockback and ignoring defense calculations.',
			keywords:     'damage, harm, inflict, attack, hit, spell, ability, true damage, elemental, magical, physical, knockback',
			data:         [
				new DropdownSelect('Type', 'type', ['Damage', 'Multiplier', 'Percent Left', 'Percent Missing'], 'Damage')
					.setTooltip('The unit to use for the amount of damage. Damage will deal flat damage, Multiplier will deal a percentage of the target\'s max health, Percent Left will deal a percentage of their current health, and Percent Missing will deal a percentage of the difference between their max health and current health'),
				new AttributeSelect('Value', 'value', 3, 1)
					.setTooltip('The amount of damage to deal'),
				new BooleanSelect('True Damage', 'true')
					.setTooltip('Whether to deal true damage. True damage ignores armor and all plugin checks, and does not have a damage animation nor knockback'),
				new StringSelect('Damage Type', 'classifier', 'default')
					.setTooltip('The type of damage to deal. Can act as elemental damage or fake physical damage. Supports Damage types from Divinity like "DIVINITY_magical"'),
				new BooleanSelect('Apply Knockback', 'knockback', true)
					.setTooltip('Whether the damage will inflict knockback. Ignored if it is True Damage'),
				new BooleanSelect('Ignore Divinity', 'ignore-divinity', false)
					.setTooltip('Whether to ignore Divinity\'s defenses and damage calculations'),
				new DropdownSelect('Damage Cause', 'cause', ['Contact', 'Custom', 'Entity Attack', 'Entity Sweep Attack', 'Projectile', 'Suffocation', 'Fall', 'Fire', 'Fire Tick', 'Melting', 'Lava', 'Drowning', 'Block Explosion', 'Entity Explosion', 'Void', 'Lightning', 'Suicide', 'Starvation', 'Poison', 'Magic', 'Wither', 'Falling Block', 'Thorns', 'Dragon Breath', 'Fly Into Wall', 'Hot Floor', 'Cramming', 'Dryout', 'Freeze', 'Sonic Boom'], 'Custom')
					.setTooltip('Damage Cause considered by the server. This will have influence over the death message and Divinity\' defenses')
					.requireValue('true', [false])
			],
			summaryItems: ['value', 'true', 'knockback']
		}, false);
	}

	public static override new = () => new this();
}

class DamageBuffMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Damage Buff',
			description:  'Modifies the outgoing physical or skill damage dealt by each target, either by a flat amount or a multiplier, for a limited duration. This is useful for applying temporary offensive buffs or debuffs to individual entities.',
			keywords:     'damage buff, damage dealt, physical damage, skill damage, offensive buff, debuff, modify, multiplier, flat, temporary, combat',
			data:         [
				new DropdownSelect('Type', 'type', ['Flat', 'Multiplier'], 'Flat')
					.setTooltip('The type of buff to apply. Flat increases damage by a fixed amount while multiplier increases it by a percentage'),
				new BooleanSelect('Skill Damage', 'skill')
					.setTooltip('Whether to buff skill damage. If false, it will affect physical damage'),
				new AttributeSelect('Value', 'value', 1)
					.setTooltip('The amount to increase/decrease the damage by. A negative amount with the "Flat" type will decrease damage, similar to a number less than 1 for the multiplier'),
				new AttributeSelect('Seconds', 'seconds', 3)
					.setTooltip('The duration of the buff in seconds'),
				new StringSelect('Classification', 'classification', 'default')
					.setTooltip('The classification of the buff. This is intended to be used with damage classifiers from the DamageMechanic, or Divinity damage types using <code>DIVINITY_type</code>')
					.requireValue('skill', [true])
			],
			summaryItems: ['type', 'skill', 'value', 'seconds']
		}, false);
	}

	public static override new = () => new this();
}

class DamageLoreMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Damage Lore',
			description:  'Calculates and inflicts damage to each target based on a numerical value extracted from the lore of an item held by the caster. This allows for dynamic damage scaling tied to item properties.',
			keywords:     'damage, lore, item, dynamic damage, scaling, custom item, text parsing, regex, held item',
			data:         [
				new DropdownSelect('Hand', 'hand', ['Main', 'Offhand'], 'Main')
					.setTooltip('The hand to check for the item. Offhand items are MC 1.9+ only'),
				new StringSelect('Regex', 'regex', 'Damage: {value}')
					.setTooltip('The regex for the text to look for. Use {value} for where the important number should be. If you do not know about regex, consider looking it up on Wikipedia or avoid using major characters such as [ ] { } ( ) . + ? * ^ \\ |'),
				new AttributeSelect('Multiplier', 'multiplier', 1)
					.setTooltip('The multiplier to use on the value to get the actual damage to deal'),
				new BooleanSelect('True Damage', 'true')
					.setTooltip('Whether to deal true damage. True damage ignores armor and all plugin checks'),
				new StringSelect('Classifier', 'classifier', 'default')
					.setTooltip('The type of damage to deal. Can act as elemental damage or fake physical damage'),
				new BooleanSelect('Apply Knockback', 'knockback', true)
					.setTooltip('Whether the damage will inflict knockback. Ignored if it is True Damage'),
				new BooleanSelect('Ignore Divinity', 'ignore-divinity', false)
					.setTooltip('Whether to ignore Divinity\'s defenses and damage calculations'),
				new DropdownSelect('Damage Cause', 'cause', ['Contact', 'Entity Attack', 'Entity Sweep Attack', 'Projectile', 'Suffocation', 'Fall', 'Fire', 'Fire Tick', 'Melting', 'Lava', 'Drowning', 'Block Explosion', 'Entity Explosion', 'Void', 'Lightning', 'Suicide', 'Starvation', 'Poison', 'Magic', 'Wither', 'Falling Block', 'Thorns', 'Dragon Breath', 'Custom', 'Fly Into Wall', 'Hot Floor', 'Cramming', 'Dryout', 'Freeze', 'Sonic Boom'], 'Entity Attack')
					.setTooltip('Damage Cause considered by the server. This will have influence over the death message and Divinity\' defenses')
					.requireValue('true', [false])
			],
			summaryItems: ['hand', 'multiplier', 'true', 'knockback']
		}, false);
	}

	public static override new = () => new this();
}

class DefenseBuffMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Defense Buff',
			description:  'Modifies the incoming physical or skill damage taken by each target, either by a flat amount or a multiplier, for a limited duration. This is useful for applying temporary defensive buffs or debuffs, reducing incoming damage, or increasing vulnerability.',
			keywords:     'defense buff, damage taken, physical defense, skill defense, defensive buff, debuff, modify, multiplier, flat, temporary, combat, vulnerability',
			data:         [
				new DropdownSelect('Type', 'type', ['Flat', 'Multiplier'], 'Flat')
					.setTooltip('The type of buff to apply. Flat will increase/reduce incoming damage by a fixed amount where Multiplier does it by a percentage of the damage. Multipliers above 1 will increase damage taken while multipliers below 1 reduce damage taken'),
				new BooleanSelect('Skill Defense', 'skill')
					.setTooltip('Whether to buff skill defense. If false, it will affect physical defense'),
				new AttributeSelect('Value', 'value', 1)
					.setTooltip('The amount to increase/decrease incoming damage by'),
				new AttributeSelect('Seconds', 'seconds', 3)
					.setTooltip('The duration of the buff in seconds'),
				new StringSelect('Classification', 'classification', 'default')
					.setTooltip('The classification of the buff. This is intended to be used with damage classifiers from the DamageMechanic, or Divinity damage types using <code>DIVINITY_type</code>')
					.requireValue('skill', [true])
			],
			summaryItems: ['type', 'skill', 'value', 'seconds']
		}, false);
	}

	public static override new = () => new this();
}

class DelayMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Delay',
			description:  'Pauses the execution of subsequent child components for a specified duration. This is useful for sequencing abilities, creating timed effects, or adding a brief pause before an effect triggers.',
			keywords:     'delay, pause, wait, timer, timed effect, sequence, ability timing, cooldown, interruptible',
			data:         [
				new AttributeSelect('Delay', 'delay', 2)
					.setTooltip('The amount of time to wait before applying child components in seconds'),
				new BooleanSelect('Cleanup', 'cleanup', true)
					.setTooltip('Whether this delay should be cleaned up on abort or logout/class change'),
				new BooleanSelect('Single Instance', 'single-instance', false)
					.setTooltip('Whether to only allow one instance of this delay per player. ' +
						'When true, this will cancel the previous delay and start the new one in its place.')
			],
			summaryItems: ['delay', 'cleanup', 'single-instance']
		}, true);
	}

	public static override new = () => new this();
}

class DisguiseMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Disguise',
			description:  'Transforms the appearance of each target into a specified mob, player, or miscellaneous entity. This mechanic requires the "LibsDisguise" plugin to be installed. It is useful for stealth, role-playing, or creating unique visual effects.',
			keywords:     'disguise, transform, hidden, stealth, roleplay, appearance, mob, player, libsdisguise, mimic, illusion',
			data:         [
				new AttributeSelect('Duration', 'duration', -1)
					.setTooltip('How long to apply the disguise for in seconds. Use a negative number to permanently disguise the targets'),
				new DropdownSelect('Type', 'type', ['Mob', 'Player', 'Misc'], 'Mob')
					.setTooltip('The type of disguise to use, as defined by the LibsDisguise plugin'),

				new DropdownSelect('Mob', 'mob', getMobDisguises, 'Zombie')
					.requireValue('type', ['Mob'])
					.setTooltip('The type of mob to disguise the target as'),
				new BooleanSelect('Adult', 'adult', true)
					.requireValue('type', ['Mob'])
					.setTooltip('Whether to use the adult variant of the mob'),

				new StringSelect('Player', 'player', 'Eniripsa96')
					.requireValue('type', ['Player'])
					.setTooltip('The player to disguise the target as'),

				new DropdownSelect('Misc', 'misc', getMiscDisguises, 'Painting')
					.requireValue('type', ['Misc'])
					.setTooltip('The object to disguise the target as'),
				new IntSelect('Data', 'data', 0)
					// .requireValue('type', [ 'Misc' ])
					.requireValue('misc', ['Area effect cloud',
						'Armor stand',
						'Arrow',
						'Boat',
						'Dragon fireball',
						'Egg',
						'Ender crystal',
						'Ender pearl',
						'Ender signal',
						'Experience orb',
						'Fireball',
						'Firework',
						'Fishing hook',
						'Item frame',
						'Leash hitch',
						'Minecart',
						'Minecart chest',
						'Minecart command',
						'Minecart furnace',
						'Minecart hopper',
						'Minecart mob spawner',
						'Minecart tnt',
						'Painting',
						'Primed tnt',
						'Shulker bullet',
						'Snowball',
						'Spectral arrow',
						'Splash potion',
						'Thrown exp bottle',
						'Wither skull'])
					.setTooltip('Data value to use for the disguise type. What it does depends on the disguise'),

				new DropdownSelect('Material', 'mat', (() => [...getMaterials()]), 'Arrow')
					.requireValue('misc', ['Dropped item'])
					.setTooltip('Material to use for the disguise type.'),
				new DropdownSelect('Material', 'mat', (() => [...getBlocks()]), 'Anvil')
					.requireValue('misc', ['Falling block'])
					.setTooltip('Block to use for the disguise type.')
			],
			summaryItems: ['duration', 'type', 'mob', 'player', 'misc']
		}, false);
	}

	public static override new = () => new this();
}

class DurabilityMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Durability',
			description:  'Reduces the durability of an item held by the target, either in their main hand or offhand. This is useful for simulating wear and tear, adding weapon degradation, or as a consequence of certain abilities.',
			keywords:     'durability, wear, tear, degrade, item, weapon, tool, damage item, reduce durability, main hand, offhand',
			data:         [
				new AttributeSelect('Amount', 'amount', 1)
					.setTooltip('Amount to reduce the item\'s durability by'),
				new BooleanSelect('Offhand', 'offhand')
					.setTooltip('Whether to apply to the offhand slot')
			],
			summaryItems: ['amount', 'offhand']
		}, false);
	}

	public static override new = () => new this();
}

class ExperienceMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Experience',
			description:  'Modifies a target player\'s vanilla experience or their experience within a specific class group. This can be used to grant rewards, impose penalties, or adjust progression rates.',
			keywords:     'experience, exp, level, gain, lose, set, vanilla, class, progression, reward, penalty',
			data:         [
				new BooleanSelect('Vanilla', 'vanilla', false)
					.setTooltip('Whether to give the target vanilla experience levels instead of fabled class ones.'),
				new IntSelect('Value', 'value', 1)
					.setTooltip('How much experience, levels, or percent to give.'),
				new DropdownSelect('Mode', 'mode', ['give', 'set', 'take'], 'give', false)
					.setTooltip('To give, take or set specified valued. When using the percent type this will mean value%. Example: Value of 50 would mean 50%'),
				new DropdownSelect('Type', 'type', ['flat', 'percent', 'levels'], 'flat', false)
					.setTooltip('To a flat, percentage, or levels'),
				new StringSelect('Group', 'group', 'class')
					.setTooltip('Which group to give experience too. This will be ignored if vanilla is set to true.')
					.requireValue('vanilla', [false]),
				new BooleanSelect('Level Down', 'level-down', true)
					.setTooltip('If losing experience allows leveling down or remaining at the current level.')],
			summaryItems: ['value', 'mode', 'type', 'group', 'level-down', 'vanilla']
		}, false);
	}

	public static override new = () => new this();
}

class ExplosionMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Explosion',
			description:  'Generates an explosion at the target\'s location, with configurable power, block damage, and fire effects. This is useful for offensive area-of-effect abilities, environmental destruction, or creating dynamic combat scenarios.',
			keywords:     'explosion, blast, boom, area damage, aoe, destroy, ignite, tnt, bomb, environmental damage',
			data:         [
				new AttributeSelect('Power', 'power', 3)
					.setTooltip('The strength of the explosion'),
				new BooleanSelect('Damage Blocks', 'damage')
					.setTooltip('Whether to damage blocks with the explosion'),
				new BooleanSelect('Fire', 'fire')
					.setTooltip('Whether to set affected blocks on fire')
			],
			summaryItems: ['power', 'damage', 'fire']
		}, false);
	}

	public static override new = () => new this();
}

class FireMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Fire',
			description:  'Sets the target entity on fire for a specified duration, inflicting continuous fire damage. This is useful for offensive abilities that apply a burning debuff or interact with fire-sensitive enemies.',
			keywords:     'fire, burn, ignite, flame, damage over time, dot, debuff, scorching, inferno',
			data:         [
				new AttributeSelect('Damage', 'damage', 1)
					.setTooltip('The damage dealt by each fire tick'),
				new AttributeSelect('Seconds', 'seconds', 3, 1)
					.setTooltip('The duration of the fire in seconds')
			],
			summaryItems: ['damage', 'seconds']
		}, false);
	}

	public static override new = () => new this();
}

class FlagMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Flag',
			description:  'Marks the target with a temporary custom flag for a specified duration. These flags can then be checked by other skills or conditions, enabling complex state-based abilities and inter-skill synergies.',
			keywords:     'flag, custom flag, mark, status, temporary, buff, debuff, state, condition, key, synergy',
			data:         [
				new StringSelect('Key', 'key', 'key')
					.setTooltip('The unique string for the flag. Use the same key when checking it in a Flag Condition'),
				new AttributeSelect('Seconds', 'seconds', 3, 1)
					.setTooltip('The duration the flag should be set for. To set one indefinitely, use Flag Toggle')
			],
			summaryItems: ['key', 'seconds']
		});
	}

	public static override new = () => new this();
}

class FlagClearMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Flag Clear',
			description:  'Removes a specific custom flag from the target, effectively ending any state or condition associated with that flag. This is useful for cleaning up temporary effects, resetting states, or managing complex skill interactions.',
			keywords:     'flag, clear, remove, reset, status, condition, temporary, cleanup, end, key',
			data:         [
				new StringSelect('Key', 'key', 'key')
					.setTooltip('The unique string for the flag. This should match that of the mechanic that set the flag to begin with')
			],
			summaryItems: ['key']
		});
	}

	public static override new = () => new this();
}

class FlagToggleMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Flag Toggle',
			description:  'Toggles a custom flag on or off for the target. If the flag is currently set, it will be removed; if it\'s not set, it will be applied. This is ideal for creating toggleable abilities or persistent state changes.',
			keywords:     'flag, toggle, switch, custom condition, state change, persistent, on/off, key',
			data:         [
				new StringSelect('Key', 'key', 'key')
					.setTooltip('The unique string for the flag. Use the same key when checking it in a Flag Condition')
			],
			summaryItems: ['key']
		});
	}

	public static override new = () => new this();
}

class FlyMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Fly',
			description:  'Grants or revokes creative flight capabilities to a target for a specified duration, along with control over flight speed. This is useful for temporary aerial movement, escape abilities, or controlling player mobility.',
			keywords:     'fly, flight, aerial, creative flight, grants flight, revoke flight, speed, mobility, movement',
			data:         [
				new BooleanSelect('Allow Flight', 'allow-flight', true)
					.setTooltip('Should the player be allowed to fly when in Survival or Adventure mode? Setting this to false will revoke flight in Survival or Adventure mode.'),
				new AttributeSelect('Seconds', 'seconds', 3, 1)
					.setTooltip('The duration for how long flight should be granted in seconds.')
					.requireValue('allow-flight', [true]),
				new AttributeSelect('Flyspeed', 'flyspeed', 0.1, 0)
					.setTooltip('How fast the player should be able to fly. NOTE: Default flight speed is 0.1, and values greater than 1 or less than -1 will show no change in speed.')
					.requireValue('allow-flight', [true]),
				new BooleanSelect('Force Flight', 'flying', true)
					.setTooltip('If the player is in the air, should they be forced to fly.')
					.requireValue('allow-flight', [true])
			],
			summaryItems: ['allow-flight', 'seconds', 'flyspeed', 'flying']
		});
	}

	public static override new = () => new this();
}

class FoodMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Food',
			description:  'Modifies a player\'s hunger and saturation levels. Positive values increase, while negative values decrease these stats. This is useful for healing, applying hunger debuffs, or interacting with food-related gameplay mechanics.',
			keywords:     'food, hunger, saturation, consume, heal, debuff, restore, deplete, nutrition, appetite',
			data:         [
				new AttributeSelect('Food', 'food', 1, 1)
					.setTooltip('The amount of food to give. Use a negative number to lower the food meter'),
				new AttributeSelect('Saturation', 'saturation')
					.setTooltip('How much saturation to give. Use a negative number to lower saturation. This is the hidden value that determines how long until food starts going down')
			],
			summaryItems: ['food', 'saturation']
		}, false);
	}

	public static override new = () => new this();
}

class ForgetTargetsMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Forget Targets',
			description:  'Clears a group of entities previously stored by the "Remember Targets" mechanic, identified by a unique key. This is useful for managing multi-stage abilities that track specific entities, allowing them to be deselected or removed from tracking.',
			keywords:     'forget, clear, remove, targets, stored, tracking, deselect, untrack, key',
			data:         [
				new StringSelect('Key', 'key', 'key')
					.setTooltip('The unique key the targets were stored under')
			],
			summaryItems: ['key']
		}, false);
	}

	public static override new = () => new this();
}

class HealMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Heal',
			description:  'Restores health to each targeted entity, either by a flat amount or a percentage of their maximum health. This is a fundamental mechanic for supportive abilities, regeneration effects, or emergency health boosts.',
			keywords:     'heal, restore, health, hp, life, regeneration, support, recovery, cure',
			data:         [
				new DropdownSelect('Type', 'type', ['Health', 'Percent'], 'Health')
					.setTooltip('The unit to use for the amount of health to restore. Health restores a flat amount while Percent restores a percentage of their max health'),
				new AttributeSelect('Value', 'value', 3, 1)
					.setTooltip('The amount of health to restore')
			],
			summaryItems: ['type', 'value']
		}, false);
	}

	public static override new = () => new this();
}

class HealthSetMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Health Set',
			description:  'Sets the target\'s health to a specific, absolute amount, overriding all resistances, damage buffs, and other modifiers. This provides precise control over an entity\'s health state.',
			keywords:     'health, set, absolute health, override, precise, life, hp, hit points, exact',
			data:         [
				new AttributeSelect('Health', 'health', 1)
					.setTooltip('The health to set to')
			],
			summaryItems: ['health']
		}, false);
	}

	public static override new = () => new this();
}

class HeldItemMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Held Item',
			description:  'Sets the currently held item slot for a target player. This is useful for automatically equipping specific items, changing a player\'s active tool/weapon, or preparing for a subsequent action. This mechanic will not affect skill slots.',
			keywords:     'held item, equip, inventory, slot, main hand, offhand, weapon, tool, switch, change, quick equip',
			data:         [
				new AttributeSelect('Slot', 'slot')
					.setTooltip('The slot to set it to')
			],
			summaryItems: ['slot']
		}, false);
	}

	public static override new = () => new this();
}

class ImmunityMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Immunity',
			description:  'Grants temporary immunity or resistance to specific damage types for a limited duration. This is useful for defensive abilities, invulnerability frames, or mitigating incoming threats.',
			keywords:     'immunity, resistance, invulnerability, damage reduction, defense, shield, absorb, temporary, buff',
			data:         [
				new DropdownSelect('Type', 'type', getDamageTypes, 'Poison')
					.setTooltip('The damage type to give an immunity for'),
				new AttributeSelect('Seconds', 'seconds', 3)
					.setTooltip('How long to give an immunity for'),
				new AttributeSelect('Multiplier', 'multiplier')
					.setTooltip('The multiplier for the incoming damage. Use 0 if you want full immunity')
			],
			summaryItems: ['type', 'seconds', 'multiplier']
		});
	}

	public static override new = () => new this();
}

class InterruptMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:        'Interrupt',
			description: 'Immediately stops any ongoing channeling abilities or other interruptible actions being performed by the targeted entities. This is useful for crowd control, breaking enemy spell casts, or preventing channeled effects.',
			keywords:    'interrupt, stop, cancel, channeling, spell cast, crowd control, break, halt, silence'
		});
	}

	public static override new = () => new this();
}

class InvisibilityMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Invisibility',
			description:  'Applies the invisibility potion effect to the target, optionally hiding their equipment (requires ProtocolLib). This is useful for stealth abilities, escape mechanisms, or temporary visual concealment.',
			keywords:     'invisibility, stealth, hidden, vanish, conceal, escape, visual, potion effect',
			data:         [
				new IntSelect('Duration', 'duration', 200)
					.setTooltip('Duration in ticks'),
				new BooleanSelect('Hide Equipment', 'hideEquipment', false)
					.setTooltip('Whether to hide equipment or not. Requires ProtocolLib.')
			],
			summaryItems: ['duration', 'hideEquipment']
		});
	}

	public static override new = () => new this();
}

class ItemMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Item',
			description:  'Grants each targeted player a specified item, with options for quantity, custom data, name, and lore. This is useful for rewarding players, giving quest items, or providing temporary consumables.',
			keywords:     'item, give, grant, reward, inventory, add item, custom item, loot, material, amount',
			data:         [...itemOptions()],
			summaryItems: ['material', 'amount']
		});
	}

	public static override new = () => new this();
}

class ItemDropMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Item Drop',
			description:  'Spawns a custom dropped item entity at a specified location, with configurable pickup delay and despawn duration. This is useful for creating loot drops, quest objectives, or interactive environmental elements.',
			keywords:     'item, drop, spawn, loot, entity, custom item, ground item, reward, quest, material, amount',
			data:         [
				new AttributeSelect('Pickup Delay', 'pickup_delay', 10)
					.setTooltip('How many ticks must pass before the item can be picked up, in ticks'),
				new AttributeSelect('Duration', 'duration', 6000)
					.setTooltip('The time after which the item will despawn if not picked up, in ticks. Caps at 6000'),

				...itemOptions(),

				new SectionMarker('Offset'),
				new AttributeSelect('Forward offset', 'forward')
					.setTooltip('How far forward in blocks to teleport. A negative value teleports backwards'),
				new AttributeSelect('Upward offset', 'upward')
					.setTooltip('How far upward in blocks to teleport. A negative value teleports downward'),
				new AttributeSelect('Right offset', 'right')
					.setTooltip('How far to the right in blocks to teleport. A negative value teleports to the left')
			],
			summaryItems: ['pickup_delay', 'duration', 'material', 'amount']
		});
	}

	public static override new = () => new this();
}

class ItemProjectileMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Item Projectile',
			description:  'Launches a projectile that uses a specified item as its visual representation. Upon landing or colliding, it applies child components to the hit entity or location. This is highly customizable with homing capabilities, various spread patterns, and collision handling.',
			keywords:     'projectile, item, launch, fire, shoot, homing, collision, spread, visual, custom, ranged, attack',
			data:         [
				new DropdownSelect('Group', 'group', ['Ally', 'Enemy'], 'Enemy')
					.setTooltip('The alignment of targets to hit'),
				new BooleanSelect('Wall Collisions', 'walls', true)
					.setTooltip('Whether to account for wall collisions. If false, the item will just slide through them.'),
				new AttributeSelect('Collision Radius', 'collision-radius', 0.2)
					.setTooltip('The radius of the projectile considered when calculating collisions.'),

				...itemOptions(),
				...homingOptions(),
				...projectileOptions(),
				...effectOptions(true)
			],
			preview:      [
				new IntSelect('Refresh period', 'period', 5)
					.setTooltip('How many ticks to wait before refreshing the preview, recalculating targets and the location of the particle effects'),

				new SectionMarker('Particles at target'),
				new BooleanSelect('Particles at target', 'per-target', false)
					.setTooltip('Displays particles at the location of the current targets'),
				new DropdownSelect('Particle', 'per-target-particle', getParticles, 'Crit')
					.setTooltip('The type of particle to display')
					.requireValue('per-target', [true]),
				new DropdownSelect('Material', 'per-target-material', (() => [...getMaterials()]), 'Arrow')
					.requireValue('per-target-particle', ['Item crack', 'Item'])
					.requireValue('per-target', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'per-target-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('per-target-particle', [
						'Block crack',
						'Block dust',
						'Block',
						'Falling dust',
						'Block marker'])
					.requireValue('per-target', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'per-target-durability', 0)
					.requireValue('per-target-particle', ['Item crack', 'Item'])
					.requireValue('per-target', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'per-target-type', 0)
					.requireValue('per-target-particle', ['Item crack', 'Item'])
					.requireValue('per-target', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'per-target-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'per-target-final-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'per-target-dust-size', 1)
					.requireValue('per-target-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The size of the dust particles'),

				// Bukkit particle data value
				new IntSelect('Effect Data', 'per-target-data')
					.requireValue('per-target-particle',
						[
							'Smoke',
							'Ender Signal',
							'Mobspawner Flames',
							'Potion Break',
							'Sculk charge'
						])
					.requireValue('per-target', [true])
					.setTooltip('The data value to use for the particle. The effect changes between particles such as the orientation for smoke particles or the color for potion break'),
				new DoubleSelect('DX', 'per-target-dx')
					.requireValue('per-target', [true])
					.setTooltip('Offset in the X direction, used as the Red value for some particles'),
				new DoubleSelect('DY', 'per-target-dy')
					.requireValue('per-target', [true])
					.setTooltip('Offset in the Y direction, used as the Green value for some particles'),
				new DoubleSelect('DZ', 'per-target-dz')
					.requireValue('per-target', [true])
					.setTooltip('Offset in the Z direction, used as the Blue value for some particles'),
				new DoubleSelect('Amount', 'per-target-amount', 1)
					.requireValue('per-target', [true])
					.setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color'),
				new DoubleSelect('Speed', 'per-target-speed', 0.1)
					.requireValue('per-target', [true])
					.setTooltip('Speed of the particle. For some particles controls other parameters, such as size'),
				new DropdownSelect('Arrangement', 'per-target-arrangement', ['Sphere', 'Circle', 'Hemisphere'], 'Sphere')
					.requireValue('per-target', [true])
					.setTooltip('The arrangement to use for the particles. Circle is a 2D circle, Hemisphere is half a 3D sphere, and Sphere is a 3D sphere'),
				new DropdownSelect('Circle Direction', 'per-target-direction', ['XY', 'XZ', 'YZ'], 'XZ')
					.requireValue('per-target' + '-arrangement', ['Circle'])
					.requireValue('per-target', [true])
					.setTooltip('The orientation of the circle. XY and YZ are vertical circles while XZ is a horizontal circle'),
				new AttributeSelect('Radius', 'per-target-radius', 0.5)
					.requireValue('per-target', [true])
					.setTooltip('The radius of the arrangement in blocks'),
				new BooleanSelect('Increase size by hitbox', 'per-target-hitbox', true)
					.requireValue('per-target', [true])
					.setTooltip('Increases the \'radius\' parameter by the size of the target\'s hitbox'),
				new AttributeSelect('Points', 'per-target-particles', 20)
					.requireValue('per-target', [true])
					.setTooltip('The amount of points that conform the chosen arrangement'),

				new SectionMarker('Path Preview'),
				new BooleanSelect('Path Preview', 'path', false)
					.setTooltip('Displays particles through the paths of the projectiles'),
				new DoubleSelect('Steps per particle', 'path-steps', 2)
					.setTooltip('How many collision steps to run between each particle display')
					.requireValue('path', [true]),
				new DropdownSelect('Particle', 'path-particle', getParticles, 'Crit')
					.setTooltip('The type of particle to display')
					.requireValue('path', [true]),
				new DropdownSelect('Material', 'path-material', (() => [...getMaterials()]), 'Arrow')
					.requireValue('path-particle', ['Item crack', 'Item'])
					.requireValue('path', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'path-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('path-particle', [
						'Block crack',
						'Block dust',
						'Block',
						'Falling dust',
						'Block marker'])
					.requireValue('path', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'path-durability', 0)
					.requireValue('path-particle', ['Item crack', 'Item'])
					.requireValue('path', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'path-type', 0)
					.requireValue('path-particle', ['Item crack', 'Item'])
					.requireValue('path', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'path-dust-color', '#FF0000')
					.requireValue('path-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'path-final-dust-color', '#FF0000')
					.requireValue('path-particle', ['Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'path-dust-size', 1)
					.requireValue('path-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The size of the dust particles'),

				// Bukkit particle data value
				new IntSelect('Effect Data', 'path-data')
					.requireValue('path-particle',
						[
							'Smoke',
							'Ender Signal',
							'Mobspawner Flames',
							'Potion Break',
							'Sculk charge'
						])
					.requireValue('path', [true])
					.setTooltip('The data value to use for the particle. The effect changes between particles such as the orientation for smoke particles or the color for potion break'),
				new DoubleSelect('DX', 'path-dx')
					.requireValue('path', [true])
					.setTooltip('Offset in the X direction, used as the Red value for some particles'),
				new DoubleSelect('DY', 'path-dy')
					.requireValue('path', [true])
					.setTooltip('Offset in the Y direction, used as the Green value for some particles'),
				new DoubleSelect('DZ', 'path-dz')
					.requireValue('path', [true])
					.setTooltip('Offset in the Z direction, used as the Blue value for some particles'),
				new DoubleSelect('Amount', 'path-amount', 1)
					.requireValue('path', [true])
					.setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color'),
				new DoubleSelect('Speed', 'path-speed', 0.1)
					.requireValue('path', [true])
					.setTooltip('Speed of the particle. For some particles controls other parameters, such as size')
			],
			summaryItems: ['group', 'material', 'velocity', 'spread', 'angle', 'correction', 'homing', 'collision-radius']
		}, true);
	}

	public static override new = () => new this();
}

class ItemRemoveMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Item Remove',
			description:  'Removes a specified quantity of a particular item from a target player\'s inventory. This mechanic does not affect mobs. It\'s useful for implementing resource costs, consuming items, or clearing specific items from a player\'s possession.',
			keywords:     'item, remove, inventory, consume, cost, resource, clear, delete, player only, material, amount',
			data:         [
				new AttributeSelect('Amount', 'amount', 1)
					.setTooltip('The amount of the item needed in the player\'s inventory'),

				...itemConditionOptions()
			],
			summaryItems: ['amount', 'material']
		}, false);
	}

	public static override new = () => new this();
}

class LaunchMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Launch',
			description:  'Propels the target entity in a specified direction and with a defined speed, relative to their current facing. This is useful for creating knockbacks, dashes, or custom movement abilities.',
			keywords:     'launch, propel, knockback, dash, move, movement, push, pull, propel, custom movement, physics',
			data:         [
				new DropdownSelect('Relative', 'relative', ['Target', 'Caster', 'Between'], 'Target')
					.setTooltip('Determines what is considered "forward". Target uses the direction the target is facing, Caster uses the direction the caster is facing, and Between uses the direction from the target to the caster'),
				new BooleanSelect('Reset Y', 'reset-y')
					.setTooltip('Whether to reset the Y value. False means the upward velocity is a combination of the setting and the relative vector.'),
				new AttributeSelect('Forward Speed', 'forward')
					.setTooltip('The speed to give the target in the direction they are facing/looking'),
				new AttributeSelect('Upward Speed', 'upward', 2, 0.5)
					.setTooltip('The speed to give the target upwards, this is added to the calculated vector if \'Use Look\' is true'),
				new AttributeSelect('Right Speed', 'right')
					.setTooltip('The speed to give the target to their right')
			],
			summaryItems: ['relative', 'reset-y', 'forward', 'upward', 'right']
		}, false);
	}

	public static override new = () => new this();
}

class LightningMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Lightning',
			description:  'Strikes a bolt of lightning at or near the target\'s position, inflicting damage and potentially setting blocks on fire. Child components are applied to any entity struck by the lightning. This is useful for offensive abilities, environmental destruction, or dramatic visual effects.',
			keywords:     'lightning, strike, thunder, storm, electric, damage, fire, environmental, dramatic, offensive, weather',
			data:         [
				new AttributeSelect('Damage', 'damage', 5)
					.setTooltip('The damage dealt by the lightning bolt'),
				new DropdownSelect('Group', 'group', ['Ally', 'Enemy', 'Both'], 'Enemy')
					.setTooltip('The alignment of targets to hit'),
				new BooleanSelect('Include Caster', 'caster')
					.setTooltip('Whether the lightning strike can hit the caster'),
				new BooleanSelect('Fire', 'fire', true)
					.setTooltip('Whether the lightning should start a fire on hit'),

				new SectionMarker('Offset'),
				new AttributeSelect('Forward Offset', 'forward')
					.setTooltip('How far in front of the target in blocks to place the lightning'),
				new AttributeSelect('Right Offset', 'right')
					.setTooltip('How far to the right of the target in blocks to place the lightning')
			],
			summaryItems: ['damage', 'group', 'caster', 'fire']
		}, true);
	}

	public static override new = () => new this();
}

class ManaMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Mana',
			description:  'Restores or depletes a target\'s mana (or other energy resource) by a flat amount or a percentage of their maximum. This is a fundamental mechanic for managing resource costs, granting mana regeneration, or applying energy drains.',
			keywords:     'mana, energy, resource, mp, spell points, magic, restore, deplete, regeneration, drain, cost',
			data:         [
				new DropdownSelect('Type', 'type', ['Mana', 'Percent'], 'Mana')
					.setTooltip('The unit to use for the amount of mana to restore/drain. Mana does a flat amount while Percent does a percentage of their max mana'),
				new AttributeSelect('Value', 'value', 1)
					.setTooltip('The amount of mana to restore/drain')
			],
			summaryItems: ['type', 'value']
		}, false);
	}

	public static override new = () => new this();
}

class MessageMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Message',
			description:  'Sends a customizable message to each targeted player, supporting dynamic placeholders for caster/target names and stored values. This is useful for providing feedback, quest updates, or role-playing interactions.',
			keywords:     'message, chat, communicate, display, feedback, text, notification, broadcast, player, custom message',
			data:         [
				new StringSelect('Message', 'message', 'text')
					.setTooltip('The message to display. {player} = caster\'s name, {target} = target\'s name, {targetUUID} = target\'s UUID (useful if targets are non players), &lc: "{", &rc: "}", &sq: "\'"')
			],
			summaryItems: ['message']
		});
	}

	public static override new = () => new this();
}

class MineMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Mine',
			description:  'Destroys a selection of blocks at the target\'s location, with options for dropping items and using a virtual tool. This is useful for abilities that create excavations, clear paths, or interact with resource gathering in an area.',
			keywords:     'mine, destroy, break, block, excavate, clear, path, resource, gather, explosion, area effect',
			data:         [
				new DropdownSelect('Material', 'materials', (() => ['Origin', 'Any', ...getBlocks()]), ['Origin'], true)
					.setTooltip('The types of blocks allowed to be broken. \'Origin\' refers to the material at the targeted location'),
				new BooleanSelect('Drop', 'drop', true)
					.setTooltip('Whether to create drops for the destroyed blocks'),
				new DropdownSelect('Tool', 'tool', (() => ['Caster', 'Target', ...getMaterials()]), 'Diamond pickaxe').requireValue('drop', [true])
					.setTooltip('What tool to use when breaking the blocks. This allows to take into account the fact that, for example, Diamond Ore does not drop when mined with a Stone Pickaxe, as well as to consider enchantments like Looting and Silk Touch. \'Caster\' an \'Target\' refers to the items in their respective main hands'),

				new DropdownSelect('Shape', 'shape', ['Sphere', 'Cuboid'], 'Sphere')
					.setTooltip('The shape of the region to mine'),

				// Sphere options
				new AttributeSelect('Radius', 'radius', 2).requireValue('shape', ['Sphere'])
					.setTooltip('The radius of the sphere, in blocks'),

				// Cuboid options
				new AttributeSelect('Width (X)', 'width', 3).requireValue('shape', ['Cuboid'])
					.setTooltip('The width of the cuboid, in blocks'),
				new AttributeSelect('Height (Y)', 'height', 3).requireValue('shape', ['Cuboid'])
					.setTooltip('The height of the cuboid, in blocks'),
				new AttributeSelect('Depth (Z)', 'depth', 3).requireValue('shape', ['Cuboid'])
					.setTooltip('The depth of the cuboid, in blocks'),

				new SectionMarker('Offset'),
				new AttributeSelect('Forward Offset', 'forward')
					.setTooltip('How far forward in front of the target the region should be in blocks. A negative value will put it behind'),
				new AttributeSelect('Upward Offset', 'upward')
					.setTooltip('How far above the target the region should be in blocks. A negative value will put it below'),
				new AttributeSelect('Right Offset', 'right')
					.setTooltip('How far to the right the region should be of the target. A negative value will put it to the left')
			],
			preview:      [
				...particlesAtTargetPreviewOptions(),
				new BooleanSelect('Center only', 'per-target-center-only', true)
					.setTooltip('Whether to display particles only at the center of the affected area, or at each affected block')
					.requireValue('per-target', [true])
			],
			summaryItems: ['materials', 'drop', 'tool', 'shape', 'radius']
		});
	}

	public static override new = () => new this();
}

class MoneyMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Money',
			description:  'Modifies a target player\'s money balance by adding or multiplying a specified amount. This requires a Vault-compatible economy plugin. It\'s useful for implementing economic costs, rewarding players, or creating dynamic marketplace interactions.',
			keywords:     'money, economy, balance, vault, add, multiply, cost, reward, currency, financial',
			data:         [
				new DropdownSelect('Type', 'type', ['Add', 'Multiply'], 'Add')
					.setTooltip('Whether the target\'s balance will be added or multiplied by the set amount'),
				new AttributeSelect('Amount', 'amount', 5)
					.setTooltip('The amount that the target\'s balance will be added or multiplied by. Can be negative'),
				new BooleanSelect('Allows negative', 'allows_negative')
					.setTooltip('Whether the mechanic will be executed even if it will result in the target having a negative balance')
			],
			summaryItems: ['type', 'amount']
		});
	}

	public static override new = () => new this();
}

class MountMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Mount',
			description:  'Mounts one entity onto another, with options for direction (caster mounts target or target mounts caster) and stack size. This is useful for creating custom mounts, controlling mobs, or unique movement abilities.',
			keywords:     'mount, ride, control, vehicle, entity, mob, player, custom mount, transport, stack',
			data:         [
				new DropdownSelect('Type', 'type', ['Caster->Target', 'Target->Caster'], 'Caster->Target')
					.setTooltip('The direction of the mounting'),
				new AttributeSelect('Stack Size', 'max', 5)
					.setTooltip('The maximum amount of entities to stack')
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class MythicMobSkill extends FabledMechanic {
	public constructor() {
		super({
			name:         'MythicMob Skill',
			description:  'Executes a specific skill defined within the MythicMobs plugin on the targeted entities. This allows for complex custom mob behaviors and interactions with Fabled skills.',
			keywords:     'mythicmob, custom skill, mob ability, execute skill, trigger, summon, custom mob behavior',
			data:         [
				new StringSelect('MythicMob Skill', 'skill')
					.setTooltip('The MythicMob skill to cast')
			],
			summaryItems: ['skill']
		});
	}

	public static override new = () => new this();
}

class ParticleMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Particle',
			description:  'Spawns a static or arranged particle effect at or around the target\'s location. This is useful for visual feedback, aesthetic enhancements, or marking specific areas.',
			keywords:     'particle, visual, effect, static, arranged, sphere, circle, dust, smoke, spark, star, mark, indicate',
			data:         [
				...particleOptions(),
				new SectionMarker('Offset'),
				new DoubleSelect('Forward Offset', 'forward')
					.setTooltip('How far forward in front of the target in blocks to play the particles. A negative value will go behind'),
				new DoubleSelect('Upward Offset', 'upward')
					.setTooltip('How far above the target in blocks to play the particles. A negative value will go below'),
				new DoubleSelect('Right Offset', 'right')
					.setTooltip('How far to the right of the target to play the particles. A negative value will go to the left')
			],
			summaryItems: ['particle', 'amount', 'spread', 'dust-color']
		});
	}

	public static override new = () => new this();
}

class ParticleAnimationMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Particle Animation',
			description:  'Plays a complex, animated particle effect at the location of each target, applying various transformations over time. This allows for highly dynamic and visually striking effects that evolve or move.',
			keywords:     'particle, animation, dynamic, visual, effect, complex, transforming, motion, rotate, translate, scale',
			data:         [
				new IntSelect('Steps', 'steps', 1)
					.setTooltip('The number of times to play particles and apply translations each application'),
				new DoubleSelect('Frequency', 'frequency', 0.05)
					.setTooltip('How often to apply the animation in seconds. 0.05 is the fastest (1 tick). Lower than that will act the same'),
				new IntSelect('Angle', 'angle', 0)
					.setTooltip('How far the animation should rotate over the duration in degrees'),
				new IntSelect('Start Angle', 'start', 0)
					.setTooltip('The starting orientation of the animation. Horizontal translations and the forward/right offsets will be based off of this'),
				new AttributeSelect('Duration', 'duration', 5)
					.setTooltip('How long the animation should last for in seconds'),
				new AttributeSelect('H-Translation', 'h-translation')
					.setTooltip('How far the animation moves horizontally relative to the center over a cycle. Positive values make it expand from the center while negative values make it contract'),
				new AttributeSelect('V-Translation', 'v-translation')
					.setTooltip('How far the animation moves vertically over a cycle. Positive values make it rise while negative values make it sink'),
				new IntSelect('H-Cycles', 'h-cycles', 1)
					.setTooltip('How many times to move the animation position throughout the animation. Every other cycle moves it back to where it started. For example, two cycles would move it out and then back in'),
				new IntSelect('V-Cycles', 'v-cycles', 1)
					.setTooltip('How many times to move the animation position throughout the animation. Every other cycle moves it back to where it started. For example, two cycles would move it up and then back down'),

				...particleOptions(),

				new SectionMarker('Offset'),
				new DoubleSelect('Forward Offset', 'forward', 0)
					.setTooltip('How far forward in front of the target in blocks to play the particles. A negative value will go behind'),
				new DoubleSelect('Upward Offset', 'upward', 0)
					.setTooltip('How far above the target in blocks to play the particles. A negative value will go below'),
				new DoubleSelect('Right Offset', 'right', 0)
					.setTooltip('How far to the right of the target to play the particles. A negative value will go to the left'),

				new BooleanSelect('Rotate w/ Player', '-with-rotation')
					.setTooltip('Whether to follow the rotation of the player for the effect')
			],
			summaryItems: ['steps', 'frequency', 'angle', 'duration', 'particle', 'amount', 'spread', 'dust-color']
		});
	}

	public static override new = () => new this();
}

class ParticleEffectMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Particle Effect',
			description:  'Plays a particle effect that continuously follows the current target, utilizing custom formulas to define its shape, size, and motion. This is suitable for persistent visual auras, buffs, or environmental interactions that dynamically track an entity.',
			keywords:     'particle, effect, follow, track, aura, persistent, visual, shape, size, motion, dynamic, formula',
			data:         [
				...effectOptions(false)
			],
			summaryItems: ['effect-key', '-particle', '-particle-dust-color']
		});
	}

	public static override new = () => new this();
}

class ParticleImageMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Particle Image',
			description:  'Renders a static or animated image using particles, which can optionally follow the current target. This is useful for custom visual branding, spell effects, or creating dynamic scene elements.',
			keywords:     'particle, image, visual, effect, custom image, animated, gif, display, branding, spell, scene',
			data:         [
				new StringSelect('Effect Key', 'effect-key', 'default')
					.setTooltip('The key to refer to the effect by. Only one effect of each key can be active at a time.'),
				new StringSelect('Image', 'img', 'default.png')
					.setTooltip('The image to display. Put images in the plugins/Fabled/images folder'),
				new AttributeSelect('Duration', 'duration', 5)
					.setTooltip('The time to play the effect for in seconds'),
				new IntSelect('Interval', 'interval', 5)
					.setTooltip('Number of ticks between playing particles.'),
				new IntSelect('Frame Frequency', 'iterations-per-frame', 3)
					.setTooltip('Number of iterations before moving to the next frame of a gif. 1 is the fastest, 0 will not animate'),
				new IntSelect('View Range', 'view-range', 25)
					.setTooltip('How far away the effect can be seen from.'),

				new DropdownSelect('Direction', 'direction', ['XY', 'YZ', 'XZ'], 'XY')
					.setTooltip('The plane the shape formula applies to. Player follows the player\'s look direction. XZ would be flat, the other two are vertical.'),

				new DoubleSelect('Width', 'width', 3)
					.setTooltip('The width of the image in blocks'),
				new DoubleSelect('Height', 'height', 3)
					.requireValue('lock-aspect', [false])
					.setTooltip('The height of the image in blocks'),
				new BooleanSelect('Lock Aspect Ratio', 'lock-aspect', true)
					.setTooltip('Whether to keep the aspect ratio of the image'),
				new IntSelect('Resolution', 'resolution', 6)
					.setTooltip('Number of particles per block. 6 particles per block is typically pretty decent with a dust size of 1'),

				new StringSelect('Dust Size', 'dust-size', '1')
					.setTooltip('The formula for the size of the dust particles. <code>t</code> is the number of iterations, <code>l</code> is the skill level'),
				new StringSelect('Rotate', 'rotate', '0')
					.setTooltip('The formula to rotate the effect, <code>t</code> is the number of iterations, <code>l</code> is the skill level'),
				new StringSelect('Tilt', 'tilt', '0')
					.setTooltip('The formula to tilt the effect forward, <code>t</code> is the number of iterations, <code>l</code> is the skill level'),
				new StringSelect('Spin', 'spin', '0')
					.setTooltip('The formula to spin the effect, <code>t</code> is the number of iterations, <code>l</code> is the skill level'),
				new StringSelect('Scale', 'scale', '1')
					.setTooltip('The formula to scale the effect, <code>t</code> is the number of iterations, <code>l</code> is the skill level'),
				new StringSelect('Forward Offset', 'forward', '0')
					.setTooltip('The formula to offset the effect forward, <code>t</code> is the number of iterations, <code>l</code> is the skill level'),
				new StringSelect('Upward Offset', 'upward', '0')
					.setTooltip('The formula to offset the effect upward, <code>t</code> is the number of iterations, <code>l</code> is the skill level'),
				new StringSelect('Right Offset', 'right', '0')
					.setTooltip('The formula to offset the effect to the right, <code>t</code> is the number of iterations, <code>l</code> is the skill level'),
				new BooleanSelect('Rotate w/ Player', 'with-rotation', true)
					.setTooltip('Whether to follow the rotation of the player for the effect.')
			],
			summaryItems: ['effect-key', 'img', 'duration']
		});
	}

	public static override new = () => new this();
}

class ParticleProjectileMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Particle Projectile',
			description:  'Launches a projectile using particles as its visual that applies child components upon landing. The target passed on will be the collided target or the location where it landed if it missed',
			data:         [
				new BooleanSelect('Pierce', 'pierce')
					.setTooltip('Whether this projectile should pierce through initial targets and continue hitting those behind them'),
				new DropdownSelect('Group', 'group', ['Ally', 'Enemy'], 'Enemy')
					.setTooltip('The alignment of targets to hit'),
				new IntSelect('Steps', 'steps', 2)
					.setTooltip('Amount of collision steps to run per meter travelled.'),
				new AttributeSelect('Collision Radius', 'collision-radius', 1.5)
					.setTooltip('The radius of the projectile considered when calculating collisions.'),
				new AttributeSelect('Gravity', 'gravity', -0.04)
					.setTooltip('Vertical acceleration the projectile is subjected to, in meters per squared tick. Negative values make it fall while positive values make it rise.'),
				new AttributeSelect('Drag', 'drag', 0.02)
					.setTooltip('Air resistance of the projectile, in inverse seconds. Greater values mean the projectile will slow down more over time, and reach a lower terminal velocity.'),
				new IntSelect('Particle period', 'period', 2)
					.setTooltip('How often to play a particle effect where the projectile is.'),
				...homingOptions(),
				...projectileOptions(),
				...particleOptions(),

				...effectOptions(true)
			],
			preview:      [
				new IntSelect('Refresh period', 'period', 5)
					.setTooltip('How many ticks to wait before refreshing the preview, recalculating targets and the location of the particle effects'),

				new SectionMarker('Particles at target'),
				new BooleanSelect('Particles at target', 'per-target', false)
					.setTooltip('Displays particles at the location of the current targets'),
				new DropdownSelect('Particle', 'per-target-particle', getParticles, 'Crit')
					.setTooltip('The type of particle to display')
					.requireValue('per-target', [true]),
				new DropdownSelect('Material', 'per-target-material', (() => [...getMaterials()]), 'Arrow')
					.requireValue('per-target-particle', ['Item crack', 'Item'])
					.requireValue('per-target', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'per-target-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('per-target-particle', [
						'Block crack',
						'Block dust',
						'Block',
						'Falling dust',
						'Block marker'])
					.requireValue('per-target', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'per-target-durability', 0)
					.requireValue('per-target-particle', ['Item crack', 'Item'])
					.requireValue('per-target', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'per-target-type', 0)
					.requireValue('per-target-particle', ['Item crack', 'Item'])
					.requireValue('per-target', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'per-target-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'per-target-final-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'per-target-dust-size', 1)
					.requireValue('per-target-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The size of the dust particles'),

				// Bukkit particle data value
				new IntSelect('Effect Data', 'per-target-data')
					.requireValue('per-target-particle',
						[
							'Smoke',
							'Ender Signal',
							'Mobspawner Flames',
							'Potion Break',
							'Sculk charge'
						])
					.requireValue('per-target', [true])
					.setTooltip('The data value to use for the particle. The effect changes between particles such as the orientation for smoke particles or the color for potion break'),
				new DoubleSelect('DX', 'per-target-dx')
					.requireValue('per-target', [true])
					.setTooltip('Offset in the X direction, used as the Red value for some particles'),
				new DoubleSelect('DY', 'per-target-dy')
					.requireValue('per-target', [true])
					.setTooltip('Offset in the Y direction, used as the Green value for some particles'),
				new DoubleSelect('DZ', 'per-target-dz')
					.requireValue('per-target', [true])
					.setTooltip('Offset in the Z direction, used as the Blue value for some particles'),
				new DoubleSelect('Amount', 'per-target-amount', 1)
					.requireValue('per-target', [true])
					.setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color'),
				new DoubleSelect('Speed', 'per-target-speed', 0.1)
					.requireValue('per-target', [true])
					.setTooltip('Speed of the particle. For some particles controls other parameters, such as size'),
				new DropdownSelect('Arrangement', 'per-target-arrangement', ['Sphere', 'Circle', 'Hemisphere'], 'Sphere')
					.requireValue('per-target', [true])
					.setTooltip('The arrangement to use for the particles. Circle is a 2D circle, Hemisphere is half a 3D sphere, and Sphere is a 3D sphere'),
				new DropdownSelect('Circle Direction', 'per-target-direction', ['XY', 'XZ', 'YZ'], 'XZ')
					.requireValue('per-target' + '-arrangement', ['Circle'])
					.requireValue('per-target', [true])
					.setTooltip('The orientation of the circle. XY and YZ are vertical circles while XZ is a horizontal circle'),
				new AttributeSelect('Radius', 'per-target-radius', 0.5)
					.requireValue('per-target', [true])
					.setTooltip('The radius of the arrangement in blocks'),
				new BooleanSelect('Increase size by hitbox', 'per-target-hitbox', true)
					.requireValue('per-target', [true])
					.setTooltip('Increases the \'radius\' parameter by the size of the target\'s hitbox'),
				new AttributeSelect('Points', 'per-target-particles', 20)
					.requireValue('per-target', [true])
					.setTooltip('The amount of points that conform the chosen arrangement'),

				new SectionMarker('Path Preview'),
				new BooleanSelect('Path Preview', 'path', false)
					.setTooltip('Displays particles through the paths of the projectiles'),
				new DoubleSelect('Steps per particle', 'path-steps', 2)
					.setTooltip('How many collision steps to run between each particle display')
					.requireValue('path', [true]),
				new DropdownSelect('Particle', 'path-particle', getParticles, 'Crit')
					.setTooltip('The type of particle to display')
					.requireValue('path', [true]),
				new DropdownSelect('Material', 'path-material', (() => [...getMaterials()]), 'Arrow')
					.requireValue('path-particle', ['Item crack', 'Item'])
					.requireValue('path', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'path-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('path-particle', [
						'Block crack',
						'Block dust',
						'Block',
						'Falling dust',
						'Block marker'])
					.requireValue('path', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'path-durability', 0)
					.requireValue('path-particle', ['Item crack', 'Item'])
					.requireValue('path', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'path-type', 0)
					.requireValue('path-particle', ['Item crack', 'Item'])
					.requireValue('path', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'path-dust-color', '#FF0000')
					.requireValue('path-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'path-final-dust-color', '#FF0000')
					.requireValue('path-particle', ['Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'path-dust-size', 1)
					.requireValue('path-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The size of the dust particles'),

				// Bukkit particle data value
				new IntSelect('Effect Data', 'path-data')
					.requireValue('path-particle',
						[
							'Smoke',
							'Ender Signal',
							'Mobspawner Flames',
							'Potion Break',
							'Sculk charge'
						])
					.requireValue('path', [true])
					.setTooltip('The data value to use for the particle. The effect changes between particles such as the orientation for smoke particles or the color for potion break'),
				new DoubleSelect('DX', 'path-dx')
					.requireValue('path', [true])
					.setTooltip('Offset in the X direction, used as the Red value for some particles'),
				new DoubleSelect('DY', 'path-dy')
					.requireValue('path', [true])
					.setTooltip('Offset in the Y direction, used as the Green value for some particles'),
				new DoubleSelect('DZ', 'path-dz')
					.requireValue('path', [true])
					.setTooltip('Offset in the Z direction, used as the Blue value for some particles'),
				new DoubleSelect('Amount', 'path-amount', 1)
					.requireValue('path', [true])
					.setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color'),
				new DoubleSelect('Speed', 'path-speed', 0.1)
					.requireValue('path', [true])
					.setTooltip('Speed of the particle. For some particles controls other parameters, such as size')
			],
			summaryItems: ['steps', 'gravity', 'drag', 'frequency', 'pierce', 'group', 'particle', 'amount', 'spread', 'dust-color', 'correction', 'homing']
		}, true);
	}

	public static override new = () => new this();
}

class PassiveMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Passive',
			description:  'Applies child components continuously at regular intervals (defined by the period). This is ideal for persistent buffs, damage over time effects, or ongoing area-of-effect abilities.',
			keywords:     'passive, continuous, periodic, interval, buff, dot, damage over time, aura, persistent, ongoing',
			data:         [
				new AttributeSelect('Seconds', 'seconds', 1)
					.setTooltip('The delay in seconds between each application')
			],
			summaryItems: ['seconds']
		}, true);
	}

	public static override new = () => new this();
}

class PermissionMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Permission',
			description:  'Grants a specific Minecraft permission to each targeted player for a limited duration. This mechanic requires a Vault-compatible permissions plugin. It\'s useful for temporarily elevating player privileges, unlocking restricted areas, or granting access to special commands.',
			keywords:     'permission, grant, temporary, access, privilege, role, rank, vault, security, command',
			data:         [
				new StringSelect('Permission', 'perm', 'plugin.perm.key')
					.setTooltip('The permission to give to the player'),
				new AttributeSelect('Seconds', 'seconds', 3)
					.setTooltip('How long in seconds to give the permission to the player')
			],
			summaryItems: ['perm', 'seconds']
		});
	}

	public static override new = () => new this();
}

class PotionMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Potion',
			description:  'Applies a standard Minecraft potion effect to the target for a specified duration and intensity. This is a versatile mechanic for applying buffs, debuffs, or special status effects like speed, strength, poison, or slowness.',
			keywords:     'potion, effect, status, buff, debuff, speed, strength, poison, weakness, heal, damage, duration, tier, amplifier',
			data:         [
				new DropdownSelect('Potion', 'potion', getPotionTypes, 'Absorption')
					.setTooltip('The type of potion effect to apply'),
				new BooleanSelect('Ambient Particles', 'ambient', true)
					.setTooltip('Whether to show ambient particles'),
				new AttributeSelect('Tier', 'tier', 1)
					.setTooltip('The strength of the potion'),
				new AttributeSelect('Seconds', 'seconds', 3, 1)
					.setTooltip('How long to apply the effect for')
			],
			summaryItems: ['potion', 'tier', 'seconds']
		}, false);
	}

	public static override new = () => new this();
}

class PotionProjectileMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Potion Projectile',
			description:  'Launches a projectile that visually appears as a splash potion (or lingering potion) and applies child components upon impact. The mechanic is highly configurable, allowing for custom colors, homing, spread patterns, and detailed area-of-effect clouds for lingering potions.',
			keywords:     'projectile, potion, splash potion, lingering potion, launch, throw, impact, homing, area effect, cloud, custom color, ranged, support',
			data:         [
				new ColorSelect('Color', 'color', '#ff0000')
					.setTooltip('The hex color code to use for the potion'),
				new DropdownSelect('Group', 'group', ['Ally', 'Enemy', 'Both'], 'Enemy')
					.setTooltip('The alignment of entities to hit'),
				new BooleanSelect('Flaming', 'flaming', false)
					.setTooltip('Whether to make the launched projectiles on fire'),
				new BooleanSelect('Linger', 'linger', false)
					.setTooltip('Whether the potion should be a lingering potion (for 1.9+ only)'),

				new SectionMarker('Area Effect Cloud')
					.requireValue('linger', [true]),
				new AttributeSelect('Duration', 'duration', 30)
					.setTooltip('How long the resulting area effect cloud lasts, in seconds.')
					.requireValue('linger', [true]),
				new AttributeSelect('Wait time', 'wait-time', 0.5)
					.setTooltip('How long an entity has to be exposed to the cloud before its effect is applied, in seconds.')
					.requireValue('linger', [true]),
				new AttributeSelect('Reapplication delay', 'reapplication-delay', 1)
					.setTooltip('For how long an entity will be immune from subsequent exposure, in seconds.')
					.requireValue('linger', [true]),
				new AttributeSelect('Duration on use', 'duration-on-use', 0)
					.setTooltip('How much the duration of the cloud will decrease by when it applies an effect to an entity, in seconds.')
					.requireValue('linger', [true]),
				new AttributeSelect('Radius', 'cloud-radius', 3)
					.setTooltip('The initial radius of the cloud, in meters.')
					.requireValue('linger', [true]),
				new AttributeSelect('Radius on use', 'radius-on-use', -0.5)
					.setTooltip('How much the radius of the cloud will decrease by when it applies an effect to an entity, in meters.')
					.requireValue('linger', [true]),
				new AttributeSelect('Radius per tick', 'radius-per-tick', -0.1)
					.setTooltip('How much the radius of the cloud will decrease by, in meters per second')
					.requireValue('linger', [true]),
				new DropdownSelect('Cloud Particle', 'cloud-particle', getParticles, 'Spell mob')
					.setTooltip('The type of particle the cloud is composed of')
					.requireValue('linger', [true]),
				new DropdownSelect('Material', 'cloud-material', (() => [...getMaterials()]), 'Arrow')
					.requireValue('cloud-particle', ['Item crack', 'Item'])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'cloud-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('cloud-particle', [
						'Block crack',
						'Block dust',
						'Block',
						'Falling dust',
						'Block marker'])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'cloud-durability', 0)
					.requireValue('cloud-particle', ['Item crack', 'Item'])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'cloud-type', 0)
					.requireValue('cloud-particle', ['Item crack', 'Item'])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'cloud-dust-color', '#FF0000')
					.requireValue('cloud-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'cloud-final-dust-color', '#FF0000')
					.requireValue('cloud-particle', ['Dust color transition'])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'cloud-dust-size', 1)
					.requireValue('cloud-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.setTooltip('The size of the dust particles'),

				...homingOptions(),
				...projectileOptions(),
				...particleOptions(),
				...effectOptions(true)
			],
			preview:      [
				new IntSelect('Refresh period', 'period', 5)
					.setTooltip('How many ticks to wait before refreshing the preview, recalculating targets and the location of the particle effects'),

				new SectionMarker('Particles at target'),
				new BooleanSelect('Particles at target', 'per-target', false)
					.setTooltip('Displays particles at the location of the current targets'),
				new DropdownSelect('Particle', 'per-target-particle', getParticles, 'Crit')
					.setTooltip('The type of particle to display')
					.requireValue('per-target', [true]),
				new DropdownSelect('Material', 'per-target-material', (() => [...getMaterials()]), 'Arrow')
					.requireValue('per-target-particle', ['Item crack', 'Item'])
					.requireValue('per-target', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'per-target-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('per-target-particle', [
						'Block crack',
						'Block dust',
						'Block',
						'Falling dust',
						'Block marker'])
					.requireValue('per-target', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'per-target-durability', 0)
					.requireValue('per-target-particle', ['Item crack', 'Item'])
					.requireValue('per-target', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'per-target-type', 0)
					.requireValue('per-target-particle', ['Item crack', 'Item'])
					.requireValue('per-target', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'per-target-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'per-target-final-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'per-target-dust-size', 1)
					.requireValue('per-target-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The size of the dust particles'),

				// Bukkit particle data value
				new IntSelect('Effect Data', 'per-target-data')
					.requireValue('per-target-particle',
						[
							'Smoke',
							'Ender Signal',
							'Mobspawner Flames',
							'Potion Break',
							'Sculk charge'
						])
					.requireValue('per-target', [true])
					.setTooltip('The data value to use for the particle. The effect changes between particles such as the orientation for smoke particles or the color for potion break'),
				new DoubleSelect('DX', 'per-target-dx')
					.requireValue('per-target', [true])
					.setTooltip('Offset in the X direction, used as the Red value for some particles'),
				new DoubleSelect('DY', 'per-target-dy')
					.requireValue('per-target', [true])
					.setTooltip('Offset in the Y direction, used as the Green value for some particles'),
				new DoubleSelect('DZ', 'per-target-dz')
					.requireValue('per-target', [true])
					.setTooltip('Offset in the Z direction, used as the Blue value for some particles'),
				new DoubleSelect('Amount', 'per-target-amount', 1)
					.requireValue('per-target', [true])
					.setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color'),
				new DoubleSelect('Speed', 'per-target-speed', 0.1)
					.requireValue('per-target', [true])
					.setTooltip('Speed of the particle. For some particles controls other parameters, such as size'),
				new DropdownSelect('Arrangement', 'per-target-arrangement', ['Sphere', 'Circle', 'Hemisphere'], 'Sphere')
					.requireValue('per-target', [true])
					.setTooltip('The arrangement to use for the particles. Circle is a 2D circle, Hemisphere is half a 3D sphere, and Sphere is a 3D sphere'),
				new DropdownSelect('Circle Direction', 'per-target-direction', ['XY', 'XZ', 'YZ'], 'XZ')
					.requireValue('per-target' + '-arrangement', ['Circle'])
					.requireValue('per-target', [true])
					.setTooltip('The orientation of the circle. XY and YZ are vertical circles while XZ is a horizontal circle'),
				new AttributeSelect('Radius', 'per-target-radius', 0.5)
					.requireValue('per-target', [true])
					.setTooltip('The radius of the arrangement in blocks'),
				new BooleanSelect('Increase size by hitbox', 'per-target-hitbox', true)
					.requireValue('per-target', [true])
					.setTooltip('Increases the \'radius\' parameter by the size of the target\'s hitbox'),
				new AttributeSelect('Points', 'per-target-particles', 20)
					.requireValue('per-target', [true])
					.setTooltip('The amount of points that conform the chosen arrangement'),

				new SectionMarker('Path Preview'),
				new BooleanSelect('Path Preview', 'path', false)
					.setTooltip('Displays particles through the paths of the projectiles'),
				new DoubleSelect('Steps per particle', 'path-steps', 2)
					.setTooltip('How many collision steps to run between each particle display')
					.requireValue('path', [true]),
				new DropdownSelect('Particle', 'path-particle', getParticles, 'Crit')
					.setTooltip('The type of particle to display')
					.requireValue('path', [true]),
				new DropdownSelect('Material', 'path-material', (() => [...getMaterials()]), 'Arrow')
					.requireValue('path-particle', ['Item crack', 'Item'])
					.requireValue('path', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'path-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('path-particle', [
						'Block crack',
						'Block dust',
						'Block',
						'Falling dust',
						'Block marker'])
					.requireValue('path', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'path-durability', 0)
					.requireValue('path-particle', ['Item crack', 'Item'])
					.requireValue('path', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'path-type', 0)
					.requireValue('path-particle', ['Item crack', 'Item'])
					.requireValue('path', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'path-dust-color', '#FF0000')
					.requireValue('path-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'path-final-dust-color', '#FF0000')
					.requireValue('path-particle', ['Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'path-dust-size', 1)
					.requireValue('path-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The size of the dust particles'),

				// Bukkit particle data value
				new IntSelect('Effect Data', 'path-data')
					.requireValue('path-particle',
						[
							'Smoke',
							'Ender Signal',
							'Mobspawner Flames',
							'Potion Break',
							'Sculk charge'
						])
					.requireValue('path', [true])
					.setTooltip('The data value to use for the particle. The effect changes between particles such as the orientation for smoke particles or the color for potion break'),
				new DoubleSelect('DX', 'path-dx')
					.requireValue('path', [true])
					.setTooltip('Offset in the X direction, used as the Red value for some particles'),
				new DoubleSelect('DY', 'path-dy')
					.requireValue('path', [true])
					.setTooltip('Offset in the Y direction, used as the Green value for some particles'),
				new DoubleSelect('DZ', 'path-dz')
					.requireValue('path', [true])
					.setTooltip('Offset in the Z direction, used as the Blue value for some particles'),
				new DoubleSelect('Amount', 'path-amount', 1)
					.requireValue('path', [true])
					.setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color'),
				new DoubleSelect('Speed', 'path-speed', 0.1)
					.requireValue('path', [true])
					.setTooltip('Speed of the particle. For some particles controls other parameters, such as size')
			],
			summaryItems: ['group', 'color', 'linger', 'velocity', 'spread', 'angle', 'amount', 'correction', 'homing']
		}, true);
	}

	public static override new = () => new this();
}

class ProjectileMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Projectile',
			description:  'Launches a generic projectile (e.g., arrow, snowball, fire charge) that applies child components upon hitting a target. This mechanic is highly configurable, offering options for item overrides, flaming effects, and various projectile behaviors.',
			keywords:     'projectile, launch, fire, shoot, arrow, snowball, fire charge, item override, custom projectile, homing, spread',
			data:         [
				new DropdownSelect('Projectile', 'projectile', getProjectiles, 'Arrow')
					.setTooltip('The type of projectile to fire'),
				new BooleanSelect('Flaming', 'flaming', false)
					.setTooltip('Whether to make the launched projectiles on fire'),
				new DropdownSelect('Cost', 'cost', ['None', 'All', 'One'], 'None')
					.setTooltip('The item cost of the skill. "One" will only charge the player 1 item of it\'s type, whereas "All" will charge 1 for each fired projectile'),

				new SectionMarker('Item Override')
					.requireValue('projectile', ['Egg', 'Ender pearl', 'Snowball', 'Splash potion', 'Thrown exp bottle', 'Trident']),
				new BooleanSelect('Override item', 'override-item', false)
					.setTooltip('Whether to override the item display of the projectile'),
				new DropdownSelect('Material', 'material', (() => [...getMaterials()]), 'Snowball')
					.requireValue('projectile', ['Egg', 'Ender pearl', 'Snowball', 'Splash potion', 'Thrown exp bottle', 'Trident'])
					.requireValue('override-item', [true])
					.setTooltip('The material to use for the projectile'),
				new BooleanSelect('Enchanted', 'enchanted', false)
					.requireValue('projectile', ['Egg', 'Ender pearl', 'Snowball', 'Splash potion', 'Thrown exp bottle', 'Trident'])
					.requireValue('override-item', [true])
					.setTooltip('Whether to apply the enchanted glint in the item'),
				new IntSelect('Durability', 'durability', 0)
					.requireValue('projectile', ['Egg', 'Ender pearl', 'Snowball', 'Splash potion', 'Thrown exp bottle', 'Trident'])
					.requireValue('override-item', [true])
					.setTooltip('The durability to be reduced from the item used for the projectile'),
				new IntSelect('CustomModelData', 'custom-model-data', 0)
					.requireValue('projectile', ['Egg', 'Ender pearl', 'Snowball', 'Splash potion', 'Thrown exp bottle', 'Trident'])
					.requireValue('override-item', [true])
					.setTooltip('The CustomModelData of the item used for the projectile'),

				...homingOptions(),
				...projectileOptions(),
				...particleOptions(),
				...effectOptions(true)
			],
			preview:      [
				new IntSelect('Refresh period', 'period', 5)
					.setTooltip('How many ticks to wait before refreshing the preview, recalculating targets and the location of the particle effects'),

				new SectionMarker('Particles at target'),
				new BooleanSelect('Particles at target', 'per-target', false)
					.setTooltip('Displays particles at the location of the current targets'),
				new DropdownSelect('Particle', 'per-target-particle', getParticles, 'Crit')
					.setTooltip('The type of particle to display')
					.requireValue('per-target', [true]),
				new DropdownSelect('Material', 'per-target-material', (() => [...getMaterials()]), 'Arrow')
					.requireValue('per-target-particle', ['Item crack', 'Item'])
					.requireValue('per-target', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'per-target-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('per-target-particle', [
						'Block crack',
						'Block dust',
						'Block',
						'Falling dust',
						'Block marker'])
					.requireValue('per-target', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'per-target-durability', 0)
					.requireValue('per-target-particle', ['Item crack', 'Item'])
					.requireValue('per-target', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'per-target-type', 0)
					.requireValue('per-target-particle', ['Item crack', 'Item'])
					.requireValue('per-target', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'per-target-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'per-target-final-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'per-target-dust-size', 1)
					.requireValue('per-target-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The size of the dust particles'),

				// Bukkit particle data value
				new IntSelect('Effect Data', 'per-target-data')
					.requireValue('per-target-particle',
						[
							'Smoke',
							'Ender Signal',
							'Mobspawner Flames',
							'Potion Break',
							'Sculk charge'
						])
					.requireValue('per-target', [true])
					.setTooltip('The data value to use for the particle. The effect changes between particles such as the orientation for smoke particles or the color for potion break'),
				new DoubleSelect('DX', 'per-target-dx')
					.requireValue('per-target', [true])
					.setTooltip('Offset in the X direction, used as the Red value for some particles'),
				new DoubleSelect('DY', 'per-target-dy')
					.requireValue('per-target', [true])
					.setTooltip('Offset in the Y direction, used as the Green value for some particles'),
				new DoubleSelect('DZ', 'per-target-dz')
					.requireValue('per-target', [true])
					.setTooltip('Offset in the Z direction, used as the Blue value for some particles'),
				new DoubleSelect('Amount', 'per-target-amount', 1)
					.requireValue('per-target', [true])
					.setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color'),
				new DoubleSelect('Speed', 'per-target-speed', 0.1)
					.requireValue('per-target', [true])
					.setTooltip('Speed of the particle. For some particles controls other parameters, such as size'),
				new DropdownSelect('Arrangement', 'per-target-arrangement', ['Sphere', 'Circle', 'Hemisphere'], 'Sphere')
					.requireValue('per-target', [true])
					.setTooltip('The arrangement to use for the particles. Circle is a 2D circle, Hemisphere is half a 3D sphere, and Sphere is a 3D sphere'),
				new DropdownSelect('Circle Direction', 'per-target-direction', ['XY', 'XZ', 'YZ'], 'XZ')
					.requireValue('per-target' + '-arrangement', ['Circle'])
					.requireValue('per-target', [true])
					.setTooltip('The orientation of the circle. XY and YZ are vertical circles while XZ is a horizontal circle'),
				new AttributeSelect('Radius', 'per-target-radius', 0.5)
					.requireValue('per-target', [true])
					.setTooltip('The radius of the arrangement in blocks'),
				new BooleanSelect('Increase size by hitbox', 'per-target-hitbox', true)
					.requireValue('per-target', [true])
					.setTooltip('Increases the \'radius\' parameter by the size of the target\'s hitbox'),
				new AttributeSelect('Points', 'per-target-particles', 20)
					.requireValue('per-target', [true])
					.setTooltip('The amount of points that conform the chosen arrangement'),

				new SectionMarker('Path Preview'),
				new BooleanSelect('Path Preview', 'path', false)
					.setTooltip('Displays particles through the paths of the projectiles'),
				new DoubleSelect('Steps per particle', 'path-steps', 2)
					.setTooltip('How many collision steps to run between each particle display')
					.requireValue('path', [true]),
				new DropdownSelect('Particle', 'path-particle', getParticles, 'Crit')
					.setTooltip('The type of particle to display')
					.requireValue('path', [true]),
				new DropdownSelect('Material', 'path-material', (() => [...getMaterials()]), 'Arrow')
					.requireValue('path-particle', ['Item crack', 'Item'])
					.requireValue('path', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'path-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('path-particle', [
						'Block crack',
						'Block dust',
						'Block',
						'Falling dust',
						'Block marker'])
					.requireValue('path', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'path-durability', 0)
					.requireValue('path-particle', ['Item crack', 'Item'])
					.requireValue('path', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'path-type', 0)
					.requireValue('path-particle', ['Item crack', 'Item'])
					.requireValue('path', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'path-dust-color', '#FF0000')
					.requireValue('path-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'path-final-dust-color', '#FF0000')
					.requireValue('path-particle', ['Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'path-dust-size', 1)
					.requireValue('path-particle', ['Redstone', 'Dust', 'Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The size of the dust particles'),

				// Bukkit particle data value
				new IntSelect('Effect Data', 'path-data')
					.requireValue('path-particle',
						[
							'Smoke',
							'Ender Signal',
							'Mobspawner Flames',
							'Potion Break',
							'Sculk charge'
						])
					.requireValue('path', [true])
					.setTooltip('The data value to use for the particle. The effect changes between particles such as the orientation for smoke particles or the color for potion break'),
				new DoubleSelect('DX', 'path-dx')
					.requireValue('path', [true])
					.setTooltip('Offset in the X direction, used as the Red value for some particles'),
				new DoubleSelect('DY', 'path-dy')
					.requireValue('path', [true])
					.setTooltip('Offset in the Y direction, used as the Green value for some particles'),
				new DoubleSelect('DZ', 'path-dz')
					.requireValue('path', [true])
					.setTooltip('Offset in the Z direction, used as the Blue value for some particles'),
				new DoubleSelect('Amount', 'path-amount', 1)
					.requireValue('path', [true])
					.setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color'),
				new DoubleSelect('Speed', 'path-speed', 0.1)
					.requireValue('path', [true])
					.setTooltip('Speed of the particle. For some particles controls other parameters, such as size')
			],
			summaryItems: ['projectile', 'flaming', 'cost', 'particle', 'amount', 'spread', 'dust-color', 'effect-key', 'correction', 'homing']
		}, true);
	}

	public static override new = () => new this();
}

class PurgeMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Purge',
			description:  'Removes beneficial potion effects or custom positive status effects from the targeted entities. This is useful for counteracting enemy buffs, strategic debuffing, or specialized cleanse abilities that target only positive effects.',
			keywords:     'purge, dispel, remove buff, cleanse, counter, debuff, status, potion, beneficial, positive',
			data:         [
				new DropdownSelect('Potion', 'potion', getGoodPotions, undefined, true)
					.setTooltip('The potion effect to remove from the target, if any'),
				new DropdownSelect('Status', 'status', ['All', 'Absorb', 'Invincible'], ['All'], true)
					.setTooltip('The status to remove from the target, if any')
			],
			summaryItems: ['potion', 'status']
		}, false);
	}

	public static override new = () => new this();
}

class PushMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Push',
			description:  'Applies a force to push or pull the target entity relative to the caster, creating knockback or drawing them closer. This is useful for crowd control, repositioning enemies, or aiding allies.',
			keywords:     'push, pull, knockback, force, propel, reposition, crowd control, movement, physics, repel, attract',
			data:         [
				new DropdownSelect('Type', 'type', ['Fixed', 'Inverse', 'Scaled'], 'Fixed')
					.setTooltip('How to scale the speed based on relative position. Fixed does the same speed to all targets. Inverse pushes enemies farther away faster. Scaled pushes enemies closer faster'),
				new AttributeSelect('Speed', 'speed', 3, 1)
					.setTooltip('How fast to push the target away. Use a negative value to pull them closer'),
				new StringSelect('Source', 'source', 'none')
					.setTooltip('The source to push/pull from. This should be a key used in a Remember Targets mechanic. If no targets are remembered, this will default to the caster')
			],
			summaryItems: ['type', 'speed']
		}, false);
	}

	public static override new = () => new this();
}

class RememberTargetsMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Remember Targets',
			description:  'Stores the currently targeted entities under a unique key, allowing them to be recalled and targeted by subsequent abilities. This is essential for creating multi-stage abilities or tracking specific entities across different parts of a skill chain.',
			keywords:     'remember, store, save, track, recall, targets, multi-stage, chain, persistent, entities',
			data:         [
				new StringSelect('Key', 'key', 'target')
					.setTooltip('The unique key to store the targets under. The "Remember" target will use this key to apply effects to the targets later on'),
				new BooleanSelect('Overwrite', 'overwrite', true)
					.setTooltip('Whether to overwrite an existing target group, setting this to False would append all targets to said group')
			],
			summaryItems: ['key']
		}, false);
	}

	public static override new = () => new this();
}

class RepeatMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Repeat',
			description:  'Executes child components multiple times, with configurable delays between repetitions and an initial delay. This is useful for creating sustained effects, pulsing abilities, or multi-hit attacks.',
			keywords:     'repeat, loop, iterate, multiple, delay, period, sustained, pulsing, multi-hit, interval',
			data:         [
				new AttributeSelect('Repetitions', 'repetitions', 3)
					.setTooltip('How many times to activate child components'),
				new DoubleSelect('Period', 'period', 1)
					.setTooltip('The time in seconds between each time applying child components'),
				new DoubleSelect('Delay', 'delay')
					.setTooltip('The initial delay before starting to apply child components'),
				new BooleanSelect('Stop on Fail', 'stop-on-fail', false)
					.setTooltip('Whether to stop the repeat task early if the effects fail')
			],
			summaryItems: ['repetitions', 'period', 'delay', 'stop-on-fail']
		}, true);
	}

	public static override new = () => new this();
}

class ShieldMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Shield',
			description:  'Applies a temporary shield to the target that absorbs a specified amount of incoming damage, optionally with a percentage reduction. This is useful for defensive abilities, mitigating bursts of damage, or granting temporary invulnerability.',
			keywords:     'shield, absorb, damage reduction, defense, protect, guard, temporary, invulnerability, mitigation, buff',
			data:         [
				new StringSelect('Name', 'name', 'Shield')
					.setTooltip('The name of the shield. This is used for display purposes'),
				new AttributeSelect('Amount', 'amount', 5)
					.setTooltip('The amount of damage the shield can absorb'),
				new AttributeSelect('Duration', 'duration', 3)
					.setTooltip('How long in seconds the shield will last'),
				new StringSelect('Classifier', 'classifier', 'skill_defense_default')
					.setTooltip('The classifier of the shield. This is intended to be used with damage classifiers from the DamageMechanic, or Divinity damage types using <code>DIVINITY_type</code>'),
				new AttributeSelect('Percent', 'percent', 1)
					.setTooltip('The percentage of damage to absorb. 1 is 100% and 0.5 is 50%'),
				new DropdownSelect('Display', 'display', ['Action Bar', 'Title', 'Chat', 'Boss Bar'])
					.setTooltip('Where to display the shield information when active'),
				new DropdownSelect('Bar Color', 'color', [
					'Blue',
					'Green',
					'Pink',
					'Purple',
					'Red',
					'White',
					'Yellow'
				], 'Green')
					.setTooltip('The color of the boss bar when displayed')
					.requireValue('display', ['Boss Bar']),
				new DropdownSelect('Bar Style', 'style', ['Solid', 'Segmented 6', 'Segmented 10', 'Segmented 12', 'Segmented 20'], 'Solid')
					.setTooltip('The style of the boss bar when displayed')
					.requireValue('display', ['Boss Bar']),
				new DropdownSelect('Hit Sound', 'hit-sound', () => ['None', ...getSounds()], 'None')
					.setTooltip('The sound to play when the shield is hit'),
				new DropdownSelect('Break Sound', 'break-sound', () => ['None', ...getSounds()], 'None')
					.setTooltip('The sound to play when the shield is broken')
			],
			summaryItems: ['amount', 'duration', 'percent', 'display']
		});
	}

	public static override new = () => new this();
}

class SignalEmitMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Signal Emit',
			description:  'Emits a custom signal to targeted entities, allowing for complex inter-skill communication and event-driven abilities. This signal can carry arguments and be processed independently by other triggers or mechanics.',
			keywords:     'signal, emit, custom event, communicate, trigger, event, inter-skill, arguments, broadcast',
			data:         [
				new StringSelect('Signal', 'signal')
					.setTooltip('The name of signal will be emit.'),
				new BooleanSelect('Self-handling', 'handler', false)
					.setTooltip('\nIf true, the signal will be sent to the caster itself and the target are the current targets.\n If false, a signal is sent to each target and the target is caster'),
				new StringListSelect('Arguments', 'argument')
					.setTooltip('Arguments used for signal processing. One value per line. The value will be stored in value api-arg[<index>]. The first value will be specially stored at api-arg')
			],
			summaryItems: ['signal', 'handler', 'argument']
		}, false);
	}

	public static override new = () => new this();
}

class SkillCastMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Skill Cast',
			description:  'Forces targeted entities (recommended for players) to cast a specified skill, with options for cast mode (all, first, random) and force casting regardless of skill availability. This is useful for creating combined abilities, skill sequences, or AI behavior.',
			keywords:     'skill cast, force cast, ability, spell, sequence, combo, ai behavior, combined ability, trigger skill',
			data:         [
				new DropdownSelect('Cast mode', 'mode', ['All', 'First', 'Random'], 'All')
					.setTooltip('Choose which skills to cast (excluding unavailable skills).'),
				new BooleanSelect('Force cast', 'force', false)
					.setTooltip('True if target will cast regardless of whether they have that skill or not'),
				new StringListSelect('Skills', 'skills')
					.setTooltip('The list of skills. Each skill can come with the level like "example skill:3". If the target is a player and has the skill, level will is available level. Else, level is 1.')
			],
			summaryItems: ['mode', 'force', 'skills']
		}, false);
	}

	public static override new = () => new this();
}

class SoundMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Sound',
			description:  'Plays a specified sound effect at the target\'s location, with configurable volume and pitch. This can be a custom sound from a resource pack or a default game sound. It is useful for audio feedback, immersive spell effects, or environmental cues.',
			keywords:     'sound, audio, play sound, custom sound, game sound, volume, pitch, feedback, immersive, effect',
			data:         [
				new DropdownSelect('Sound', 'sound', (() => ['Custom', ...getSounds()]), 'Ambient Cave')
					.setTooltip('The sound clip to play. Select \'Custom\' to enter custom sounds from your resource pack'),
				new StringSelect('Custom sound name', 'custom', 'myrp:some_sound')
					.requireValue('sound', ['Custom'])
					.setTooltip('Namespaced key of your custom sound'),
				new AttributeSelect('Volume', 'volume', 100)
					.setTooltip('The volume of the sound as a percentage. Numbers above 100 will not get any louder, but will be heard from a farther distance'),
				new AttributeSelect('Pitch', 'pitch', 1)
					.setTooltip('The pitch of the sound as a numeric speed multiplier between 0.5 and 2')
			],
			summaryItems: ['sound', 'volume', 'pitch']
		}, false);
	}

	public static override new = () => new this();
}

class StatMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Stat',
			description:  'Temporarily modifies a player\'s bonus statistics (stats) from the attributes system, suchs as movement speed, attack damage, or health regeneration. This is distinct from core attributes and provides granular control over temporary stat adjustments. All available stats are detailed in the <a href="https://github.com/magemonkeystudio/fabled/wiki/attributes.yml">attributes wiki</a>.',
			keywords:     'stat, bonus, modify, temporary, player stats, movement speed, attack damage, health regeneration, buff, debuff, attribute, enhance, weaken',
			data:         [
				new StringSelect('Stat', 'key', 'health')
					.setTooltip('The name of the stat to add to'),
				new DropdownSelect('Operation', 'operation', ['ADD_NUMBER', 'MULTIPLY_PERCENTAGE'], 'ADD_NUMBER')
					.setTooltip('The operation on the original value by amount, ADD_NUMBER: Scalar adding, MULTIPLY_PERCENTAGE: Multiply the value by amount'),
				new AttributeSelect('Amount', 'amount', 5, 2)
					.setTooltip('The amount to use with the operation'),
				new AttributeSelect('Seconds', 'seconds', 3)
					.setTooltip('How long in seconds to give the stat to the player'),
				new BooleanSelect('Stackable', 'stackable')
					.setTooltip('Whether applying multiple times stacks the effects')
			],
			summaryItems: ['key', 'operation', 'amount', 'seconds']
		});
	}

	public static override new = () => new this();
}

class StatusMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Status',
			description:  'Applies a custom status effect (e.g., Stun, Root, Silence) to the target for a specified duration. This is a powerful mechanic for crowd control, applying unique debuffs, or enforcing specific combat states.',
			keywords:     'status, effect, custom status, buff, debuff, crowd control, stun, root, silence, invincible, invulnerable, duration',
			data:         [
				new DropdownSelect('Status', 'status', ['Absorb',
					'Curse',
					'Disarm',
					'Invincible',
					'Invulnerable',
					'Root',
					'Silence',
					'Stun'], 'Stun')
					.setTooltip('The status to apply'),
				new AttributeSelect('Duration', 'duration', 3, 1)
					.setTooltip('How long in seconds to apply the status')
			],
			summaryItems: ['status', 'duration']
		});
	}

	public static override new = () => new this();
}

class SummonMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Summon',
			description:  'Summons a specified mob at each target\'s location. Child components will then apply effects directly to the newly summoned mob. Hostile summoned mobs may attack the caster. This is useful for creating minions, temporary allies, or environmental hazards.',
			keywords:     'summon, mob, creature, minion, ally, temporary, spawn, monster, entity, hostile, friendly',
			data:         [
				new DropdownSelect('Type', 'type', getEntities, 'Zombie'),
				new StringSelect('Name', 'name', '{player}\'s Minion')
					.setTooltip('The displayed name of the wolf. Use {player} to embed the caster\'s name'),
				new AttributeSelect('Health', 'health', 10)
					.setTooltip('The starting health of the mob'),
				new AttributeSelect('Amount', 'amount', 1)
					.setTooltip('How many mobs to summon')
			],
			summaryItems: ['type', 'name', 'amount']
		}, true);
	}

	public static override new = () => new this();
}

class TauntMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Taunt',
			description:  'Forces targeted creatures to focus their aggression on the caster, effectively drawing aggro. This is useful for tanking abilities, crowd control, or protecting allies by redirecting enemy attention.',
			keywords:     'taunt, aggro, draw aggro, threat, tank, focus, redirect, enemy, mob, hostile, control',
			data:         [
				new AttributeSelect('Amount', 'amount', 1)
					.setTooltip('The amount of aggro to apply if MythicMobs is active. Use negative amounts to reduce aggro')
			],
			summaryItems: ['amount']
		}, false);
	}

	public static override new = () => new this();
}

class ThrowMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Throw',
			description:  'Throws entities that are currently mounted on the target\'s head, propelling them in a specified direction. The thrown entities then become the targets for subsequent child components. This is useful for dismounting effects, aerial combat maneuvers, or repositioning entities.',
			keywords:     'throw, dismount, propel, cast, launch, physics, reposition, aerial, combat, crowd control',
			data:         [
				new DropdownSelect('Relative', 'relative', ['Target', 'Caster', 'Thrown'], 'Caster')
					.setTooltip('Determines what is considered "forward". Target uses the direction the target is facing, Caster uses the direction the caster is facing, Thrown uses the direction of the entity to be thrown'),
				new AttributeSelect('Speed', 'speed', 2)
					.setTooltip('The speed to give the target in the direction they are facing')
			],
			summaryItems: ['relative', 'speed']
		}, true);
	}

	public static override new = () => new this();
}

class TriggerMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Trigger',
			description:  'Establishes a temporary listener on the targeted entities for a specific trigger event (e.g., Death, Block Break, Skill Cast) for a set duration. When the event occurs, child components are activated. This is useful for creating reactive abilities, traps, or conditional follow-up effects.',
			keywords:     'trigger, listen, event, reactive, trap, conditional, follow-up, duration, monitor, detect',
			data:         [
				new DropdownSelect('Trigger', 'trigger', () => Object.values(get(triggers)).map((trigger: {
					name: string,
					component: typeof FabledComponent
				}) => trigger.name), 'Death')
					.setTooltip('The trigger to listen for'),
				new AttributeSelect('Duration', 'duration', 5)
					.setTooltip('How long to listen to the trigger for'),
				new BooleanSelect('Stackable', 'stackable', true)
					.setTooltip('Whether different players (or the same player) can listen to the same target at the same time'),
				new BooleanSelect('Once', 'once', true)
					.setTooltip('Whether the trigger should only be used once each cast. When false, the trigger can execute as many times as it happens for the duration'),

				//BLOCK
				new DropdownSelect('Material', 'material', getAnyMaterials, ['Any'], true)
					.requireValue('trigger', ['Block Break', 'Block Place']) // Just the block triggers
					.setTooltip('The type of block expected to be handled'),

				new IntSelect('Data', 'data', -1)
					.requireValue('trigger', ['Block Break', 'Block Place'])
					.setTooltip('The expected data value of the block (-1 for any data value)'),

				//CLICK
				new DropdownSelect('Crouch', 'crouch', ['Crouch', 'Dont crouch', 'Both'], 'Crouch')
					.requireValue('trigger', ['Left Click', 'Right Click']) // Just the click triggers
					.setTooltip('If the player has to be crouching in order for this trigger to function'),

				// CROUCH
				new DropdownSelect('Type', 'type', ['Start Crouching', 'Stop Crouching', 'Both'], 'Start Crouching')
					.requireValue('trigger', ['Crouch'])
					.setTooltip('Whether you want to apply components when crouching or not crouching'),

				// DROP_ITEM
				new DropdownSelect('Drop multiple', 'drop multiple', ['True', 'False', 'Ignore'], 'Ignore')
					.requireValue('trigger', ['Drop Item'])
					.setTooltip('Whether the player has to drop multiple items or a single item'),

				// ENVIRONMENT_DAMAGE
				new DropdownSelect('Type', 'type', getAnyDamageTypes, ['Fall'], true)
					.requireValue('trigger', ['Environment Damage'])
					.setTooltip('The source of damage to apply for'),

				// HEAL
				new DoubleSelect('Min Heal', 'heal-min', 0)
					.setTooltip('The minimum health that needs to be received')
					.requireValue('trigger', ['Heal']),
				new DoubleSelect('Max Heal', 'heal-max', 999)
					.setTooltip('The maximum health that needs to be received')
					.requireValue('trigger', ['Heal']),

				// ITEM_SWAP
				new BooleanSelect('Cancel swap', 'cancel', true)
					.requireValue('trigger', ['Item Swap'])
					.setTooltip('True cancels the item swap. False allows the item swap'),

				// LAND
				new DoubleSelect('Min Distance', 'min-distance', 0)
					.requireValue('trigger', ['Land'])
					.setTooltip('The minimum distance the player should fall before effects activating'),

				// LAUNCH
				new DropdownSelect('Type', 'type', getAnyProjectiles, 'Any')
					.requireValue('trigger', ['Launch'])
					.setTooltip('The type of projectile that should be launched'),

				// PHYSICAL
				new DropdownSelect('Type', 'type', ['Both', 'Melee', 'Projectile'], 'Both')
					.requireValue('trigger', ['Physical Damage', 'Took Physical Damage'])
					.setTooltip('The type of damage dealt'),

				// SKILL
				new StringSelect('Category', 'category', '')
					.requireValue('trigger', ['Skill Damage', 'Took Skill Damage'])
					.setTooltip('The type of skill damage to apply for. Leave this empty to apply to all skill damage'),


				// DAMAGE
				new BooleanSelect('Target Listen Target', 'target', true)
					.requireValue('trigger', ['Physical Damage', 'Skill Damage', 'Took Physical Damage', 'Took Skill Damage']) // Just damage triggers
					.setTooltip('True makes children target the target that has been listened to. False makes children target the entity fighting the target entity'),
				new DoubleSelect('Min Damage', 'dmg-min', 0)
					.requireValue('trigger', ['Physical Damage', 'Skill Damage', 'Took Physical Damage', 'Took Skill Damage'])
					.setTooltip('The minimum damage that needs to be dealt'),
				new DoubleSelect('Max Damage', 'dmg-max', 999)
					.requireValue('trigger', ['Physical Damage', 'Skill Damage', 'Took Physical Damage', 'Took Skill Damage'])
					.setTooltip('The maximum damage that needs to be dealt'),

				// SIGNAL
				new StringSelect('Signal', 'signal', '')
					.requireValue('trigger', ['Signal'])
					.setTooltip('The name of signal will be listened to')
			],
			summaryItems: ['trigger', 'duration', 'once', 'signal']
		}, true);
	}

	public static override new = () => new this();
}

class ValueAddMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Add',
			description:  'Adds a specified amount to a dynamically stored numerical value, identified by a unique key, for the caster. If the value does not exist, it will be initialized with the given amount. This is useful for tracking counters, scores, or accumulating resources.',
			keywords:     'value, add, increment, counter, score, resource, accumulate, custom stat, variable, store, modify',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new AttributeSelect('Amount', 'amount', 1)
					.setTooltip('The amount to add to the value'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')
			],
			summaryItems: ['key', 'amount', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueAttributeMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Attribute',
			description:  'Loads the numerical value of a player\'s specific attribute (e.g., Strength, Vitality) and stores it under a unique key, making it accessible for calculations or conditions in other mechanics. This is useful for dynamic scaling or attribute-based effects.',
			keywords:     'value, attribute, load, store, player stats, strength, vitality, dexterity, intelligence, dynamic scaling, calculation',
			data:         [
				new StringSelect('Key', 'key', 'attribute')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new DropdownSelect('Attribute', 'attribute', attributeStore.getAttributeNames, 'Vitality', false)
					.setTooltip('The attribute you are loading the value of'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')
			],
			summaryItems: ['key', 'attribute', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueCopyMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Copy',
			description:  'Copies a dynamically stored value from the caster to the target, or from the target to the caster. This is useful for transferring statistics, sharing progress, or replicating data between entities.',
			keywords:     'value, copy, transfer, replicate, share, data, statistics, caster, target, duplicate',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new StringSelect('Destination', 'destination', 'value')
					.setTooltip('The key to copy the original value to'),
				new BooleanSelect('To target', 'to-target', true)
					.setTooltip('Whether to copy the value to the target or from the target to the caster'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'destination', 'to-target', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueDistanceMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Distance',
			description:  'Calculates the physical distance between the caster and the target and stores this numerical value under a unique key. This is useful for abilities that need to know separation for scaling, targeting, or conditional effects.',
			keywords:     'value, distance, range, proximity, measure, calculate, separation, spatial, numerical, store',
			data:         [
				new StringSelect('Key', 'key', 'attribute')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueDivideMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Divide',
			description:  'Divides a dynamically stored numerical value (identified by a unique key) for the caster by a specified divisor. This is useful for scaling values down, distributing resources, or performing proportional calculations.',
			keywords:     'value, divide, scale, proportional, numerical, calculation, reduce, distribute, custom stat, variable',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new AttributeSelect('Divisor', 'divisor', 1)
					.setTooltip('The amount to divide the value by'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'divisor', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueHealthMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Health',
			description:  'Stores the target\'s current health, maximum health, missing health, or health percentage as a numerical value under a unique key for the caster. This is useful for conditional healing, executing abilities at specific health thresholds, or displaying dynamic health information.',
			keywords:     'value, health, hp, life, store, current health, max health, missing health, percent health, numerical, threshold, display',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new DropdownSelect('Type', 'type', ['Current', 'Max', 'Missing', 'Percent'], 'Current')
					.setTooltip('Current provides the health the target has, max provides their total health, missing provides how much health they have lost, and percent is the ratio of health to total health'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'type', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueLocationMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Location',
			description:  'Stores the current precise location of the first targeted entity under a unique key, making it available for subsequent teleportation, area-of-effect calculations, or positional checks. This allows for advanced spatial abilities.',
			keywords:     'value, location, store, position, coordinates, spatial, teleport, waypoint, mark, retrieve',
			data:         [
				new StringSelect('Key', 'key', 'location')
					.setTooltip('The unique key to store the location under. This key can be used in place of attribute values to use the stored value'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueLoadMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Load',
			description:  'Retrieves a persistently stored numerical value from the player\'s account and makes it accessible for use in other mechanics during the current skill execution. This is useful for recalling saved progress, scores, or custom player data.',
			keywords:     'value, load, retrieve, persistent, saved, account data, score, progress, custom data, recall',
			data:         [
				new StringSelect('Key', 'key')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value.'),
				new BooleanSelect('Override', 'override', true)
					.setTooltip('If false and the current value have been set, nothing will change.'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')
			],
			summaryItems: ['key', 'override', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueLoreMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Lore',
			description:  'Extracts a numerical value from the lore of an item held by the caster (main hand or offhand) and stores it under a unique key. This allows for dynamic skill effects that scale based on custom item properties defined in their lore.',
			keywords:     'value, lore, item, held item, extract, custom item, dynamic scaling, regex, text parsing, inventory, read data',
			data:         [
				new StringSelect('Key', 'key', 'lore')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new DropdownSelect('Hand', 'hand', ['Main', 'Offhand'], 'Main')
					.setTooltip('The hand to check for the item. Offhand items are MC 1.9+ only'),
				new StringSelect('Regex', 'regex', 'Damage: {value}')
					.setTooltip('The regex string to look for, using {value} as the number to store. If you do not know about regex, consider looking it up on Wikipedia or avoid using major characters such as [ ] { } ( ) . + ? * ^ \\ |'),
				new AttributeSelect('Multiplier', 'multiplier', 1)
					.setTooltip('The multiplier for the acquired value. If you want the value to remain unchanged, leave this value at 1'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'hand', 'regex', 'multiplier', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueLoreSlotMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Lore Slot',
			description:  'Extracts a numerical value from the lore of an item located in a specific inventory slot of the caster and stores it under a unique key. This is similar to the Value Lore mechanic but allows specifying any inventory slot, not just held items.',
			keywords:     'value, lore, item, inventory slot, extract, custom item, dynamic scaling, regex, text parsing, inventory, read data, specific slot',
			data:         [
				new StringSelect('Key', 'key', 'lore')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new IntSelect('Slot', 'slot', 9)
					.setTooltip('The slot of the inventory to fetch the item from. Slots 0-8 are the hotbar, 9-35 are the main inventory, 36-39 are armor, and 40 is the offhand slot'),
				new StringSelect('Regex', 'regex', 'Damage: {value}')
					.setTooltip('The regex string to look for, using {value} as the number to store. If you do not know about regex, consider looking it up on Wikipedia or avoid using major characters such as [ ] { } ( ) . + ? * ^ \\ |'),
				new AttributeSelect('Multiplier', 'multiplier', 1)
					.setTooltip('The multiplier for the acquired value. If you want the value to remain unchanged, leave this value at 1'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'slot', 'regex', 'multiplier', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueManaMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Mana',
			description:  'Stores a target player\'s current mana, maximum mana, missing mana, or mana percentage as a numerical value under a unique key for the caster. This is useful for conditional mana usage, mana-scaling abilities, or displaying dynamic mana information.',
			keywords:     'value, mana, energy, mp, store, current mana, max mana, missing mana, percent mana, numerical, resource, display',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new DropdownSelect('Type', 'type', ['Current', 'Max', 'Missing', 'Percent'], 'Current')
					.setTooltip('Current provides the mana the target has, max provides their total mana, missing provides how much mana they have lost, and percent is the ratio of health to total mana'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'type', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueMathMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Math',
			description:  'Performs mathematical calculations using a custom formula to determine the value to set. ' +
											'The formula can use values as <code>{value}</code> type placeholders and complex math operations ' +
											'including addition, subtraction, multiplication, division, modulo (<code>%</code>), ' +
											'square root (<code>sqrt()</code>), exponents (<code>^</code>), and more. Use <code>random()</code> ' +
											'to generate a psuedo-random number between 0 and 1. See the ' +
											'<a href="https://github.com/magemonkeystudio/fabled/wiki/Formulas-‐-Complex">wiki</a> for more information',
			keywords:     'value, math, calculate, formula, dynamic, scaling, damage, custom logic, numerical, random, store',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new StringSelect('Function', 'function', '1 + {value}')
					.setTooltip('The function used to determine the value'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')
			],
			summaryItems: ['key', 'function', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueMultiplyMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Multiply',
			description:  'Multiplies a dynamically stored numerical value (identified by a unique key) for the caster by a specified multiplier. If the value does not exist, this mechanic will not perform any action. This is useful for scaling values up or down proportionally.',
			keywords:     'value, multiply, scale, proportional, numerical, calculation, increase, decrease, custom stat, variable',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new AttributeSelect('Multiplier', 'multiplier', 1)
					.setTooltip('The amount to multiply the value by'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'multiplier', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValuePlaceholderMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Placeholder',
			description:  'Stores the result of a placeholder string (either a number or text) as a dynamic value under a unique key for the caster. This is highly versatile for capturing game data, player statistics, or custom text outputs from other plugins.',
			keywords:     'value, placeholder, store, game data, player stats, custom text, variable, numerical, string, capture',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new DropdownSelect('Type', 'type', ['Number', 'String'], 'Number')
					.setTooltip('The type of value to store. Number values require numeric placeholders. String values can be used in messages or commands'),
				new StringSelect('Placeholder', 'placeholder', '{value}')
					.setTooltip('The placeholder string to use. Can contain multiple placeholders if using the String type'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'type', 'placeholder', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueRandomMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Random',
			description:  'Generates a random numerical value within a specified range (min/max), optionally as an integer, and stores it under a unique key for the caster. This is fundamental for abilities with probabilistic outcomes, loot generation, or dynamic spell effects.',
			keywords:     'value, random, RNG, dice roll, chance, probability, generate, number, integer, float, store',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new BooleanSelect('Integer', 'integer', false)
					.setTooltip('Whether to only generate integer values'),
				new DropdownSelect('Type', 'type', ['Normal', 'Triangular'], 'Normal')
					.setTooltip('The type of random to use. Triangular favors numbers in the middle, similar to rolling two dice'),
				new AttributeSelect('Min', 'min')
					.setTooltip('The minimum value it can be'),
				new AttributeSelect('Max', 'max')
					.setTooltip('The maximum value it can be'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'type', 'min', 'max', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueRotationMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Rotation',
			description:  'Calculates the rotational difference (angle) between a target\'s current look direction and a specified source location (which can be a remembered location or the caster\'s position). This numerical value is then stored under a unique key, useful for directional spells or cinematic effects.',
			keywords:     'value, rotation, angle, direction, look, facing, cinematic, spatial, store, numerical',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new StringSelect('Source', 'source', '')
					.setTooltip('The key to use as the source location for the rotation. If left empty, the caster is used'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'source', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueRoundMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Round',
			description:  'Applies various rounding operations (round, ceiling, floor) to a dynamically stored numerical value, identified by a unique key. This is useful for normalizing calculations, ensuring integer results, or adjusting values for display purposes.',
			keywords:     'value, round, ceiling, floor, numerical, normalize, integer, calculation, modify, store',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new DropdownSelect('Type', 'type', ['Round', 'Ceiling', 'Floor'], 'Round')
					.setTooltip('The type of rounding to use. Round rounds to the nearest integer, ceiling rounds up, and floor rounds down'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'type', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueSetMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Value Set',
			description:  'Assigns a specific numerical value to a unique key for the caster, effectively creating or updating a dynamically stored variable. This is a fundamental mechanic for managing custom statistics, flags, or any numerical game state.',
			keywords:     'value, set, assign, store, variable, custom stat, flag, game state, numerical, update, initialize',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new AttributeSelect('Value', 'value', 1)
					.setTooltip('The value to store under the key'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'value', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class WarpMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Warp',
			description:  'Teleports the target entity a specified distance in a direction relative to their current facing, with options for passing through walls or landing only in open spaces. This is useful for dash abilities, tactical repositioning, or escaping danger.',
			keywords:     'warp, teleport, dash, blink, movement, reposition, escape, evade, through walls, open space, tactical',
			data:         [
				new BooleanSelect('Through Walls', 'walls')
					.setTooltip('Whether to allow the target to teleport through walls'),
				new BooleanSelect('Open Spaces Only', 'open', true)
					.setTooltip('Whether to only allow teleporting to open spaces. The side-effect of open spaces is the warp position is set to the middle of the block instead of the complete relative position'),
				new SectionMarker('Position'),
				new AttributeSelect('Forward', 'forward', 3, 1)
					.setTooltip('How far forward in blocks to teleport. A negative value teleports backwards'),
				new AttributeSelect('Upward', 'upward')
					.setTooltip('How far upward in blocks to teleport. A negative value teleports downward'),
				new AttributeSelect('Right', 'right')
					.setTooltip('How far to the right in blocks to teleport. A negative value teleports to the left'),
				...warpOptions()
			],
			preview:      [
				...particlesAtTargetPreviewOptions()
			],
			summaryItems: ['walls', 'forward', 'upward', 'right', 'preserve']
		}, false);
	}

	public static override new = () => new this();
}

class WarpLocMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Warp Location',
			description:  'Teleports the target entity to a precise, predefined location in a specific world. This is useful for creating checkpoints, teleporting to safe zones, or initiating specific encounters.',
			keywords:     'warp, teleport, location, coordinates, world, specific point, checkpoint, safe zone, travel',
			data:         [
				new StringSelect('World (or "current")', 'world', 'current')
					.setTooltip('The name of the world that the location is in'),
				new DoubleSelect('X', 'x', 0)
					.setTooltip('The X-coordinate of the desired position'),
				new DoubleSelect('Y', 'y', 0)
					.setTooltip('The Y-coordinate of the desired position'),
				new DoubleSelect('Z', 'z', 0)
					.setTooltip('The Z-coordinate of the desired position'),
				...warpOptions()
			],
			preview:      [
				...particlesAtTargetPreviewOptions()
			],
			summaryItems: ['world', 'x', 'y', 'z', 'preserve']
		}, false);
	}

	public static override new = () => new this();
}

class WarpRandomMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Warp Random',
			description:  'Teleports the target entity a specified distance in a random direction, with options for horizontal-only movement and passing through walls. This is useful for escape abilities, disorienting enemies, or unpredictable movement spells.',
			keywords:     'warp, teleport, random, unpredictable, escape, disorient, movement, evade, blink, dash',
			data:         [
				new BooleanSelect('Only Horizontal', 'horizontal', true)
					.setTooltip('Whether to limit the random position to the horizontal plane'),
				new BooleanSelect('Through Walls', 'walls', false)
					.setTooltip('Whether to allow the target to teleport through walls'),
				new AttributeSelect('Distance', 'distance', 3, 1)
					.setTooltip('The max distance in blocks to teleport'),
				...warpOptions()
			],
			summaryItems: ['horizontal', 'walls', 'distance', 'preserve']
		}, false);
	}

	public static override new = () => new this();
}

class WarpSwapMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Warp Swap',
			description:  'Exchanges the physical locations of the caster and the primary target. This is useful for tactical repositioning, saving allies, or disorienting enemies in close combat.',
			keywords:     'warp, teleport, swap, exchange, switch, reposition, tactical, rescue, disorient, close combat',
			data:         [...warpOptions()],
			summaryItems: ['preserve']
		});
	}

	public static override new = () => new this();
}

class WarpTargetMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Warp Target',
			description:  'Teleports either the targeted entity to the caster\'s position, or the caster to the target\'s position. This is useful for closing gaps, engaging enemies, or extracting allies.',
			keywords:     'warp, teleport, target, caster, close gap, engage, disengage, extract, reposition, movement',
			data:         [
				new DropdownSelect('Type', 'type', ['Caster to Target', 'Target to Caster'], 'Caster to Target')
					.setTooltip('The direction to warp the involved targets'),
				...warpOptions()
			],
			summaryItems: ['type', 'preserve']
		});
	}

	public static override new = () => new this();
}

class WarpValueMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Warp Value',
			description:  'Teleports all targeted entities to a specific location previously stored using the "Value Location" mechanic. This allows for complex, multi-point teleportation or precise recall abilities.',
			keywords:     'warp, teleport, location, stored location, recall, reposition, move, custom location, multi-point',
			data:         [
				new StringSelect('Key', 'key', 'location')
					.setTooltip('The unique key the location is stored under. This should be the same key used in the Value Location mechanic'),
				...warpOptions()
			],
			preview:      [
				...particlesAtTargetPreviewOptions()
			],
			summaryItems: ['key', 'preserve']
		}, false);
	}

	public static override new = () => new this();
}

class WolfMechanic extends FabledMechanic {
	public constructor() {
		super({
			name:         'Wolf',
			description:  'Summons a tamed wolf companion for each targeted entity, with customizable color, name, health, and damage. The wolf persists for a duration and can be given its own skillset (excluding Cast triggers). Child components will apply to the summoned wolf, allowing for dynamic pet abilities or temporary allies.',
			keywords:     'wolf, summon, companion, pet, tamed, ally, animal, summoner, minion, temporary, skillset',
			data:         [
				new DropdownSelect('Collar Color', 'color', getDyes, 'Black')
					.setTooltip('The color of the collar that the wolf should wear'),
				new StringSelect('Wolf Name', 'name', '{player}\'s Wolf')
					.setTooltip('The displayed name of the wolf. Use {player} to embed the caster\'s name'),
				new AttributeSelect('Health', 'health', 10)
					.setTooltip('The starting health of the wolf'),
				new AttributeSelect('Damage', 'damage', 3)
					.setTooltip('The damage dealt by the wolf each attack'),
				new BooleanSelect('Sitting', 'sitting', false)
					.setTooltip('Whether the wolf starts off sitting'),
				new AttributeSelect('Duration', 'seconds', 10)
					.setTooltip('How long to summon the wolf for'),
				new AttributeSelect('Amount', 'amount', 1)
					.setTooltip('How many wolves to summon'),
				new SkillSelect('Skills', 'skills', true)
					.setTooltip('The skills to give the wolf. Skills are executed at the level of the skill summoning the wolf. Skills needing a Cast trigger will not work')
			],
			summaryItems: ['color', 'name', 'seconds', 'amount']
		}, true);
	}

	public static override new = () => new this();
}

const particlePreviewOptions = (key: string): ComponentOption[] => {
	return [
		new IntSelect('Refresh period', key + '-period', 5)
			.requireValue(key, [true])
			.setTooltip('How many ticks to wait before refreshing the preview, recalculating targets and the location of the particle effects'),
		new DropdownSelect('Particle', key + '-particle', getParticles, 'Crit')
			.setTooltip('The type of particle to display')
			.requireValue(key, [true]),
		new DropdownSelect('Material', key + '-material', (() => [...getMaterials()]), 'Arrow')
			.requireValue(key + '-particle', ['Item crack', 'Item'])
			.requireValue(key, [true])
			.setTooltip('The material to use for the particles'),
		new DropdownSelect('Material', key + '-material', (() => [...getBlocks()]), 'Dirt')
			.requireValue(key + '-particle', [
				'Block crack',
				'Block dust',
				'Block',
				'Falling dust',
				'Block marker'])
			.requireValue(key, [true])
			.setTooltip('The block to use for the particles'),
		new IntSelect('Durability', key + '-durability', 0)
			.requireValue(key + '-particle', ['Item crack', 'Item'])
			.requireValue(key, [true])
			.setTooltip('The durability to be reduced from the item used to make the particles'),
		new IntSelect('CustomModelData', key + '-type', 0)
			.requireValue(key + '-particle', ['Item crack', 'Item'])
			.requireValue(key, [true])
			.setTooltip('The CustomModelData of the item used to make the particles'),
		new ColorSelect('Dust Color', key + '-dust-color', '#FF0000')
			.requireValue(key + '-particle', ['Redstone', 'Dust', 'Dust color transition'])
			.requireValue(key, [true])
			.setTooltip('The color of the dust particles in hex RGB'),
		new ColorSelect('Final Dust Color', key + '-final-dust-color', '#FF0000')
			.requireValue(key + '-particle', ['Dust color transition'])
			.requireValue(key, [true])
			.setTooltip('The color to transition to, in hex RGB'),
		new DoubleSelect('Dust Size', key + '-dust-size', 1)
			.requireValue(key + '-particle', ['Redstone', 'Dust', 'Dust color transition'])
			.requireValue(key, [true])
			.setTooltip('The size of the dust particles'),

		// Bukkit particle data value
		new IntSelect('Effect Data', key + '-data')
			.requireValue(key + '-particle',
				[
					'Smoke',
					'Ender Signal',
					'Mobspawner Flames',
					'Potion Break',
					'Sculk charge'
				])
			.requireValue(key, [true])
			.setTooltip('The data value to use for the particle. The effect changes between particles such as the orientation for smoke particles or the color for potion break'),
		new DoubleSelect('DX', key + '-dx')
			.requireValue(key, [true])
			.setTooltip('Offset in the X direction, used as the Red value for some particles'),
		new DoubleSelect('DY', key + '-dy')
			.requireValue(key, [true])
			.setTooltip('Offset in the Y direction, used as the Green value for some particles'),
		new DoubleSelect('DZ', key + '-dz')
			.requireValue(key, [true])
			.setTooltip('Offset in the Z direction, used as the Blue value for some particles'),
		new DoubleSelect('Amount', key + '-amount', 1)
			.requireValue(key, [true])
			.setTooltip('Number of particles to play per point. For "Spell mob" and "Spell mob ambient" particles, set to 0 to control the particle color'),
		new DoubleSelect('Speed', key + '-speed', 0.1)
			.requireValue(key, [true])
			.setTooltip('Speed of the particle. For some particles controls other parameters, such as size')
	];
};

export const initComponents = () => {
	triggers.set({
		AIR:              { name: 'Air', component: AirTrigger },
		ATTR_CHANGE:      { name: 'Attribute Change', component: AttributeChangeTrigger },
		BLOCK_BREAK:      { name: 'Block Break', component: BlockBreakTrigger },
		BLOCK_PLACE:      { name: 'Block Place', component: BlockPlaceTrigger },
		CAST:             { name: 'Cast', component: CastTrigger },
		CHAT:             { name: 'Chat', component: ChatTrigger },
		CLEANUP:          { name: 'Cleanup', component: CleanupTrigger },
		CROUCH:           { name: 'Crouch', component: CrouchTrigger },
		DEATH:            { name: 'Death', component: DeathTrigger },
		ENTITY_TARGET:    { name: 'Entity Target', component: EntityTargetTrigger },
		ENTITY_RESURRECT: { name: 'Entity Resurrect', component: EntityResurrectTrigger },
		EXPERIENCE:       { name: 'Experience', component: ExperienceTrigger },
		FLIGHT_TOGGLE:    { name: 'Flight Toggle', component: FlightToggleTrigger },
		GLIDE:            { name: 'Glide', component: GlideTrigger },
		HARVEST:          { name: 'Harvest', component: HarvestTrigger },
		HEAL:             { name: 'Heal', component: HealTrigger },
		INIT:             { name: 'Initialize', component: InitializeTrigger },
		JUMP:             { name: 'Jump', component: JumpTrigger },
		KILL:             { name: 'Kill', component: KillTrigger },
		LAND:             { name: 'Land', component: LandTrigger },
		LEFT_CLICK:       { name: 'Left Click', component: LeftClickTrigger },
		RIGHT_CLICK:      { name: 'Right Click', component: RightClickTrigger },
		MOVE:             { name: 'Move', component: MoveTrigger },
		PROJ_HIT:         { name: 'Projectile Hit', component: ProjectileHitTrigger },
		PROJ_LAUNCH:      { name: 'Projectile Launch', alias: 'Launch', component: LaunchTrigger },
		PROJ_TICK:        { name: 'Projectile Tick', component: ProjectileTickTrigger },
		RIPTIDE:          { name: 'Riptide', component: RiptideTrigger },
		SHEAR:            { name: 'Shear', component: ShearTrigger },
		SHIELD:           { name: 'Shield', component: ShieldTrigger },
		SIGNAL:           { name: 'Signal', component: SignalTrigger },
		SKILL_CAST:       { name: 'Skill Cast', component: SkillCastTrigger },
		SPRINT:           { name: 'Sprint', component: SprintTrigger },
		STRIP_LOG:        { name: 'Strip Log', component: StripLogTrigger },
		WORLD_CHANGE:     { name: 'World Change', component: WorldChangeTrigger },

		ARMOR_EQUIP: { name: 'Armor Equip', component: ArmorEquipTrigger, section: 'Item' },
		CONSUME:     { name: 'Consume', component: ConsumeTrigger, section: 'Item' },
		DROP_ITEM:   { name: 'Drop Item', component: DropItemTrigger, section: 'Item' },
		ITEM_SWAP:   { name: 'Item Swap', component: ItemSwapTrigger, section: 'Item' },

		FISHING:        { name: 'Fishing', component: FishingTrigger, section: 'Fishing' },
		FISHING_BITE:   { name: 'Fishing Bite', component: FishingBiteTrigger, section: 'Fishing' },
		FISHING_FAIL:   { name: 'Fishing Fail', component: FishingFailTrigger, section: 'Fishing' },
		FISHING_GRAB:   { name: 'Fishing Grab', component: FishingGrabTrigger, section: 'Fishing' },
		FISHING_GROUND: { name: 'Fishing Ground', component: FishingGroundTrigger, section: 'Fishing' },
		FISHING_REEL:   { name: 'Fishing Reel', component: FishingReelTrigger, section: 'Fishing' },

		ENV_DAMAGE:   { name: 'Environment Damage', component: EnvironmentDamageTrigger, section: 'Damage' },
		PHYS_DAMAGE:  { name: 'Physical Damage', component: PhysicalDamageTrigger, section: 'Damage' },
		TOOK_PHYS:    { name: 'Took Physical Damage', component: TookPhysicalTrigger, section: 'Damage' },
		SKILL_DAMAGE: { name: 'Skill Damage', component: SkillDamageTrigger, section: 'Damage' },
		TOOK_SKILL:   { name: 'Took Skill Damage', component: TookSkillTrigger, section: 'Damage' },

		FLAG:        { name: 'Flag', component: FlagTrigger, section: 'Flag' },
		FLAG_EXPIRE: { name: 'Flag Expire', component: FlagExpireTrigger, section: 'Flag' }

	});
	targets.set({
		AREA:     { name: 'Area', component: AreaTarget },
		CONE:     { name: 'Cone', component: ConeTarget },
		LINEAR:   { name: 'Linear', component: LinearTarget },
		LOCATION: { name: 'Location', component: LocationTarget },
		NEAREST:  { name: 'Nearest', component: NearestTarget },
		OFFSET:   { name: 'Offset', component: OffsetTarget },
		REMEMBER: { name: 'Remember', component: RememberTarget },
		PARTY:    { name: 'Party', component: PartyTarget },
		SELF:     { name: 'Self', component: SelfTarget },
		SINGLE:   { name: 'Single', component: SingleTarget },
		WORLD:    { name: 'World', component: WorldTarget }
	});
	conditions.set({
		ACTIONBAR:      { name: 'Action Bar', component: ActionBarCondition },
		AIR:            { name: 'Air', component: AirCondition },
		ALTITUDE:       { name: 'Altitude', component: AltitudeCondition },
		ARMOR:          { name: 'Armor', component: ArmorCondition },
		ATK_INDICATOR:  { name: 'Attack Indicator', component: AttackIndicatorCondition },
		ATTRIBUTE:      { name: 'Attribute', component: AttributeCondition },
		BIOME:          { name: 'Biome', component: BiomeCondition },
		BLOCK:          { name: 'Block', component: BlockCondition },
		BURNING:        { name: 'Burning', component: BurningCondition },
		BLOCKING:       { name: 'Blocking', component: BlockingCondition },
		CEILING:        { name: 'Ceiling', component: CeilingCondition },
		CHANCE:         { name: 'Chance', component: ChanceCondition },
		CLASS:          { name: 'Class', component: ClassCondition },
		CLASS_LEVEL:    { name: 'Class Level', component: ClassLevelCondition },
		COLOR:          { name: 'Color', component: ColorCondition },
		COMBAT:         { name: 'Combat', component: CombatCondition },
		CROUCH:         { name: 'Crouch', component: CrouchCondition },
		DIRECTION:      { name: 'Direction', component: DirectionCondition },
		DISTANCE:       { name: 'Distance', component: DistanceCondition },
		ELEVATION:      { name: 'Elevation', component: ElevationCondition },
		ELSE:           { name: 'Else', component: ElseCondition },
		ENTITY_TYPE:    { name: 'Entity Type', component: EntityTypeCondition },
		FIRE:           { name: 'Fire', component: FireCondition },
		FLAG:           { name: 'Flag', component: FlagCondition },
		FOOD:           { name: 'Food', component: FoodCondition },
		GLIDE:          { name: 'Glide', component: GlideCondition },
		GROUND:         { name: 'Ground', component: GroundCondition },
		HEALTH:         { name: 'Health', component: HealthCondition },
		INVENTORY:      { name: 'Inventory', component: InventoryCondition },
		ITEM:           { name: 'Item', component: ItemCondition },
		LIGHT:          { name: 'Light', component: LightCondition },
		MANA:           { name: 'Mana', component: ManaCondition },
		MONEY:          { name: 'Money', component: MoneyCondition },
		MOON:           { name: 'Moon', component: MoonCondition },
		MOUNTED:        { name: 'Mounted', component: MountedCondition },
		MOUNTING:       { name: 'Mounting', component: MountingCondition },
		MYTHICMOB_TYPE: { name: 'MythicMob Type', component: MythicMobTypeCondition },
		NAME:           { name: 'Name', component: NameCondition },
		OFFHAND:        { name: 'Offhand', component: OffhandCondition },
		PERMISSION:     { name: 'Permission', component: PermissionCondition },
		POTION:         { name: 'Potion', component: PotionCondition },
		SKILL_LEVEL:    { name: 'Skill Level', component: SkillLevelCondition },
		SLOT:           { name: 'Slot', component: SlotCondition },
		SPRINT:         { name: 'Sprint', component: SprintCondition },
		STATUS:         { name: 'Status', component: StatusCondition },
		TIME:           { name: 'Time', component: TimeCondition },
		TOOL:           { name: 'Tool', component: ToolCondition },
		VALUE:          { name: 'Value', component: ValueCondition },
		VALUETEXT:      { name: 'Value Text', component: ValueTextCondition },
		WATER:          { name: 'Water', component: WaterCondition },
		WEATHER:        { name: 'Weather', component: WeatherCondition },
		WORLD:          { name: 'World', component: WorldCondition },
		YAW:            { name: 'Yaw', component: YawCondition }
	});
	mechanics.set({
		AIR_MODIFY:         { name: 'Air Modify', component: AirModify },
		AIR_SET:            { name: 'Air Set', component: AirSet },
		ABORT_SKILL:        { name: 'Abort Skill', component: AbortSkillMechanic },
		ARMOR:              { name: 'Armor', component: ArmorMechanic },
		ARMOR_STAND:        { name: 'Armor Stand', component: ArmorStandMechanic },
		ARMOR_STAND_POSE:   { name: 'Armor Stand Pose', component: ArmorStandPoseMechanic },
		ARMOR_STAND_REMOVE: { name: 'Armor Stand Remove', component: ArmorStandRemoveMechanic },
		ATTRIBUTE:          { name: 'Attribute', component: AttributeMechanic },
		BLOCK:              { name: 'Block', component: BlockMechanic },
		BUFF:               { name: 'Buff', component: BuffMechanic },
		CANCEL:             { name: 'Cancel', component: CancelMechanic },
		CHANNEL:            { name: 'Channel', component: ChannelMechanic },
		CLEANSE:            { name: 'Cleanse', component: CleanseMechanic },
		COMMAND:            { name: 'Command', component: CommandMechanic },
		COOLDOWN:           { name: 'Cooldown', component: CooldownMechanic },
		DAMAGE:             { name: 'Damage', component: DamageMechanic },
		DAMAGE_BUFF:        { name: 'Damage Buff', component: DamageBuffMechanic },
		DAMAGE_LORE:        { name: 'Damage Lore', component: DamageLoreMechanic },
		DEFENSE_BUFF:       { name: 'Defense Buff', component: DefenseBuffMechanic },
		DELAY:              { name: 'Delay', component: DelayMechanic },
		DISGUISE:           { name: 'Disguise', component: DisguiseMechanic },
		DURABILITY:         { name: 'Durability', component: DurabilityMechanic },
		EXPERIENCE:         { name: 'Experience', component: ExperienceMechanic },
		EXPLOSION:          { name: 'Explosion', component: ExplosionMechanic },
		FIRE:               { name: 'Fire', component: FireMechanic },
		FLY:                { name: 'Fly', component: FlyMechanic },
		FOOD:               { name: 'Food', component: FoodMechanic },
		FORGET_TARGETS:     { name: 'Forget Targets', component: ForgetTargetsMechanic },
		HEAL:               { name: 'Heal', component: HealMechanic },
		HEALTH_SET:         { name: 'Health Set', component: HealthSetMechanic },
		HELD_ITEM:          { name: 'Held Item', component: HeldItemMechanic },
		IMMUNITY:           { name: 'Immunity', component: ImmunityMechanic },
		INTERRUPT:          { name: 'Interrupt', component: InterruptMechanic },
		INVISIBILITY:       { name: 'Invisibility', component: InvisibilityMechanic },
		ITEM:               { name: 'Item', component: ItemMechanic },
		ITEM_DROP:          { name: 'Item Drop', component: ItemDropMechanic },
		ITEM_PROJECTILE:    { name: 'Item Projectile', component: ItemProjectileMechanic },
		ITEM_REMOVE:        { name: 'Item Remove', component: ItemRemoveMechanic },
		LAUNCH:             { name: 'Launch', component: LaunchMechanic },
		LIGHTNING:          { name: 'Lightning', component: LightningMechanic },
		MANA:               { name: 'Mana', component: ManaMechanic },
		MESSAGE:            { name: 'Message', component: MessageMechanic },
		MINE:               { name: 'Mine', component: MineMechanic },
		MONEY:              { name: 'Money', component: MoneyMechanic },
		MOUNT:              { name: 'Mount', component: MountMechanic },
		MYTHICMOB_SKILL:    { name: 'MythicMob Skill', component: MythicMobSkill },
		PASSIVE:            { name: 'Passive', component: PassiveMechanic },
		PERMISSION:         { name: 'Permission', component: PermissionMechanic },
		POTION:             { name: 'Potion', component: PotionMechanic },
		POTION_PROJECTILE:  { name: 'Potion Projectile', component: PotionProjectileMechanic },
		PROJECTILE:         { name: 'Projectile', component: ProjectileMechanic },
		PURGE:              { name: 'Purge', component: PurgeMechanic },
		PUSH:               { name: 'Push', component: PushMechanic },
		REMEMBER_TARGETS:   { name: 'Remember Targets', component: RememberTargetsMechanic },
		REPEAT:             { name: 'Repeat', component: RepeatMechanic },
		SHIELD:             { name: 'Shield', component: ShieldMechanic },
		SIGNAL_EMIT:        { name: 'Signal Emit', component: SignalEmitMechanic },
		SKILL_CAST:         { name: 'Skill Cast', component: SkillCastMechanic },
		SOUND:              { name: 'Sound', component: SoundMechanic },
		STAT:               { name: 'Stat', component: StatMechanic },
		STATUS:             { name: 'Status', component: StatusMechanic },
		SUMMON:             { name: 'Summon', component: SummonMechanic },
		TAUNT:              { name: 'Taunt', component: TauntMechanic },
		THROW:              { name: 'Throw', component: ThrowMechanic },
		TRIGGER:            { name: 'Trigger', component: TriggerMechanic },
		WOLF:               { name: 'Wolf', component: WolfMechanic },

		CANCEL_EFFECT:       { name: 'Cancel Effect', component: CancelEffectMechanic, section: 'Particle' },
		PARTICLE:            { name: 'Particle', component: ParticleMechanic, section: 'Particle' },
		PARTICLE_ANIMATION:  { name: 'Particle Animation', component: ParticleAnimationMechanic, section: 'Particle' },
		PARTICLE_EFFECT:     { name: 'Particle Effect', component: ParticleEffectMechanic, section: 'Particle' },
		PARTICLE_IMAGE:      { name: 'Particle Image', component: ParticleImageMechanic, section: 'Particle' },
		PARTICLE_PROJECTILE: { name: 'Particle Projectile', component: ParticleProjectileMechanic, section: 'Particle' },

		FLAG:        { name: 'Flag', component: FlagMechanic, section: 'Flag' },
		FLAG_CLEAR:  { name: 'Flag Clear', component: FlagClearMechanic, section: 'Flag' },
		FLAG_TOGGLE: { name: 'Flag Toggle', component: FlagToggleMechanic, section: 'Flag' },

		VALUE_ADD:         { name: 'Value Add', component: ValueAddMechanic, section: 'Value' },
		VALUE_ATTRIBUTE:   { name: 'Value Attribute', component: ValueAttributeMechanic, section: 'Value' },
		VALUE_COPY:        { name: 'Value Copy', component: ValueCopyMechanic, section: 'Value' },
		VALUE_DISTANCE:    { name: 'Value Distance', component: ValueDistanceMechanic, section: 'Value' },
		VALUE_DIVIDE:      { name: 'Value Divide', component: ValueDivideMechanic, section: 'Value' },
		VALUE_HEALTH:      { name: 'Value Health', component: ValueHealthMechanic, section: 'Value' },
		VALUE_LOAD:        { name: 'Value Load', component: ValueLoadMechanic, section: 'Value' },
		VALUE_LOCATION:    { name: 'Value Location', component: ValueLocationMechanic, section: 'Value' },
		VALUE_LORE:        { name: 'Value Lore', component: ValueLoreMechanic, section: 'Value' },
		VALUE_LORE_SLOT:   { name: 'Value Lore Slot', component: ValueLoreSlotMechanic, section: 'Value' },
		VALUE_MANA:        { name: 'Value Mana', component: ValueManaMechanic, section: 'Value' },
		VALUE_MATH:        { name: 'Value Math', component: ValueMathMechanic, section: 'Value' },
		VALUE_MULTIPLY:    { name: 'Value Multiply', component: ValueMultiplyMechanic, section: 'Value' },
		VALUE_PLACEHOLDER: { name: 'Value Placeholder', component: ValuePlaceholderMechanic, section: 'Value' },
		VALUE_RANDOM:      { name: 'Value Random', component: ValueRandomMechanic, section: 'Value' },
		VALUE_ROTATION:    { name: 'Value Rotation', component: ValueRotationMechanic, section: 'Value' },
		VALUE_ROUND:       { name: 'Value Round', component: ValueRoundMechanic, section: 'Value' },
		VALUE_SET:         { name: 'Value Set', component: ValueSetMechanic, section: 'Value' },

		WARP:        { name: 'Warp', component: WarpMechanic, section: 'Warp' },
		WARP_LOC:    { name: 'Warp Location', component: WarpLocMechanic, section: 'Warp' },
		WARP_RANDOM: { name: 'Warp Random', component: WarpRandomMechanic, section: 'Warp' },
		WARP_SWAP:   { name: 'Warp Swap', component: WarpSwapMechanic, section: 'Warp' },
		WARP_TARGET: { name: 'Warp Target', component: WarpTargetMechanic, section: 'Warp' },
		WARP_VALUE:  { name: 'Warp Value', component: WarpValueMechanic, section: 'Warp' }
	});
	initialized.set(true);
};

// Add deprecated components to this list
export const deprecated: (typeof FabledComponent)[] = [
	ValueAddMechanic,
	ValueDivideMechanic,
	ValueMultiplyMechanic,
	ValueRoundMechanic
];
