<script lang="ts">
  import {
    addClass,
    classes,
    classFolders,
    closeSidebar,
    isShowClasses,
    showClasses,
    showSkills,
    skills
  } from "../../data/store";
  import SidebarEntry from "./SidebarEntry.svelte";
  import { squish } from "../../data/squish";
  import { goto } from "$app/navigation";
  import { onDestroy, onMount } from "svelte";
  import type { Unsubscriber } from "svelte/store";
  import { ProFolder } from "../../api/profolder";
  import { ProClass } from "../../api/proclass";
  import { ProSkill } from "../../api/proskill";
  import Folder from "../Folder.svelte";

  let folders: ProFolder[] = [];
  let classSub: Unsubscriber;
  let included: Array<ProClass | ProSkill> = [];

  onMount(() => {
    classSub = classFolders.subscribe(fold => {
      folders = fold;
      included = [];
      const appendIncluded = (item: Array<ProFolder | ProClass | ProSkill> | ProFolder | ProClass | ProSkill) => {
        if (item instanceof Array) item.forEach(fold => appendIncluded(fold));
        if (item instanceof ProFolder && item.isFolder) appendIncluded(item.data);
        else if (item instanceof ProSkill || item instanceof ProClass) included.push(item);
      };

      appendIncluded(fold);
    });
  });

  onDestroy(() => {
    if (classSub) classSub();
  });

  const addSkill = () => {
    console.log("Hello!");
  };

  const go = (url: string) => {
    closeSidebar();
    goto(url);
  };
</script>

<div id="sidebar" transition:squish>
  <div class="type-wrap">
    <div id="type-selector" class:c-selected={$isShowClasses}>
      <div class="classes" on:click={showClasses}>Classes</div>
      <div class="skills" on:click={showSkills}>Skills</div>
    </div>
    <hr />
  </div>
  {#if $isShowClasses}
    {#each folders as cf}
      <Folder folder={cf} />
    {/each}
    {#each $classes.filter(c => !included.includes(c)) as cl, i (cl.name)}
      <SidebarEntry
        data={cl}
        delay={200 + 100*i}
        on:click={() => go(`/class/${cl.name}`)} />
    {/each}
    <SidebarEntry
      delay={200 + 100*($classes.length+1)}
      on:click={addClass}>
      + New Class
    </SidebarEntry>
  {:else}
    {#each skills as sk, i (sk.name)}
      <SidebarEntry
        data={sk}
        type="skill"
        delay={200 + 100*i}
        on:click={() => go(`/skill/${sk.name}`)} />
    {/each}
    <SidebarEntry
      delay={200 + 100*(skills.length+1)}
      on:click={addSkill}>
      + New Skill
    </SidebarEntry>
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
</style>