import type { Icon, ProClassData, Serializable } from "./types";
import type { ProSkill } from "./proskill";
import { YAMLObject } from "./yaml";

export class ProClass implements Serializable {
  isClass = true;
  public key = {};
  name = "Class";
  prefix?: string;
  group = "class";
  manaName = "&2Mana";
  maxLevel = 40;
  parent?: ProClass;
  permission = false;
  expSources: string[] = [];
  health = 20;
  healthModifier = 0;
  mana = 20;
  manaModifier = 0;
  manaRegen = 1;
  attributes: {
    [key: string]: {
      base: number;
      modifier: number;
    }
  } = {};
  skillTree: "Requirement" | "Basic Horizontal" | "Basic Vertical" | "Level Horizontal" | "Level Vertical" | "Flood" = "Requirement";
  skills: ProSkill[] = [];
  icon: Icon = {
    material: "Pumpkin",
    customModelData: 0
  };
  unusableItems: string[] = [];
  actionBar = "";

  constructor(data?: ProClassData) {
    if (data?.name) this.name = data.name;
    if (data?.prefix) this.prefix = data.prefix;
    if (data?.group) this.group = data.group;
    if (data?.manaName) this.manaName = data.manaName;
    if (data?.maxLevel) this.maxLevel = data.maxLevel;
    if (data?.parent) this.parent = data.parent;
    if (data?.permission) this.permission = data.permission;
    if (data?.expSources) this.expSources = data.expSources;
    if (data?.health) this.health = data.health;
    if (data?.healthModifier) this.healthModifier = data.healthModifier;
    if (data?.mana) this.mana = data.mana;
    if (data?.manaModifier) this.manaModifier = data.manaModifier;
    if (data?.manaRegen) this.manaRegen = data.manaRegen;
    if (data?.attributes) this.attributes = data.attributes;
    if (data?.skillTree) this.skillTree = data.skillTree;
    if (data?.skills) this.skills = data.skills;
    if (data?.icon) this.icon = data.icon;
    if (data?.unusableItems) this.unusableItems = data.unusableItems;
    if (data?.actionBar) this.actionBar = data.actionBar;
  }

  public serializeYaml = (): YAMLObject => {
    const yaml = new YAMLObject(this.name);
    const data = new YAMLObject();
    data.put("name", this.name);
    data.put("prefix", this.prefix);
    data.put("group", this.group);
    data.put("manaName", this.manaName);
    data.put("maxLevel", this.maxLevel);
    if (this.parent) data.put("parent", this.parent.name);
    data.put("permission", this.permission);
    data.put("expSources", this.expSources);
    data.put("mana-regen", this.manaRegen);
    data.put("tree", this.skillTree);
    data.put("blacklist", this.unusableItems);
    data.put("icon", this.icon.material);
    data.put("icon-data", this.icon.customModelData);
    data.put("icon-lore", this.icon.lore);
    data.put("action-bar", this.actionBar);


    //TODO
    data.put("skills", this.skillTree);
    data.put("attributes", this.attributes);
    data.put("health-base", this.health);
    data.put("health-scale", this.healthModifier);
    data.put("mana-base", this.mana);
    data.put("mana-scale", this.manaModifier);
    yaml.data = data.data;

    return yaml;
  };
}