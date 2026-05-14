<script lang='ts'>
	import { closeSidebar, loadProgress, shownTab, sidebarOpen } from '../../data/store';
	import SidebarEntry                            from './SidebarEntry.svelte';
	import { squish }                              from '../../data/squish';
	import { goto }                                from '$app/navigation';
	import { onDestroy, onMount }                  from 'svelte';
	import type { Unsubscriber }                   from 'svelte/store';
	import { get }                                 from 'svelte/store';
	import Folder                                  from '../Folder.svelte';
	import { fly }                                 from 'svelte/transition';
	import { clickOutside }                        from '$api/clickoutside';
	import { browser }                             from '$app/environment';
	import Tabs                                    from '../input/Tabs.svelte';
	import { base }                                from '$app/paths';
	import { socketService }                       from '$api/socket/socket-connector';
	import { Tab }                                 from '$api/tab';
	import FabledSkill, { skillStore }             from '../../data/skill-store.svelte';
	import FabledClass, { classStore }             from '../../data/class-store.svelte';
	import { attributeStore }                      from '../../data/attribute-store';
	import { FabledFolder }                        from '../../data/folder-store.svelte';
	import { sort }                                from '$api/api';
	import VirtualList                             from '../VirtualList.svelte';

	let folders: FabledFolder[]                         = [];
	let tabSub: Unsubscriber;
	let classSub: Unsubscriber;
	let skillSub: Unsubscriber;
	let classIncluded: Array<FabledClass | FabledSkill> = $state([]);
	let skillIncluded: Array<FabledClass | FabledSkill> = $state([]);

	let built = $state(false);

	let width: number   = $state(0);
	let height: number  = $state(0);
	let scrollY: number = $state(0);

	const skills       = skillStore.skills;
	const skillFolders = skillStore.skillFolders;
	const classes      = classStore.classes;
	const classFolders = classStore.classFolders;
	const attributes   = attributeStore.attributes;

	const rebuildFolders = (fold?: FabledFolder[]) => {
		switch (get(shownTab)) {
			case Tab.CLASSES: {
				folders       = fold || get(classFolders);
				classIncluded = [];
				for (let folder of folders) {
					classIncluded.push(...folder.getAllClasses());
				}
				break;
			}
			case Tab.SKILLS: {
				folders       = fold || get(skillFolders);
				skillIncluded = [];
				for (let folder of folders) {
					skillIncluded.push(...folder.getAllSkills());
				}
				break;
			}
		}
	};

	onMount(() => {
		if (!browser) return;

		tabSub   = shownTab.subscribe(() => rebuildFolders());
		classSub = classFolders.subscribe(rebuildFolders);
		skillSub = skillFolders.subscribe(rebuildFolders);
		rebuildFolders();
		built = true;
	});

	onDestroy(() => {
		if (tabSub) tabSub();
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

<svelte:window
	bind:innerHeight={height}
	bind:innerWidth={width}
	bind:scrollY
	onbeforeunload={() => socketService.disconnect()}
/>

<div
	id='sidebar'
	onintroend={() => sidebarOpen.set(true)}
	onoutroend={() => sidebarOpen.set(false)}
	style:--height='100%'
	transition:squish
	use:clickOutside={clickOut}
>
	<div class='type-wrap'>
		<Tabs
			bind:selectedTab={$shownTab}
			color='#111'
			data={['Classes', 'Skills', 'Attributes']}
			inline={false}
		/>
		<hr />
	</div>

	{#if $loadProgress}
		<div class='progress-wrap'>
			<span class='progress-label'>{$loadProgress.label}</span>
			<div class='progress-track'>
				<div
					class='progress-fill'
					style:width='{Math.round($loadProgress.processed / $loadProgress.total * 100)}%'
				></div>
			</div>
			<span class='progress-count'>{$loadProgress.processed} / {$loadProgress.total}</span>
		</div>
	{/if}

	{#if built}
		<div class='list-area'>
			{#if $shownTab === Tab.CLASSES}
				<div class='items' in:fly={{ x: -100 }} out:fly={{ x: -100 }}>
					{#each sort($classFolders) as cf (cf.key)}
						<Folder folder={cf} />
					{/each}
					<div class='vl-wrap'>
						<VirtualList
							items={sort($classes).filter((c) => !classIncluded.includes(c))}
							itemHeight={44}
							key={(item) => item.key}
						>
							{#snippet children({ item: cl })}
								<SidebarEntry
									data={cl}
									onclick={() => goto(`${base}/class/${cl.name}/edit`)}
								>
									{cl.name}{cl.location === 'server' ? '*' : ''}
								</SidebarEntry>
							{/snippet}
						</VirtualList>
					</div>
					<div class='new-row'>
						<div class='new'>
							<span
								tabindex='0'
								role='button'
								onclick={() => classStore.addClass()}
								onkeypress={(e) => e.key === 'Enter' && classStore.addClass()}>New Class</span>
							<span
								class='new-folder'
								tabindex='0'
								role='button'
								onclick={() => classStore.addClassFolder(new FabledFolder())}
								onkeypress={(e) => e.key === 'Enter' && classStore.addClassFolder(new FabledFolder())}
							>New Folder</span>
						</div>
					</div>
				</div>
			{:else if $shownTab === Tab.SKILLS}
				<div class='items' in:fly={{ x: 100 }} out:fly={{ x: 100 }}>
					{#each sort($skillFolders) as sk}
						<Folder folder={sk} />
					{/each}
					<div class='vl-wrap'>
						<VirtualList
							items={sort($skills).filter((s) => !skillIncluded.includes(s))}
							itemHeight={44}
							key={(item) => item.key}
						>
							{#snippet children({ item: sk })}
								<SidebarEntry
									data={sk}
									direction='right'
									onclick={() => goto(`${base}/skill/${sk.name}`)}
								>
									{sk.name}{sk.location === 'server' ? '*' : ''}
								</SidebarEntry>
							{/snippet}
						</VirtualList>
					</div>
					<div class='new-row'>
						<div class='new'>
							<span
								tabindex='0'
								role='button'
								onclick={() => skillStore.addSkill()}
								onkeypress={(e) => e.key === 'Enter' && skillStore.addSkill()}>New Skill</span>
							<span
								class='new-folder'
								tabindex='0'
								role='button'
								onclick={() => skillStore.addSkillFolder(new FabledFolder())}
								onkeypress={(e) => e.key === 'Enter' && skillStore.addSkillFolder(new FabledFolder())}
							>New Folder</span>
						</div>
					</div>
				</div>
			{:else if $shownTab === Tab.ATTRIBUTES}
				<div class='items' in:fly={{ x: 100 }} out:fly={{ x: 100 }}>
					<div class='vl-wrap'>
						<VirtualList
							items={sort($attributes)}
							itemHeight={44}
							key={(item) => item.key}
						>
							{#snippet children({ item: att })}
								<SidebarEntry
									data={att}
									direction='right'
									onclick={() => goto(`${base}/attribute/${att.name}/edit`)}
								>
									{att.name}{att.location === 'server' ? '*' : ''}
								</SidebarEntry>
							{/snippet}
						</VirtualList>
					</div>
					<div class='new-row'>
						<div class='new'>
							<span
								tabindex='0'
								role='button'
								onclick={() => attributeStore.addAttribute()}
								onkeypress={(e) => e.key === 'Enter' && attributeStore.addAttribute()}
							>New Attribute</span>
						</div>
					</div>
				</div>
			{/if}
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
        width: 75%;
        display: flex;
        flex-direction: column;
        overflow: hidden;
    }

    hr {
        margin-bottom: 0;
    }

    .type-wrap {
        flex-shrink: 0;
        z-index: 2;
        background-color: #222;
        padding: 0.4rem;
        user-select: none;
        -webkit-user-select: none;
    }

    /* Progress bar */
    .progress-wrap {
        flex-shrink: 0;
        padding: 0.4rem 0.6rem 0.3rem;
        background-color: #1a1a1a;
        border-bottom: 1px solid #333;
        display: flex;
        flex-direction: column;
        gap: 0.2rem;
    }

    .progress-label {
        font-size: 0.7rem;
        color: #aaa;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .progress-track {
        width: 100%;
        height: 4px;
        background-color: #333;
        border-radius: 2px;
        overflow: hidden;
    }

    .progress-fill {
        height: 100%;
        background-color: #0083ef;
        border-radius: 2px;
        transition: width 0.1s linear;
    }

    .progress-count {
        font-size: 0.65rem;
        color: #666;
        text-align: right;
    }

    /* List area fills remaining height; acts as the positioning context for .items */
    .list-area {
        flex: 1;
        min-height: 0;
        position: relative;
        overflow: hidden;
    }

    /* Each tab's content pane fills the list-area absolutely so fly transitions overlap cleanly */
    .items {
        position: absolute;
        inset: 0;
        display: flex;
        flex-direction: column;
    }

    /* VirtualList wrapper — takes all remaining height after folders + new-button */
    .vl-wrap {
        flex: 1;
        min-height: 0;
    }

    /* "New X / New Folder" row — sticky at the bottom of the list pane */
    .new-row {
        position: sticky;
        bottom: 0;
        flex-shrink: 0;
        margin-top: 0.5rem;
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
        }
    }
</style>
