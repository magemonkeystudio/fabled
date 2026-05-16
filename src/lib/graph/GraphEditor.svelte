<script lang="ts">
    import { SvelteFlow, Controls, Background, BackgroundVariant } from '@xyflow/svelte';
    import FabledNode from './FabledNode.svelte';
    import type FabledComponent from '$api/components/fabled-component.svelte';
    import type { YamlComponentData } from '$api/types';
    import type { FabledGraphNode, FabledGraphEdge } from './types';
    import { autoLayout } from './converter';
    import { onMount } from 'svelte';
    import Registry from '$api/components/registry';

    let { skill, triggers, onEditNode, onSave, onAddChild, getSnapshot, restoreSnapshot, fullscreen = false, onToggleFullscreen }: {
        skill: { removeComponent(comp: FabledComponent): void; addComponent(comp: FabledComponent): void };
        triggers: FabledComponent[];
        onEditNode?: (comp: FabledComponent) => void;
        onSave?: () => void;
        onAddChild?: (parent: FabledComponent, onAdded: () => void) => void;
        getSnapshot?: () => string;
        restoreSnapshot?: (yaml: string) => Promise<void>;
        fullscreen?: boolean;
        onToggleFullscreen?: () => void;
    } = $props();

    let nodes = $state<FabledGraphNode[]>([]);
    let edges = $state<FabledGraphEdge[]>([]);
    let selectedNodeIds = $state<string[]>([]);

    const nodeTypes = { 'fabled-node': FabledNode };

    let _rootId = '';
    let _compLookup = new Map<string, FabledComponent>();
    let _orphans = new Set<FabledComponent>();

    // ========= Undo =========
    let _undoStack: string[] = [];
    let _undoIdx = -1;
    const MAX_UNDO = 50;

    function pushUndo() {
        if (!getSnapshot) return;
        const snap = getSnapshot();
        // If the snapshot is the same as the current top, skip
        if (_undoIdx >= 0 && _undoStack[_undoIdx] === snap) return;
        _undoStack = _undoStack.slice(0, _undoIdx + 1);
        _undoStack.push(snap);
        if (_undoStack.length > MAX_UNDO) { _undoStack.shift(); } else { _undoIdx++; }
    }

    async function undo() {
        if (_undoIdx < 0 || !restoreSnapshot) return;
        const snap = _undoStack[_undoIdx];
        _undoIdx--;
        await restoreSnapshot(snap);
        rebuild();
    }

    // ========= Clipboard (in-memory) =========
    let _clipboard: any = null;

    function copySelected() {
        if (!selectedNodeIds.length) return;
        const id = selectedNodeIds[0];
        if (id === _rootId) return;
        const comp = _compLookup.get(id);
        if (!comp) return;
        const yaml = comp.toYamlObj();
        const wrapper: Record<string, any> = {};
        wrapper[comp.name] = yaml;
        _clipboard = wrapper;
    }

    function pasteSelected() {
        if (!_clipboard) return;
        // Determine paste target
        let parent: FabledComponent | undefined;
        if (selectedNodeIds.length > 0 && selectedNodeIds[0] !== _rootId) {
            parent = _compLookup.get(selectedNodeIds[0]);
        }
        if (!parent && triggers.length > 0) parent = triggers[0];
        if (!parent) return;

        // Deserialize from clipboard YAML wrapper
        const cloned = Registry.deserializeComponents(_clipboard as YamlComponentData);
        for (const c of cloned) {
            parent!.addComponent(c);
        }
        onSave?.();
        rebuild();
    }

    function rebuild() {
        _next = 0;
        _compLookup.clear();
        if (!triggers.length && _orphans.size === 0) { nodes = []; edges = []; return; }

        _rootId = uid();
        const rootNode: FabledGraphNode = {
            id: _rootId, type: 'fabled-node',
            position: { x: 0, y: 0 },
            data: { componentId: _rootId, label: '▶ Skill', typeName: 'trigger',
                     summary: '', isCompound: false, childCount: triggers.length }
        };
        const allNodes: FabledGraphNode[] = [rootNode];
        const allEdges: FabledGraphEdge[] = [];
        const seen = new Set<FabledComponent>();

        function walk(comp: FabledComponent, parentId: string, depth: number, sibIdx: number): string {
            const id = uid();
            _compLookup.set(id, comp);
            seen.add(comp);
            const children = getChildren(comp);
            const summary = comp.summaryItems
                .map(k => {
                    const opt = comp.data.find(o => (o as any).key === k || (o as any).matches?.(k));
                    return opt ? String((opt as any).getDisplayValue?.() ?? (opt as any).value ?? '') : '';
                })
                .filter(Boolean).join(' | ');

            allNodes.push({
                id, type: 'fabled-node',
                position: { x: 0, y: 0 },
                data: {
                    componentId: id, label: comp.name, typeName: comp.type,
                    summary, isCompound: false, childCount: children.length,
                    onAddChild: onAddChild ? () => onAddChild(comp, () => {
                        rebuild();
                        const children = getChildren(comp);
                        const newChild = children.find((c: FabledComponent) => c._defaultOpen);
                        if (newChild && onEditNode) {
                            newChild._defaultOpen = false;
                            // Delay: ComponentSelectModal calls closeModal() right after
                            // our callback, which would close our ComponentModal too.
                            onEditNode!(newChild);
                        }
                    }) : undefined,
                    onDisconnect: (ht: 'source' | 'target') => handleDisconnect(comp, ht)
                }
            });

            children.forEach((c, i) => {
                const cid = walk(c, id, depth + 1, i);
                allEdges.push({
                    id: `${id}->${cid}`, source: id, target: cid,
                    sourceHandle: 's', targetHandle: 't',
                    type: 'smoothstep', data: { order: i }
                });
            });
            return id;
        }

        triggers.forEach((t, i) => {
            const firstId = walk(t, _rootId, 0, i);
            allEdges.push({
                id: `${_rootId}->${firstId}`,
                source: _rootId, target: firstId,
                sourceHandle: 's', targetHandle: 't',
                type: 'smoothstep', data: { order: i }
            });
        });

        // Orphans: floating nodes with no edges
        for (const orphan of _orphans) {
            if (seen.has(orphan)) continue;
            const id = uid();
            _compLookup.set(id, orphan);
            seen.add(orphan);
            const children = getChildren(orphan);
            allNodes.push({
                id, type: 'fabled-node',
                position: { x: 0, y: 0 },
                data: {
                    componentId: id, label: orphan.name, typeName: orphan.type,
                    summary: '', isCompound: false, childCount: children.length,
                    onAddChild: onAddChild ? () => onAddChild(orphan, () => {
                        rebuild();
                        const children = getChildren(orphan);
                        const newChild = children.find((c: FabledComponent) => c._defaultOpen);
                        if (newChild && onEditNode) {
                            newChild._defaultOpen = false;
                            onEditNode!(newChild);
                        }
                    }) : undefined,
                    onDisconnect: (ht: 'source' | 'target') => handleDisconnect(orphan, ht)
                }
            });
        }

        autoLayout(allNodes, allEdges);
        nodes = allNodes;
        edges = allEdges;
        selectedNodeIds = [];
    }

    let _next = 0;
    function uid() { return `n${_next++}`; }

    function getChildren(comp: FabledComponent): FabledComponent[] {
        const s = comp.components as any;
        if (!s) return [];
        if (typeof s.subscribe === 'function') {
            let val: FabledComponent[] = [];
            s.subscribe((v: FabledComponent[]) => { val = v; })();
            return Array.isArray(val) ? val : [];
        }
        if (Array.isArray(s)) return s;
        return [];
    }

    $effect(() => { triggers; rebuild(); });

    // ========= Disconnect =========
    function handleDisconnect(comp: FabledComponent, handleType: 'source' | 'target') {
        pushUndo();
        if (handleType === 'target') {
            for (const t of triggers) {
                if (t.contains(comp)) { t.removeComponent(comp); break; }
            }
            _orphans.add(comp);
        } else {
            const kids = [...getChildren(comp)];
            for (const kid of kids) { comp.removeComponent(kid); _orphans.add(kid); }
        }
        onSave?.();
        rebuild();
    }

    // ========= Reconnect / reparent =========
    function onConnect(conn: { source: string; target: string; sourceHandle: string | null; targetHandle: string | null }) {
        pushUndo();
        if (conn.source === _rootId || conn.target === _rootId) {
            const childComp = _compLookup.get(conn.target) || _compLookup.get(conn.source);
            if (!childComp) return;
            for (const t of triggers) {
                if (t.contains(childComp)) { t.removeComponent(childComp); break; }
            }
            _orphans.add(childComp);
            onSave?.();
            rebuild();
            return;
        }

        const childComp = _compLookup.get(conn.target);
        const newParent = _compLookup.get(conn.source);
        if (!childComp || !newParent) return;

        for (const t of triggers) {
            if (t.contains(childComp)) { t.removeComponent(childComp); break; }
        }
        _orphans.delete(childComp);
        newParent.addComponent(childComp);
        onSave?.();
        rebuild();
    }

    // ========= Selection =========
    function onSelectionChange({ nodes: sel }: { nodes: FabledGraphNode[]; edges: FabledGraphEdge[] }) {
        selectedNodeIds = sel.map(n => n.id);
    }

    // ========= Delete =========
    function deleteSelectedNodes() {
        if (!selectedNodeIds.length) return;
        const delIds = new Set(selectedNodeIds);
        if (delIds.has(_rootId)) return;
        for (const id of selectedNodeIds) {
            const comp = _compLookup.get(id);
            if (!comp) continue;
            skill.removeComponent(comp);
            _orphans.delete(comp);
        }
        nodes = nodes.filter(n => !delIds.has(n.id));
        edges = edges.filter(e => !delIds.has(e.source) && !delIds.has(e.target));
        selectedNodeIds = [];
        onSave?.();
    }

    // ========= Keyboard =========
    onMount(() => {
        function handler(e: KeyboardEvent) {
            const tag = (e.target as HTMLElement)?.tagName;
            if (tag === 'INPUT' || tag === 'TEXTAREA' || (e.target as HTMLElement)?.isContentEditable) return;

            if (e.ctrlKey || e.metaKey) {
                if (e.key === 'z' || e.key === 'Z') { e.preventDefault(); undo(); return; }
                if (e.key === 'c' || e.key === 'C') { e.preventDefault(); copySelected(); return; }
                if (e.key === 'v' || e.key === 'V') { e.preventDefault(); pushUndo(); pasteSelected(); return; }
                return;
            }

            if ((e.key === 'Delete' || e.key === 'Backspace') && selectedNodeIds.length) {
                e.preventDefault();
                e.stopPropagation();
                pushUndo();
                deleteSelectedNodes();
            }
        }
        window.addEventListener('keydown', handler, true);
        return () => window.removeEventListener('keydown', handler, true);
    });

    // ========= Double-click =========
    let _clickTimer: ReturnType<typeof setTimeout> | null = null;
    function onNodeClick({ node }: { node: FabledGraphNode; event: MouseEvent | TouchEvent }) {
        if (_clickTimer) {
            clearTimeout(_clickTimer);
            _clickTimer = null;
            const comp = _compLookup.get(node.id);
            if (comp && onEditNode) onEditNode(comp);
        } else {
            _clickTimer = setTimeout(() => { _clickTimer = null; }, 300);
        }
    }
</script>

<div class="ge-root">
    <div class="ge-toolbar">
        <button class="ge-btn" onclick={rebuild} title="Re-run auto-layout">⟳ Auto Layout</button>
        <button class="ge-btn" onclick={deleteSelectedNodes} disabled={!selectedNodeIds.length}>
            🗑 Delete ({selectedNodeIds.length})
        </button>
        <span class="ge-sep"></span>
        <span class="ge-hint">Click select · Shift drag box-select · Del delete · Ctrl+Z undo · Ctrl+C/V copy/paste · + add child · Drag handle reparent · DblClick handle disconnect</span>
        <span class="ge-spacer"></span>
        {#if onToggleFullscreen}
            <button class="ge-btn" onclick={onToggleFullscreen} title={fullscreen ? 'Exit fullscreen' : 'Fullscreen'}>
                {fullscreen ? '🗕 Exit Full' : '🗖 Full'}
            </button>
        {/if}
    </div>

    <div class="ge-canvas">
        <SvelteFlow
            {nodes}
            {edges}
            {nodeTypes}
            onnodeclick={onNodeClick}
            onselectionchange={onSelectionChange}
            onconnect={onConnect}
            colorMode="dark"
            fitView
            fitViewOptions={{ padding: 0.3 }}
            snapGrid={[16, 16]}
            deleteKey={null}
            selectionKey="Shift"
            elementsSelectable={true}
            nodesDraggable={true}
            nodesConnectable={true}
            defaultEdgeOptions={{ type: 'smoothstep', animated: false, style: 'stroke:#555;stroke-width:2px;' }}
            proOptions={{ hideAttribution: true }}
        >
            <Background variant={BackgroundVariant.Dots} gap={24} size={1} bgColor="#0d1117" patternColor="#30363d" />
            <Controls position="bottom-right" showZoom={true} showFitView={true} showLock={false} />
        </SvelteFlow>
    </div>
</div>

<style>
    .ge-root { display: flex; flex-direction: column; width: 100%; height: 100%; background: #0d1117; }
    .ge-toolbar { display: flex; align-items: center; gap: 8px; padding: 8px 16px; background: #161b22; border-bottom: 1px solid #30363d; flex-shrink: 0; }
    .ge-btn { background: #21262d; color: #c9d1d9; border: 1px solid #30363d; border-radius: 6px; padding: 6px 14px; font-size: 13px; cursor: pointer; transition: background 0.15s; font-family: inherit; }
    .ge-btn:hover { background: #30363d; }
    .ge-btn:disabled { opacity: 0.4; cursor: not-allowed; }
    .ge-sep { width: 1px; height: 22px; background: #30363d; }
    .ge-spacer { flex: 1; }
    .ge-hint { color: #8b949e; font-size: 12px; }
    .ge-canvas { flex: 1; min-height: 0; width: 100%; }
</style>
