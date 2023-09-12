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
            data.preview.filter((dat: ComponentOption) => (dat['dataSource'])).forEach((dat: DropdownSelect) => dat.init());
    }
</script>

<Modal bind:open={modalOpen} on:close width='70%'>
    {#if data.isDeprecated}<h2><s>{data.name}</s> <small>deprecated</small></h2>{:else}
        <h2>{data.name}</h2>{/if}
    {#if data.description}
        <div class='modal-desc'>{data.description}</div>
    {/if}
    <hr/>
    <div class='component-entry'>
        {#each data.preview as datum}
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