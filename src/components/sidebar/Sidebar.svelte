<!--suppress CssUnresolvedCustomProperty -->
<script lang='ts'>
	import { addClass, addClassFolder, classes, classFolders } from '../../data/class-store';
	import { closeSidebar, isShowClasses, sidebarOpen }        from '../../data/store';
	import SidebarEntry                                        from './SidebarEntry.svelte';
	import { squish }                                          from '../../data/squish';
	import { goto }                                            from '$app/navigation';
	import { beforeUpdate, onDestroy, onMount }                from 'svelte';
	import type { Unsubscriber }                               from 'svelte/store';
	import { get }      from 'svelte/store';
	import FabledFolder from '$api/fabled-folder';
	import FabledClass from '$api/fabled-class';
	import FabledSkill from '$api/fabled-skill';
	import Folder      from '../Folder.svelte';
	import { fly }                                             from 'svelte/transition';
	import { clickOutside }                                    from '$api/clickoutside';
	import { browser }                                         from '$app/environment';
	import Toggle                                              from '../input/Toggle.svelte';
	import { addSkill, addSkillFolder, skillFolders, skills }  from '../../data/skill-store';
	import { base }                                            from '$app/paths';

	let folders: FabledFolder[] = [];
	let classSub: Unsubscriber;
	let skillSub: Unsubscriber;
	let classIncluded: Array<FabledClass | FabledSkill> = [];
	let skillIncluded: Array<FabledClass | FabledSkill> = [];

	let width: number;
	let height: number;
	let scrollY: number;
	const appendIncluded                                = (item: Array<FabledFolder | FabledClass | FabledSkill> | FabledFolder | FabledClass | FabledSkill, include: Array<FabledClass | FabledSkill>) => {
		if (item instanceof Array) item.forEach(fold => appendIncluded(fold, include));
		if (item instanceof FabledFolder) appendIncluded(item.data, include);
		else if (item instanceof FabledClass || item instanceof FabledSkill) include.push(item);
	};

	const rebuildFolders = (fold?: FabledFolder[]) => {
		if (get(isShowClasses)) {
			folders       = fold || get(classFolders);
			classIncluded = [];
			appendIncluded(folders, classIncluded);
		} else {
			folders       = fold || get(skillFolders);
			skillIncluded = [];
			appendIncluded(folders, skillIncluded);
		}
	};

	onMount(() => {
		if (!browser) return;

		classSub = classFolders.subscribe(rebuildFolders);
		skillSub = skillFolders.subscribe(rebuildFolders);
	});

	beforeUpdate(rebuildFolders);

	onDestroy(() => {
		if (classSub) classSub();
		if (skillSub) skillSub();
	});

	const clickOut = (e: MouseEvent) => {
		if (width < 500) {
			e.stopPropagation();
			closeSidebar();
		}
	};
</script>

<svelte:window bind:innerWidth={width} bind:innerHeight={height} bind:scrollY={scrollY} />

<div id='sidebar'
		 transition:squish
		 on:introend={() => sidebarOpen.set(true)}
		 on:outroend={() => sidebarOpen.set(false)}
		 use:clickOutside={clickOut}
		 style:--height='calc({height}px - 6rem + min(3rem, {scrollY}px))'>
	<div class='type-wrap'>
		<Toggle bind:data={$isShowClasses} left='Classes' right='Skills' color='#111' inline={false} />
		<hr />
	</div>
	{#if $isShowClasses}
		<div class='items'
				 in:fly={{x: -100}}
				 out:fly={{x: -100}}>
			{#each $classFolders as cf}
				<Folder folder={cf} />
			{/each}
			{#each $classes.filter(c => !classIncluded.includes(c)) as cl, i (cl.key)}
				<SidebarEntry
					data={cl}
					delay={200 + 100*i}
					on:click={() => goto(`${base}/class/${cl.name}/edit`)}>
					{cl.name}{cl.location === 'server' ? '*' : ''}
				</SidebarEntry>
			{/each}
			<SidebarEntry
				delay={200 + 100*($classes.length+1)}>
				<div class='new'>
					<span tabindex='0'
								role='button'
								on:click={() => addClass()}
								on:keypress={(e) => e.key === 'Enter' && addClass()}>New Class</span>
					<span class='new-folder'
								tabindex='0'
								role='button'
								on:click={() => addClassFolder(new FabledFolder())}
								on:keypress={(e) => e.key === 'Enter' && addClassFolder(new FabledFolder())}>New Folder</span>
				</div>
			</SidebarEntry>
		</div>
	{:else}
		<div class='items'
				 in:fly={{ x: 100 }}
				 out:fly={{ x: 100 }}>
			{#each $skillFolders as sk}
				<Folder folder={sk} />
			{/each}
			{#each $skills.filter(s => !skillIncluded.includes(s)) as sk, i (sk.key)}
				<SidebarEntry
					data={sk}
					direction='right'
					delay={200 + 100*i}
					on:click={() => goto(`${base}/skill/${sk.name}`)}>
					{sk.name}{sk.location === 'server' ? '*' : ''}
				</SidebarEntry>
			{/each}
			<SidebarEntry
				delay={200 + 100*($skills.length+1)}
				direction='right'>
				<div class='new'>
					<span tabindex='0'
								role='button'
								on:click={() => addSkill()}
								on:keypress={(e) => e.key === 'Enter' && addSkill()}>New Skill</span>
					<span class='new-folder'
								tabindex='0'
								role='button'
								on:click={() => addSkillFolder(new FabledFolder())}
								on:keypress={(e) => e.key === 'Enter' && addSkillFolder(new FabledFolder())}>New Folder</span>
				</div>
			</SidebarEntry>
		</div>
	{/if}
</div>

<style>
    #sidebar {
        position: absolute;
        top: 0;
        left: 0;
        z-index: 30;
        background-color: #222;
        max-height: var(--height);
        height: var(--height);
        overflow-y: auto;
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
        -webkit-user-select: none;
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
        display: grid;
        place-items: center;
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
            position: sticky;
            top: 3rem;
            width: 15rem;
            min-width: 10rem;
            overflow-x: hidden;
            overflow-y: auto;
        }
    }
</style>