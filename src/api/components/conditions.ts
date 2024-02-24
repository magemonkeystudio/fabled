import ProComponent                                   from './procomponent';
import type { ComponentOption }                       from '../options/options';
import type { ComponentData, Unknown, YamlComponent } from '$api/types';
import Registry                                       from '$api/components/registry';

export default class ProCondition extends ProComponent {
	iconKey = '';

	public constructor(data: ComponentData, isDeprecated = false) {
		super('condition', data, isDeprecated);
	}

	public override getData(raw = false): Unknown {
		const data: Unknown = {};
		data['icon-key']    = this.iconKey;

		this.data
			.filter(opt => raw || opt.meetsRequirements(this))
			.forEach((opt: ComponentOption) => {
				const optData: { [key: string]: unknown } = opt.getData();
				Object.keys(optData).forEach(key => data[key] = optData[key]);
			});

		return data;
	}

	public override deserialize(yaml: YamlComponent): void {
		super.deserialize(yaml);
		const data = yaml.data;

		this.iconKey = <string>data['icon-key'];

		if (data) this.data.forEach((opt: ComponentOption) => opt.deserialize(data));

		if (yaml.children && Object.keys(yaml.children).length > 0) {
			this.setComponents(Registry.deserializeComponents(yaml.children));
		}
	}

	public static override new = (): ProCondition => new ProCondition({ name: 'null' });
}