import type { Icon, ProClassData, Serializable } from "./types";
import type { ProSkill } from "./proskill";
import { YAMLObject } from "./yaml";
import { ProAttribute } from "./proattribute";
import { getSkill } from "../data/store";
import { toEditorCase, toProperCase } from "./api";

export class ProClass implements Serializable {
  isClass = true;
  public key = {};
  name: string;
  prefix = "";
  group = "class";
  manaName = "&2Mana";
  maxLevel = 40;
  parentStr = "";
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
    else this.prefix = "&6" + this.name;
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

  public updateAttributes = (attribs: string[]) => {
    const included: string[] = [];
    this.attributes = this.attributes.filter(a => {
      if (attribs?.includes(a.name)) {
        included.push(a.name);
        return true;
      }
      return false;
    });

    attribs = attribs.filter(a => !included.includes(a));

    for (const attrib of attribs) {
      this.attributes.push(new ProAttribute(attrib, 0, 0));
    }
  };

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
    data.put("tree", this.skillTree.toUpperCase().replace(/ /g, "_"));
    data.put("blacklist", this.unusableItems);
    data.put("skills", this.skills.map(s => s.name));
    data.put("icon", this.icon.material);
    data.put("icon-data", this.icon.customModelData);
    data.put("icon-lore", this.icon.lore);
    data.put("exp-source", this.expSources);

    yaml.data = data.data;
    return yaml;
  };

  public updateParent = (classes: ProClass[]) => {
    if (!this.parentStr) return;
    this.parent = classes.find(c => c.name === this.parentStr);
  };

  public load = (yaml: YAMLObject) => {
    this.name = yaml.get("name", this.name);
    this.actionBar = yaml.get("action-bar", this.actionBar);
    this.prefix = yaml.get("prefix", this.prefix);
    this.group = yaml.get("group", this.group);
    this.mana = yaml.get("mana", this.mana);
    this.maxLevel = yaml.get("max-level", this.maxLevel);
    this.parentStr = yaml.get("parent", this.parentStr);
    // this.parent = yaml.get<string, ProClass>("parent", this.parent, getClass);
    this.permission = yaml.get("permission", this.permission);
    this.attributes = yaml.get<YAMLObject, ProAttribute[]>("attributes", this.attributes,
      (obj: YAMLObject) => {
        const attributes: ProAttribute[] = [];

        const healthBase: number = obj.get<number, number>("health-base", 20);
        const healthMod: number = obj.get<number, number>("health-scale", 1);
        const manaBase: number = obj.get<number, number>("mana-base", 20);
        const manaMod: number = obj.get<number, number>("mana-scale", 1);
        this.health = new ProAttribute("health", healthBase, healthMod);
        this.mana = new ProAttribute("mana", manaBase, manaMod);
        obj.remove("health-base");
        obj.remove("health-scale");
        obj.remove("mana-base");
        obj.remove("mana-scale");

        const map: { [key: string]: ProAttribute } = {};
        Object.keys(obj.data).forEach(key => {
          const split = key.split("-");
          const name = toProperCase(split[0]);
          if (!map[name]) map[name] = new ProAttribute(name, 0, 0);

          const attr = map[name];
          if (split[1] == "base") attr.base = Number.parseFloat(obj.data[key]);
          if (split[1] == "scale") attr.scale = Number.parseFloat(obj.data[key]);
        });

        attributes.push(...Object.values(map));

        return attributes;
      });
    this.manaRegen = yaml.get("mana-regen", this.manaRegen);
    this.skillTree = yaml.get("tree", this.skillTree, toProperCase);
    this.unusableItems = yaml.get("blacklist", this.unusableItems);
    this.skills = yaml.get<string[], ProSkill[]>("skills", this.skills,
      (list: string[]) => list.map(s => getSkill(s)).filter(s => !!s));
    this.icon.material = yaml.get<string, string>("icon", this.icon.material, toEditorCase);
    this.icon.customModelData = yaml.get("icon-data", this.icon.customModelData);
    this.icon.lore = yaml.get("icon-lore", this.icon.lore);
    this.expSources = yaml.get("exp-source", this.expSources);
  };
}