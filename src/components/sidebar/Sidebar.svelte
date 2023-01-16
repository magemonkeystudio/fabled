<script lang="ts">
  import {
    addClass,
    addClassFolder,
    addSkill,
    addSkillFolder,
    classes,
    classFolders,
    closeSidebar,
    isShowClasses,
    showClasses,
    showSkills,
    sidebarOpen,
    skillFolders,
    skills
  } from "../../data/store";
  import SidebarEntry from "./SidebarEntry.svelte";
  import { squish } from "../../data/squish";
  import { goto } from "$app/navigation";
  import { onDestroy, onMount } from "svelte";
  import type { Unsubscriber } from "svelte/store";
  import { get } from "svelte/store";
  import { ProFolder } from "../../api/profolder";
  import { ProClass } from "../../api/proclass";
  import { ProSkill } from "../../api/proskill";
  import Folder from "../Folder.svelte";
  import { fly } from "svelte/transition";
  import { clickOutside } from "../../api/clickoutside";

  let folders: ProFolder[] = [];
  let classSub: Unsubscriber;
  let skillSub: Unsubscriber;
  let classIncluded: Array<ProClass | ProSkill> = [];
  let skillIncluded: Array<ProClass | ProSkill> = [];

  let width: number;
  let height: number;
  let scrollY: number;

  onMount(() => {
    classSub = classFolders.subscribe(fold => {
      if (!get(isShowClasses)) return;
      classIncluded = [];
      const appendIncluded = (item: Array<ProFolder | ProClass | ProSkill> | ProFolder | ProClass | ProSkill) => {
        if (item instanceof Array) item.forEach(fold => appendIncluded(fold));
        if (item instanceof ProFolder) appendIncluded(item.data);
        else if (item instanceof ProClass) classIncluded.push(item);
      };

      appendIncluded(fold);
    });

    skillSub = skillFolders.subscribe(fold => {
      if (get(isShowClasses)) return;
      folders = fold;
      skillIncluded = [];
      const appendIncluded = (item: Array<ProFolder | ProClass | ProSkill> | ProFolder | ProClass | ProSkill) => {
        if (item instanceof Array) item.forEach(fold => appendIncluded(fold));
        if (item instanceof ProFolder) appendIncluded(item.data);
        else if (item instanceof ProSkill) skillIncluded.push(item);
      };

      appendIncluded(fold);
    });
  });

  onDestroy(() => {
    if (classSub) classSub();
    if (skillSub) skillSub();
  });

  const clickOut = (e: any) => {
    if (width < 500) {
      e.detail.stopPropagation();
      closeSidebar();
    }
  };
</script>

<svelte:window bind:innerWidth={width} bind:innerHeight={height} bind:scrollY={scrollY} />

<div id="sidebar" transition:squish
     use:clickOutside
     on:outclick={clickOut}
     on:introend={() => sidebarOpen.set(true)}
     on:outroend={() => sidebarOpen.set(false)}
     style:--height="calc({height}px - 6rem + min(3.5rem, {scrollY}px))">
  <div class="type-wrap">
    <div id="type-selector" class:c-selected={$isShowClasses}>
      <div class="classes" on:click={showClasses}>Classes</div>
      <div class="skills" on:click={showSkills}>Skills</div>
    </div>
    <hr />
  </div>
  {#if $isShowClasses}
    <div class="items"
         in:fly={{x: -100}}
         out:fly={{x: -100}}>
      {#each $classFolders as cf}
        <Folder folder={cf} />
      {/each}
      {#each $classes.filter(c => !classIncluded.includes(c)) as cl, i (cl.key)}
        <SidebarEntry
          data={cl}
          delay={200 + 100*i}
          on:click={() => goto(`/class/${cl.name}`)}>
          {cl.name}
        </SidebarEntry>
      {/each}
      <SidebarEntry
        delay={200 + 100*($classes.length+1)}>
        <div class="new">
          <span on:click={() => addClass()}>New Class</span>
          <span class="new-folder"
                on:click={() => addClassFolder(new ProFolder())}>New Folder</span>
        </div>
      </SidebarEntry>
    </div>
  {:else}
    <div class="items"
         in:fly={{ x: 100 }}
         out:fly={{ x: 100 }}>
      {#each $skillFolders as sk}
        <Folder folder={sk} />
      {/each}
      {#each $skills.filter(s => !skillIncluded.includes(s)) as sk, i (sk.key)}
        <SidebarEntry
          data={sk}
          direction="right"
          delay={200 + 100*i}
          on:click={() => goto(`/skill/${sk.name}`)}>
          {sk.name}
        </SidebarEntry>
      {/each}
      <SidebarEntry
        delay={200 + 100*($skills.length+1)}
        direction="right">
        <div class="new">
          <span on:click={() => addSkill()}>New Skill</span>
          <span class="new-folder"
                on:click={() => addSkillFolder(new ProFolder())}>New Folder</span>
        </div>
      </SidebarEntry>
    </div>
  {/if}
</div>

<style>
    #sidebar {
        position: sticky;
        top: 3rem;
        z-index: 3;
        background-color: #222;
        padding-bottom: 0.5rem;
        max-height: var(--height);
        height: var(--height);
        overflow-y: auto;
        margin-bottom: calc(-1 * var(--height));
        width: 75%;
    }

    hr {
        margin-bottom: 0;
    }

    .type-wrap {
        position: sticky;
        z-index: 2;
        top: 0;
        background-color: #222;
        padding: 0.4rem;
        user-select: none;
    }

    #type-selector {
        overflow: hidden;
        display: flex;
        text-align: center;
        background-color: #111;
        border-radius: 0.4rem;
    }

    #type-selector:before {
        content: '';
        height: 100%;
        width: 50%;
        border-radius: 0.4rem;
        background-color: #0083ef;
        position: absolute;
        left: 0;
        transition: left 350ms ease-in-out;
    }

    #type-selector:not(.c-selected):before {
        left: 50%;
    }

    #type-selector > div {
        flex: 1;
        padding: 0.2rem;
    }

    #type-selector > div:hover {
        cursor: pointer;
    }

    .items {
        position: absolute;
        width: 100%;
    }

    .new {
        width: 100%;
        display: flex;
        justify-content: space-around;
        margin: 0.3rem;
    }

    .new span {
        display: block;
        flex: 1;
        border-radius: 100vw;
        text-align: center;
        padding: 0.4rem 0.6rem;
        background-color: #333;
    }

    .new span:first-child {
        margin-right: 0.5rem;
    }

    .new span:last-child {
        margin-left: 0.5rem;
    }

    .new span:hover {
        background-color: #0083ef;
    }

    @media screen and (min-width: 500px) {
        #sidebar {
            float: left;
            width: 15rem;
            min-width: 10rem;
            overflow-x: hidden;
            overflow-y: auto;
        }
    }
</style>