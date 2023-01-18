import type { Icon, ProSkillData, Serializable, Trigger } from "./types";
import { YAMLObject } from "./yaml";

export default class ProSkill implements Serializable {
  isSkill = true;
  public key = {};
  name: string;
  type = "Dynamic";
  maxLevel = 5;
  skillReq?: ProSkill;
  skillReqLevel?: number;
  permission = false;
  levelReq = 1;
  levelReqModifier = 0;
  cost = 1;
  costModifier = 0;
  cooldown = 0;
  cooldownModifier = 0;
  cooldownMessage = true;
  mana = 0;
  manaModifier = 0;
  minSpent = 0;
  minSpentModifier = 0;
  castMessage = "&6{player} &2has cast &6{skill}";
  combo?: string;
  indicator: "2D" | "3D" | "None" = "2D";
  icon: Icon = {
    material: "Pumpkin",
    customModelData: 0
  };
  incompatible: ProSkill[] = [];
  triggers: Trigger[] = [];

  constructor(data?: ProSkillData) {
    this.name = data ? data.name : "Skill";
    if (data?.type) this.type = data.type;
    if (data?.maxLevel) this.maxLevel = data.maxLevel;
    if (data?.skillReq) this.skillReq = data.skillReq;
    if (data?.skillReqLevel) this.skillReqLevel = data.skillReqLevel;
    if (data?.permission) this.permission = data.permission;
    if (data?.levelReq) this.levelReq = data.levelReq;
    if (data?.levelReqModifier) this.levelReqModifier = data.levelReqModifier;
    if (data?.cost) this.cost = data.cost;
    if (data?.costModifier) this.costModifier = data.costModifier;
    if (data?.cooldown) this.cooldown = data.cooldown;
    if (data?.cooldownModifier) this.cooldownModifier = data.cooldownModifier;
    if (data?.cooldownMessage) this.cooldownMessage = data.cooldownMessage;
    if (data?.mana) this.mana = data.mana;
    if (data?.manaModifier) this.manaModifier = data.manaModifier;
    if (data?.minSpent) this.minSpent = data.minSpent;
    if (data?.minSpentModifier) this.minSpentModifier = data.minSpentModifier;
    if (data?.castMessage) this.castMessage = data.castMessage;
    if (data?.combo) this.combo = data.combo;
    if (data?.indicator) this.indicator = data.indicator;
    if (data?.icon) this.icon = data.icon;
    if (data?.incompatible) this.incompatible = data.incompatible;
    if (data?.triggers) this.triggers = data.triggers;
  }

  public serializeYaml = (): YAMLObject => {
    const yaml = new YAMLObject();
    yaml.data.name = this.name;

    return yaml;
  };
}