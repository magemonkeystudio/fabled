import ProSkill from "./proskill";
import ProClass from "./proclass";
import { YAMLObject } from "./yaml";
import { ProAttribute } from "./proattribute";
import ProTrigger from "./components/triggers";
import ProComponent from "$api/components/procomponent";
import ComponentOption from "./options/options";

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
  levelReq?: ProAttribute;
  cost?: ProAttribute;
  cooldown?: ProAttribute;
  cooldownMessage?: boolean;
  mana?: ProAttribute;
  minSpent?: ProAttribute;
  castMessage?: string;
  combo?: string;
  indicator?: "2D" | "3D" | "None";
  icon?: Icon;
  incompatible?: ProSkill[];

  triggers?: ProTrigger[];
}

export interface Icon {
  material: string;
  customModelData: number;
  lore?: string[];
}

export interface TriggerData {
  name: string;
  mana?: boolean;
  cooldown?: boolean;
  data?: ComponentOption[];
  components?: ProComponent[];
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