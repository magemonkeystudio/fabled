<script lang='ts'>
    export let title = '';
    export let icon = '';
    export let color = 'red';

    const keypress = (e: KeyboardEvent) => e.key === 'Enter' && (<HTMLElement>e.target).click();
</script>

<div class='control'
     style:--color={color}
     {title}
     tabindex='0'
     role='button'
     on:click|stopPropagation
     on:keypress|stopPropagation={keypress}
>
    {#if icon}
        <span class='material-symbols-rounded'>{icon}</span>
    {/if}
    <slot></slot>
</div>

<style>
    .control {
        user-select: none;
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        background: var(--color);
        padding: 0.25rem;
        border-radius: 0.4rem;
        margin: 0.15rem;
        border: 2px solid rgba(0, 0, 0, 0.4);
        color: rgba(0, 0, 0, 0.4);
        transition: color 0.3s ease, background 0.3s ease;
    }

    .control:hover {
        cursor: pointer;
        box-shadow: inset 0 0 0.5rem rgba(0, 0, 0, 0.4);
        background: color-mix(in srgb, var(--color), black 10%);
    }

    .control:active {
        background: color-mix(in srgb, var(--color), black 20%);
    }
</style>