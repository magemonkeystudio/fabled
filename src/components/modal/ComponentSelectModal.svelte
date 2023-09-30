<script lang='ts'>
    import ProInput from '$input/ProInput.svelte';
    import Modal from '$components/Modal.svelte';
    import {closeModal} from '../../data/modal-service';
    import type ProComponent from '$api/components/procomponent';
    import {createEventDispatcher, onDestroy, onMount} from 'svelte';
    import Registry from '$api/components/registry';
    import type {Unsubscriber} from 'svelte/types/runtime/store';

    export let data: ProComponent;

    let searchParams = '';

    let targets: { [key: string]: { name: string, component: typeof ProComponent } } = {};
    let conditions: { [key: string]: { name: string, component: typeof ProComponent } } = {};
    let mechanics: { [key: string]: { name: string, component: typeof ProComponent } } = {};

    let sortedTargets: Array<{ name: string, component: typeof ProComponent }>;
    let sortedConditions: Array<{ name: string, component: typeof ProComponent }>;
    let sortedMechanics: Array<{ name: string, component: typeof ProComponent }>;

    $: {
        sortedTargets = Object.keys(targets)
            .filter(key => targets[key].name.toLowerCase().includes(searchParams.toLowerCase()))
            .sort((a, b) => (targets[a].component.new().isDeprecated ? 0 : -1) - (targets[b].component.new().isDeprecated ? 0 : -1))
            .map(key => targets[key]);

        sortedConditions = Object.keys(conditions)
            .filter(key => conditions[key].name.toLowerCase().includes(searchParams.toLowerCase()))
            .sort((a, b) => (conditions[a].component.new().isDeprecated ? 0 : -1) - (conditions[b].component.new().isDeprecated ? 0 : -1))
            .map(key => conditions[key]);

        sortedMechanics = Object.keys(mechanics)
            .filter(key => mechanics[key].name.toLowerCase().includes(searchParams.toLowerCase()))
            .sort((a, b) => (mechanics[a].component.new().isDeprecated ? 0 : -1) - (mechanics[b].component.new().isDeprecated ? 0 : -1))
            .map(key => mechanics[key]);
    }

    let targetSub: Unsubscriber;
    let conditionSub: Unsubscriber;
    let mechanicSub: Unsubscriber;

    const dispatch = createEventDispatcher();

    onMount(() => {
        targetSub = Registry.targets
            .subscribe((tar: { [key: string]: { name: string, component: typeof ProComponent } }) => targets = tar);
        conditionSub = Registry.conditions
            .subscribe((con: { [key: string]: { name: string, component: typeof ProComponent } }) => conditions = con);
        mechanicSub = Registry.mechanics
            .subscribe((mech: { [key: string]: { name: string, component: typeof ProComponent } }) => mechanics = mech);
    });

    onDestroy(() => {
        if (targetSub) targetSub();
        if (conditionSub) conditionSub();
        if (mechanicSub) mechanicSub();
    });

    const addComponent =
        (comp: typeof ProComponent) => {
            data.addComponent(comp.new().defaultOpen());
            searchParams = '';
            dispatch('save');
            closeModal();
        }
</script>

<Modal width='70%'
       open={true}
       on:close={closeModal}>
    <div class='modal-header-wrapper'>
        <div/>
        <h2>Add a Component</h2>
        <div class='search-bar'>
            <ProInput bind:value={searchParams} placeholder='Search...' autofocus/>
        </div>
    </div>
    {#if sortedTargets.length > 0}
        <hr/>
        <div class='comp-modal-header'>
            <h3>Targets</h3>
        </div>
        <div class='triggers'>
            {#each sortedTargets as target}
                <div class='comp-select' on:click={() => addComponent(target.component)}
                     on:keypress={(e) => {
                       if (e.key === 'Enter') addComponent(target.component);
                     }}
                >
                    {#if target.component.new().isDeprecated}<s>{target.name}</s>{:else}{target.name}{/if}
                </div>
            {/each}
        </div>
    {/if}
    {#if sortedConditions.length > 0}
        <hr/>
        <div class='comp-modal-header'>
            <h3>Conditions</h3>
        </div>
        <div class='triggers'>
            {#each sortedConditions as condition}
                <div class='comp-select' on:click={() => addComponent(condition.component)}
                     on:keypress={(e) => {
                  if (e.key === 'Enter') addComponent(condition.component);
                }}
                >
                    {#if condition.component.new().isDeprecated}<s>{condition.name}</s>{:else}{condition.name}{/if}
                </div>
            {/each}
        </div>
    {/if}
    {#if sortedMechanics.length > 0}
        <hr/>
        <div class='comp-modal-header'>
            <h3>Mechanics</h3>
        </div>
        <div class='triggers'>
            {#each sortedMechanics as mechanic}
                <div class='comp-select' on:click={() => addComponent(mechanic.component)}
                     on:keypress={(e) => {
                       if (e.key === 'Enter') addComponent(mechanic.component);
                     }}
                >
                    {#if mechanic.component.new().isDeprecated}<s>{mechanic.name}</s>{:else}{mechanic.name}{/if}
                </div>
            {/each}
        </div>
    {/if}
    <hr/>
    <div class='cancel' on:click={() => closeModal}
         on:keypress={(e) => {
           if (e.key === 'Enter') closeModal();
         }}
    >Cancel
    </div>
</Modal>

<style>
    .comp-modal-header {
        display: flex;
        justify-content: space-between;
        width: 100%;
    }

    h3 {
        text-decoration: underline;
    }
</style>