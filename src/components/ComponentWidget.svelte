<!--suppress CssUnresolvedCustomProperty -->
<script lang="ts">
  import ProComponent                                  from "$api/components/procomponent";
  import ProTrigger                                    from "$api/components/triggers";
  import ProCondition                                  from "$api/components/conditions";
  import ProTarget                                     from "$api/components/targets";
  import ProMechanic                                   from "$api/components/mechanics";
  import { slide }                                     from "svelte/transition";
  import { backOut }                                   from "svelte/easing";
  import { draggingComponent }                         from "../data/store";
  import Modal                                         from "$components/Modal.svelte";
  import ProInput                                      from "$input/ProInput.svelte";
  import Toggle                                        from "$input/Toggle.svelte";
  import ProSkill                                      from "$api/proskill";
  import { createEventDispatcher, onDestroy, onMount } from "svelte";
  import DropdownSelect                                from "$api/options/dropdownselect";
  import Registry                                      from "$api/components/registry";
  import type { Unsubscriber }                         from "svelte/types/runtime/store";
  import { useSymbols }                                from "../data/settings";
  import type { ComponentOption }                      from "$api/options/options";
  import { get }                                       from "svelte/store";

  export let skill: ProSkill;
  export let component: ProComponent;
  let wrapper: HTMLElement;
  let children: HTMLElement;
  let draggedover: ComponentOption;
  let childrenList: ProComponent[] = [];

  const dispatch = createEventDispatcher();

  let modalOpen      = false;
  let componentModal = false;
  let collapsed      = false;
  let over           = false;
  let overChildren   = false;
  let top            = false;
  let bottom         = false;

  let searchParams = "";

  let targetSub: Unsubscriber;
  let conditionSub: Unsubscriber;
  let mechanicSub: Unsubscriber;
  let childCompsSub: Unsubscriber;

  let targets: { [key: string]: { name: string, component: typeof ProComponent } }    = {};
  let conditions: { [key: string]: { name: string, component: typeof ProComponent } } = {};
  let mechanics: { [key: string]: { name: string, component: typeof ProComponent } }  = {};

  let sortedTargets: Array<{ name: string, component: typeof ProComponent }>;
  let sortedConditions: Array<{ name: string, component: typeof ProComponent }>;
  let sortedMechanics: Array<{ name: string, component: typeof ProComponent }>;

  $: {
    sortedTargets = Object.keys(targets)
      .filter(key => targets[key].name.toLowerCase().includes(searchParams.toLowerCase()))
      .sort()
      .map(key => targets[key]);

    sortedConditions = Object.keys(conditions)
      .filter(key => conditions[key].name.toLowerCase().includes(searchParams.toLowerCase()))
      .sort()
      .map(key => conditions[key]);

    sortedMechanics = Object.keys(mechanics)
      .filter(key => mechanics[key].name.toLowerCase().includes(searchParams.toLowerCase()))
      .sort()
      .map(key => mechanics[key]);
  }

  $: if (component) dispatch("save");
  $: if (modalOpen && component) component.data.filter(dat => (dat["dataSource"])).forEach((dat: DropdownSelect) => dat.init());

  onMount(() => {
    childCompsSub = component.components.subscribe(comps => childrenList = comps);

    targetSub    = Registry.targets.subscribe(tar => targets = tar);
    conditionSub = Registry.conditions.subscribe(con => conditions = con);
    mechanicSub  = Registry.mechanics.subscribe(mech => mechanics = mech);

    modalOpen = component._defaultOpen;
  });

  onDestroy(() => {
    if (childCompsSub) childCompsSub();
    if (targetSub) targetSub();
    if (conditionSub) conditionSub();
    if (mechanicSub) mechanicSub();
  });

  const getName = (symbols = false) => {
    if (symbols) {
      if (component instanceof ProTrigger) {
        return "🚩";
      } else if (component instanceof ProCondition) {
        return "⚠";
      } else if (component instanceof ProTarget) {
        return "🎯";
      } else if (component instanceof ProMechanic) {
        return "🔧";
      }
    }

    if (component instanceof ProTrigger) {
      return "Trigger";
    } else if (component instanceof ProCondition) {
      return "Condition";
    } else if (component instanceof ProTarget) {
      return "Target";
    } else if (component instanceof ProMechanic) {
      return "Mechanic";
    }

    return "???";
  };

  const addComponent = (comp: typeof ProComponent) => {
    component.addComponent(comp.new().defaultOpen());
    componentModal = false;
    searchParams   = "";
    dispatch("save");
  };

  const getColor = () => {
    if (component instanceof ProTrigger) {
      return "#0083ef";
    } else if (component instanceof ProCondition) {
      return "#feac00";
    } else if (component instanceof ProTarget) {
      return "#04af38";
    } else if (component instanceof ProMechanic) {
      return "#ff3a3a";
    }

    return "orange";
  };

  const spin = (node, { duration }) => {
    return {
      duration,
      css: t => {
        const eased = backOut(t);

        return `transform: rotate(${180 - (eased * 180)}deg);`;
      }
    };
  };

  const move = (e) => {
    if (component == get(draggingComponent)) return;
    over = true;
    if (component instanceof ProTrigger) return;
    const rect = wrapper.getBoundingClientRect();

    top    = e.clientY < (rect.height / 2) + rect.top;
    bottom = e.clientY >= (rect.height / 2) + rect.top;
  };

  const clearStatus = () => {
    over   = false;
    top    = false;
    bottom = false;
  };

  const leave = (e) => {
    if (!e.relatedTarget || !wrapper?.contains(e.relatedTarget) || children?.contains(e.relatedTarget)) {
      clearStatus();
    }
  };

  const drop = () => {
    let comp = get(draggingComponent);

    dispatch("addskill", { comp, relativeTo: component, above: top });
    clearStatus();
  };

  const addSkill = (e) => {
    let comp       = e.detail.comp;
    let relativeTo = e.detail.relativeTo;
    let above      = e.detail.above;
    let index      = childrenList.indexOf(relativeTo);

    skill.removeComponent(comp);
    component.addComponent(comp, index + (!above ? 1 : 0));
    dispatch("save");
  };
</script>

<div class="wrapper">
  <div out:slide
       bind:this={wrapper}
       draggable="true"
       on:dragstart|stopPropagation={() => draggingComponent.set(component)}
       on:dragend={() => draggingComponent.set(undefined)}
       on:drop|stopPropagation={drop}
       on:click|stopPropagation={() => modalOpen = true}
       on:dragover|preventDefault|stopPropagation={move}
       on:dragleave|stopPropagation={leave}
       class="comp-body"
       class:over
       class:top
       class:bottom
       class:dragging={$draggingComponent === component}
       style:--comp-color={getColor()}>
    {#if collapsed}
      <span class="material-symbols-rounded" in:spin|local={{duration: 400}}>expand_more</span>
    {:else}
      <span class="material-symbols-rounded" in:spin|local={{duration: 400}}>expand_less</span>
    {/if}
    <div class="corner" on:click|stopPropagation={() => collapsed = !collapsed}/>
    <div class="name"><span>{getName($useSymbols)}</span>{($useSymbols ? ' ' : ': ') + component.name}</div>

    {#if !collapsed}
      <div class="controls">
        <div class="material-symbols-rounded control copy"
             title="Copy"
             transition:slide
             on:click|stopPropagation={() => console.log('clicked copy')}
        >content_copy
        </div>
        <div class="material-symbols-rounded control delete"
             title="Delete"
             transition:slide
             on:click|stopPropagation={() => {
               skill.removeComponent(component);
               dispatch("update");
             }}
        >delete
        </div>
      </div>
      <div class="children" transition:slide|local>
        {#if childrenList.length == 0}
          {#if component.isParent && (over || overChildren)}
            <div class="filler"
                 transition:slide
                 class:overChildren
                 on:dragenter|stopPropagation={() => {
                   overChildren = true;
                   over = false;
                 }}
                 on:dragover|preventDefault|stopPropagation={() => {

                 }}
                 on:dragleave={(e) => {
                   overChildren = false;
                   if (wrapper?.contains(e.relatedTarget) && !children?.contains(e.relatedTarget)) over = true;
                 }}
                 on:drop|preventDefault|stopPropagation={() => {
                   overChildren = false;

                   let comp = get(draggingComponent);
                   skill.removeComponent(comp);
                   component.addComponent(comp);
                 }}/>
          {/if}
        {:else}
          <div class="child-wrapper" bind:this={children}>
            {#each childrenList as child (child.id)}
            <span transition:slide|local>
              <svelte:self {skill} bind:component={child} on:update on:save on:addskill={addSkill}/>
            </span>
            {/each}
          </div>
        {/if}
        {#if component.isParent}
          <div class="chip" on:click|stopPropagation={() => componentModal = true}>
            + Add Component
          </div>
        {/if}
      </div>
    {/if}
  </div>
</div>

<Modal bind:open={modalOpen} width="70%">
  <h2>{component.name}</h2>
  {#if component.description}
    <div class="modal-desc">{component.description}</div>
  {/if}
  <hr/>
  <div class="component-entry">
    {#if component instanceof ProTrigger && component.name != 'Cast' && component.name != 'Initialize' && component.name != 'Cleanup'}
      <ProInput label="Mana" tooltip="[mana] Whether this trigger requires the mana cost to activate">
        <Toggle bind:data={component.mana}/>
      </ProInput>
      <ProInput label="Cooldown" tooltip="[cooldown] Whether this trigger requires to be off cooldown to activate">
        <Toggle bind:data={component.cooldown}/>
      </ProInput>
    {:else if component instanceof ProTarget || component instanceof ProCondition || component instanceof ProMechanic}
      <ProInput label="Icon Key" bind:value={component.iconKey}
                tooltip={'[icon-key] The key used by the component in the Icon Lore. If this is set to "example" and has a value name of "value", it can be referenced using the string "{attr:example.value}"'}/>
    {/if}
    {#if component instanceof ProMechanic}
      <ProInput label="Counts as Cast"
                tooltip={'[counts] Whether this mechanic running treats the skill as "casted" and will consume mana and start the cooldown. Set to false if it is a mechanic applled when the skill fails such as cleanup or an error message"'}>
        <Toggle bind:data={component.countsAsCast}/>
      </ProInput>
    {/if}

    {#each component.data as datum}
      {#if datum.meetsRequirements(component)}
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

<Modal bind:open={componentModal} width="70%">
  <div class="modal-header-wrapper">
    <div/>
    <h2>Add a Component</h2>
    <div class="search-bar">
      <ProInput bind:value={searchParams} placeholder="Search..."/>
    </div>
  </div>
  {#if sortedTargets.length > 0}
    <hr/>
    <div class="comp-modal-header">
      <h3>Targets</h3>
    </div>
    <div class="triggers">
      {#each sortedTargets as target}
        <div class="comp-select" on:click={() => addComponent(target.component)}>{target.name}</div>
      {/each}
    </div>
  {/if}
  {#if sortedConditions.length > 0}
    <hr/>
    <div class="comp-modal-header">
      <h3>Conditions</h3>
    </div>
    <div class="triggers">
      {#each sortedConditions as condition}
        <div class="comp-select" on:click={() => addComponent(condition.component)}>{condition.name}</div>
      {/each}
    </div>
  {/if}
  {#if sortedMechanics.length > 0}
    <hr/>
    <div class="comp-modal-header">
      <h3>Mechanics</h3>
    </div>
    <div class="triggers">
      {#each sortedMechanics as mechanic}
        <div class="comp-select" on:click={() => addComponent(mechanic.component)}>{mechanic.name}</div>
      {/each}
    </div>
  {/if}
  <hr/>
  <div class="cancel" on:click={() => componentModal = false}>Cancel</div>
</Modal>

<style>
    span.material-symbols-rounded {
        color: rgba(0, 0, 0, 0.4);
        position: absolute;
        top: 0;
        right: 0;
        z-index: 3;
        pointer-events: none;
        /*text-shadow: 0 0 0.1rem black;*/
    }

    .comp-body {
        display: flex;
        flex: 1;
        flex-direction: column;
        align-items: stretch;
        justify-items: center;
        padding: 0.25rem;
        background-color: #333;
        box-shadow: inset 0 0 0.5rem #111;
        border: 0.13rem solid #444;
        border-left: 0.3rem solid var(--comp-color);
        border-radius: 0 0.4rem 0.4rem 0;
        overflow: hidden;
        user-select: none;
        transition: border 0.2s ease;
    }

    .corner {
        position: absolute;
        right: -3rem;
        top: 0;
        height: 2rem;
        width: 4rem;
        z-index: 2;
        transform-origin: 50% 0;
        transform: rotate(45deg);
        background-color: var(--comp-color);
        /*border-left: 1.3rem solid transparent;*/
        transition: right 0.25s ease;
        box-shadow: inset 0 -2px 0.5rem #222;
    }

    .corner:hover {
        right: -2rem;
        cursor: pointer;
    }

    .name {
        margin: 0.5rem 1rem 0 0.25rem;
    }

    .name span {
        font-weight: bold;
        color: var(--comp-color);
    }

    .children {
        display: flex;
        flex-direction: column;
        align-items: stretch;
    }

    .chip {
        background-color: #1a1a1a;
        margin: 0.5rem 0.25rem 0.25rem;
    }

    .chip:hover {
        background-color: #0083ef;
        cursor: pointer;
    }

    .component-entry {
        display: grid;
        grid-template-columns: calc(50% - 3rem) calc(50% + 3rem);
        width: 100%;
        padding-inline: 0.5rem;
        padding-top: 0.25rem;
    }

    .wrapper {
        display: flex;
    }

    .control {
        user-select: none;
        flex: 1;
        text-align: center;
        background: red;
        padding: 0.25rem;
        border-radius: 0.4rem;
        margin: 0.15rem;
        border: 2px solid rgba(0, 0, 0, 0.4);
        color: rgba(0, 0, 0, 0.4);
        transition: color 0.3s ease, background 0.3s ease;
    }

    .copy {
        background: #0083ef;
    }

    .control:hover {
        cursor: pointer;
        background: #ff5656;
    }

    .control.copy:hover {
        background: #00a5ff;
    }

    .control:active {
        color: rgba(255, 255, 255, 0.5);
        box-shadow: inset 0 0 0.5rem rgba(0, 0, 0, 0.4);
        background: #b40000;
    }

    .control.copy:active {
        background: #006bc2;
    }

    .comp-modal-header {
        display: flex;
        justify-content: space-between;
        width: 100%;
    }

    h3 {
        text-decoration: underline;
    }

    .controls {
        display: flex;
        justify-content: stretch;
        align-items: center;
    }

    .modal-desc {
        max-width: 100%;
        white-space: break-spaces;
        text-align: center;
    }

    .over:not(.dragging).bottom {
        border-bottom: 1rem solid #0083ef;
    }

    .over:not(.dragging).top {
        border-top: 1rem solid #0083ef;
    }

    .dragging {
        opacity: 0.2;
    }

    .filler {
        height: 3rem;
        border: 5px dashed #666;
        border-radius: 0.5rem;
    }

    .overChildren {
        border: 5px solid #0083ef;
    }
</style>