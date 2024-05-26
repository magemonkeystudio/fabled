<script lang='ts'>
	import { active, activeType, setImporting, toggleSidebar } from '../data/store';
	import { get }                                             from 'svelte/store';
	import { createPaste }                                     from '$api/hastebin';
	import YAML                                                from 'yaml';
	import type FabledClass                                    from '../data/class-store';
	import type FabledSkill                                    from '../data/skill-store';
	import type FabledAttribute                                from '$api/fabled-attribute';

	const haste = () => {
		let act: FabledClass | FabledSkill | FabledAttribute | undefined = get(active);
		if (!act) return;

		let data = YAML.stringify({ [act.name]: act.serializeYaml() }, { lineWidth: 0, aliasDuplicateObjects: false });
		createPaste(data)
			.then((urlToPaste) => {
				navigator?.clipboard?.writeText(urlToPaste);
				window.open(urlToPaste);
			})
			.catch((requestError) => console.error(requestError));
	};

	const openImport = () => {
		setImporting(true);
	};
</script>

<div class='nav-wrap'>
	<nav>
		<div class='chip hamburger'
				 tabindex='0'
				 role='button'
				 on:click|stopPropagation={toggleSidebar}
				 on:keypress={(e) => {
					 if (e.key === 'Enter') {
             e.stopPropagation();
             toggleSidebar();
           }
         }}
		>
			<span class='material-symbols-rounded'>menu</span>
		</div>

		<div />

		<div class='transfer'>
			<div class='chip import'
					 tabindex='0'
					 role='button'
					 on:click|stopPropagation={openImport}
					 on:keypress={(e) => {
						 if (e.key === 'Enter') {
							 e.stopPropagation();
							 openImport();
						 }
					 }}
					 title='Import Data'>
				Import
			</div>

			{#if $activeType}
				<div class='chip share'
						 tabindex='0'
						 role='button'
						 on:click|stopPropagation={haste}
						 on:keypress={(e) => {
							 if (e.key === 'Enter') {
								 e.stopPropagation();
								 haste();
							 }
						 }}
						 title='Share {$activeType.substring(0, 1).toUpperCase() + $activeType.substring(1)}'>
					Share {$activeType.substring(0, 1).toUpperCase() + $activeType.substring(1)}
				</div>
			{/if}
		</div>
	</nav>
</div>

<style>
    .nav-wrap {
        position: sticky;
        top: 0;
        z-index: 25;
    }

    nav {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr;
        background-color: #444;
        align-items: center;
        justify-content: space-between;
        padding: 0.5rem;
    }

    nav .chip {
        background: var(--color-bg);
        font-weight: bold;
        padding: 0.25rem;
        font-size: 1.1rem;
        text-align: center;
        border-radius: 100vw;
        white-space: nowrap;
    }

    nav .chip:not(.hamburger) {
        border-radius: 0.5rem;
        padding: 0.25rem 0.5rem;
        margin-left: 0.5rem;
        background-color: #004f8f;
        border: 1px solid var(--color-fg);
    }

    nav .chip.import {
        background-color: #077e1c;
    }

    .chip:hover {
        cursor: pointer;
    }

    .chip.hamburger {
        display: flex;
        justify-self: flex-start;
        align-items: center;
        justify-content: center;
        width: 3.5rem;
        font-size: 1.5rem;
    }

    .transfer {
        display: flex;
        justify-content: flex-end;
    }
</style>