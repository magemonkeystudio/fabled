<script lang='ts'>
	import { updateSidebar }           from '../data/store';
	import ProInput                    from './input/ProInput.svelte';
	import SearchableSelect            from './input/SearchableSelect.svelte';
	import Toggle                      from './input/Toggle.svelte';
	import AttributeInput              from './input/AttributeInput.svelte';
	import IconInput                   from './input/IconInput.svelte';
	import DynamicAttributeInput       from '$input/DynamicAttributeInput.svelte';
	import FabledSkill, { skillStore } from '../data/skill-store';

	export let data: FabledSkill;
	const skills = skillStore.skills;

	$: {
		if (data?.name) updateSidebar();
		data.save();
	}
</script>

{#if data}
	<ProInput label='Name'
						tooltip='The name of the skill. This should not contain color codes'
						bind:value={data.name} />
	<ProInput label='Type'
						tooltip='The flavor text describing the skill such as "AOE utility" or whatever you want it to be'
						bind:value={data.type} />
	<ProInput label='Max Level'
						tooltip='The maximum level the skill can reach'
						bind:value={data.maxLevel} />
	<ProInput label='Cost'
						tooltip='The amount of skill points needed to unlock and upgrade this skill'>
		<AttributeInput bind:value={data.cost} />
	</ProInput>
	<ProInput label='Cooldown'
						tooltip='The time in seconds before the skill can be cast again (only works with the Cast trigger)'>
		<AttributeInput bind:value={data.cooldown} />
	</ProInput>
	<ProInput label='Cooldown Message'
						tooltip='Whether to send a message when attempting to run the skill while in cooldown'>
		<Toggle bind:data={data.cooldownMessage} />
	</ProInput>
	<ProInput label='Mana'
						tooltip='The amount of mana it takes to cast the skill (only works with the Cast trigger)'>
		<AttributeInput bind:value={data.mana} />
	</ProInput>
	<ProInput label='Cast Message'
						tooltip='The message to display to players around the caster when the skill is cast. The radius of the area is in the config.yml options'
						bind:value={data.castMessage} />
	<ProInput label='Combo'
						tooltip='The click combo to assign the skill (if enabled). Use L, R, S, LS, RS, P, Q and F for the types of clicks separated by spaces. For example, "L L R R" would work for 4 click combos.'
						bind:value={data.combo} />
	<IconInput bind:icon={data.icon} />
	<ProInput label='Incompatible'
						tooltip='List of skills that must not be upgraded in order to upgrade this skill'>
		<SearchableSelect multiple={true}
											data={$skills.filter(s => s!== data)}
											bind:selected={data.incompatible} />
	</ProInput>

	<div class='header'>Requirements</div>
	<ProInput label='Skill Requirement'
						tooltip='The skill that needs to be upgraded before this one can be unlocked'>
		<SearchableSelect id='skillReq'
											bind:selected={data.skillReq}
											data={$skills.filter(s => s !== data)} />
	</ProInput>
	<ProInput label='Skill Req Level'
						tooltip='The level that the required skill needs to reach before this one can be unlocked'
						bind:value={data.skillReqLevel} />
	<ProInput label='Permission'
						tooltip='Whether this skill requires a permission to unlock. The permission would be "skillapi.skill.{data.name}"'>
		<Toggle bind:data={data.permission} />
	</ProInput>
	<ProInput label='Level Req'
						tooltip='The class level the player needs to be before unlocking or upgrading this skill'>
		<AttributeInput bind:value={data.levelReq} />
	</ProInput>
	<ProInput label='Min Spent'
						tooltip='The amount of skill points that need to be spent before upgrading this skill'>
		<AttributeInput bind:value={data.minSpent} />
	</ProInput>

	<!-- Attribute Requirements -->
	<div class='header'>Attribute Requirements</div>
	<DynamicAttributeInput bind:value={data.attributeRequirements} />
{/if}

<style>
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
</style>
