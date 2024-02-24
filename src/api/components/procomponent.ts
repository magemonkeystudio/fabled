import type { ComponentOption }                                                       from '$api/options/options';
import {
	Constructable
}                                                                                     from '$api/components/constructable';
import type { ComponentData, PreviewData, Unknown, YamlComponent, YamlComponentData } from '$api/types';
import type { Writable }                                                              from 'svelte/store';
import { get, writable }                                                              from 'svelte/store';

export default abstract class ProComponent extends Constructable {
	public type: 'trigger' | 'condition' | 'mechanic' | 'target';
	public name: string;
	public description: string;
	public comment: string;
	public components: Writable<ProComponent[]> = writable([]);
	public data: ComponentOption[]              = [];
	public preview: ComponentOption[]           = [];
	public enablePreview                        = false;
	public summaryItems: string[]               = [];
	public isParent                             = true;
	public isDeprecated                         = false;
	public id                                   = {};
	public _defaultOpen                         = false;
	public parent: ProComponent | undefined;

	protected constructor(type: 'trigger' | 'condition' | 'mechanic' | 'target', data: ComponentData, isDeprecated = false) {
		super();
		this.type         = type;
		this.name         = data.name;
		this.description  = data.description ?? '';
		this.summaryItems = data.summaryItems ?? [];
		this.comment      = data.comment ?? '';
		this.setComponents(data.components || []);
		this.data         = data.data || [];
		this.preview      = data.preview || [];
		this.isDeprecated = isDeprecated;
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

	protected nextChar = (c: string) => {
		if (/z$/.test(c)) {
			return c.replaceAll(/z$/g, 'a') + 'a';
		}
		return c.substring(0, c.length - 1) + String.fromCharCode(c.charCodeAt(c.length - 1) + 1);
	};

	public toYamlObj(): YamlComponent {
		const comp = <YamlComponent>{
			type:    this.type,
			comment: this.comment
		};

		if (this.preview.length > 0) {
			const previewData = <PreviewData>{
				enabled: this.enablePreview
			};

			this.preview
				.filter(opt => opt.meetsPreviewRequirements(this))
				.forEach((opt: ComponentOption) => {
					const optData: { [key: string]: unknown } = opt.getData();
					Object.keys(optData).forEach(key => previewData[key] = optData[key]);
				});

			comp.preview = previewData;
		}

		const data = this.getData();
		if (Object.keys(data).length > 0) comp.data = data;

		if (this.isParent) {
			const comps = get(this.components);
			if (comps.length > 0) {
				const compData = <YamlComponentData>{};
				comps.forEach(comp => {
					const yamlData = comp.toYamlObj();
					let name       = comp.name;
					let suffix     = 'a';
					while (compData[name]) {
						suffix = this.nextChar(suffix);
						name   = comp.name + '-' + suffix;
					}
					compData[name] = yamlData;
				});
				comp.children = compData;
			}
		}

		return comp;
	};

	public abstract getData(raw: boolean): Unknown;

	public getRawPreviewData(): Unknown {
		const data: Unknown = {};

		this.preview
			.forEach((opt: ComponentOption) => {
				const optData: {
					[key: string]: unknown
				} = opt.getData();
				Object.keys(optData).forEach(key => data[key] = optData[key]);
			});

		return data;
	}

	public deserialize(yaml: YamlComponent): void {
		const preview = yaml.preview;
		if (preview) {
			this.enablePreview = preview.enabled;
			this.preview.forEach((opt: ComponentOption) => opt.deserialize(preview));
		}

		if (yaml.comment) {
			this.comment = yaml.comment.replaceAll('\\n', '\n');
		}
	}
}