export default abstract class ComponentOption implements Cloneable {
  abstract clone: <T>() => T;
}

interface Cloneable {
  clone: <T>() => T;
}