export class ProAttribute {
	name: string;
	base: number;
	scale: number;

	public constructor(name: string, base: number, scale: number) {
		this.name  = name;
		this.base  = base;
		this.scale = scale;
	}
}