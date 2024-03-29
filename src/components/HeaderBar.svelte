<script lang='ts'>
	import DiscordLogo from './DiscordLogo.svelte';
	import { goto }    from '$app/navigation';
	import { base }    from '$app/paths';
</script>

<div id='header'>
	<div class='home'
			 tabindex='0'
			 role='button'
			 on:click={() => goto(`${base}/`)}
			 on:keypress={(e) => e.key === "Enter" && goto(`${base}/`)}
	>
		<h1 class='accent'>Fabled</h1>
		<h2>Dynamic Editor</h2>
		<div class='copy'>&copy; ProMCTeam {new Date().getFullYear()}</div>
	</div>

	<div class='socials'>
		<a class='social discord' href='https://discord.gg/6UzkTe6RvW' title='Join our Discord'>
			<DiscordLogo />
		</a>
		<a class='social github'
			 href='https://github.com/promcteam/proskillapi'
			 title='Check out our GitHub'>
			<img src='{base}/github-mark-white.svg' alt='Github' />
		</a>
		<a class='social spigot'
			 href='https://www.spigotmc.org/resources/proskillapi-create-custom-races-classes-skills-spells-with-an-easy-online-editor.91913/'
			 title='Check out our resource on Spigot'>
			<img src='{base}/spigot.png' alt='Spigot' />
		</a>
		<a class='social wiki'
			 href='https://promcteam.com/wiki/index.php?title=Proskillapi:Proskillapi'
			 title='Wiki'>
			<span class='material-symbols-rounded'>help</span>
		</a>
	</div>
</div>

<style>
    #header {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 0.5rem;
    }

    div > * {
        text-align: center;
    }

    .home {
        display: flex;
        align-items: center;
    }

    h1, h2 {
        display: inline;
        font-family: Mojave-Web, sans-serif;
        margin: 0;
        padding: 0 0 0.5rem;
    }

    h1 {
        font-size: 2rem;
    }

    h2 {
        margin-left: 0.5rem;
        font-size: 1.25rem;
    }

    .socials {
        display: flex;
        justify-content: flex-end;
    }

    .social, .social:visited, .social:active {
        display: flex;
        justify-content: center;
        align-items: center;
        user-select: none;
        -webkit-user-select: none;
        color: var(--color-fg);
        background-color: #0083ef;
        border-radius: 50%;
        height: 2rem;
        aspect-ratio: 1;
        padding: 0.3rem;
        margin: 0 0.2rem;
        text-decoration: none;
    }

    .social img {
        height: 100%;
        aspect-ratio: 1;
    }

    .copy {
        align-self: flex-end;
        font-size: 0.7rem;
        padding-left: 0.5rem;
    }

    @media screen and (min-width: 500px) {
        #header {
            flex-direction: row;
            justify-content: space-between;
        }

        h1, h2 {
            padding: unset;
        }
    }
</style>