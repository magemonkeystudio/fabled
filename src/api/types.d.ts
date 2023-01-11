import { ProSkill } from "./proskill";
import { ProClass } from "./proclass";
import { YAMLObject } from "./yaml";

export interface Attribute {
  base: number;
  scale: number;
}

export interface ProClassData {
  name: string;
  prefix?: string;
  group?: string;
  manaName?: string;
  maxLevel?: number;
  parent?: ProClass;
  permission?: boolean;
  expSources?: string[];
  health?: Attribute;
  mana?: Attribute;
  manaRegen?: number;
  attributes?: {
    [key: string]: {
      base: number;
      modifier: number;
    }
  };
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

export abstract class Serializable {
  public abstract serializeYaml: () => YAMLObject;
}