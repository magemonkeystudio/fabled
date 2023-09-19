import type { YAMLObject } from "$api/yaml";
import type ProComponent   from "$api/components/procomponent";

export interface ComponentOption extends Cloneable<ComponentOption> {
  key: string;
  getData: () => { [key: string]: any };
  deserialize: (yaml: YAMLObject) => void;
  setTooltip: (tooltip: string) => this;
  meetsRequirements: (comp: ProComponent) => boolean;
  meetsPreviewRequirements: (comp: ProComponent) => boolean;
  getSummary: () => string;
}

interface Cloneable<T> {
  clone: () => T;
}

export abstract class Requirements {
  private requirements: { [key: string]: any[] } = {};
  public requireValue        = (key: string, value: any[]): this => {
    this.requirements[key] = value;
    return this;
  };

  meetsRequirements = (comp: ProComponent): boolean => {
		for (const key in this.requirements) {
      if (!this.requirements[key].includes(comp.getRawData().get(key))) return false;
		}
    return true;
  };

  meetsPreviewRequirements = (comp: ProComponent): boolean => {
		for (const key in this.requirements) {
      if (!this.requirements[key].includes(comp.getRawPreviewData().get(key))) return false;
		}
    return true;
  };
}
