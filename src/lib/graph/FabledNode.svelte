<script lang="ts">
    import { Handle, Position } from '@xyflow/svelte';
    import type { FabledGraphNode } from './types';

    let { data, selected }: { data: FabledGraphNode['data']; selected: boolean } = $props();

    const colorMap: Record<string, string> = {
        trigger:   '#0083ef',
        condition: '#feac00',
        target:    '#04af38',
        mechanic:  '#ff3a3a'
    };
    const color = $derived(colorMap[data.typeName] || '#888');
    const icon = $derived(
        data.typeName === 'trigger' ? '⚡' :
        data.typeName === 'condition' ? '◇' :
        data.typeName === 'target' ? '◎' :
        '⚙'
    );

    let showAdd = $state(false);

    function handleDblClick(handleType: 'source' | 'target', e: MouseEvent) {
        e.stopPropagation();
        e.preventDefault();
        data.onDisconnect?.(handleType);
    }
</script>

<div
    class="fn" class:selected
    style="border-top-color: {color}"
    onmouseenter={() => showAdd = true}
    onmouseleave={() => showAdd = false}
>
    <!-- target handle (top) - dblclick to disconnect from parent -->
    <Handle
        id="t" type="target" position={Position.Top}
        ondblclick={(e: MouseEvent) => handleDblClick('target', e)}
    />

    <div class="fn-head">
        <span class="fn-icon" style="color:{color}">{icon}</span>
        <span class="fn-type" style="background:{color}20;color:{color}">{data.typeName}</span>
        {#if data.childCount > 0}
            <span class="fn-count">{data.childCount}</span>
        {/if}
    </div>

    <div class="fn-name">{data.label}</div>

    {#if data.summary}
        <div class="fn-summary">{data.summary}</div>
    {/if}

    <!-- Add child button (visible on hover) -->
    {#if showAdd && data.onAddChild}
        <button class="fn-add-btn" onclick={data.onAddChild} title="Add child component">+</button>
    {/if}

    <!-- source handle (bottom) - dblclick to disconnect children -->
    <Handle
        id="s" type="source" position={Position.Bottom}
        ondblclick={(e: MouseEvent) => handleDblClick('source', e)}
    />
</div>

<style>
    .fn {
        position: relative;
        background: #21262d;
        border: 1px solid #388bfd33;
        border-top: 3px solid #888;
        border-radius: 10px;
        padding: 10px 14px;
        min-width: 180px;
        max-width: 240px;
        font-size: 13px;
        transition: box-shadow 0.12s, border-color 0.12s;
    }
    .fn.selected {
        box-shadow: 0 0 0 2px #58a6ff;
        border-color: #58a6ff;
    }
    .fn:hover { border-color: #58a6ff88; }

    .fn-head {
        display: flex;
        align-items: center;
        gap: 6px;
        margin-bottom: 6px;
    }
    .fn-icon { font-size: 14px; }
    .fn-type {
        font-size: 10px;
        font-weight: 600;
        text-transform: uppercase;
        padding: 1px 6px;
        border-radius: 4px;
        letter-spacing: 0.5px;
    }
    .fn-count {
        margin-left: auto;
        font-size: 10px;
        color: #8b949e;
        background: #21262d;
        padding: 1px 6px;
        border-radius: 10px;
    }
    .fn-name {
        font-weight: 600;
        color: #e6edf3;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }
    .fn-summary {
        color: #8b949e;
        font-size: 11px;
        margin-top: 3px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }
    .fn-add-btn {
        position: absolute;
        bottom: -10px;
        right: -10px;
        width: 22px;
        height: 22px;
        border-radius: 50%;
        background: #1f6feb;
        color: #fff;
        border: 2px solid #0d1117;
        font-size: 14px;
        line-height: 1;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 0;
        z-index: 10;
        transition: transform 0.12s;
    }
    .fn-add-btn:hover {
        transform: scale(1.15);
        background: #388bfd;
    }
</style>
