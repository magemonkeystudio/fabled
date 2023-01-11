import type { Attribute, Icon, ProClassData, Serializable } from "./types";
import type { ProSkill } from "./proskill";
import { YAMLObject } from "./yaml";

export class ProClass implements Serializable {
  isClass = true;
  public key = {};
  name: string;
  prefix?: string;
  group = "class";
  manaName = "&2Mana";
  maxLevel = 40;
  parent?: ProClass;
  permission = false;
  expSources: string[] = [];
  health: Attribute = {
    base: 20,
    scale: 1
  };
  mana: Attribute = {
    base: 20,
    scale: 1
  };
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
    this.name = data ? data.name : "Class";
    if (data?.prefix) this.prefix = data.prefix;
    if (data?.group) this.group = data.group;
    if (data?.manaName) this.manaName = data.manaName;
    if (data?.maxLevel) this.maxLevel = data.maxLevel;
    if (data?.parent) this.parent = data.parent;
    if (data?.permission) this.permission = data.permission;
    if (data?.expSources) this.expSources = data.expSources;
    if (data?.health) this.health = data.health;
    if (data?.mana) this.mana = data.mana;
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
    data.put("health-base", this.health.base);
    data.put("health-scale", this.health.scale);


    //TODO
    data.put("skills", this.skillTree);
    data.put("attributes", this.attributes);
    data.put("mana-base", this.mana.base);
    data.put("mana-scale", this.mana.scale);
    yaml.data = data.data;

    return yaml;
  };
}