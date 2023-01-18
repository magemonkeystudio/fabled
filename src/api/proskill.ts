import type { Icon, ProSkillData, Serializable, Trigger } from "./types";
import { YAMLObject } from "./yaml";
import { ProAttribute } from "./proattribute";

export default class ProSkill implements Serializable {
  isSkill = true;
  public key = {};
  name: string;
  type = "Dynamic";
  maxLevel = 5;
  skillReq?: ProSkill;
  skillReqLevel = 0;
  permission = false;
  levelReq: ProAttribute = new ProAttribute("level", 1, 0);
  cost: ProAttribute = new ProAttribute("cost", 1, 0);
  cooldown: ProAttribute = new ProAttribute("cooldown", 0, 0);
  cooldownMessage = true;
  mana: ProAttribute = new ProAttribute("mana", 0, 0);
  minSpent: ProAttribute = new ProAttribute("points-spent-req", 0, 0);
  castMessage = "&6{player} &2has cast &6{skill}";
  combo = "";
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
    if (data?.cost) this.cost = data.cost;
    if (data?.cooldown) this.cooldown = data.cooldown;
    if (data?.cooldownMessage) this.cooldownMessage = data.cooldownMessage;
    if (data?.mana) this.mana = data.mana;
    if (data?.minSpent) this.minSpent = data.minSpent;
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