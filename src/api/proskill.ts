import type { Icon, ProSkillData, Serializable, Trigger } from "./types";
import { YAMLObject } from "./yaml";

export class ProSkill implements Serializable {
  isSkill = true;
  public key = {};
  name: string;
  type: string;
  maxLevel: number;
  skillReq?: ProSkill;
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
  indicator: "2D" | "3D" | "None";
  icon: Icon;
  incompatible?: ProSkill[];
  triggers: Trigger[];

  constructor(data: ProSkillData) {
    this.name = data.name;
    this.type = data.type;
    this.maxLevel = data.maxLevel;
    this.skillReq = data.skillReq;
    this.skillReqLevel = data.skillReqLevel;
    this.permission = data.permission;
    this.levelReq = data.levelReq;
    this.levelReqModifier = data.levelReqModifier;
    this.cost = data.cost;
    this.costModifier = data.costModifier;
    this.cooldown = data.cooldown;
    this.cooldownModifier = data.cooldownModifier;
    this.cooldownMessage = data.cooldownMessage;
    this.mana = data.mana;
    this.manaModifier = data.manaModifier;
    this.minSpent = data.minSpent;
    this.minSpentModifier = data.minSpentModifier;
    this.castMessage = data.castMessage;
    this.combo = data.combo;
    this.indicator = data.indicator;
    this.icon = data.icon;
    this.incompatible = data.incompatible;
    this.triggers = data.triggers;
  }

  public serializeYaml = (): YAMLObject => {
    const yaml = new YAMLObject();
    yaml.data.name = this.name;

    return yaml;
  };
}