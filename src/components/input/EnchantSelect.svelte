<script lang='ts'>
	import SearchableSelect from './SearchableSelect.svelte';
	import { versionData }  from '../../version/data';
	import type { Enchant } from '$api/options/enchantselect';
	import ProInput         from '$input/ProInput.svelte';

	export let id: string | undefined                    = undefined;
	export let placeholder                               = '';
	export let multiple                                  = false;
	export let any                                       = false;
	export let selected: Enchant[] | Enchant | undefined = undefined;

	let input: SearchableSelect;

	const handleSelect = (e: CustomEvent<string>, index = -1): boolean => {
		if (multiple) {
			selected = selected || [];

			if (index !== -1) {
				(<Enchant[]>selected)[index] = { name: e.detail, level: 1 };
			} else {
				(<Enchant[]>selected).push({ name: e.detail, level: 1 });
			}

			input.focus();

			return false;
		} else {
			selected = { name: e.detail, level: 1 };
		}

		return true;
	};

	const handleRemove = (index: number): boolean => {
		console.log('remove', index);
		if (selected && selected instanceof Array) {
			(<Enchant[]>selected).splice(index, 1);
			selected = [...selected];
		} else {
			selected = undefined;
		}

		return true;
	};
</script>

{#if multiple}
	{#if selected && selected instanceof Array}
		{#each selected as value, i}
			<SearchableSelect
				{id}
				{placeholder}
				selected='{value.name}'
				on:select={(e) => handleSelect(e, i)}
				on:remove={() => handleRemove(i)}
				data={any ? ["Any", ...$versionData.ENCHANTS] : $versionData.ENCHANTS}
			/>
			<div class='enchant'>
				<ProInput
					label='Level'
					type='number'
					bind:value={value.level}
				/>
			</div>
		{/each}
	{/if}
	<SearchableSelect
		bind:this={input}
		{id}
		{placeholder}
		on:select={handleSelect}
		data={any ? ["Any", ...$versionData.ENCHANTS] : $versionData.ENCHANTS}
	/>
{:else}
	<SearchableSelect
		{id}
		{placeholder}
		bind:selected={selected}
		data={any ? ["Any", ...$versionData.ENCHANTS] : $versionData.ENCHANTS}
	/>
{/if}

<style>
    .enchant {
        display: grid;
        grid-template-columns: 1fr 0.5fr;
    }
</style>