<script lang='ts'>
    import ProTrigger from '$api/components/triggers';
    import ProTarget from '$api/components/targets';
    import ProCondition from '$api/components/conditions';
    import ProMechanic from '$api/components/mechanics';
    import Toggle from '$input/Toggle.svelte';
    import ProInput from '$input/ProInput.svelte';
    import Modal from '$components/Modal.svelte';
    import type ProComponent from '$api/components/procomponent';
    import type DropdownSelect from "$api/options/dropdownselect";
    import type {ComponentOption} from "$api/options/options";

    export let data: ProComponent | undefined = undefined;
    let modalOpen = true;

    $: if (modalOpen && data) {
        data.data.filter((dat: ComponentOption) => (dat['dataSource'])).forEach((dat: DropdownSelect) => dat.init());
    }
</script>

<Modal bind:open={modalOpen} on:close width='70%'>
    <h2 class:deprecated={data.isDeprecated}><span>{data.name}</span></h2>
    {#if data.description}
        <div class='modal-desc'>{data.description}</div>
    {/if}
    <hr/>
    <div class='component-entry'>
        {#if data instanceof ProTrigger && data.name != 'Cast' && data.name != 'Initialize' && data.name != 'Cleanup'}
            <ProInput label='Mana' tooltip='[mana] Whether this trigger requires the mana cost to activate'>
                <Toggle bind:data={data.mana}/>
            </ProInput>
            <ProInput label='Cooldown'
                      tooltip='[cooldown] Whether this trigger requires to be off cooldown to activate'>
                <Toggle bind:data={data.cooldown}/>
            </ProInput>
        {:else if data instanceof ProTarget || data instanceof ProCondition || data instanceof ProMechanic}
            <ProInput label='Icon Key' bind:value={data.iconKey}
                      tooltip={'[icon-key] The key used by the component in the Icon Lore. If this is set to "example" and has a value name of "value", it can be referenced using the string "{attr:example.value}"'}/>
        {/if}
        {#if data instanceof ProMechanic}
            <ProInput label='Counts as Cast'
                      tooltip={'[counts] Whether this mechanic running treats the skill as "casted" and will consume mana and start the cooldown. Set to false if it is a mechanic applled when the skill fails such as cleanup or an error message"'}>
                <Toggle bind:data={data.countsAsCast}/>
            </ProInput>
        {/if}

        {#each data.data as datum}
            {#if datum.meetsRequirements(data)}
                <svelte:component
                        this={datum.component}
                        bind:data={datum.data}
                        name={datum.name}
                        tooltip="{datum.key ? '[' + datum.key + '] ' : ''}{datum.tooltip}"
                        multiple={datum.multiple}
                        on:save/>
            {/if}
        {/each}
    </div>
</Modal>

<style>
    .deprecated {
        align-items: center;
        display: flex;
    }
    .deprecated > span {
        text-decoration: line-through;
    }

    .deprecated::after {
        text-decoration: unset;
        margin-left: 0.5rem;
        content: 'deprecated';
        font-size: 0.6em;
        color: goldenrod;
    }

    .component-entry {
        display: grid;
        grid-template-columns: calc(50% - 3rem) calc(50% + 3rem);
        width: 100%;
        padding-inline: 0.5rem;
        padding-top: 0.25rem;
    }

    .component-entry {
        display: grid;
        grid-template-columns: calc(50% - 3rem) calc(50% + 3rem);
        width: 100%;
        padding-inline: 0.5rem;
        padding-top: 0.25rem;
    }

    .modal-desc {
        max-width: 100%;
        white-space: break-spaces;
        text-align: center;
    }
</style>