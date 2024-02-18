import ProMechanic                                               from '$api/components/mechanics';
import BlockSelect                                               from '$api/options/blockselect';
import ProCondition                                              from '$api/components/conditions';
import DropdownSelect                                            from '$api/options/dropdownselect';
import ProTrigger                                                from '$api/components/triggers';
import ProTarget                                                 from '$api/components/targets';
import MaterialSelect                                            from '$api/options/materialselect';
import {
	getAnyConsumable,
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
import BooleanSelect                                             from '$api/options/booleanselect';
import DoubleSelect                                              from '$api/options/doubleselect';
import { conditions, initialized, mechanics, targets, triggers } from '$api/components/registry';
import StringListSelect                                          from '$api/options/stringlistselect';
import type { ComponentOption, Requirements }                    from '$api/options/options';
import AttributeSelect                                           from '$api/options/attributeselect';
import SectionMarker                                             from '$api/options/sectionmarker';
import StringSelect                                              from '$api/options/stringselect';
import ClassSelect                                               from '$api/options/classselect';
import SkillSelect                                               from '$api/options/skillselect';
import IntSelect                                                 from '$api/options/intselect';
import ColorSelect                                               from '$api/options/colorselect';
import { get }                                                   from 'svelte/store';
import type ProComponent                                         from '$api/components/procomponent';

// TRIGGERS

class ArmorEquipTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Armor Equip',
			description:  'Applies skill effects when a player equips a new item in an armor or hand slot',
			data:         [new DropdownSelect('Slots', 'slots', ['Any', 'Helmet', 'Chestplate', 'Leggings', 'Boots', 'Main hand', 'Offhand'], ['Any'], true)
				.setTooltip('The armor slots to check for')],
			summaryItems: ['slots']
		});
	}

	public static override new = () => new this();
}

class BlockBreakTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Block Break',
			description:  'Applies skill effects when a player breaks a block matching the given details',
			data:         [new BlockSelect(
				'The type of block expected to be broken',
				'The expected data value of the block (-1 for any data value)'
			)],
			summaryItems: ['block']
		});
	}

	public static override new = () => new this();
}

class BlockPlaceTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Block Place',
			description:  'Applies skill effects when a player places a block matching the given details',
			data:         [new BlockSelect(
				'The type of block expected to be placed',
				'The expected data value of the block (-1 for any data value)'
			)],
			summaryItems: ['block']
		});
	}

	public static override new = () => new this();
}

class CastTrigger extends ProTrigger {
	public constructor() {
		super({
			name:        'Cast',
			description: 'Applies skill effects when a player casts the skill using either the cast command, the skill bar, or click combos'
		});
	}

	public static override new = () => new this();
}

class ChatTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Chat',
			description:  'Applies skill effects when a player sends a chat message in the specified format',
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

class CleanupTrigger extends ProTrigger {
	public constructor() {
		super({
			name:        'Cleanup',
			description: 'Applies skill effects when the player disconnects or unlearns the skill. This is always applied with a skill level of 1 just for the sake of math'
		});
	}

	public static override new = () => new this();
}

class CrouchTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Crouch',
			description:  'Applies skill effects when a player starts or stops crouching using the shift key',
			data:         [
				new DropdownSelect('Crouching', 'type', ['Start Crouching', 'Stop Crouching', 'Both'])
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class DeathTrigger extends ProTrigger {
	public constructor() {
		super({
			name:        'Death',
			description: 'Applies skill effects when a player dies'
		});
	}

	public static override new = () => new this();
}

class DropItemTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Drop Item',
			description:  'Applies skill effects upon dropping an item',
			data:         [
				new DropdownSelect('Drop multiple', 'drop multiple', ['True', 'False', 'Ignore'], 'Ignore')
					.setTooltip('Whether the player has to drop multiple items or a single item')
			],
			summaryItems: ['drop multiple']
		});
	}

	public static override new = () => new this();
}

class EntityTargetTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Entity Target',
			description:  'Applies skill effects when an entity targets the caster',
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

class EnvironmentDamageTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Environment Damage',
			description:  'Applies skill effects when a player takes environmental damage',
			data:         [
				new DropdownSelect('Type', 'type', getDamageTypes, 'Fall')
					.setTooltip('The source of damage to apply for')
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class FishingTrigger extends ProTrigger {
	public constructor() {
		super({
			name:        'Fishing',
			description: 'Applies skill effects upon right-clicking with a fishing rod'
		});
	}

	public static override new = () => new this();
}

class FishingBiteTrigger extends ProTrigger {
	public constructor() {
		super({
			name:        'Fishing Bite',
			description: 'Applies skill effects when a fish bites the fishing rod of a player'
		});
	}

	public static override new = () => new this();
}

class FishingFailTrigger extends ProTrigger {
	public constructor() {
		super({
			name:        'Fishing Fail',
			description: 'Applies skill effects when a player fails to catch a fish due to poor timing'
		});
	}

	public static override new = () => new this();
}

class FishingGrabTrigger extends ProTrigger {
	public constructor() {
		super({
			name:        'Fishing Grab',
			description: 'Applies skill effects when a player catches a fish'
		});
	}

	public static override new = () => new this();
}

class FishingGroundTrigger extends ProTrigger {
	public constructor() {
		super({
			name:        'Fishing Ground',
			description: 'Applies skill effects when the bobber of a fishing rod hits the ground'
		});
	}

	public static override new = () => new this();
}

class FishingReelTrigger extends ProTrigger {
	public constructor() {
		super({
			name:        'Fishing Reel',
			description: 'Applies skill effects when a player reels in a fishing rod out of water or air with no fish on the rod'
		});
	}

	public static override new = () => new this();
}

class InitializeTrigger extends ProTrigger {
	public constructor() {
		super({
			name:        'Initialize',
			description: 'Applies skill effects immediately. This can be used for passive abilities'
		});
	}

	public static override new = () => new this();
}

class ItemSwapTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Item Swap',
			description:  'Applies skill effects upon pressing the swap-key on your keyboard',
			data:         [
				new BooleanSelect('Cancel Swap', 'cancel', true)
					.setTooltip('True cancels the item swap. False allows the item swap')
			],
			summaryItems: ['cancel']
		});
	}

	public static override new = () => new this();
}

class KillTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Kill',
			description:  'Applies skill effects upon killing something',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes children target the caster. False makes children target the attacking entity')
			],
			summaryItems: ['target']
		});
	}

	public static override new = () => new this();
}

class LandTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Land',
			description:  'Applies skill effects when a player lands on the ground',
			data:         [new DoubleSelect('Min Distance', 'min-distance')
				.setTooltip('The minimum distance the player should fall before effects activate')],
			summaryItems: ['min-distance']
		});
	}

	public static override new = () => new this();
}

class LaunchTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Launch',
			description:  'Applies skill effects when a player launches a projectile',
			data:         [new DropdownSelect('Type', 'type', getAnyProjectiles, 'Any')
				.setTooltip('The type of projectile that should be launched')],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class LeftClickTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Left Click',
			description:  'Applies skill effects upon performing a left-click',
			data:         [new DropdownSelect('Crouch', 'crouch', ['Crouch', 'Dont crouch', 'Both'], 'Crouch')
				.setTooltip('If the player has to be crouching in order for this trigger to function')],
			summaryItems: ['crouch']
		});
	}

	public static override new = () => new this();
}

class MoveTrigger extends ProTrigger {
	public constructor() {
		super({
			name:        'Move',
			description: 'Applies skill effects when a player moves around. This triggers every tick the player is moving, so use this sparingly. Use the \'api-moved\' value to check/use the distance traveled'
		});
	}

	public static override new = () => new this();
}

class PhysicalDamageTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Physical Damage',
			description:  'Applies skill effects when a player deals physical (or non-skill) damage. This includes melee attacks and firing a bow',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes the children target the caster. False makes children target the damaged entity'),
				new DropdownSelect('Type', 'type', ['Both', 'Melee', 'Projectile'], 'Both')
					.setTooltip('The type of damage dealt'),
				new DoubleSelect('Min Damage', 'dmg-min', 0)
					.setTooltip('The minimum damage that needs to be dealt'),
				new DoubleSelect('Max Damage', 'dmg-max', 999)
					.setTooltip('The minimum damage that needs to be dealt')
			],
			summaryItems: ['target', 'type', 'dmg-min', 'dmg-max']
		});
	}

	public static override new = () => new this();
}

class ProjectileHitTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Projectile Hit',
			description:  'Applies skill effects when a projectile hits a block/entity',
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

class ProjectileTickTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Projectile Tick',
			description:  'Applies skill effects every interval while a projectile is in the air',
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

class RightClickTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Right Click',
			description:  'Applies skill effects upon performing a right-click (NOTE: When clicking in air, you have to have an item in your hand)',
			data:         [
				new DropdownSelect('Crouch', 'crouch', ['Crouch', 'Dont crouch', 'Both'], 'Crouch')
					.setTooltip('If the player has to be crouching in order for this trigger to function')
			],
			summaryItems: ['crouch']
		});
	}

	public static override new = () => new this();
}

class SkillCastTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Skill Cast',
			description:  'Applies skill effects when a player casts a skill',
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

class SkillDamageTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Skill Damage',
			description:  'Applies skill effects when a player deals damage with a skill',
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

class TookPhysicalTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Took Physical Damage',
			description:  'Applies skill effects when a player takes physical (or non-skill) damage. This includes melee attacks and projectiles not fired by a skill',
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

class TookSkillTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Took Skill Damage',
			description:  'Applies skill effects when a player takes damage from a skill other than their own',
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

class ConsumeTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Consume',
			description:  'Applies skill effects when a player consumes an item',
			data:         [
				new DropdownSelect('Material', 'material', getAnyConsumable, 'Any')
					.setTooltip('The type of item that the player has consumed.'),
				new DropdownSelect('Potion', 'potion', getAnyPotion, 'Any')
					.requireValue('material', ['Potion'])
					.setTooltip('The type of potion effect to apply')
			],
			summaryItems: ['material', 'potion']
		});
	}

	public static override new = () => new this();
}

class HealTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Heal',
			description:  'Applies skill effects when the player receives heal from any source. Use {api-heal} to get heal value',
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

class ShieldTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Shield',
			description:  'Applies skill effects when the player blocks damage with their shield. Use {api-blocked} to get amount of blocked damage.',
			data:         [
				new BooleanSelect('Target Caster', 'target', true)
					.setTooltip('True makes children target the caster. False makes children target the attacking entity'),
				new DropdownSelect('Type', 'type', ['Both', 'Melee', 'Projectile'], 'Both')
					.setTooltip('The type of damage dealt'),
				new DoubleSelect('Damage Heal', 'dmg-min', 0)
					.setTooltip('The minimum damage that needs to be blocked'),
				new DoubleSelect('Damage Heal', 'dmg-max', 999)
					.setTooltip('The maximum damage that needs to be blocked')
			],
			summaryItems: ['target', 'type', 'dmg-min', 'dmg-max']
		});
	}

	public static override new = () => new this();
}

class SignalTrigger extends ProTrigger {
	public constructor() {
		super({
			name:         'Signal',
			description:  'Applies skill effects when the player receive a signal emitted from Signal Emit mechanic.',
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

// TARGETS

const targetOptions = (): ComponentOption[] => {
	return [new DropdownSelect('Group', 'group', ['Ally', 'Enemy', 'Both'], 'Enemy')
		.setTooltip('The alignment of targets to get'),
		new BooleanSelect('Through Wall', 'wall', false)
			.setTooltip('Whether to allow targets to be on the other side of a wall'),
		new BooleanSelect('Include Invulnerable', 'invulnerable', false)
			.setTooltip('Whether to target on invulnerable entities'),
		new DropdownSelect('Include Caster', 'caster', ['True', 'False', 'In area'], 'False')
			.setTooltip('Whether to include the caster in the target list. "True" will always include them, "False" will never, and "In area" will only if they are within the targeted area'),
		new AttributeSelect('Max Targets', 'max', 99)
			.setTooltip('The max amount of targets to apply children to')];
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

class AreaTarget extends ProTarget {
	public constructor() {
		super({
			name:         'Area',
			description:  'Targets all units in a radius from the current target (the casting player is the default target)',
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

class ConeTarget extends ProTarget {
	public constructor() {
		super({
			name:         'Cone',
			description:  'Targets all units in a line in front of the current target (the casting player is the default target). If you include the caster, that counts towards the max amount',
			data:         [
				new AttributeSelect('Range', 'range', 5)
					.setTooltip('The max distance away any target can be in blocks'),
				new AttributeSelect('Angle', 'angle', 90)
					.setTooltip('The angle of the cone arc in degrees'),
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

class LinearTarget extends ProTarget {
	public constructor() {
		super({
			name:         'Linear',
			description:  'Targets all units in a line in front of the current target (the casting player is the default target)',
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

class LocationTarget extends ProTarget {
	public constructor() {
		super({
			name:         'Location',
			description:  'Targets the location the target or caster is looking at. Combine this with another targeting type for ranged area effects',
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

class NearestTarget extends ProTarget {
	public constructor() {
		super({
			name:         'Nearest',
			description:  'Targets the closest unit(s) in a radius from the current target (the casting player is the default target). If you include the caster, that counts towards the max number',
			data:         [
				new AttributeSelect('Range', 'range', 3)
					.setTooltip('The radius of the area to target in blocks'),
				...targetOptions()
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
			summaryItems: ['range', 'group', 'wall', 'caster', 'max']
		});
	}

	public static override new = () => new this();
}

class OffsetTarget extends ProTarget {
	public constructor() {
		super({
			name:         'Offset',
			description:  'Targets a location that is the given offset away from each target',
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

class RememberTarget extends ProTarget {
	public constructor() {
		super({
			name:         'Remember',
			description:  'Targets entities stored using the "Remember Targets" mechanic for the matching key. If it was never set, this will fail',
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

class SelfTarget extends ProTarget {
	public constructor() {
		super({
			name:        'Self',
			description: 'Returns the current target back to the caster'
		});
	}

	public static override new = () => new this();
}

class SingleTarget extends ProTarget {
	public constructor() {
		super({
			name:         'Single',
			description:  'Targets a single unit in front of the current target (the casting player is the default target)',
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

class WorldTarget extends ProTarget {
	public constructor() {
		super({
			name:         'World',
			description:  'Targets all entities in the caster\'s world',
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
const itemConditionOptions = (): ComponentOption[] => {
	const data: ComponentOption[] = [
		new BooleanSelect('Check Material', 'check-mat', true)
			.setTooltip('Whether the item needs to be a certain type'),
		new MaterialSelect(false, 'Arrow')
			.requireValue('check-mat', [true])
			.setTooltip('The type the item needs to be'),
		new BooleanSelect('Check Data', 'check-data', false)
			.setTooltip('Whether the item needs to have a certain data value'),
		new IntSelect('Data', 'data')
			.requireValue('check-data', [true])
			.setTooltip('The data value the item must have'),
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

	return data;
};

class AltitudeCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Altitude',
			description:  'Applies child components whenever the player is on a certain height-level',
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

class ArmorCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Armor',
			description:  'Applies child components when the target is wearing an armor item matching the given details',
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

class AttributeCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Attribute',
			description:  'Requires the target to have a given number of attributes',
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

class BiomeCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Biome',
			description:  'Applies child components when in a specified biome',
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

class BlockCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Block',
			description:  'Applies child components if the target is currently standing on a block of the given type',
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

class BurningCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Burning',
			description:  'Applies child components if the caster burns or not',
			data:         [
				new DropdownSelect('Type', 'burn', ['Burn', 'Dont burn'], 'Burn')
					.setTooltip('Specifies whether the player has to be burning for this skill to be performed')
			],
			summaryItems: ['burn']
		});
	}

	public static override new = () => new this();
}

class CeilingCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Ceiling',
			description:  'Checks the height of the ceiling above each target',
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

class ChanceCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Chance',
			description:  'Rolls a chance to apply child components',
			data:         [
				new AttributeSelect('Chance', 'chance', 25)
					.setTooltip('The chance to execute children as a percentage. "25" would be 25%')
			],
			summaryItems: ['chance']
		});
	}

	public static override new = () => new this();
}

class ClassCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Class',
			description:  'Applies child components when the target is the given class or optionally a profession of that class. For example, if you check for "Fighter" which professes into "Warrior", a "Warrior" will pass the check if you do not enable "exact"',
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

class ClassLevelCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Class Level',
			description:  'Applies child components when the level of the class with this skill is within the range. This only checks the level of the caster, not the targets',
			data:         [
				new IntSelect('Min Level', 'min-level', 2)
					.setTooltip('The minimum class level the player should be. If the player has multiple classes, this will be of their main class'),
				new IntSelect('Max Level', 'max-level', 99)
					.setTooltip('The maximum class level the player should be. If the player has multiple classes, this will be of their main class')
			],
			summaryItems: ['min-level', 'max-level']
		});
	}

	public static override new = () => new this();
}

class CombatCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Combat',
			description:  'Applies child components to targets that are in/out of combat, depending on the settings',
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

class CrouchCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Crouch',
			description:  'Applies child components if the target player(s) are crouching',
			data:         [
				new BooleanSelect('Crouching', 'crouch', true)
					.setTooltip('Whether the player should be crouching')
			],
			summaryItems: ['crouch']
		});
	}

	public static override new = () => new this();
}

class DirectionCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Direction',
			description:  'Applies child components when the target or caster is facing the correct direction relative to the other',
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

class DistanceCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Distance',
			description:  'Applies child components when the distance between the caster and the target matches the settings',
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

class ElevationCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Elevation',
			description:  'Applies child components when the elevation of the target matches the settings',
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

class ElseCondition extends ProCondition {
	public constructor() {
		super({
			name:        'Else',
			description: 'Applies child elements if the previous component failed to execute. This not only applies for conditions not passing, but mechanics failing due to no target or other cases'
		});
	}

	public static override new = () => new this();
}

class EntityTypeCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Entity Type',
			description:  'Applies child elements if the target matches one of the selected entity types',
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

class FireCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Fire',
			description:  'Applies child components when the target is on fire',
			data:         [
				new DropdownSelect('Type', 'type', ['On Fire', 'Not On Fire'], 'On Fire')
					.setTooltip('Whether the target should be on fire')
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class FlagCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Flag',
			description:  'Applies child components when the target is marked by the appropriate flag',
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

class FoodCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Food',
			description:  'Applies child components when the target\'s food level matches the settings',
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

class GroundCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Ground',
			description:  'Applies child components when the target is on the ground',
			data:         [
				new DropdownSelect('Type', 'type', ['On Ground', 'Not On Ground'])
					.setTooltip('Whether the target should be on the ground')
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class HealthCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Health',
			description:  'Applies child components when the target\'s health matches the settings',
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

class ItemCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Item',
			description:  'Applies child components when the target is wielding an item matching the given material',
			data:         [...itemConditionOptions()],
			summaryItems: ['material', 'data', 'lore', 'name']
		});
	}

	public static override new = () => new this();
}

class InventoryCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Inventory',
			description:  'Applies child components when the target player contains the given item in their inventory. This does not work on mobs',
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

class LightCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Light',
			description:  'Applies child components when the light level at the target\'s location matches the settings',
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

class ManaCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Mana',
			description:  'Applies child components when the target\'s mana matches the settings',
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

class MoneyCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Money',
			description:  'Applies child components when the target\'s balance matches the settings (requires Vault and an economy plugin). Always is false for non-player targets',
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

class MountedCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Mounted',
			description:  'Applies child elements if the target is being mounted by one of the selected entity types',
			data:         [
				new DropdownSelect('Types', 'types', getAnyEntities, ['Any'], true)
					.setTooltip('The entity types that can be mounting the target')
			],
			summaryItems: ['types']
		});
	}

	public static override new = () => new this();
}

class MountingCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Mounting',
			description:  'Applies child elements if the target is mounting one of the selected entity types',
			data:         [
				new DropdownSelect('Types', 'types', getAnyEntities, ['Any'], true)
					.setTooltip('The entity types the target can be mounting')
			],
			summaryItems: ['types']
		});
	}

	public static override new = () => new this();
}

class MythicMobTypeCondition extends ProCondition {
	public constructor() {
		super({
			name:         'MythicMob Type',
			description:  'Applies child elements if the target corresponds to one of the entered MythicMob types, or is not a MythicMob if left empty',
			data:         [
				new StringListSelect('MythicMob Types', 'types')
					.setTooltip('The MythicMob types to target')
			],
			summaryItems: ['types']
		});
	}

	public static override new = () => new this();
}

class NameCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Name',
			description:  'Applies child components when the target has a name matching the settings',
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

class OffhandCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Offhand',
			description:  'Applies child components when the target is wielding an item matching the given material as an offhand item. This is for v1.9+ servers only',
			data:         [...itemConditionOptions()],
			summaryItems: ['material', 'data', 'lore', 'name']
		});
	}

	public static override new = () => new this();
}

class PermissionCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Permission',
			description:  'Applies child components if the caster has the required permission',
			data:         [
				new StringSelect('Permission', 'perm', 'some.permission')
					.setTooltip('The permission the player needs to have')
			],
			summaryItems: ['perm']
		});
	}

	public static override new = () => new this();
}

class PotionCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Potion',
			description:  'Applies child components when the target has the potion effect',
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

class SkillLevelCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Skill Level',
			description:  'Applies child components when the skill level is with the range. This checks the skill level of the caster, not the targets',
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

class SlotCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Slot',
			description:  'Applies child components when the target player has a matching item in the given slot',
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

class StatusCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Status',
			description:  'Applies child components when the target has the status condition',
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

class TimeCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Time',
			description:  'Applies child components when the server time matches the settings',
			data:         [
				new DropdownSelect('Time', 'time', ['Day', 'Night'], 'Day')
					.setTooltip('The time to check for in the current world')
			],
			summaryItems: ['time']
		});
	}

	public static override new = () => new this();
}

class ToolCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Tool',
			description:  'Applies child components when the target is wielding a matching tool',
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

class ValueCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Value',
			description:  'Applies child components if a stored value is within the given range',
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

class ValueTextCondition extends ProCondition {
	public constructor() {
		super({
			name:        'Value Text',
			description: 'Applies child components if text value match to the settings',
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

class WaterCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Water',
			description:  'Applies child components when the target is in or out of water, depending on the settings',
			data:         [
				new DropdownSelect('State', 'state', ['In Water', 'Out Of Water'])
					.setTooltip('Whether the target needs to be in the water')
			],
			summaryItems: ['state']
		});
	}

	public static override new = () => new this();
}

class WeatherCondition extends ProCondition {
	public constructor() {
		super({
			name:         'Weather',
			description:  'Applies child components when the target\'s location has the given weather condition',
			data:         [
				new DropdownSelect('Type', 'type', ['Rain', 'None', 'Snow', 'Thunder'])
					.setTooltip('Whether the target needs to be in the water')
			],
			summaryItems: ['type']
		});
	}

	public static override new = () => new this();
}

class WorldCondition extends ProCondition {
	public constructor() {
		super({
			name:         'World',
			description:  'Applies child components when the target is in a specific world',
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

// MECHANICS

/**
 * Adds the options for item related effects to the component
 */
const itemOptions = (): ComponentOption[] => {
	const data: ComponentOption[] = [
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
			.setTooltip('The armor color in hex RGB')
	];

	return data;
};

const warpOptions = (): ComponentOption[] => {
	return [
		// General data
		new BooleanSelect('Preserve Velocity', 'preserve')
			.setTooltip('Whether to preserve the target\'s velocity post-warp'),
		new BooleanSelect('Set Yaw', 'setYaw', false)
			.setTooltip('Whether to set the target\'s yaw on teleport'),
		new AttributeSelect('Yaw', 'yaw', 0)
			.requireValue('setYaw', [true])
			.setTooltip('The Yaw of the desired position (left/right orientation)'),
		new BooleanSelect('Set Pitch', 'setPitch', false)
			.setTooltip('Whether to set the target\'s pitch on teleport'),
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
		new DropdownSelect('Particle', 'particle', getParticles, 'Villager happy')
			.setTooltip('The type of particle to display'),

		new DropdownSelect('Material', 'material', (() => [...getMaterials()]), 'Arrow')
			.requireValue('particle', ['Item crack'])
			.setTooltip('The material to use for the particles'),
		new DropdownSelect('Material', 'material', (() => [...getBlocks()]), 'Dirt')
			.requireValue('particle', [
				'Block crack',
				'Block dust',
				'Falling dust',
				'Block marker'])
			.setTooltip('The block to use for the particles'),
		new IntSelect('Durability', 'durability', 0)
			.requireValue('particle', ['Item crack'])
			.setTooltip('The durability to be reduced from the item used to make the particles'),
		new IntSelect('CustomModelData', 'type', 0)
			.requireValue('particle', ['Item crack'])
			.setTooltip('The CustomModelData of the item used to make the particles'),
		new ColorSelect('Dust Color', 'dust-color', '#FF0000')
			.requireValue('particle', ['Redstone', 'Dust color transition'])
			.setTooltip('The color of the dust particles in hex RGB'),
		new ColorSelect('Final Dust Color', 'final-dust-color', '#FF0000')
			.requireValue('particle', ['Dust color transition'])
			.setTooltip('The color to transition to, in hex RGB'),
		new DoubleSelect('Dust Size', 'dust-size', 1)
			.requireValue('particle', ['Redstone', 'Dust color transition'])
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
			.setTooltip('How long in secods before the projectile will expire in case it doesn\'t hit anything.'),
		new BooleanSelect('On Expire', 'on-expire', false)
			.setTooltip('Whether to add the projectile\'s expire location as one of the targets. You can filter out this target with EntityTypeContidion: Location'),
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
			.setTooltip('The plane the animation motion moves through. XZ wold be flat, the other two are vertical.')),
		opt(new StringSelect('Animation Size', '-anim-size', '1')
			.setTooltip('Formula for deciding the multiplier of the animation distance. This can be any sort of formula using the operations defined in the wiki.')),
		opt(new IntSelect('Interval', '-interval', 1)
			.setTooltip('Number of ticks between playing particles.')),
		opt(new IntSelect('View Range', '-view-range', 25)
			.setTooltip('How far away the effect can be seen from.')),

		opt(new DropdownSelect('Particle', '-particle-type', getParticles, 'Villager happy')
			.setTooltip('The type of particle to use.')),
		opt(new DropdownSelect('Material', '-particle-material', getMaterials, 'Dirt')
			.requireValue('-particle-type', ['Item crack'])
			.setTooltip('The material to use for the particle.')),
		opt(new DropdownSelect('Material', '-particle-material', getBlocks, 'Dirt')
			.requireValue('-particle-type', ['Block crack', 'Block dust', 'Falling dust', 'Block marker'])
			.setTooltip('The block to use for the particle.')),
		opt(new IntSelect('Durability', '-particle-durability')
			.requireValue('particle', ['Item crack'])
			.setTooltip('The durability to be reduced from the item used to make the particles')),
		opt(new IntSelect('CustomModelData', '-particle-data')
			.requireValue('-particle-type', ['Item crack'])
			.setTooltip('The data value for the material used by the particle. For 1.14+ determines the CustomModelData of the item.')),
		new ColorSelect('Dust Color', '-particle-dust-color', '#FF0000').requireValue('-particle-type', ['Redstone', 'Dust color transition'])
			.setTooltip('The color of the dust particles in hex RGB'),
		new ColorSelect('Final Dust Color', '-particle-final-dust-color', '#FF0000').requireValue('-particle-type', ['Dust color transition'])
			.setTooltip('The color to transition to, in hex RGB'),
		new DoubleSelect('Dust Size', '-particle-dust-size', 1).requireValue('-particle-type', ['Redstone', 'Dust color transition'])
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

class ArmorMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Armor',
			description:  'Sets the specified armor slot of the target to the item defined by the settings',
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

class ArmorStandMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Armor Stand',
			description:  'Summons an armor stand that can be used as a marker or for item display (check Armor Mechanic for latter). Applies child components on the armor stand',
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

class ArmorStandPoseMechanic extends ProMechanic {
	public constructor() {
		super({
			name:        'Armor Stand Pose',
			description: 'Sets the pose of an armor stand target. Values should be in the format x,y,z where rotations are in degrees. Example: 0.0,0.0,0.0',
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

class ArmorStandRemoveMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Armor Stand Remove',
			description:  'Removes an armor stand with the given key',
			data:         [
				new StringSelect('Armor Stand Key', 'key', 'default')
					.setTooltip('The key to refer to the armor stand by. Only one armor stand of each key can be active per target at a time')
			],
			summaryItems: ['key']
		});
	}

	public static override new = () => new this();
}

class AttributeMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Attribute',
			description:  'Gives a player bonus attributes temporarily',
			data:         [
				new StringSelect('Attribute', 'key', 'Intelligence')
					.setTooltip('The name of the attribute to add to'),
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

class BlockMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Block',
			description:  'Changes blocks to the given type of block for a limited duration',
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
				new AttributeSelect('Seconds', 'seconds', 5)
					.setTooltip('How long the blocks should be replaced for'),

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

class BuffMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Buff',
			description:  'Buffs combat stats of the target',
			data:         [
				new BooleanSelect('Immediate', 'immediate', false)
					.setTooltip('Whether to apply the buff to the current damage trigger'),
				new DropdownSelect('Type', 'type', ['DAMAGE',
					'DEFENSE',
					'SKILL_DAMAGE',
					'SKILL_DEFENSE',
					'HEALING'], 'DAMAGE')
					.requireValue('immediate', [false])
					.setTooltip('What type of buff to apply. DAMAGE/DEFENSE is for regular attacks, SKILL_DAMAGE/SKILL_DEFENSE are for damage from abilities, and HEALING is for healing from abilities'),
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

class CancelMechanic extends ProMechanic {
	public constructor() {
		super({
			name:        'Cancel',
			description: 'Cancels the event that caused the trigger this is under to go off. For example, damage based triggers will stop the damage that was dealt while the Launch trigger would stop the projectile from firing'
		});
	}

	public static override new = () => new this();
}

class CancelEffectMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Cancel Effect',
			description:  'Stops a particle effect prematurely',
			data:         [
				new StringSelect('Effect Key', 'effect-key', 'default')
					.setTooltip('The key used when setting up the effect')
			],
			summaryItems: ['effect-key']
		}, false);
	}

	public static override new = () => new this();
}

class ChannelMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Channel',
			description:  'Applies child effects after a duration which can be interrupted. During the channel, the player cannot move, attack, or use other spells',
			data:         [
				new BooleanSelect('Still', 'still', true)
					.setTooltip('Whether to hold the player in place while channeling'),
				new AttributeSelect('Time', 'time', 3)
					.setTooltip('The amouont of time, in seconds, to channel for')
			],
			summaryItems: ['still', 'time']
		}, true);
	}

	public static override new = () => new this();
}

class CleanseMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Cleanse',
			description:  'Cleanses negative potion or status effects from the targets',
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

class CommandMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Command',
			description:  'Executes a command for each of the targets',
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

class CooldownMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Cooldown',
			description:  'Lowers the cooldowns of the target\'s skill(s). If you provide a negative amount, it will increase the cooldown',
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

class DamageMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Damage',
			description:  'Inflicts skill damage to each target. Multiplier type would be a percentage of the target health',
			data:         [
				new DropdownSelect('Type', 'type', ['Damage', 'Multiplier', 'Percent Left', 'Percent Missing'], 'Damage')
					.setTooltip('The unit to use for the amount of damage. Damage will deal flat damage, Multiplier will deal a percentage of the target\'s max health, Percent Left will deal a percentage of their current health, and Percent Missing will deal a percentage of the difference between their max health and current health'),
				new AttributeSelect('Value', 'value', 3, 1)
					.setTooltip('The amount of damage to deal'),
				new BooleanSelect('True Damage', 'true')
					.setTooltip('Whether to deal true damage. True damage ignores armor and all plugin checks, and doesn not have a damage animation nor knockback'),
				new StringSelect('Classifier', 'classifier', 'default')
					.setTooltip('The type of damage to deal. Can act as elemental damage or fake physical damage'),
				new BooleanSelect('Apply Knockback', 'knockback', true)
					.setTooltip('Whether the damage will inflict knockback. Ignored if it is True Damage'),
				new DropdownSelect('Damage Cause', 'cause', ['Contact', 'Custom', 'Entity Attack', 'Entity Sweep Attack', 'Projectile', 'Suffocation', 'Fall', 'Fire', 'Fire Tick', 'Melting', 'Lava', 'Drowning', 'Block Explosion', 'Entity Explosion', 'Void', 'Lightning', 'Suicide', 'Starvation', 'Poison', 'Magic', 'Wither', 'Falling Block', 'Thorns', 'Dragon Breath', 'Fly Into Wall', 'Hot Floor', 'Cramming', 'Dryout', 'Freeze', 'Sonic Boom'], 'Custom')
					.setTooltip('Damage Cause considered by the server. This will have influence over the death message and ProRPGItems\' defenses')
					.requireValue('true', [false])
			],
			summaryItems: ['value', 'true', 'knockback']
		}, false);
	}

	public static override new = () => new this();
}

class DamageBuffMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Damage Buff',
			description:  'Modifies the physical damage dealt by each target by a multiplier or a flat amount for a limited duration. Negative flat amounts or multipliers less than one will reduce damage dealt while the opposite will increase damage dealt. (e.g. a 5% damage buff would be a multiplier or 1.05)',
			data:         [
				new DropdownSelect('Type', 'type', ['Flat', 'Multiplier'], 'Flat')
					.setTooltip('The type of buff to apply. Flat increases damage by a fixed amount while multiplier increases it by a percentage'),
				new BooleanSelect('Skill Damage', 'skill')
					.setTooltip('Whether to buff skill damage. If false, it will affect physical damage'),
				new AttributeSelect('Value', 'value', 1)
					.setTooltip('The amount to increase/decrease the damage by. A negative amoutn with the "Flat" type will decrease damage, similar to a number less than 1 for the multiplier'),
				new AttributeSelect('Seconds', 'seconds', 3)
					.setTooltip('The duration of the buff in seconds')
			],
			summaryItems: ['type', 'skill', 'value', 'seconds']
		}, false);
	}

	public static override new = () => new this();
}

class DamageLoreMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Damage Lore',
			description:  'Damages each target based on a value found in the lore of the item held by the caster',
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
				new DropdownSelect('Damage Cause', 'cause', ['Contact', 'Entity Attack', 'Entity Sweep Attack', 'Projectile', 'Suffocation', 'Fall', 'Fire', 'Fire Tick', 'Melting', 'Lava', 'Drowning', 'Block Explosion', 'Entity Explosion', 'Void', 'Lightning', 'Suicide', 'Starvation', 'Poison', 'Magic', 'Wither', 'Falling Block', 'Thorns', 'Dragon Breath', 'Custom', 'Fly Into Wall', 'Hot Floor', 'Cramming', 'Dryout', 'Freeze', 'Sonic Boom'], 'Entity Attack')
					.setTooltip('Damage Cause considered by the server. This will have influence over the death message and ProRPGItems\' defenses')
					.requireValue('true', [false])
			],
			summaryItems: ['hand', 'multiplier', 'true', 'knockback']
		}, false);
	}

	public static override new = () => new this();
}

class DefenseBuffMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Defense Buff',
			description:  'Modifies the physical damage taken by each target by a multiplier or a flat amount for a limited duration. Negative flag amounts or multipliers less than one will reduce damage taken while the opposite will increase damage taken. (e.g. a 5% defense buff would be a multiplier or 0.95, since you would be taking 95% damage)',
			data:         [
				new DropdownSelect('Type', 'type', ['Flat', 'Multiplier'], 'Flat')
					.setTooltip('The type of buff to apply. Flat will increase/reduce incoming damage by a fixed amount where Multiplier does it by a percentage of the damage. Multipliers above 1 will increase damage taken while multipliers below 1 reduce damage taken'),
				new BooleanSelect('Skill Defense', 'skill')
					.setTooltip('Whether to buff skill defense. If false, it will affect physical defense'),
				new AttributeSelect('Value', 'value', 1)
					.setTooltip('The amount to increase/decrease incoming damage by'),
				new AttributeSelect('Seconds', 'seconds', 3)
					.setTooltip('The duration of the buff in seconds')
			],
			summaryItems: ['type', 'skill', 'value', 'seconds']
		}, false);
	}

	public static override new = () => new this();
}

class DelayMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Delay',
			description:  'Applies child components after a delay',
			data:         [
				new AttributeSelect('Delay', 'delay', 2)
					.setTooltip('The amount of time to wait before applying child components in seconds')
			],
			summaryItems: ['delay']
		}, true);
	}

	public static override new = () => new this();
}

class DisguiseMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Disguise',
			description:  'Disguises each target according to the settings. This mechanic requires the LibsDisguise plugin to be installed on your server',
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

class DurabilityMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Durability',
			description:  'Lowers the durability of a held item',
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

class ExperienceMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Experience',
			description:  'Modifies target\'s specified class experience',
			data:         [
				new IntSelect('Value', 'value', 1),
				new DropdownSelect('Mode', 'mode', ['give', 'take', 'set'], 'give', false)
					.setTooltip('To give, take or set specified valued'),
				new DropdownSelect('Type', 'type', ['flat', 'percent'], 'flat', false)
					.setTooltip('Flat value or percent from next level experience'),
				new StringSelect('Group', 'group', 'class')
					.setTooltip('Group name to modify experience'),
				new BooleanSelect('Level Down', 'level-down', false)
					.setTooltip('Whether to use skill and level down player class if current exp is insufficient')
			],
			summaryItems: ['value', 'mode', 'type', 'group']
		}, false);
	}

	public static override new = () => new this();
}

class ExplosionMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Explosion',
			description:  'Causes an explosion at the current target\'s position',
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

class FireMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Fire',
			description:  'Sets the target on fire for a duration',
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

class FlagMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Flag',
			description:  'Marks the target with a flag for a duration. Flags can be checked by other triggers, spells or the related for interesting synergies and effects',
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

class FlagClearMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Flag Clear',
			description:  'Clears a flag from the target',
			data:         [
				new StringSelect('Key', 'key', 'key')
					.setTooltip('The unique string for the flag. This should match that of the mechanic that set the flag to begin with')
			],
			summaryItems: ['key']
		});
	}

	public static override new = () => new this();
}

class FlagToggleMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Flag Toggle',
			description:  'Toggles a flag on or off for the target. This can be used to make toggle effects',
			data:         [
				new StringSelect('Key', 'key', 'key')
					.setTooltip('The unique string for the flag. Use the same key when checking it in a Flag Condition')
			],
			summaryItems: ['key']
		});
	}

	public static override new = () => new this();
}

class FoodMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Food',
			description:  'Adds or removes to a player\'s hunger and saturation',
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

class ForgetTargetsMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Forget Targets',
			description:  'Clears targets stored by the "Remember Targets" mechanic',
			data:         [
				new StringSelect('Key', 'key', 'key')
					.setTooltip('The unique key the targets were stored under')
			],
			summaryItems: ['key']
		}, false);
	}

	public static override new = () => new this();
}

class HealMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Heal',
			description:  'Restores health to each target',
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

class HealthSetMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Health Set',
			description:  'Sets the target\'s health to the specified amount, ignoring resistances, damage buffs, and so on',
			data:         [
				new AttributeSelect('Health', 'health', 1)
					.setTooltip('The health to set to')
			],
			summaryItems: ['health']
		}, false);
	}

	public static override new = () => new this();
}

class HeldItemMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Held Item',
			description:  'Sets the held item slot of the target player. This will do nothing if trying to set it to a skill slot',
			data:         [
				new AttributeSelect('Slot', 'slot')
					.setTooltip('The slot to set it to')
			],
			summaryItems: ['slot']
		}, false);
	}

	public static override new = () => new this();
}

class ImmunityMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Immunity',
			description:  'Provides damage immunity from one source for a duration',
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

class InterruptMechanic extends ProMechanic {
	public constructor() {
		super({
			name:        'Interrupt',
			description: 'Interrupts any channeling being done by each target if applicable'
		});
	}

	public static override new = () => new this();
}

class InvisibilityMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Invisibility',
			description:  'Applies invisibility effect on target, optionally hiding equipment (Requires ProtocolLib).',
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

class ItemMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Item',
			description:  'Gives each player target the item defined by the settings',
			data:         [...itemOptions()],
			summaryItems: ['material', 'amount']
		});
	}

	public static override new = () => new this();
}

class ItemDropMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Item Drop',
			description:  'Spawns a dropped item defined by the settings at the specified location',
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

class ItemProjectileMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Item Projectile',
			description:  'Launches a projectile using an item as its visual that applies child components upon landing. The target passed on will be the collided target or the location where it landed if it missed',
			data:         [
				new DropdownSelect('Group', 'group', ['Ally', 'Enemy'], 'Enemy')
					.setTooltip('The alignment of targets to hit'),
				new BooleanSelect('Wall Collisions', 'walls', true)
					.setTooltip('Wheter to account for wall collisions. If false, the item will just slide through them.'),
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
					.requireValue('per-target-particle', ['Item crack'])
					.requireValue('per-target', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'per-target-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('per-target-particle', [
						'Block crack',
						'Block dust',
						'Falling dust',
						'Block marker'])
					.requireValue('per-target', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'per-target-durability', 0)
					.requireValue('per-target-particle', ['Item crack'])
					.requireValue('per-target', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'per-target-type', 0)
					.requireValue('per-target-particle', ['Item crack'])
					.requireValue('per-target', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'per-target-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Redstone', 'Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'per-target-final-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'per-target-dust-size', 1)
					.requireValue('per-target-particle', ['Redstone', 'Dust color transition'])
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
					.requireValue('path-particle', ['Item crack'])
					.requireValue('path', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'path-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('path-particle', [
						'Block crack',
						'Block dust',
						'Falling dust',
						'Block marker'])
					.requireValue('path', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'path-durability', 0)
					.requireValue('path-particle', ['Item crack'])
					.requireValue('path', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'path-type', 0)
					.requireValue('path-particle', ['Item crack'])
					.requireValue('path', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'path-dust-color', '#FF0000')
					.requireValue('path-particle', ['Redstone', 'Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'path-final-dust-color', '#FF0000')
					.requireValue('path-particle', ['Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'path-dust-size', 1)
					.requireValue('path-particle', ['Redstone', 'Dust color transition'])
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

class ItemRemoveMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Item Remove',
			description:  'Removes an item from a player inventory. This does nothing to mobs',
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

class LaunchMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Launch',
			description:  'Launches the target relative to their forward direction. Use negative values to go in the opposite direction (e.g. negative forward makes the target go backwards)',
			data:         [
				new DropdownSelect('Relative', 'relative', ['Target', 'Caster', 'Between'], 'Target')
					.setTooltip('Determines what is considered "forward". Target uses the direction the target is facing, Caster uses the direction the caster is facing, and Between uses the direction from the caster to the target'),
				new AttributeSelect('Forward Speed', 'forward')
					.setTooltip('The speed to give the target in the direction they are facing'),
				new AttributeSelect('Upward Speed', 'upward', 2, 0.5)
					.setTooltip('The speed to give the target upwards'),
				new AttributeSelect('Right Speed', 'right')
					.setTooltip('The speed to give the target to their right')
			],
			summaryItems: ['relative', 'forward', 'upward', 'right']
		}, false);
	}

	public static override new = () => new this();
}

class LightningMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Lightning',
			description:  'Strikes lightning on or near the target, applying child components to the struck targets. Negative offsets will offset it in the opposite direction (e.g. negative forward offset puts it behind the target)',
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

class ManaMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Mana',
			description:  'Restores or deducts mana from the target',
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

class MessageMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Message',
			description:  'Sends a message to each player target. To include numbers from Value mechanics, use the filters {<key>} where <key> is the key the value is stored under',
			data:         [
				new StringSelect('Message', 'message', 'text')
					.setTooltip('The message to display. {player} = caster\'s name, {target} = target\'s name, {targetUUID} = target\'s UUID (useful if targets are non players), &lc: "{", &rc: "}", &sq: "\'"')
			],
			summaryItems: ['message']
		});
	}

	public static override new = () => new this();
}

class MineMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Mine',
			description:  'Destroys a selection of blocks at the location of the target',
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

class MoneyMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Money',
			description:  'Adds or multiplies the target\'s balance by some amount (requires Vault and an economy plugin). Fails if the resulting balance is not within the range allowed by the economy plugin',
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

class MountMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Mount',
			description:  'Mounts entities',
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

class ParticleMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Particle',
			description:  'Plays a particle effect about the target',
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

class ParticleAnimationMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Particle Animation',
			description:  'Plays an animated particle effect at the location of each target over time by applying various transformations',
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

class ParticleEffectMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Particle Effect',
			description:  'Plays a particle effect that follows the current target, using formulas to determine shape, size, and motion',
			data:         [
				...effectOptions(false)
			],
			summaryItems: ['effect-key', '-particle', '-particle-dust-color']
		});
	}

	public static override new = () => new this();
}

class ParticleProjectileMechanic extends ProMechanic {
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
					.requireValue('per-target-particle', ['Item crack'])
					.requireValue('per-target', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'per-target-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('per-target-particle', [
						'Block crack',
						'Block dust',
						'Falling dust',
						'Block marker'])
					.requireValue('per-target', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'per-target-durability', 0)
					.requireValue('per-target-particle', ['Item crack'])
					.requireValue('per-target', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'per-target-type', 0)
					.requireValue('per-target-particle', ['Item crack'])
					.requireValue('per-target', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'per-target-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Redstone', 'Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'per-target-final-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'per-target-dust-size', 1)
					.requireValue('per-target-particle', ['Redstone', 'Dust color transition'])
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
					.requireValue('path-particle', ['Item crack'])
					.requireValue('path', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'path-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('path-particle', [
						'Block crack',
						'Block dust',
						'Falling dust',
						'Block marker'])
					.requireValue('path', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'path-durability', 0)
					.requireValue('path-particle', ['Item crack'])
					.requireValue('path', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'path-type', 0)
					.requireValue('path-particle', ['Item crack'])
					.requireValue('path', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'path-dust-color', '#FF0000')
					.requireValue('path-particle', ['Redstone', 'Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'path-final-dust-color', '#FF0000')
					.requireValue('path-particle', ['Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'path-dust-size', 1)
					.requireValue('path-particle', ['Redstone', 'Dust color transition'])
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

class PassiveMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Passive',
			description:  'Applies child components continuously every period. The seconds value below is the period or how often it applies',
			data:         [
				new AttributeSelect('Seconds', 'seconds', 1)
					.setTooltip('The delay in seconds between each application')
			],
			summaryItems: ['seconds']
		}, true);
	}

	public static override new = () => new this();
}

class PermissionMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Permission',
			description:  'Grants each player target a permission for a limited duration. This mechanic requires Vault with an accompanying permissions plugin in order to work',
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

class PotionMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Potion',
			description:  'Applies a potion effect to the target for a duration',
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

class PotionProjectileMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Potion Projectile',
			description:  'Drops a splash potion from each target that does not apply potion effects by default. This will apply child elements when the potion lands. The targets supplied will be everything hit by the potion. If nothing is hit by the potion, the target will be the location it landed',
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
					.requireValue('cloud-particle', ['Item crack'])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'cloud-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('cloud-particle', [
						'Block crack',
						'Block dust',
						'Falling dust',
						'Block marker'])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'cloud-durability', 0)
					.requireValue('cloud-particle', ['Item crack'])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'cloud-type', 0)
					.requireValue('cloud-particle', ['Item crack'])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'cloud-dust-color', '#FF0000')
					.requireValue('cloud-particle', ['Redstone', 'Dust color transition'])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'cloud-final-dust-color', '#FF0000')
					.requireValue('cloud-particle', ['Dust color transition'])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'cloud-dust-size', 1)
					.requireValue('cloud-particle', ['Redstone', 'Dust color transition'])
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
					.requireValue('per-target-particle', ['Item crack'])
					.requireValue('per-target', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'per-target-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('per-target-particle', [
						'Block crack',
						'Block dust',
						'Falling dust',
						'Block marker'])
					.requireValue('per-target', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'per-target-durability', 0)
					.requireValue('per-target-particle', ['Item crack'])
					.requireValue('per-target', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'per-target-type', 0)
					.requireValue('per-target-particle', ['Item crack'])
					.requireValue('per-target', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'per-target-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Redstone', 'Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'per-target-final-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'per-target-dust-size', 1)
					.requireValue('per-target-particle', ['Redstone', 'Dust color transition'])
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
					.requireValue('path-particle', ['Item crack'])
					.requireValue('path', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'path-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('path-particle', [
						'Block crack',
						'Block dust',
						'Falling dust',
						'Block marker'])
					.requireValue('path', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'path-durability', 0)
					.requireValue('path-particle', ['Item crack'])
					.requireValue('path', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'path-type', 0)
					.requireValue('path-particle', ['Item crack'])
					.requireValue('path', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'path-dust-color', '#FF0000')
					.requireValue('path-particle', ['Redstone', 'Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'path-final-dust-color', '#FF0000')
					.requireValue('path-particle', ['Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'path-dust-size', 1)
					.requireValue('path-particle', ['Redstone', 'Dust color transition'])
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

class ProjectileMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Projectile',
			description:  'Launches a projectile that applies child components on hit. The target supplied will be the struck target',
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
					.requireValue('per-target-particle', ['Item crack'])
					.requireValue('per-target', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'per-target-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('per-target-particle', [
						'Block crack',
						'Block dust',
						'Falling dust',
						'Block marker'])
					.requireValue('per-target', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'per-target-durability', 0)
					.requireValue('per-target-particle', ['Item crack'])
					.requireValue('per-target', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'per-target-type', 0)
					.requireValue('per-target-particle', ['Item crack'])
					.requireValue('per-target', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'per-target-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Redstone', 'Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'per-target-final-dust-color', '#FF0000')
					.requireValue('per-target-particle', ['Dust color transition'])
					.requireValue('per-target', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'per-target-dust-size', 1)
					.requireValue('per-target-particle', ['Redstone', 'Dust color transition'])
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
					.requireValue('path-particle', ['Item crack'])
					.requireValue('path', [true])
					.setTooltip('The material to use for the particles'),
				new DropdownSelect('Material', 'path-material', (() => [...getBlocks()]), 'Dirt')
					.requireValue('path-particle', [
						'Block crack',
						'Block dust',
						'Falling dust',
						'Block marker'])
					.requireValue('path', [true])
					.setTooltip('The block to use for the particles'),
				new IntSelect('Durability', 'path-durability', 0)
					.requireValue('path-particle', ['Item crack'])
					.requireValue('path', [true])
					.setTooltip('The durability to be reduced from the item used to make the particles'),
				new IntSelect('CustomModelData', 'path-type', 0)
					.requireValue('path-particle', ['Item crack'])
					.requireValue('path', [true])
					.setTooltip('The CustomModelData of the item used to make the particles'),
				new ColorSelect('Dust Color', 'path-dust-color', '#FF0000')
					.requireValue('path-particle', ['Redstone', 'Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color of the dust particles in hex RGB'),
				new ColorSelect('Final Dust Color', 'path-final-dust-color', '#FF0000')
					.requireValue('path-particle', ['Dust color transition'])
					.requireValue('path', [true])
					.setTooltip('The color to transition to, in hex RGB'),
				new DoubleSelect('Dust Size', 'path-dust-size', 1)
					.requireValue('path-particle', ['Redstone', 'Dust color transition'])
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

class PurgeMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Purge',
			description:  'Purges the target of positive potion effects or statuses',
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

class PushMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Push',
			description:  'Pushes the target relative to the caster. This will do nothing if used with the caster as the target. Positive numbers apply knockback while negative numbers pull them in',
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

class RememberTargetsMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Remember Targets',
			description:  'Stores the current targets for later use under a specified key',
			data:         [
				new StringSelect('Key', 'key', 'target')
					.setTooltip('The unique key to store the targets under. The "Remember" target will use this key to apply effects to the targets later on')
			],
			summaryItems: ['key']
		}, false);
	}

	public static override new = () => new this();
}

class RepeatMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Repeat',
			description:  'Applies child components multiple times. When it applies them is determined by the delay (seconds before the first application) and period (seconds between successive applications)',
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

class SignalEmitMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Signal Emit',
			description:  'Send a custom signal to all target that can be reused and processed separately.',
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

class SkillCastMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Skill cast',
			description:  'Make target cast other skill. Applicable to players only!',
			data:         [
				new DropdownSelect('Cast mode', 'mode', ['All', 'First', 'Random'], 'All')
					.setTooltip('Choose which skills to cast (excluding unavailable skills).'),
				new BooleanSelect('Force cast', 'force', false)
					.setTooltip('True if player will cast regardless of whether they have that skill or not'),
				new StringListSelect('Skills', 'skills')
					.setTooltip('The list of skills.Each skill can come with the level like "example skill:3". If player has skill, level will is available level. Else, level is 1.')
			],
			summaryItems: ['mode', 'force', 'skills']
		}, false);
	}

	public static override new = () => new this();
}

class SoundMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Sound',
			description:  'Plays a sound at the target\'s location',
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

class StatMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Stat',
			description:  'Gives a player bonus stat temporarily. All available <a href="https://github.com/promcteam/proskillapi/wiki/attributes#attribute-stats">attribute stats</a>',
			data:         [
				// new DropdownSelect('Stat', 'key', ['health',
				//                                              'mana',
				//                                              'mana-regen',
				//                                              'physical-damage',
				//                                              'melee-damage',
				//                                              'projectile-damage',
				//                                              'physical-damage',
				//                                              'melee-defense',
				//                                              'projectile-defense',
				//                                              'skill-damage',
				//                                              'skill-defense',
				//                                              'move-speed',
				//                                              'attack-speed',
				//                                              'armor',
				//                                              'luck',
				//                                              'armor-toughness',
				//                                              'exp',
				//                                              'hunger',
				//                                              'hunger-heal',
				//                                              'cooldown',
				//                                              'knockback-resist'], 'health')
				//     .setTooltip('The name of the stat to add to')
				// ,
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

class StatusMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Status',
			description:  'Applies a status effect to the target for a duration',
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

class SummonMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Summon',
			description:  'Summons a mob on each target. Child components will start off targeting the mob so you can add effects to it. Hostile mobs may attack the caster',
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

class TauntMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Taunt',
			description:  'Draws aggro of targeted creatures. Regular mobs are set to attack the caster. The Spigot/Bukkit API for this was not functional on older versions, so it may not work on older servers. For MythicMobs, this uses their aggro system using the amount chosen below',
			data:         [
				new AttributeSelect('Amount', 'amount', 1)
					.setTooltip('The amount of aggro to apply if MythicMobs is active. Use negative amounts to reduce aggro')
			],
			summaryItems: ['amount']
		}, false);
	}

	public static override new = () => new this();
}

class ThrowMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Throw',
			description:  'Throws entities off of the target\'s head and targets them for child components',
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

class TriggerMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Trigger',
			description:  'Listens for a trigger on the current targets for a duration',
			data:         [
				new DropdownSelect('Trigger', 'trigger', () => Object.values(get(triggers)).map((trigger: {
					name: string,
					component: typeof ProComponent
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

				//DROP_ITEM
				new DropdownSelect('Drop multiple', 'drop multiple', ['True', 'False', 'Ignore'], 'Ignore')
					.requireValue('trigger', ['Drop Item'])
					.setTooltip('Whether the player has to drop multiple items or a single item'),

				// ENVIRONMENT_DAMAGE
				new DropdownSelect('Type', 'type', getDamageTypes, 'FALL')
					.requireValue('trigger', ['Environment Damage'])
					.setTooltip('The source of damage to apply for'),

				//ITEM_SWAP
				new BooleanSelect('Cancel swap', 'cancel', true)
					.requireValue('trigger', ['Item Swap'])
					.setTooltip('True cancels the item swap. False allows the item swap'),

				// LAND
				new DoubleSelect('Min Distance', 'min-distance', 0)
					.requireValue('trigger', ['Land'])
					.setTooltip('The minimum distance the player should fall before effects activating'),

				// LAUNCH
				new DropdownSelect('Type', 'type', getAnyProjectiles(), 'Any')
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

class ValueAddMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Add',
			description:  'Adds to a stored value under a unique key for the caster. If the value wasn\'t set before, this will set the value to the given amount',
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

class ValueAttributeMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Attribute',
			description:  'Loads a player\'s attribute count for a specific attribute as a stored value to be used in other mechanics',
			data:         [
				new StringSelect('Key', 'key', 'attribute')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new StringSelect('Attribute', 'attribute', 'Vitality')
					.setTooltip('The name of the attribute you are loading the value of'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')
			],
			summaryItems: ['key', 'attribute', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueCopyMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Copy',
			description:  'Copies a stored value from the caster to the target or vice versa',
			data:         [
				new StringSelect('Key', 'key', 'value')
					.setTooltip('The unique key to store the value under. This key can be used in place of attribute values to use the stored value'),
				new StringSelect('Destination', 'destination', 'value')
					.setTooltip('The key to copy the original value to'),
				new BooleanSelect('To target', 'to-target', true)
					.setTooltip('The amount to add to the value'),
				new BooleanSelect('Save', 'save', false)
					.setTooltip('If true, save the key value to persistent value. Persistent value is not lost when the player leaves the server and is stored separately on each account')

			],
			summaryItems: ['key', 'destination', 'to-target', 'save']
		}, false);
	}

	public static override new = () => new this();
}

class ValueDistanceMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Distance',
			description:  'Stores the distance between the target and the caster into a value',
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

class ValueHealthMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Health',
			description:  'Stores the target\'s current health as a value under a given key for the caster',
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

class ValueLocationMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Location',
			description:  'Loads the first target\'s current location into a stored value for use at a later time',
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

class ValueLoadMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Load',
			description:  'If there is a value already stored on the account, that value will be retrieved and then be used as a normal value.',
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

class ValueLoreMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Lore',
			description:  'Loads a value from a held item\'s lore into a stored value under the given unique key for the caster',
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

class ValueLoreSlotMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Lore Slot',
			description:  'Loads a value from an item\'s lore into a stored value under the given unique key for the caster',
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

class ValueManaMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Mana',
			description:  'Stores the target player\'s current mana as a value under a given key for the caster',
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

class ValueMultiplyMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Multiply',
			description:  'Multiplies a stored value under a unique key for the caster. If the value wasn\'t set before, this will not do anything',
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

class ValuePlaceholderMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Placeholder',
			description:  'Uses a placeholder string and stores it as a value for the caster',
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

class ValueRandomMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Random',
			description:  'Stores a specified value under a given key for the caster',
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

class ValueRoundMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Round',
			description:  'Rounds a stored value under a unique key for the caster. If the value wasn\'t set before, this will not do anything',
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

class ValueSetMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Value Set',
			description:  'Stores a specified value under a given key for the caster',
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

class WarpMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Warp',
			description:  'Warps the target relative to their forward direction. Use negative numbers to go in the opposite direction (e.g. negative forward will cause the target to warp backwards)',
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

class WarpLocMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Warp Location',
			description:  'Warps the target to a specified location',
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

class WarpRandomMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Warp Random',
			description:  'Warps the target in a random direction the given distance',
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

class WarpSwapMechanic extends ProMechanic {
	public constructor() {
		super({
			name:        'Warp Swap',
			description: 'Switches the location of the caster and the target. If multiple targets are provided, this takes the first one',
			data:        [...warpOptions()],
			preview: ['preserve']
		});
	}

	public static override new = () => new this();
}

class WarpTargetMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Warp Target',
			description:  'Warps either the target or the caster to the other. This does nothing when the target is the caster',
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

class WarpValueMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Warp Value',
			description:  'Warps all targets to a location remembered using the Value Location mechanic',
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

class WolfMechanic extends ProMechanic {
	public constructor() {
		super({
			name:         'Wolf',
			description:  'Summons a wolf on each target for a duration. Child components will start off targeting the wolf so you can add effects to it. You can also give it its own skillset, though Cast triggers will not occur',
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
			.requireValue(key + '-particle', ['Item crack'])
			.requireValue(key, [true])
			.setTooltip('The material to use for the particles'),
		new DropdownSelect('Material', key + '-material', (() => [...getBlocks()]), 'Dirt')
			.requireValue(key + '-particle', [
				'Block crack',
				'Block dust',
				'Falling dust',
				'Block marker'])
			.requireValue(key, [true])
			.setTooltip('The block to use for the particles'),
		new IntSelect('Durability', key + '-durability', 0)
			.requireValue(key + '-particle', ['Item crack'])
			.requireValue(key, [true])
			.setTooltip('The durability to be reduced from the item used to make the particles'),
		new IntSelect('CustomModelData', key + '-type', 0)
			.requireValue(key + '-particle', ['Item crack'])
			.requireValue(key, [true])
			.setTooltip('The CustomModelData of the item used to make the particles'),
		new ColorSelect('Dust Color', key + '-dust-color', '#FF0000')
			.requireValue(key + '-particle', ['Redstone', 'Dust color transition'])
			.requireValue(key, [true])
			.setTooltip('The color of the dust particles in hex RGB'),
		new ColorSelect('Final Dust Color', key + '-final-dust-color', '#FF0000')
			.requireValue(key + '-particle', ['Dust color transition'])
			.requireValue(key, [true])
			.setTooltip('The color to transition to, in hex RGB'),
		new DoubleSelect('Dust Size', key + '-dust-size', 1)
			.requireValue(key + '-particle', ['Redstone', 'Dust color transition'])
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
		BLOCK_BREAK:   { name: 'Block Break', component: BlockBreakTrigger },
		BLOCK_PLACE:   { name: 'Block Place', component: BlockPlaceTrigger },
		CAST:          { name: 'Cast', component: CastTrigger },
		CHAT:          { name: 'Chat', component: ChatTrigger },
		CLEANUP:       { name: 'Cleanup', component: CleanupTrigger },
		CROUCH:        { name: 'Crouch', component: CrouchTrigger },
		DEATH:         { name: 'Death', component: DeathTrigger },
		ENTITY_TARGET: { name: 'Entity Target', component: EntityTargetTrigger },
		HEAL:          { name: 'Heal', component: HealTrigger },
		INIT:          { name: 'Initialize', component: InitializeTrigger },
		KILL:          { name: 'Kill', component: KillTrigger },
		LAND:          { name: 'Land', component: LandTrigger },
		LEFT_CLICK:    { name: 'Left Click', component: LeftClickTrigger },
		RIGHT_CLICK:   { name: 'Right Click', component: RightClickTrigger },
		MOVE:          { name: 'Move', component: MoveTrigger },
		PROJ_HIT:      { name: 'Projectile Hit', component: ProjectileHitTrigger },
		PROJ_LAUNCH:   { name: 'Projectile Launch', alias: 'Launch', component: LaunchTrigger },
		PROJ_TICK:     { name: 'Projectile Tick', component: ProjectileTickTrigger },
		SIGNAL:        { name: 'Signal', component: SignalTrigger },
		SHIELD:        { name: 'Shield', component: ShieldTrigger },
		SKILL_CAST:    { name: 'Skill Cast', component: SkillCastTrigger },

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
		TOOK_SKILL:   { name: 'Took Skill Damage', component: TookSkillTrigger, section: 'Damage' }
	});
	targets.set({
		AREA:     { name: 'Area', component: AreaTarget },
		CONE:     { name: 'Cone', component: ConeTarget },
		LINEAR:   { name: 'Linear', component: LinearTarget },
		LOCATION: { name: 'Location', component: LocationTarget },
		NEAREST:  { name: 'Nearest', component: NearestTarget },
		OFFSET:   { name: 'Offset', component: OffsetTarget },
		REMEMBER: { name: 'Remember', component: RememberTarget },
		SELF:     { name: 'Self', component: SelfTarget },
		SINGLE:   { name: 'Single', component: SingleTarget },
		WORLD:    { name: 'World', component: WorldTarget }
	});
	conditions.set({
		ALTITUDE:       { name: 'Altitude', component: AltitudeCondition },
		ARMOR:          { name: 'Armor', component: ArmorCondition },
		ATTRIBUTE:      { name: 'Attribute', component: AttributeCondition },
		BIOME:          { name: 'Biome', component: BiomeCondition },
		BLOCK:          { name: 'Block', component: BlockCondition },
		BURNING:        { name: 'Burning', component: BurningCondition },
		CEILING:        { name: 'Ceiling', component: CeilingCondition },
		CHANCE:         { name: 'Chance', component: ChanceCondition },
		CLASS:          { name: 'Class', component: ClassCondition },
		CLASS_LEVEL:    { name: 'Class Level', component: ClassLevelCondition },
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
		GROUND:         { name: 'Ground', component: GroundCondition },
		HEALTH:         { name: 'Health', component: HealthCondition },
		INVENTORY:      { name: 'Inventory', component: InventoryCondition },
		ITEM:           { name: 'Item', component: ItemCondition },
		LIGHT:          { name: 'Light', component: LightCondition },
		MANA:           { name: 'Mana', component: ManaCondition },
		MONEY:          { name: 'Money', component: MoneyCondition },
		MOUNTED:        { name: 'Mounted', component: MountedCondition },
		MOUNTING:       { name: 'Mounting', component: MountingCondition },
		MYTHICMOB_TYPE: { name: 'MythicMob Type', component: MythicMobTypeCondition },
		NAME:           { name: 'Name', component: NameCondition },
		OFFHAND:        { name: 'Offhand', component: OffhandCondition },
		PERMISSION:     { name: 'Permission', component: PermissionCondition },
		POTION:         { name: 'Potion', component: PotionCondition },
		SKILL_LEVEL:    { name: 'Skill Level', component: SkillLevelCondition },
		SLOT:           { name: 'Slot', component: SlotCondition },
		STATUS:         { name: 'Status', component: StatusCondition },
		TIME:           { name: 'Time', component: TimeCondition },
		TOOL:           { name: 'Tool', component: ToolCondition },
		VALUE:          { name: 'Value', component: ValueCondition },
		VALUETEXT:      { name: 'Value Text', component: ValueTextCondition },
		WATER:          { name: 'Water', component: WaterCondition },
		WEATHER:        { name: 'Weather', component: WeatherCondition },
		WORLD:          { name: 'World', component: WorldCondition }
	});
	mechanics.set({
		ARMOR:               { name: 'Armor', component: ArmorMechanic },
		ARMOR_STAND:         { name: 'Armor Stand', component: ArmorStandMechanic },
		ARMOR_STAND_POSE:    { name: 'Armor Stand Pose', component: ArmorStandPoseMechanic },
		ARMOR_STAND_REMOVE:  { name: 'Armor Stand Remove', component: ArmorStandRemoveMechanic },
		ATTRIBUTE:           { name: 'Attribute', component: AttributeMechanic },
		BLOCK:               { name: 'Block', component: BlockMechanic },
		BUFF:                { name: 'Buff', component: BuffMechanic },
		CANCEL:              { name: 'Cancel', component: CancelMechanic },
		CHANNEL:             { name: 'Channel', component: ChannelMechanic },
		CLEANSE:             { name: 'Cleanse', component: CleanseMechanic },
		COMMAND:             { name: 'Command', component: CommandMechanic },
		COOLDOWN:            { name: 'Cooldown', component: CooldownMechanic },
		DAMAGE:              { name: 'Damage', component: DamageMechanic },
		DAMAGE_BUFF:         { name: 'Damage Buff', component: DamageBuffMechanic },
		DAMAGE_LORE:         { name: 'Damage Lore', component: DamageLoreMechanic },
		DEFENSE_BUFF:        { name: 'Defense Buff', component: DefenseBuffMechanic },
		DELAY:               { name: 'Delay', component: DelayMechanic },
		DISGUISE:            { name: 'Disguise', component: DisguiseMechanic },
		DURABILITY:          { name: 'Durability', component: DurabilityMechanic },
		EXPERIENCE:          { name: 'Experience', component: ExperienceMechanic },
		EXPLOSION:           { name: 'Explosion', component: ExplosionMechanic },
		FIRE:                { name: 'Fire', component: FireMechanic },
		FOOD:                { name: 'Food', component: FoodMechanic },
		FORGET_TARGETS:      { name: 'Forget Targets', component: ForgetTargetsMechanic },
		HEAL:                { name: 'Heal', component: HealMechanic },
		HEALTH_SET:          { name: 'Health Set', component: HealthSetMechanic },
		HELD_ITEM:           { name: 'Held Item', component: HeldItemMechanic },
		IMMUNITY:            { name: 'Immunity', component: ImmunityMechanic },
		INTERRUPT:           { name: 'Interrupt', component: InterruptMechanic },
		INVISIBILITY:        { name: 'Invisibility', component: InvisibilityMechanic },
		ITEM:                { name: 'Item', component: ItemMechanic },
		ITEM_DROP:           { name: 'Item Drop', component: ItemDropMechanic },
		ITEM_PROJECTILE:     { name: 'Item Projectile', component: ItemProjectileMechanic },
		ITEM_REMOVE:         { name: 'Item Remove', component: ItemRemoveMechanic },
		LAUNCH:              { name: 'Launch', component: LaunchMechanic },
		LIGHTNING:           { name: 'Lightning', component: LightningMechanic },
		MANA:                { name: 'Mana', component: ManaMechanic },
		MESSAGE:             { name: 'Message', component: MessageMechanic },
		MINE:                { name: 'Mine', component: MineMechanic },
		MONEY:               { name: 'Money', component: MoneyMechanic },
		MOUNT:               { name: 'Mount', component: MountMechanic },
		PARTICLE:            { name: 'Particle', component: ParticleMechanic },
		PARTICLE_ANIMATION:  { name: 'Particle Animation', component: ParticleAnimationMechanic },
		PARTICLE_EFFECT:     { name: 'Particle Effect', component: ParticleEffectMechanic },
		CANCEL_EFFECT:       { name: 'Cancel Effect', component: CancelEffectMechanic },
		PARTICLE_PROJECTILE: { name: 'Particle Projectile', component: ParticleProjectileMechanic },
		PASSIVE:             { name: 'Passive', component: PassiveMechanic },
		PERMISSION:          { name: 'Permission', component: PermissionMechanic },
		POTION:              { name: 'Potion', component: PotionMechanic },
		POTION_PROJECTILE:   { name: 'Potion Projectile', component: PotionProjectileMechanic },
		PROJECTILE:          { name: 'Projectile', component: ProjectileMechanic },
		PURGE:               { name: 'Purge', component: PurgeMechanic },
		PUSH:                { name: 'Push', component: PushMechanic },
		REMEMBER_TARGETS:    { name: 'Remember Targets', component: RememberTargetsMechanic },
		REPEAT:              { name: 'Repeat', component: RepeatMechanic },
		SIGNAL_EMIT:         { name: 'Signal Emit', component: SignalEmitMechanic },
		SKILL_CAST:          { name: 'Skill Cast', component: SkillCastMechanic },
		SOUND:               { name: 'Sound', component: SoundMechanic },
		STAT:                { name: 'Stat', component: StatMechanic },
		STATUS:              { name: 'Status', component: StatusMechanic },
		SUMMON:              { name: 'Summon', component: SummonMechanic },
		TAUNT:               { name: 'Taunt', component: TauntMechanic },
		THROW:               { name: 'Throw', component: ThrowMechanic },
		TRIGGER:             { name: 'Trigger', component: TriggerMechanic },
		WOLF:                { name: 'Wolf', component: WolfMechanic },

		FLAG:        { name: 'Flag', component: FlagMechanic, section: 'Flag' },
		FLAG_CLEAR:  { name: 'Flag Clear', component: FlagClearMechanic, section: 'Flag' },
		FLAG_TOGGLE: { name: 'Flag Toggle', component: FlagToggleMechanic, section: 'Flag' },

		VALUE_ADD:         { name: 'Value Add', component: ValueAddMechanic, section: 'Value' },
		VALUE_ATTRIBUTE:   { name: 'Value Attribute', component: ValueAttributeMechanic, section: 'Value' },
		VALUE_COPY:        { name: 'Value Copy', component: ValueCopyMechanic, section: 'Value' },
		VALUE_DISTANCE:    { name: 'Value Distance', component: ValueDistanceMechanic, section: 'Value' },
		VALUE_HEALTH:      { name: 'Value Health', component: ValueHealthMechanic, section: 'Value' },
		VALUE_LOAD:        { name: 'Value Load', component: ValueLoadMechanic, section: 'Value' },
		VALUE_LOCATION:    { name: 'Value Location', component: ValueLocationMechanic, section: 'Value' },
		VALUE_LORE:        { name: 'Value Lore', component: ValueLoreMechanic, section: 'Value' },
		VALUE_LORE_SLOT:   { name: 'Value Lore Slot', component: ValueLoreSlotMechanic, section: 'Value' },
		VALUE_MANA:        { name: 'Value Mana', component: ValueManaMechanic, section: 'Value' },
		VALUE_MULTIPLY:    { name: 'Value Multiply', component: ValueMultiplyMechanic, section: 'Value' },
		VALUE_PLACEHOLDER: { name: 'Value Placeholder', component: ValuePlaceholderMechanic, section: 'Value' },
		VALUE_RANDOM:      { name: 'Value Random', component: ValueRandomMechanic, section: 'Value' },
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
