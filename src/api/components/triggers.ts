import type { TriggerData, Unknown, YamlComponent } from '../types';
import ProComponent                                 from './procomponent';
import type { ComponentOption }                     from '../options/options';
import Registry                                     from '$api/components/registry';
import { get }                                      from 'svelte/store';
import { parseBool }                                from '$api/api';

export default class ProTrigger extends ProComponent {
	public mana     = false;
	public cooldown = false;

	protected constructor(data: TriggerData, isDeprecated = false) {
		super('trigger', data, isDeprecated);
		this.mana     = data.mana || false;
		this.cooldown = data.cooldown || false;
	}

	public clone = (): ProTrigger => {
		return new ProTrigger({
			name:         this.name,
			components:   [...get(this.components)],
			mana:         this.mana,
			cooldown:     this.cooldown,
			data:         [...this.data],
			preview:      [...this.preview],
			comment:      this.comment,
			description:  this.description,
			summaryItems: [...this.summaryItems]
		});
	};

	public override getData(raw = false): Unknown {
		const data: Unknown = {};
		if (this.name != 'Cast' && this.name != 'Initialize' && this.name != 'Cleanup') {
			data.mana     = this.mana;
			data.cooldown = this.cooldown;
		}

		this.data
			.filter(opt => raw || opt.meetsRequirements(this))
			.forEach((opt: ComponentOption) => {
				const optData: { [key: string]: unknown } = opt.getData();
				Object.keys(optData).forEach(key => data[key] = optData[key]);
			});

		return data;
	}

	public override deserialize(yaml: YamlComponent) {
		super.deserialize(yaml);
		const data = yaml.data;
		if (data) {
			this.mana     = parseBool(<boolean | string>data.mana);
			this.cooldown = parseBool(<boolean | string>data.cooldown);

			this.data.forEach((opt: ComponentOption) => opt.deserialize(data));
		}

		if (yaml.children && Object.keys(yaml.children).length > 0) {
			this.setComponents(Registry.deserializeComponents(yaml.children));
		}
	}

	public static new = (): ProTrigger => new ProTrigger({ name: 'null' });
}