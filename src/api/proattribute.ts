export class ProAttribute {
  name: string;
  base: number;
  scale: number;

  public constructor(name: string, base: number, scale: number) {
    this.name = name;
    this.base = base;
    this.scale = scale;
  }

  public toYaml = (spaces = "") => {
    return spaces + this.name.toLowerCase() + "-base: " + this.base + "\n"
      + spaces + this.name.toLowerCase() + "-scale: " + this.scale + "\n";
  };
}