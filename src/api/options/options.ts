import type { YAMLObject } from "$api/yaml";
import type ProComponent   from "$api/components/procomponent";

export interface ComponentOption extends Cloneable<ComponentOption> {
  getData: () => { [key: string]: any };
  deserialize: (yaml: YAMLObject) => void;
  setTooltip: (tooltip: string) => this;
  meetsRequirements: (comp: ProComponent) => boolean;
}

interface Cloneable<T> {
  clone: () => T;
}

export abstract class Requirements {
  private targetKey: string | undefined;
  private targetValue: any[] = [];
  public requireValue        = (key: string, value: any[]): this => {
    this.targetKey   = key;
    this.targetValue = value;
    return this;
  };

  meetsRequirements = (comp: ProComponent): boolean => {
    if (!this.targetKey) return true;

    return this.targetValue.includes(comp.getRawData().get(this.targetKey));
  };
}
