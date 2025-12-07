import { LocalStorage }                             from 'node-localstorage';
import { conditions, mechanics, targets, triggers } from '../src/api/components/registry';
import { initComponents }                           from '../src/api/components/components.svelte';
import { get }                                      from 'svelte/store';
import * as fs                                      from 'fs';
import type FabledComponent                         from '../src/api/components/fabled-component.svelte';
import type { ComponentOption }                     from '../src/api/options/options';
import type DropdownSelect                          from '../src/api/options/dropdownselect.svelte';
import type StringListSelect                        from '../src/api/options/stringlistselect.svelte';
import type ProAttributeSelect                      from '../src/api/options/attributeselect.svelte';
import type ProBooleanSelect                        from '../src/api/options/booleanselect.svelte';
import type ProClassSelect                          from '../src/api/options/classselect.svelte';
import type ProColorSelect                          from '../src/api/options/colorselect.svelte';
import type ProDoubleSelect                         from '../src/api/options/doubleselect.svelte';
import type ProEnchantSelect                        from '../src/api/options/enchantselect.svelte';
import type ProIntSelect                            from '../src/api/options/intselect.svelte';
import type ProMaterialSelect                       from '../src/api/options/materialselect.svelte';
import type ProSkillSelect                          from '../src/api/options/skillselect.svelte';
import type ProStringSelect                         from '../src/api/options/stringselect.svelte';
import type ProBlockSelect                          from '../src/api/options/blockselect.svelte';
import type ProSectionMarker                        from '../src/api/options/sectionmarker.svelte';
import { attributeStore }                           from '../src/data/attribute-store';
import { classStore }                               from '../src/data/class-store.svelte';
import { skillStore }                               from '../src/data/skill-store.svelte';

global.localStorage = new LocalStorage('./scratch');

skillStore.persistSkills  = () => {
};
classStore.persistClasses = () => {
};
attributeStore.saveAll    = () => {
};
// @ts-ignore
skillStore.getSkill       = (name: string) => ({
	save:     () => {
	},
	postLoad: () => {
	}
});
classStore.addClass       = (name?: string) => new classStore.getClass(name);
// @ts-ignore
classStore.getClass       = (name: string) => ({
	save:     () => {
	},
	postLoad: () => {
	}
});

const serializeOption = (option: ComponentOption) => {
	const result: Record<string, any> = {
		// Attempt to get a type. This is brittle.
		type:      option.constructor.name,
		label:     option.label,
		key:       option.key,
		tooltip:   option.tooltip,
		condition: option.condition
		// value:     option.value
	};

	const dropdown = option as DropdownSelect;
	if (typeof dropdown.options === 'function' || Array.isArray(dropdown.options)) {
		result.type = 'Dropdown';
		try {
			result.options = typeof dropdown.options === 'function' ? dropdown.options() : dropdown.options;
		} catch (e) {
			result.options = ['Error loading options'];
		}
		return result;
	}

	const stringlist = option as StringListSelect;
	if (stringlist.value && Array.isArray(stringlist.value)) {
		result.type = 'StringList';
		return result;
	}

	const block = option as ProBlockSelect;
	if (block.block) {
		result.type = 'Block';
		return result;
	}

	const marker = option as ProSectionMarker;
	if (marker.isMarker) {
		result.type = 'SectionMarker';
		return result;
	}

	const bool = option as ProBooleanSelect;
	if (typeof bool.value === 'boolean') {
		result.type = 'Boolean';
		return result;
	}

	const color = option as ProColorSelect;
	if (typeof color.value === 'string' && color.value.startsWith('#')) {
		result.type = 'Color';
		return result;
	}

	const num = option as ProDoubleSelect | ProIntSelect | ProAttributeSelect;
	if (typeof num.value === 'number') {
		if (Number.isInteger(num.value)) {
			result.type = 'Int';
		} else {
			result.type = 'Double';
		}
		return result;
	}

	const str = option as ProStringSelect;
	if (typeof str.value === 'string') {
		result.type = 'String';
		return result;
	}

	const classSel = option as ProClassSelect;
	if (classSel.value) {
		result.type = 'Class';
		return result;
	}

	const skillSel = option as ProSkillSelect;
	if (skillSel.value) {
		result.type = 'Skill';
		return result;
	}

	const enchant = option as ProEnchantSelect;
	if (enchant.enchants) {
		result.type = 'Enchant';
		return result;
	}

	const mat = option as ProMaterialSelect;
	if (mat.materials) {
		result.type = 'Material';
		return result;
	}


	return result;
};

const serializeComponent = (component: FabledComponent) => {
	return {
		name:        component.name,
		description: component.description,
		data:        component.data.map(serializeOption)
	};
};

const extract = async () => {
	// Some stores might need to be initialized.
	// Based on the imports in components.svelte.ts, these seem relevant.
	attributeStore.attributes.set([]);
	classStore.classes.set([]);
	skillStore.skills.set([]);
	classStore.classFolders.set([]);
	skillStore.skillFolders.set([]);


	initComponents();

	const componentData = {
		triggers:   Object.values(get(triggers)).map(entry => serializeComponent(entry.component.new())),
		targets:    Object.values(get(targets)).map(entry => serializeComponent(entry.component.new())),
		conditions: Object.values(get(conditions)).map(entry => serializeComponent(entry.component.new())),
		mechanics:  Object.values(get(mechanics)).map(entry => serializeComponent(entry.component.new()))
	};

	fs.writeFileSync('mcp-server-project/components.json', JSON.stringify(componentData, null, 2));
};

extract().then(() => {
	console.log('Component data extracted to mcp-server-project/components.json');
});
