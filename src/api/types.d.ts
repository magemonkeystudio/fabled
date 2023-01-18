import ProSkill from "./proskill";
import ProClass from "./proclass";
import { YAMLObject } from "./yaml";
import { ProAttribute } from "./proattribute";

export interface ProClassData {
  name: string;
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
  skillTree?: "Requirement" | "Basic Horizontal" | "Basic Vertical" | "Level Horizontal" | "Level Vertical" | "Flood";
  skills?: ProSkill[];
  icon?: Icon;
  unusableItems?: string[];
  actionBar?: string;
}

export interface ProSkillData {
  name: string;
  type?: string;
  maxLevel?: number;
  skillReq?: ProSkill;
  skillReqLevel?: number;
  permission?: boolean;
  levelReq?: number;
  levelReqModifier?: number;
  cost?: number;
  costModifier?: number;
  cooldown?: number;
  cooldownModifier?: number;
  cooldownMessage?: boolean;
  mana?: number;
  manaModifier?: number;
  minSpent?: number;
  minSpentModifier?: number;
  castMessage?: string;
  combo?: string;
  indicator?: "2D" | "3D" | "None";
  icon?: Icon;
  incompatible?: ProSkill[];

  triggers?: Trigger[];
}

export interface Icon {
  material: string;
  customModelData: number;
  lore?: string[];
}

export interface Trigger {
  name: string;
  components: SkillComponent[];
}

export interface SkillComponent {
  name: string;
  components: SkillComponent[];
}

export interface VersionData {
  MATERIALS: string[];
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
}

export abstract class Serializable {
  public abstract serializeYaml: () => YAMLObject;
}