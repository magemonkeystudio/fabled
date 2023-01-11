<script lang="ts">
  import {
    addClass,
    addClassFolder,
    addSkill,
    addSkillFolder,
    classes,
    classFolders,
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

  let folders: ProFolder[] = [];
  let classSub: Unsubscriber;
  let skillSub: Unsubscriber;
  let classIncluded: Array<ProClass | ProSkill> = [];
  let skillIncluded: Array<ProClass | ProSkill> = [];

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
</script>

<div id="sidebar" transition:squish
     on:introend={() => sidebarOpen.set(true)}
     on:outroend={() => sidebarOpen.set(false)}>
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
          <span on:click={addClass}>New Class</span>
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
          <span on:click={addSkill}>New Skill</span>
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
        top: 0;
        float: left;
        background-color: #222;
        width: 15rem;
        min-width: 10rem;
        max-width: 25vw;
        overflow-x: hidden;
        overflow-y: auto;
        height: 100%;
        padding-bottom: 0.5rem;
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
    }

    .new span {
        flex: 1;
        text-align: center;
        padding: 0.4rem 0;
    }

    .new span:hover {
        background-color: #333;
    }

    .new .new-folder {
        border-left: 2px solid #222;
    }
</style>