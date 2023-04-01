export default interface ComponentOption extends Cloneable<ComponentOption> {
  getData: () => { [key: string]: any };
}

interface Cloneable<T> {
  clone: () => T;
}