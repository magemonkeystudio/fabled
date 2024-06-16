import { Attribute }                              from './stat';
import ProTrigger                                 from './components/triggers';
import ProComponent                               from '$api/components/procomponent';
import ComponentOption                            from './options/options';
import type { AttributeComponent, AttributeStat } from './fabled-attribute';
import type FabledClass                           from '../data/class-store';
import type FabledSkill                           from '../data/skill-store';

export interface FabledClassData {
	name: string;
	location?: 'local' | 'server';
	prefix?: string;
	group?: string;
	manaName?: string;
	maxLevel?: number;
	parent?: FabledClass;
	permission?: boolean;
	expSources?: number;
	health?: Attribute;
	mana?: Attribute;
	manaRegen?: number;
	attributes?: Attribute[];
	skillTree?: 'Custom' | 'Requirement' | 'Basic Horizontal' | 'Basic Vertical' | 'Level Horizontal' | 'Level Vertical' | 'Flood';
	skills?: FabledSkill[];
	icon?: Icon;
	unusableItems?: string[];
	actionBar?: string;
	lInverted?: boolean;
	rInverted?: boolean;
	lsInverted?: boolean;
	rsInverted?: boolean;
	pInverted?: boolean;
	qInverted?: boolean;
	fInverted?: boolean;
	lWhitelist?: string[];
	rWhitelist?: string[];
	lsWhitelist?: string[];
	rsWhitelist?: string[];
	pWhitelist?: string[];
	qWhitelist?: string[];
	fWhitelist?: string[];
}

export interface FabledSkillData {
	name: string;
	location?: 'local' | 'server';
	type?: string;
	maxLevel?: number;
	skillReq?: FabledSkill;
	skillReqLevel?: number;
	attributeRequirements?: Attribute[];
	permission?: boolean;
	levelReq?: Attribute;
	cost?: Attribute;
	cooldown?: Attribute;
	cooldownMessage?: boolean;
	mana?: Attribute;
	minSpent?: Attribute;
	castMessage?: string;
	combo?: string;
	indicator?: '2D' | '3D' | 'None';
	icon?: Icon;
	incompatible?: FabledSkill[];

	triggers?: ProTrigger[];
}

export interface ProAttributeData {
	name: string;
	location?: 'local' | 'server';
	display?: string;
	max?: number;
	cost?: Attribute;
	icon?: Icon;
	targets?: AttributeComponent[];
	conditions?: AttributeComponent[];
	mechanics?: AttributeComponent[];
	stats?: AttributeStat[];
}

export interface Icon {
	material: string;
	customModelData: number;
	lore?: string[];
}

export interface ComponentData {
	name: string;
	data?: ComponentOption[];
	preview?: ComponentOption[];
	components?: ProComponent[];
	summaryItems?: string[];
	description?: string;
	comment?: string;
}

export interface TriggerData extends ComponentData {
	mana?: boolean;
	cooldown?: boolean;
}

export interface VersionData {
	MATERIALS: string[];
	BLOCKS: string[];
	DAMAGEABLE_MATERIALS: string[];
	SOUNDS: string[];
	ENTITIES: string[];
	PARTICLES: string[];
	BIOMES: string[];
	DAMAGE_TYPES: string[];
	POTIONS: string[];
	PROJECTILES: string[];
	MOB_DISGUISES: string[];
	MISC_DISGUISES: string[];
	ENCHANTS: string[];
	ANY_POTION?: string[];
	CONSUMABLE: string[];
}

export abstract class Serializable {
	public abstract serializeYaml: () => SkillYamlData | ClassYamlData | AttributeYamlData;
}

export interface StarterInfo {
	inverted: boolean;
	whitelist: string[];
}

export interface ComboStarters {
	L?: StarterInfo;
	R?: StarterInfo;
	LS?: StarterInfo;
	RS?: StarterInfo;
	S?: StarterInfo;
	P?: StarterInfo;
	Q?: StarterInfo;
	F?: StarterInfo;
}

export interface ClassYamlData {
	name: string;
	'action-bar': string;
	prefix: string;
	group: string;
	mana: string;
	'max-level': number;
	parent: string;
	'needs-permission': boolean;
	attributes: AttributeType;
	'mana-regen': number;
	'skill-tree': string;
	blacklist: string[];
	skills: string[];
	icon: string;
	'icon-data': number;
	'icon-lore': string[];
	'exp-source': number;

	'combo-starters': ComboStarters;
}

export interface SkillYamlData {
	name: string;
	type: string;
	'max-level': number;
	'skill-req': string;
	'skill-req-lvl': number;
	'needs-permission': boolean;
	'cooldown-message': boolean;
	msg: string;
	combo: string;
	icon: string;
	'icon-data': number;
	'icon-lore': string[];

	attributes: AttributeType & {
		'level-base': number;
		'level-scale': number;
		'cost-base': number;
		'cost-scale': number;
		'cooldown-base': number;
		'cooldown-scale': number;
		'mana-base': number;
		'mana-scale': number;
		'points-spent-req-base': number;
		'points-spent-req-scale': number;
		incompatible: string[];
	};

	components: YamlComponentData;
}

export interface AttributeYamlData {
	display: string;
	max: number;
	cost_base: number;
	cost_modifier: number;
	icon: string;
	'icon-data': number;
	'icon-lore': string[];
	global: {
		target: { [key: string]: string }
		condition: { [key: string]: string }
		mechanic: { [key: string]: string }
	};
	stats: {
		[key: string]: string
	};
}

export interface YamlComponentData {
	[key: string]: YamlComponent;
}

export type AttributeType = {
	[K in string as `${K}-base` | `${K}-scale`]: number;
}

export type Unknown = {
	[key: string]: unknown;
}

export interface PreviewData extends Unknown {
	enabled: boolean;
}

export interface YamlComponent {
	type: 'trigger' | 'target' | 'condition' | 'mechanic';
	comment?: string;
	counts?: boolean;
	preview?: PreviewData;
	data: Unknown;
	children: YamlComponentData;
}

export interface MultiClassYamlData {
	loaded?: boolean;

	[key: string]: ClassYamlData;
}

export interface MultiSkillYamlData {
	loaded?: boolean;

	[key: string]: SkillYamlData;
}

export interface MultiAttributeYamlData {
	[key: string]: AttributeYamlData;
}