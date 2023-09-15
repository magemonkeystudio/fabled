import type { ComponentOption } from '$api/options/options';
import { YAMLObject }           from '../yaml';
import { Constructable }        from '$api/components/constructable';
import type { ComponentData }   from '$api/types';
import type { Writable }        from 'svelte/store';
import { get, writable }        from 'svelte/store';
import AttributeSelect          from '$api/options/attributeselect';
import BlockSelect              from '$api/options/blockselect';

export default abstract class ProComponent extends Constructable {
	public type: 'trigger' | 'condition' | 'mechanic' | 'target';
	public name: string;
	public description: string;
	public comment: string;
	public components: Writable<ProComponent[]> = writable([]);
	public data: ComponentOption[]              = [];
	public preview: ComponentOption[]           = [];
	public summaryItems: string[]               = [];
	public isParent                             = true;
	public isDeprecated                         = false;
	public id                                   = {};
	public _defaultOpen                         = false;
	public parent: ProComponent | undefined;

	protected constructor(type: 'trigger' | 'condition' | 'mechanic' | 'target', data: ComponentData) {
		super();
		this.type         = type;
		this.name         = data.name;
		this.description  = data.description ?? '';
		this.summaryItems = data.summaryItems ?? [];
		this.comment      = data.comment ?? '';
		this.setComponents(data.components || []);
		this.data    = data.data || [];
		this.preview = data.preview || [];
	}

	public getValue(key: string): any {
		const comp: ComponentOption | undefined = this.data?.find(opt => opt.key == key);
		if (!comp || !comp.meetsRequirements(this)) return '';

		return comp.getSummary();
	}

	public setComponents = (comps: ProComponent[]) => {
		comps.forEach(comp => comp.parent = this);
		this.components.set([...comps]);
	};

	public contains = (comp: ProComponent): boolean => {
		const comps = get(this.components);
		if (comps.includes(comp)) return true;

		for (const component of comps) {
			if (component.contains(comp)) return true;
		}

		return false;
	};

	public addComponent = (comp: ProComponent, index = -1) => {
		const comps = get(this.components);
		if (index == -1)
			comps.push(comp);
		else {
			comps.splice(index, 0, comp);
		}
		this.setComponents(comps);
	};

	public removeComponent = (comp: ProComponent) => {
		const comps = get(this.components);
		if (comps.includes(comp)) {
			comps.splice(comps.indexOf(comp), 1);
			comp.parent = undefined;
			this.setComponents(comps);
			return;
		}

		for (const component of comps) {
			if (component.contains(comp))
				component.removeComponent(comp);
		}
	};

	public defaultOpen = () => {
		this._defaultOpen = true;
		return this;
	};

	public toYamlObj(): YAMLObject {
		const data = new YAMLObject(this.name);
		data.put('type', this.type);
		data.put('comment', this.comment);

		const previewData = new YAMLObject('preview');
		this.preview
			.forEach((opt: ComponentOption) => {
				const optData: { [key: string]: string } = opt.getData();
				Object.keys(optData).forEach(key => previewData.put(key, optData[key]));
			});

		if (previewData.getKeys().length > 0)
			data.put('preview', previewData);

		return data;
	};

	public abstract getData(): YAMLObject;

	public abstract getRawData(): YAMLObject;

	public deserialize(yaml: YAMLObject): void {
		const preview = yaml.get<YAMLObject, YAMLObject>('preview');
		if (preview) this.preview.forEach((opt: ComponentOption) => opt.deserialize(preview));

		this.comment = yaml.get<string, string>('comment', '').replaceAll('\\n', '\n');
	}
}