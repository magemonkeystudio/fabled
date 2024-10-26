<script lang="ts">
	import SearchableSelect from './SearchableSelect.svelte';
	import { versionData } from '../../version/data';
	import type { Enchant } from '$api/options/enchantselect';
	import ProInput from '$input/ProInput.svelte';

	interface Props {
		id?: string | undefined;
		placeholder?: string;
		multiple?: boolean;
		any?: boolean;
		selected?: Enchant[] | Enchant | undefined;
	}

	let {
		id = undefined,
		placeholder = '',
		multiple = false,
		any = false,
		selected = $bindable(undefined)
	}: Props = $props();

	let input: SearchableSelect | undefined = $state();

	const handleSelect = (e: CustomEvent<string>, index = -1) => {
		if (multiple) {
			selected = selected || [];

			if (index !== -1) {
				(<Enchant[]>selected)[index] = { name: e.detail, level: 1 };
			} else {
				(<Enchant[]>selected).push({ name: e.detail, level: 1 });
			}

			input?.focus();

			e.preventDefault();
		} else {
			selected = { name: e.detail, level: 1 };
		}
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
				selected={value.name}
				on:select={(e) => handleSelect(e, i)}
				on:remove={() => handleRemove(i)}
				data={any ? ['Any', ...$versionData.ENCHANTS] : $versionData.ENCHANTS}
			/>
			<div class="enchant">
				<ProInput label="Level" bind:value={value.level} />
			</div>
		{/each}
	{/if}
	<SearchableSelect
		bind:this={input}
		{id}
		{placeholder}
		on:select={handleSelect}
		data={any ? ['Any', ...$versionData.ENCHANTS] : $versionData.ENCHANTS}
	/>
{:else}
	<SearchableSelect
		{id}
		{placeholder}
		bind:selected
		data={any ? ['Any', ...$versionData.ENCHANTS] : $versionData.ENCHANTS}
	/>
{/if}

<style>
	.enchant {
		display: grid;
		grid-template-columns: 1fr 0.5fr;
	}
</style>
