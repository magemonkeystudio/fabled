import type { Writable }               from 'svelte/store';
import { get, writable }               from 'svelte/store';
import { browser }                     from '$app/environment';
import YAML                            from 'yaml';
import FabledAttribute                 from '$api/fabled-attribute.svelte';
import type { MultiAttributeYamlData } from '$api/types';
import { sort }                        from '$api/api';
import { parseYaml }                   from '$api/yaml';
import { active, saveError }           from './store';
import { base }                        from '$app/paths';
import { goto }                        from '$app/navigation';
import { socketService }               from '$api/socket/socket-connector';
import { classStore }                  from './class-store.svelte';
import { idbGet, idbSet }              from '$api/idb';

class AttributeStore {
	tooBig: Writable<boolean>       = writable(false);
	acknowledged: Writable<boolean> = writable(false);

	loadAttributesFromServer = async () => {
		let serverAttributes: string = '';
		try {
			serverAttributes = await socketService.getAttributeYaml();
		} catch (_) {
			return;
		}

		this.loadAttributesText(serverAttributes, 'server');
	};

	removeServerAttributes = () => {
		const tempAttributes = get(this.attributes);
		this.attributes.set(tempAttributes.filter((attr) => attr.location !== 'server'));
	};

	constructor() {
		socketService.onConnect(this.loadAttributesFromServer);
		socketService.onDisconnect(this.removeServerAttributes);

		this.getDefaultAttributes().then((defaultAttributes) => {
			setTimeout(() => {
				const attributes = get(this.attributes);
				if (attributes.length === 0) {
					this.attributes.set(defaultAttributes);
				}
			}, 500);
		});
	}

	private setupAttributeStore = <T extends FabledAttribute[]>(
		key: string,
		def: T,
		mapper: (data: string) => T,
		setAction: (data: T) => T,
		postLoad?: (saved: T) => void): Writable<T> => {
		let saved: T = def;
		if (browser) {
			const stored = localStorage.getItem(key);
			if (stored) {
				saved = mapper(stored);
				if (postLoad) postLoad(saved);
			}
		}

		const {
						subscribe,
						set,
						update
					} = writable<T>(saved);
		return {
			subscribe,
			set: (value: T) => {
				if (setAction) value = setAction(value);
				return set(value);
			},
			update
		};
	};

	getDefaultAttributes = async (): Promise<FabledAttribute[]> => {
		const yaml = parseYaml(await fetch('https://raw.githubusercontent.com/magemonkeystudio/fabled/dev/src/main/resources/attributes.yml').then(r => r.text()));
		if (!yaml) return [];
		return Object.keys(yaml).map((key: string) => {
			const attrib: FabledAttribute = new FabledAttribute({ name: key });
			attrib.load(yaml[key]);
			return attrib;
		});

	};

	attributes: Writable<FabledAttribute[]> = (() => {
		const { subscribe, set, update } = writable<FabledAttribute[]>([]);

		if (browser) {
			// Async init from IDB, with localStorage migration fallback
			idbGet('attributes', 'all').then(async stored => {
				if (!stored) {
					// Migrate from localStorage
					const lsData = localStorage.getItem('attribs');
					if (lsData) {
						await idbSet('attributes', 'all', lsData);
						localStorage.removeItem('attribs');
						stored = lsData;
					}
				}

				if (!stored) return;

				let attrs: FabledAttribute[];
				if (stored.split('\n').length < 3 && stored.charAt(0) !== '{') {
					// Old comma-separated format
					attrs = stored.replace('\n', '').split(',').map(key => new FabledAttribute({ name: key }));
				} else {
					const yaml = <MultiAttributeYamlData>parseYaml(stored);
					if (!yaml) return;
					attrs = Object.keys(yaml).map(key => {
						const attrib = new FabledAttribute({ name: key });
						attrib.load(yaml[key]);
						return attrib;
					});
				}

				classStore.updateAllAttributes(attrs.map(a => a.name));
				set(sort<FabledAttribute>(attrs));
			});
		}

		return {
			subscribe,
			set: (value: FabledAttribute[]) => {
				classStore.updateAllAttributes(value.map(a => a.name));
				return set(sort<FabledAttribute>(value));
			},
			update
		};
	})();

	getAttributeNames = (): string[] => {
		return get(this.attributes).map((attr) => attr.name);
	};

	getAttribute = (name: string): FabledAttribute | undefined => {
		for (const c of get(this.attributes)) {
			if (c.name == name) return c;
		}

		return undefined;
	};

	isAttributeNameTaken = (name: string): boolean => !!this.getAttribute(name);

	addAttribute = (name?: string): FabledAttribute => {
		const allAttributes = get(this.attributes);
		let index           = allAttributes.length + 1;
		while (!name && this.isAttributeNameTaken(name || 'attribute ' + index)) {
			index++;
		}
		const attrib = new FabledAttribute({ name: (name || 'attribute ' + index) });
		allAttributes.push(attrib);

		this.attributes.set(allAttributes);
		attrib.save();
		return attrib;
	};


	loadAttributes = (e: ProgressEvent<FileReader>) => {
		const text: string = <string>e.target?.result;
		if (!text) return;

		this.loadAttributesText(text);
	};

	/**
	 * Loads attribute data from a file
	 * e - event details
	 */
	loadAttributesText = (text: string, location: 'local' | 'server' = 'local') => {
		const yaml = <MultiAttributeYamlData>parseYaml(text);
		if (!yaml) return;

		// Get the current attributes
		const currentAttributes    = get(this.attributes);
		// Create a map of current attributes for easy lookup
		const currentAttributesMap = new Map(currentAttributes.map(attr => [attr.name, attr]));

		// Merge the current attributes with the new ones
		const mergedAttributes = [...currentAttributes];
		Object.keys(yaml).forEach((key: string) => {
			// If the attribute already exists, ignore it
			if (!currentAttributesMap.has(key)) {
				// Otherwise, create a new attribute
				const newAttribute = new FabledAttribute({ name: key, location });
				newAttribute.load(yaml[key]);
				mergedAttributes.push(newAttribute);
			}
		});

		this.attributes.set(mergedAttributes);
		this.refreshAttributes();
	};

	loadAttribute = async (data: FabledAttribute) => {
		if (data.loaded) return;

		if (data.location === 'local') {
			const raw      = await idbGet('attributes', 'all') ?? '';
			const yamlData = <MultiAttributeYamlData>parseYaml(raw);
			if (!yamlData) return;
			const attrib = yamlData[data.name];
			data.load(attrib);
		}
	};

	cloneAttribute = (data: FabledAttribute): FabledAttribute => {
		if (!data.loaded) this.loadAttribute(data);

		const attr: FabledAttribute[] = get(this.attributes);
		let name                      = data.name + ' (Copy)';
		let i                         = 1;
		while (this.isAttributeNameTaken(name)) {
			name = data.name + ' (Copy ' + i + ')';
			i++;
		}
		const attribute = new FabledAttribute();
		const yamlData  = data.serializeYaml();
		attribute.load(yamlData);
		attribute.name = name;
		attr.push(attribute);

		this.attributes.set(attr);
		attribute.save();
		return attribute;
	};

	refreshAttributes = () => this.attributes.set(sort<FabledAttribute>(get(this.attributes)));

	deleteAttribute = (data: FabledAttribute) => {
		const filtered = get(this.attributes).filter(c => c != data);
		const act      = get(active);
		this.attributes.set(filtered);
		this.saveAll();

		if (!(act instanceof FabledAttribute)) return;

		if (filtered.length === 0) {
			goto(`${base}/`).then(() => {
			});
		} else if (!filtered.find(attr => attr === get(active))) {
			goto(`${base}/attribute/${filtered[0].name}/edit`).then(() => {
			});
		}
	};

	saveAll = async () => {
		const attributeYaml: MultiAttributeYamlData = {};
		for (const attr of get(this.attributes)) {
			attributeYaml[attr.name] = attr.serializeYaml();
		}
		const yaml = YAML.stringify(attributeYaml, { lineWidth: 0, aliasDuplicateObjects: false });

		try {
			await idbSet('attributes', 'all', yaml);
			this.tooBig.set(false);
			console.log('Saved attributes 😎');
		} catch (e: any) {
			console.error('Attributes Save error', e);
			this.tooBig.set(true);
			saveError.set({ name: 'Attributes', acknowledged: false });
		}
	};
}

export const attributeStore = new AttributeStore();