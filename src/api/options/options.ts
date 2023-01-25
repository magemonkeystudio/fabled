export default abstract class ComponentOption implements Cloneable<ComponentOption> {
  abstract clone: () => ComponentOption;
}

interface Cloneable<T> {
  clone: () => T;
}