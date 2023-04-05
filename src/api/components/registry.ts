import { YAMLObject }        from "$api/yaml";
import type ProComponent     from "$api/components/procomponent";
import type ProTrigger       from "$api/components/triggers";
import type ProTarget        from "$api/components/targets";
import type ProCondition     from "$api/components/conditions";
import type ProMechanic      from "$api/components/mechanics";
import type { Writable }     from "svelte/store";
import { get, writable }     from "svelte/store";

export default class Registry {
  public static triggers:
                  Writable<{ [key: string]: { name: string, component: typeof ProTrigger } }>   = writable({});
  public static targets:
                  Writable<{ [key: string]: { name: string, component: typeof ProTarget } }>    = writable({});
  public static conditions:
                  Writable<{ [key: string]: { name: string, component: typeof ProCondition } }> = writable({});
  public static mechanics:
                  Writable<{ [key: string]: { name: string, component: typeof ProMechanic } }>  = writable({});
  public static initialized: Writable<boolean>                                                  = writable(false);

  public static getTriggerByName = (name: string): typeof ProTrigger | undefined => {
    return Object.values(get(Registry.triggers))
      .find(trig => trig.name.toLowerCase() === name.toLowerCase())?.component;
  };

  public static getTargetByName = (name: string): typeof ProTarget | undefined => {
    return Object.values(get(Registry.targets))
      .find(target => target.name.toLowerCase() === name.toLowerCase())?.component;
  };

  public static getConditionByName = (name: string): typeof ProCondition | undefined => {
    return Object.values(get(Registry.conditions))
      .find(condition => condition.name.toLowerCase() === name.toLowerCase())?.component;
  };

  public static getMechanicByName = (name: string): typeof ProMechanic | undefined => {
    return Object.values(get(Registry.mechanics))
      .find(mechanic => mechanic.name.toLowerCase() === name.toLowerCase())?.component;
  };

  public static deserializeComponents = (yaml: YAMLObject): ProComponent[] => {
    if (!yaml || !(yaml instanceof YAMLObject)) return [];
    const comps: ProComponent[] = [];

    const keys: string[] = yaml.getKeys();
    for (const key of keys) {
      let comp: ProComponent | undefined = undefined;
      const data                         = yaml.get<YAMLObject, YAMLObject>(key);
      const type                         = data.get("type");

      if (type === "trigger") {
        const trigger: typeof ProTrigger | undefined = Registry.getTriggerByName(key.split("-")[0]);
        if (trigger) {
          comp = trigger.new();
        }
      } else if (type === "target") {
        const target: typeof ProTarget | undefined = Registry.getTargetByName(key.split("-")[0]);
        if (target) {
          comp = target.new();
        }
      } else if (type === "condition") {
        const condition: typeof ProCondition | undefined = Registry.getConditionByName(key.split("-")[0]);
        if (condition) {
          comp = condition.new();
        }
      } else if (type === "mechanic") {
        const mechanic: typeof ProMechanic | undefined = Registry.getMechanicByName(key.split("-")[0]);
        if (mechanic) {
          comp = mechanic.new();
        }
      }

      if (comp) {
        comp.deserialize(data);
        comps.push(comp);
      }
    }

    return comps;
  };
}