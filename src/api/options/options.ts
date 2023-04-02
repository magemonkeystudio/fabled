import type { YAMLObject } from "$api/yaml";

export default interface ComponentOption extends Cloneable<ComponentOption> {
  getData: () => { [key: string]: any };
  deserialize: (yaml: YAMLObject) => void;
}

interface Cloneable<T> {
  clone: () => T;
}