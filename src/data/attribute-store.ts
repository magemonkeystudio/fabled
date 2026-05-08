import type { Writable } from 'svelte/store';
import {
	get,
	writable
}                        from 'svelte/store';
import FabledAttribute   from '$api/fabled-attribute.svelte';
import type {
	MultiAttributeYamlData
}                        from '$api/types';
import {
	sort
}                        from '$api/api';
import {
	parseYaml
}                        from '$api/yaml';
import {
	active,
	saveError
}                        from './store';
import {
	base
}                        from '$app/paths';
import {
	goto
}                        from '$app/navigation';
import {
	socketService
}                        from '$api/socket/socket-connector';
import {
	classStore
}                        from './class-store.svelte';
import {
	beginPersistenceSave,
	finishPersistenceSave
}                        from './persistence-state';
import {
	getPersistedAttribute,
	listPersistedAttributeRecords,
	savePersistedAttributes
}                        from './editor-persistence';

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
		_key: string,
		def: T,
		mapper: (data: string) => T,
		setAction: (data: T) => T,
		postLoad?: (saved: T) => void
	): Writable<T> => {
		let saved: T = def;
		if (postLoad) postLoad(saved);

		const { subscribe, set, update } = writable<T>(saved);
		return {
			subscribe,
			set: (value: T) => {
				if (setAction) value = setAction(value);
				return set(value);
			},
			update
		};
	};

	hydratePersistedData = async () => {
		const attributes = listPersistedAttributeRecords().map((record) => {
			const attribute = new FabledAttribute({ name: record.name, location: 'local' });
			attribute.load(record.data);
			return attribute;
		});

		this.attributes.set(sort<FabledAttribute>(attributes));
	};

	getDefaultAttributes = async (): Promise<FabledAttribute[]> => {
		const yaml = parseYaml(
			await fetch(
				'https://raw.githubusercontent.com/magemonkeystudio/fabled/dev/src/main/resources/attributes.yml'
			).then((r) => r.text())
		);
		if (!yaml) return [];
		return Object.keys(yaml).map((key: string) => {
			const attrib: FabledAttribute = new FabledAttribute({ name: key });
			attrib.load(yaml[key]);
			return attrib;
		});
	};

	attributes: Writable<FabledAttribute[]> = this.setupAttributeStore<FabledAttribute[]>(
		'attributes',
		[],
		(_data: string) => [],
		(value: FabledAttribute[]) => {
			classStore.updateAllAttributes(value.map((attr: FabledAttribute) => attr.name));
			return sort<FabledAttribute>(value);
		}
	);

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
		const attrib = new FabledAttribute({ name: name || 'attribute ' + index });
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
		const currentAttributesMap = new Map(currentAttributes.map((attr) => [attr.name, attr]));

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
			const yamlData = await getPersistedAttribute(data.name);
			if (!yamlData) return;
			data.load(yamlData);
		}
	};

	cloneAttribute = async (data: FabledAttribute): Promise<FabledAttribute> => {
		if (!data.loaded) await this.loadAttribute(data);

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
		const filtered = get(this.attributes).filter((c) => c != data);
		const act      = get(active);
		this.attributes.set(filtered);
		this.saveAll();

		if (!(act instanceof FabledAttribute)) return;

		if (filtered.length === 0) {
			goto(`${base}/`).then(() => {
			});
		} else if (!filtered.find((attr) => attr === get(active))) {
			goto(`${base}/attribute/${filtered[0].name}/edit`).then(() => {
			});
		}
	};

	saveAll = () => {
		const pendingPersist = beginPersistenceSave({
			name:         'Attributes',
			tooBig:       get(this.tooBig),
			acknowledged: get(this.acknowledged)
		});
		if (!pendingPersist.shouldPersist) {
			saveError.set({ name: 'Attributes', acknowledged: false });
			return;
		}

		const attributeYaml: MultiAttributeYamlData = {};
		for (const attr of get(this.attributes)) {
			attributeYaml[attr.name] = attr.serializeYaml();
		}

		void savePersistedAttributes(
			Object.entries(attributeYaml).map(([name, data]) => ({
				name,
				data
			}))
		).then((result) => {
			if (!result.ok) {
				if (!result.quotaExceeded) {
					console.error('Attributes Save error', result.error);
				} else {
					const persistState = finishPersistenceSave(
						{
							name:         'Attributes',
							tooBig:       get(this.tooBig),
							acknowledged: get(this.acknowledged)
						},
						result
					);
					this.tooBig.set(persistState.state.tooBig);
					this.acknowledged.set(persistState.state.acknowledged);
					saveError.set({ name: 'Attributes', acknowledged: false });
				}
			} else {
				const persistState = finishPersistenceSave(
					{
						name:         'Attributes',
						tooBig:       get(this.tooBig),
						acknowledged: get(this.acknowledged)
					},
					result
				);
				this.tooBig.set(persistState.state.tooBig);
				this.acknowledged.set(persistState.state.acknowledged);
				if (persistState.clearSaveError && get(saveError)?.name === 'Attributes') {
					saveError.set(undefined);
				}
			}

			console.log('Saved attributes 😎');
		});
	};
}

export const attributeStore = new AttributeStore();
