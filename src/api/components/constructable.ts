export abstract class Constructable {
	static new = (): any => {
		throw new Error('\'new\' method not implemented');
	};
}