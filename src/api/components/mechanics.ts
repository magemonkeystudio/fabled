import ProComponent                                   from './procomponent';
import type { ComponentOption }                       from '../options/options';
import type { ComponentData, Unknown, YamlComponent } from '$api/types';
import Registry                                       from '$api/components/registry';

export default class ProMechanic extends ProComponent {
	iconKey      = '';
	countsAsCast = true;

	public constructor(data: ComponentData, isParent = false, isDeprecated = false) {
		super('mechanic', data, isDeprecated);
		this.isParent = isParent; // This should be false unless for specific mechanics like projectiles
	}

	public override getData(raw = false): Unknown {
		const data: Unknown = {};

		data['icon-key'] = this.iconKey;
		data['counts']   = this.countsAsCast;

		this.data
			.filter(opt => raw || opt.meetsRequirements(this))
			.forEach((opt: ComponentOption) => {
				const optData: { [key: string]: unknown } = opt.getData();
				Object.keys(optData).forEach(key => data[key] = optData[key]);
			});

		return data;
	}

	deserialize(yaml: YamlComponent): void {
		super.deserialize(yaml);
		const data = yaml.data;

		if (data) this.data.forEach((opt: ComponentOption) => opt.deserialize(data));
		this.iconKey      = <string>data['icon-key'];
		this.countsAsCast = data.counts === undefined ? true : <boolean>data.counts;

		if (yaml.children && Object.keys(yaml.children).length > 0) {
			this.setComponents(Registry.deserializeComponents(yaml.children));
		}
	}
}