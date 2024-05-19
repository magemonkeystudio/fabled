import type { Writable }               from 'svelte/store';
import { get, writable }               from 'svelte/store';
import { browser }                     from '$app/environment';
import { updateAllAttributes }         from './class-store';
import YAML                            from 'yaml';
import FabledAttribute                 from '$api/fabled-attribute';
import type { MultiAttributeYamlData } from '$api/types';
import { sort }                        from '$api/api';
import { parseYaml }                   from '$api/yaml';
import { active, saveError }           from './store';
import { base }                        from '$app/paths';
import { goto }                        from '$app/navigation';
import { socketService }               from '$api/socket/socket-connector';

const tooBig: Writable<boolean>       = writable(false);
const acknowledged: Writable<boolean> = writable(false);

const loadAttributesFromServer = async () => {
	let serverAttributes: string = '';
	try {
		serverAttributes = await socketService.getAttributeYaml();
	} catch (_) {
		return;
	}

	loadAttributesText(serverAttributes, 'server');
};

const removeServerAttributes = () => {
	const tempAttributes = get(attributes);
	attributes.set(tempAttributes.filter((attr) => attr.location !== 'server'));
};

socketService.onConnect(loadAttributesFromServer);
socketService.onDisconnect(removeServerAttributes);

const setupAttributeStore = <T extends FabledAttribute[]>(
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

export const getDefaultAttributes = async (): Promise<FabledAttribute[]> => {
	const yaml = parseYaml(await fetch('https://raw.githubusercontent.com/promcteam/fabled/dev/src/main/resources/attributes.yml').then(r => r.text()));
	if (!yaml) return [];
	return Object.keys(yaml).map((key: string) => {
		const attrib: FabledAttribute = new FabledAttribute({ name: key });
		attrib.load(yaml[key]);
		return attrib;
	});

};

export const attributes: Writable<FabledAttribute[]> = setupAttributeStore<FabledAttribute[]>(
	'attribs',
	await getDefaultAttributes(),
	(data: string) => {
		if (data.split('\n').length < 3 && data.charAt(0) !== '{') { // Old format
			return data.replace('\n', '').split(',').map((key: string) => new FabledAttribute({ name: key }));
		}
		const yaml = <MultiAttributeYamlData>parseYaml(data);
		if (!yaml) return [];
		return Object.keys(yaml).map((key: string) => {
			const attrib: FabledAttribute = new FabledAttribute({ name: key });
			attrib.load(yaml[key]);
			return attrib;
		});
	},
	(value: FabledAttribute[]) => {
		updateAllAttributes(value.map((attr: FabledAttribute) => attr.name));
		return sort<FabledAttribute>(value);
	});

export const getAttributeNames = (): string[] => {
	return get(attributes).map((attr) => attr.name);
};

export const getAttribute = (name: string): FabledAttribute | undefined => {
	for (const c of get(attributes)) {
		if (c.name == name) return c;
	}

	return undefined;
};

export const isAttributeNameTaken = (name: string): boolean => !!getAttribute(name);

export const addAttribute = (name?: string): FabledAttribute => {
	const allAttributes = get(attributes);
	let index           = allAttributes.length + 1;
	while (!name && isAttributeNameTaken(name || 'attribute ' + index)) {
		index++;
	}
	const attrib = new FabledAttribute({ name: (name || 'attribute ' + index) });
	allAttributes.push(attrib);

	attributes.set(allAttributes);
	attrib.save();
	return attrib;
};


export const loadAttributes = (e: ProgressEvent<FileReader>) => {
	const text: string = <string>e.target?.result;
	if (!text) return;

	loadAttributesText(text);
};

/**
 * Loads attribute data from a file
 * e - event details
 */
export const loadAttributesText = (text: string, location: 'local' | 'server' = 'local') => {
	const yaml = <MultiAttributeYamlData>parseYaml(text);
	if (!yaml) return;

	// Get the current attributes
	const currentAttributes    = get(attributes);
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

	attributes.set(mergedAttributes);
	refreshAttributes();
};

export const loadAttribute = (data: FabledAttribute) => {
	if (data.loaded) return;

	if (data.location === 'local') {
		const yamlData = <MultiAttributeYamlData>parseYaml(localStorage.getItem('attribs') || '');
		if (!yamlData) return;
		const attrib = yamlData[data.name];
		data.load(attrib);
	}
};

export const cloneAttribute = (data: FabledAttribute): FabledAttribute => {
	if (!data.loaded) loadAttribute(data);

	const attr: FabledAttribute[] = get(attributes);
	let name                      = data.name + ' (Copy)';
	let i                         = 1;
	while (isAttributeNameTaken(name)) {
		name = data.name + ' (Copy ' + i + ')';
		i++;
	}
	const attribute = new FabledAttribute();
	const yamlData  = data.serializeYaml();
	attribute.load(yamlData);
	attribute.name = name;
	attr.push(attribute);

	attributes.set(attr);
	attribute.save();
	return attribute;
};

export const refreshAttributes = () => attributes.set(sort<FabledAttribute>(get(attributes)));

export const deleteAttribute = (data: FabledAttribute) => {
	const filtered = get(attributes).filter(c => c != data);
	const act      = get(active);
	attributes.set(filtered);
	saveAll();

	if (!(act instanceof FabledAttribute)) return;

	if (filtered.length === 0) {
		goto(`${base}/`).then(() => {
		});
	} else if (!filtered.find(attr => attr === get(active))) {
		goto(`${base}/attribute/${filtered[0].name}/edit`).then(() => {
		});
	}
};

export const saveAll = () => {
	if (get(tooBig)) return;

	if (get(tooBig) && !get(acknowledged)) {
		saveError.set(this);
		return;
	}

	const attributeYaml: MultiAttributeYamlData = {};
	for (const attr of get(attributes)) {
		attributeYaml[attr.name] = attr.serializeYaml();
	}
	const yaml = YAML.stringify(attributeYaml, { lineWidth: 0 });

	try {
		localStorage.setItem('attribs', yaml);
		tooBig.set(false);
	} catch (e: any) {
		// If the data is too big
		if (!e?.message?.includes('quota')) {
			console.error('Attributes Save error', e);
		} else {
			localStorage.removeItem('attribs');
			tooBig.set(true);
			saveError.set(this);
		}
	}
};