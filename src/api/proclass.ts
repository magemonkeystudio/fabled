import type { Icon, ProClassData, Serializable } from "./types";
import type { ProSkill } from "./proskill";
import { YAMLObject } from "./yaml";
import { ProAttribute } from "./proattribute";

export class ProClass implements Serializable {
  isClass = true;
  public key = {};
  name: string;
  prefix = "";
  group = "class";
  manaName = "&2Mana";
  maxLevel = 40;
  parent?: ProClass;
  permission = false;
  expSources = 273;
  manaRegen = 1;
  health: ProAttribute = new ProAttribute("health", 20, 1);
  mana: ProAttribute = new ProAttribute("mana", 20, 1);
  attributes: ProAttribute[] = [];
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
    data.put("action-bar", this.actionBar);
    data.put("prefix", this.prefix);
    data.put("group", this.group);
    data.put("mana", this.manaName);
    data.put("max-level", this.maxLevel);
    data.put("parent", this.parent?.name || "");
    data.put("permission", this.permission);
    data.put("attributes", [this.health, this.mana, ...this.attributes]);
    data.put("mana-regen", this.manaRegen);
    data.put("tree", this.skillTree.toUpperCase());
    data.put("blacklist", this.unusableItems);
    data.put("skills", this.skills.map(s => s.name));
    data.put("icon", this.icon.material);
    data.put("icon-data", this.icon.customModelData);
    data.put("icon-lore", this.icon.lore);
    data.put("exp-source", this.expSources);

    yaml.data = data.data;
    return yaml;
  };
}