import type { TriggerData }     from "../types";
import ProComponent             from "./procomponent";
import { YAMLObject }           from "../yaml";
import type { ComponentOption } from "../options/options";
import Registry                 from "$api/components/registry";
import { get }                  from "svelte/store";

export default class ProTrigger extends ProComponent {
  public mana     = false;
  public cooldown = false;

  protected constructor(data: TriggerData, isDeprecated = false) {
    super("trigger", data);
    this.mana     = data.mana || false;
    this.cooldown = data.cooldown || false;
    super.isDeprecated = isDeprecated;
  }

  public clone = (): ProTrigger => {
    return new ProTrigger({
      name:       this.name,
      components: [...get(this.components)],
      mana:       this.mana,
      cooldown:   this.cooldown,
      data:       [...this.data]
    });
  };

  public override toYamlObj(): YAMLObject {
    const parent: YAMLObject = super.toYamlObj();
    const data               = this.getData();
    if (data.getKeys().length > 0) parent.put("data", data);
    const comps = get(this.components);
    if (comps.length > 0)
      parent.put("children", comps);

    return parent;
  };

  public override getData(): YAMLObject {
    const data = new YAMLObject("data");
    if (this.name != "Cast" && this.name != "Initialize" && this.name != "Cleanup") {
      data.put("mana", this.mana);
      data.put("cooldown", this.cooldown);
    }

    this.data
      .filter(opt => opt.meetsRequirements(this))
      .forEach((opt: ComponentOption) => {
        const optData: { [key: string]: string } = opt.getData();
        Object.keys(optData).forEach(key => data.put(key, optData[key]));
      });

    return data;
  }

  public override getRawData(): YAMLObject {
    const data = new YAMLObject("data");
    if (this.name != "Cast" && this.name != "Initialize" && this.name != "Cleanup") {
      data.put("mana", this.mana);
      data.put("cooldown", this.cooldown);
    }

    this.data
      .forEach((opt: ComponentOption) => {
        const optData: { [key: string]: string } = opt.getData();
        Object.keys(optData).forEach(key => data.put(key, optData[key]));
      });

    return data;
  }

  public override deserialize(yaml: YAMLObject) {
    super.deserialize(yaml);
    const data = yaml.get<YAMLObject, YAMLObject>("data");
    if (data) {
      this.mana     = data.get("mana", false);
      this.cooldown = data.get("cooldown", false);

      this.data.forEach((opt: ComponentOption) => opt.deserialize(data));
    }

    this.setComponents(yaml.get<YAMLObject, ProComponent[]>("children", [], (obj) => Registry.deserializeComponents(obj)));
  }

  public static new = (): ProTrigger => new ProTrigger({ name: "null" });
}