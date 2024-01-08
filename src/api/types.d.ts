import ProSkill         from './proskill';
import ProClass         from './proclass';
import { YAMLObject }   from './yaml';
import { ProAttribute } from './proattribute';
import ProTrigger       from './components/triggers';
import ProComponent     from '$api/components/procomponent';
import ComponentOption  from './options/options';

export interface ProClassData {
	name: string;
	location?: 'local' | 'server';
	prefix?: string;
	group?: string;
	manaName?: string;
	maxLevel?: number;
	parent?: ProClass;
	permission?: boolean;
	expSources?: number;
	health?: ProAttribute;
	mana?: ProAttribute;
	manaRegen?: number;
	attributes?: ProAttribute[];
	skillTree?: 'Custom' | 'Requirement' | 'Basic Horizontal' | 'Basic Vertical' | 'Level Horizontal' | 'Level Vertical' | 'Flood';
	skills?: ProSkill[];
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

export interface ProSkillData {
	name: string;
	location?: 'local' | 'server';
	type?: string;
	maxLevel?: number;
	skillReq?: ProSkill;
	skillReqLevel?: number;
	attributeRequirements?: ProAttribute[];
	permission?: boolean;
	levelReq?: ProAttribute;
	cost?: ProAttribute;
	cooldown?: ProAttribute;
	cooldownMessage?: boolean;
	mana?: ProAttribute;
	minSpent?: ProAttribute;
	castMessage?: string;
	combo?: string;
	indicator?: '2D' | '3D' | 'None';
	icon?: Icon;
	incompatible?: ProSkill[];

	triggers?: ProTrigger[];
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
	ANY_POTION?: string[];
	CONSUMABLE: string[];
}

export abstract class Serializable {
	public abstract serializeYaml: () => YAMLObject;
}