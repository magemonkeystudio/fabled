<script lang='ts'>
	import IconInput                   from './input/IconInput.svelte';
	import MaterialSelect              from './input/MaterialSelect.svelte';
	import SearchableSelect            from './input/SearchableSelect.svelte';
	import { updateSidebar }           from '../data/store';
	import AttributeInput              from './input/AttributeInput.svelte';
	import ByteSelect                  from './input/ByteSelect.svelte';
	import { expSources }              from '../version/data';
	import { toProperCase }            from '$api/api';
	import { onDestroy, onMount }      from 'svelte';
	import { Attribute }               from '$api/stat';
	import ProInput                    from './input/ProInput.svelte';
	import Toggle                      from './input/Toggle.svelte';
	import LoreInput                   from '$input/LoreInput.svelte';
	import type { Unsubscriber }       from 'svelte/store';
	import FabledClass, { classStore } from '../data/class-store';
	import { skillStore }              from '../data/skill-store';
	import { attributeStore }          from '../data/attribute-store.js';

	export let data: FabledClass;

	let combosShown = false;
	let sub: Unsubscriber;

	const classes = classStore.classes;
	const skills  = skillStore.skills;

	onMount(() => {
		sub = attributeStore.attributes.subscribe(value => {
			const included: string[] = [];
			data.attributes          = data.attributes.filter(a => {
				if (value.some((attr) => attr.name === a.name)) {
					included.push(a.name);
					return true;
				}
				return false;
			});

			for (const attrib of value.filter(attr => !included.includes(attr.name))) {
				data.attributes.push(new Attribute(attrib.name, 0, 0));
			}
		});
	});

	onDestroy(() => {
		if (sub) sub();
	});

	$: {
		if (data?.name) updateSidebar();
		data.save();
	}
</script>

{#if data}
	<ProInput label='Name'
						tooltip='The name of the class. This should not contain color codes'
						bind:value={data.name} />
	<ProInput label='Prefix'
						tooltip='The prefix given to players who profess as the class which can contain color codes'
						bind:value={data.prefix} />
	<ProInput label='Action Bar'
						tooltip='The format for the action bar. Leave blank to use the default formatting'
						bind:value={data.actionBar} />
	<ProInput label='Group'
						tooltip='A class group are things such as "race", "class", and "trade". Different groups can be professed through at the same time, one class from each group'
						bind:value={data.group} />
	<ProInput label='Mana Name'
						tooltip='The name the class uses for mana'
						bind:value={data.manaName} />
	<ProInput label='Max Level'
						tooltip='The maximum level the class can reach. If this class turns into other classes, this will also be the level it can profess into those classes'
						bind:value={data.maxLevel} />
	<ProInput label='Parent'
						tooltip='The class that turns into this one. For example, if Fighter turns into Knight, then Knight would have its parent as Fighter'>
		<SearchableSelect id='parent'
											data={$classes.filter(c => c !== data)}
											bind:selected={data.parent} />
	</ProInput>
	<ProInput label='Permission'
						tooltip='Whether the class requires a permission to be professed as. The permission would be "skillapi.class.{data.name.toLowerCase()}"'>
		<Toggle bind:data={data.permission} />
	</ProInput>
	<ProInput label='Exp Sources'
						tooltip='The experience sources the class goes up from. Most of these only work if "use-exp-orbs" is enabled in the config.yml'>
		<ByteSelect
			data={expSources}
			bind:value={data.expSources} />
	</ProInput>
	<ProInput label='Health'
						tooltip='The amount of health the class has'>
		<AttributeInput bind:value={data.health} />
	</ProInput>
	<ProInput label='Mana'
						tooltip='The amount of mana the class has'>
		<AttributeInput bind:value={data.mana} />
	</ProInput>

	<div class='info'>Drag & Drop your attributes file to use your custom attributes</div>
	{#each data.attributes as attr (attr.name)}
		<ProInput label={toProperCase(attr.name)}
							tooltip='The amount of {attr.name} the class has'>
			<AttributeInput bind:value={attr} />
		</ProInput>
	{/each}

	<ProInput label='Mana Regen'
						tooltip='The amount of mana the class regenerates at each interval. The interval is in the config.yml and by default is once every second. If you want to regen a decimal amount per second, increase the interval'
						bind:value={data.manaRegen} />
	<ProInput label='Skill Tree'
						tooltip='The type of skill tree to use'>
		<select id='skill-tree' bind:value={data.skillTree}>
			<option value='Custom'>Custom</option>
			<option value='Requirement'>Requirement</option>
			<option value='Basic Horizontal'>Basic Horizontal</option>
			<option value='Basic Vertical'>Basic Vertical</option>
			<option value='Level Horizontal'>Level Horizontal</option>
			<option value='Level Vertical'>Level Vertical</option>
			<option value='Flood'>Flood</option>
		</select>
	</ProInput>

	<ProInput label='Skills'
						tooltip='The skills the class is able to use'>
		<SearchableSelect id='skills'
											data={$skills}
											multiple={true}
											bind:selected={data.skills}
											placeholder='No Skills' />
	</ProInput>

	<IconInput bind:icon={data.icon} />

	<ProInput label='Unusable items'
						tooltip='[blacklist] The types of items that the class cannot use'>
		<MaterialSelect multiple bind:selected={data.unusableItems} />
	</ProInput>

	<div class='header combos'
			 role='button'
			 tabindex='0'
			 on:click={() => combosShown = !combosShown}
			 on:keypress={e => {
			 	if (e.key === 'Enter') combosShown = !combosShown;
			 }}>
		Combo Starters <span class='material-symbols-rounded'>{combosShown ? 'expand_less' : 'expand_more'}</span>
	</div>
	{#if combosShown}
		<div class='info'>These are the materials that can be used as combo starters. If a material is not in the list, it
			cannot be used as a combo starter. If the list is inverted, then the materials in the list cannot be used as combo
			starters
		</div>
		<ProInput label='L Inverted'
							tooltip='Whether the L list should be used as a blacklist'>
			<Toggle bind:data={data.lInverted} />
		</ProInput>
		<ProInput label='L Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.lWhitelist} />
		</ProInput>
		<ProInput label='R Inverted'
							tooltip='Whether the R list should be used as a blacklist'>
			<Toggle bind:data={data.rInverted} />
		</ProInput>
		<ProInput label='R Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.rWhitelist} />
		</ProInput>
		<ProInput label='LS Inverted'
							tooltip='Whether the LS list should be used as a blacklist'>
			<Toggle bind:data={data.lsInverted} />
		</ProInput>
		<ProInput label='LS Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.lsWhitelist} />
		</ProInput>
		<ProInput label='RS Inverted'
							tooltip='Whether the RS list should be used as a blacklist'>
			<Toggle bind:data={data.rsInverted} />
		</ProInput>
		<ProInput label='RS Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.rsWhitelist} />
		</ProInput>
		<ProInput label='S Inverted'
							tooltip='Whether the S list should be used as a blacklist'>
			<Toggle bind:data={data.rsInverted} />
		</ProInput>
		<ProInput label='S Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.rsWhitelist} />
		</ProInput>
		<ProInput label='P Inverted'
							tooltip='Whether the P list should be used as a blacklist'>
			<Toggle bind:data={data.pInverted} />
		</ProInput>
		<ProInput label='P Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.pWhitelist} />
		</ProInput>
		<ProInput label='Q Inverted'
							tooltip='Whether the Q list should be used as a blacklist'>
			<Toggle bind:data={data.qInverted} />
		</ProInput>
		<ProInput label='Q Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.qWhitelist} />
		</ProInput>
		<ProInput label='F Inverted'
							tooltip='Whether the F list should be used as a blacklist'>
			<Toggle bind:data={data.fInverted} />
		</ProInput>
		<ProInput label='F Whitelist'
							tooltip='The materials that can be used as combo starters'>
			<LoreInput bind:value={data.fWhitelist} />
		</ProInput>
	{/if}
{/if}

<style>
    .info {
        grid-column: 1 / span 2;
        text-align: center;
        margin-left: 5rem;
        color: rgba(255, 255, 255, 0.7);
        padding-top: 0.5rem;
        padding-bottom: 0.5rem;
    }

    .header {
        grid-column: 1 / -1;
        font-size: 1.2em;
        font-weight: bold;
        text-align: center;
        padding-bottom: 1rem;
    }

    .header::before {
        content: ' ';
        display: block;
        width: 40%;
        height: 1px;
        background: white;
        margin: 1rem auto;
    }

    .combos {
        cursor: pointer;
    }
</style>