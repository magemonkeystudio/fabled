export interface SapiClass {
  name: string;
  prefix?: string;
  group: string;
  manaName: string;
  maxLevel: number;
  parent?: SapiClass;
  permission: boolean;
  expSources: string[];
  health: number;
  healthModifier: number;
  mana: number;
  manaModifier: number;
  manaRegen: number;
  attributes: {
    [key: string]: {
      base: number;
      modifier: number;
    }
  };
  skillTree: 'Requirement' | 'Basic Horizontal' | 'Basic Vertical' | 'Level Horizontal' | 'Level Vertical' | 'Flood';
  skills: Skill[];
  icon: Icon;
  usableItems: string[];
  actionBar?: string;
}

export interface Skill {
  name: string;
  type: string;
  maxLevel: number;
  skillReq?: Skill;
  skillReqLevel?: number;
  permission: boolean;
  levelReq: number;
  levelReqModifier: number;
  cost: number;
  costModifier: number;
  cooldown: number;
  cooldownModifier: number;
  cooldownMessage: boolean;
  mana: number;
  manaModifier: number;
  minSpent: number;
  minSpentModifier: number;
  castMessage: string;
  combo?: string;
  indicator: '2D' | '3D' | 'None';
  icon: Icon;
  incompatible?: Skill[];

  triggers: Trigger[];
}

export interface Icon {
  material: string;
  customModelData: number;
  lore?: string;
}

export interface Trigger {
  name: string;
  components: SkillComponent[];
}

export interface SkillComponent {
  name: string;
  components: SkillComponent[];
}
