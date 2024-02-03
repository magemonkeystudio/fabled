<!--<script lang='ts'>-->
<!--	import ProInput                                     from '$input/ProInput.svelte';-->
<!--	import { fly, slide }                               from 'svelte/transition';-->
<!--	import { messages, socketConnected, socketService } from '$api/socket/socket-connector';-->
<!--	import { onDestroy }                                from 'svelte';-->

<!--	let socketOpen           = false;-->
<!--	let clientName: string   = '';-->
<!--	let socketSecret: string = '';-->

<!--	onDestroy(() => {-->
<!--		socketService.disconnect();-->
<!--	});-->

<!--	const connect = () => {-->
<!--		if (!socketSecret || !clientName) return alert('Please enter a server secret and a name');-->

<!--		// socketService.connect(socketSecret, clientName);-->
<!--	};-->
<!--</script>-->

<!--<svelte:window on:beforeunload={() => socketService.disconnect} />-->

<!--<div id='messages'>-->
<!--	{#each $messages as message (message.id)}-->
<!--		<div class='message' transition:fly={{x: 100}}>-->
<!--			{#if message.event}<span>{ message.event }</span>{/if}-->
<!--			{#if message.from}-->
<!--				<span>from { message.from }</span>-->
<!--			{/if}-->
<!--			{#if message.content}-->
<!--				{#if message.event === 'skillYaml' || message.event === 'classYaml'}-->
<!--					<pre>{ message.content }</pre>-->
<!--				{:else}-->
<!--					<blockquote>{ message.content }</blockquote>-->
<!--				{/if}-->
<!--			{/if}-->
<!--			{#if message.args && Object.keys(message.args).length > 0}-->
<!--				<pre>{ JSON.stringify(message.args, null, 2) }</pre>-->
<!--			{/if}-->
<!--		</div>-->
<!--	{/each}-->
<!--</div>-->

<!--<div id='socket-panel'>-->
<!--	<div class='socket-tab'-->
<!--			 role='button'-->
<!--			 tabindex='0'-->
<!--			 on:click={ () => socketOpen = !socketOpen }-->
<!--			 on:keypress={ e => e.key === 'Enter' ? socketOpen = !socketOpen : null }>-->
<!--		<span class='material-symbols-rounded icon' class:open={socketOpen}>-->
<!--			double_arrow-->
<!--		</span>-->
<!--	</div>-->
<!--	{#if socketOpen}-->
<!--		<div class='socket-panel-contents' transition:slide>-->
<!--			<div class='inputs'>-->
<!--				<ProInput type='string' disabled={$socketConnected} placeholder='Server Secret' bind:value={socketSecret}-->
<!--									disableAnimation={true} />-->
<!--				<ProInput type='string' disabled={$socketConnected} placeholder='Name' bind:value={clientName}-->
<!--									disableAnimation={true} />-->
<!--			</div>-->

<!--			<div class='socket-buttons'>-->
<!--				{#if $socketConnected}-->
<!--					<div class='button center reload'-->
<!--							 role='button'-->
<!--							 tabindex='0'-->
<!--							 on:click={ () => socketService.reloadSapi() }-->
<!--							 on:keypress={ e => e.key === 'Enter' ? socketService.reloadSapi() : null }-->
<!--					>Reload PSAPI-->
<!--					</div>-->
<!--					<div class='button center'-->
<!--							 role='button'-->
<!--							 tabindex='0'-->
<!--							 on:click={ () => socketService.disconnect() }-->
<!--							 on:keypress={ e => e.key === 'Enter' ? socketService.disconnect() : null }-->
<!--					>Disconnect-->
<!--					</div>-->
<!--				{:else}-->
<!--					<div class='button center connect'-->
<!--							 role='button'-->
<!--							 tabindex='0'-->
<!--							 on:click={ connect }-->
<!--							 on:keypress={ e => e.key === 'Enter' ? connect() : null }-->
<!--					>Connect-->
<!--					</div>-->
<!--				{/if}-->
<!--			</div>-->
<!--		</div>-->
<!--	{/if}-->
<!--</div>-->

<!--<style>-->
<!--    #socket-panel {-->
<!--        &#45;&#45;bg: #727272;-->
<!--        position: fixed;-->
<!--        bottom: 0;-->
<!--        left: 0;-->
<!--        right: 0;-->
<!--        z-index: 5;-->
<!--        background: var(&#45;&#45;bg);-->
<!--    }-->

<!--    .socket-panel-contents {-->
<!--        padding-top: 0.5rem;-->
<!--        padding-inline: 0.3rem;-->
<!--    }-->

<!--    .socket-tab {-->
<!--        &#45;&#45;height: 1.5rem;-->

<!--        /* upward chevron to pop out panel */-->
<!--        position: absolute;-->
<!--        left: 50%;-->
<!--        transform: translateX(-50%);-->
<!--        color: white;-->
<!--        cursor: pointer;-->

<!--        box-sizing: border-box;-->
<!--        aspect-ratio: 2;-->
<!--        text-align: center;-->
<!--        height: var(&#45;&#45;height);-->
<!--        top: calc(var(&#45;&#45;height) * -1);-->

<!--        /* make it a circle */-->
<!--        border-top-left-radius: var(&#45;&#45;height);-->
<!--        border-top-right-radius: var(&#45;&#45;height);-->
<!--        background-color: var(&#45;&#45;bg);-->
<!--        padding: 0.25rem;-->

<!--        z-index: 10;-->
<!--    }-->

<!--    .socket-tab .icon {-->
<!--        rotate: -90deg;-->
<!--        position: relative;-->
<!--        top: 0.1rem;-->
<!--        user-select: none;-->

<!--        transition: rotate 0.2s ease;-->
<!--    }-->

<!--    .socket-tab .icon.open {-->
<!--        rotate: 90deg;-->
<!--    }-->

<!--    .socket-buttons, .inputs {-->
<!--        display: grid;-->
<!--        grid-template-columns: 1fr 1fr;-->
<!--        max-width: 400px;-->
<!--        gap: 1rem;-->
<!--        margin: 0 auto;-->
<!--    }-->

<!--    .socket-buttons .button {-->
<!--        border: 1px solid white;-->
<!--    }-->

<!--    .socket-buttons .reload {-->
<!--        background-color: #ff9800;-->
<!--    }-->

<!--    .socket-buttons .connect {-->
<!--        background-color: #1dad36;-->
<!--        grid-column: 1 / -1;-->
<!--    }-->

<!--    #messages {-->
<!--        position: fixed;-->
<!--        top: 0;-->
<!--        right: 0;-->
<!--        z-index: 50;-->
<!--        padding: 0.5rem;-->
<!--        margin: 1rem;-->
<!--        display: flex;-->
<!--        flex-direction: column-reverse;-->
<!--        gap: 0.5rem;-->
<!--    }-->

<!--    .message {-->
<!--        background-color: #727272;-->
<!--        padding: 0.5rem;-->
<!--        border-radius: 0.5rem;-->
<!--        display: flex;-->
<!--        flex-direction: column;-->
<!--        gap: 0.5rem;-->
<!--				max-width: 300px;-->
<!--    }-->

<!--    .message span {-->
<!--        font-weight: bold;-->
<!--    }-->

<!--    blockquote {-->
<!--        margin: 0.5rem;-->
<!--        padding: 0.5rem;-->
<!--        border-left: 3px solid #74f3ff;-->
<!--        font-family: monospace;-->
<!--        font-size: 0.5rem;-->
<!--    }-->

<!--    pre {-->
<!--        font-size: 0.5rem;-->
<!--    }-->
<!--</style>-->