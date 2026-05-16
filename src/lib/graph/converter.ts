import type FabledComponent from '$api/components/fabled-component.svelte';
import type { FabledGraphNode, FabledGraphEdge } from './types';

const NODE_W = 220;
const NODE_H = 90;
const H_GAP   = 60;
const V_GAP   = 50;

let _next = 0;
function uid() { return `n${_next++}`; }

/** Convert a single FabledComponent (sub)tree into flat nodes + edges. */
export function treeToGraph(
    root: FabledComponent,
    parentId?: string,
    depth = 0,
    siblingIdx = 0
): { nodes: FabledGraphNode[]; edges: FabledGraphEdge[] } {
    const id = uid();
    const children = getChildren(root);
    const summary = root.summaryItems
        .map(k => {
            const opt = root.data.find(o => (o as any).key === k || (o as any).matches?.(k));
            return opt ? String((opt as any).getDisplayValue?.() ?? (opt as any).value ?? '') : '';
        })
        .filter(Boolean)
        .join(' | ');

    const node: FabledGraphNode = {
        id,
        type: 'fabled-node',
        position: { x: 0, y: 0 },
        data: {
            componentId: id,
            label: root.name,
            typeName: root.type,
            summary,
            isCompound: false,
            childCount: children.length
        }
    };

    const edges: FabledGraphEdge[] = [];
    const allNodes: FabledGraphNode[] = [node];

    children.forEach((child, i) => {
        const sub = treeToGraph(child, id, depth + 1, i);
        allNodes.push(...sub.nodes);
        edges.push({
            id: `${id}->${sub.nodes[0].id}`,
            source: id,
            target: sub.nodes[0].id,
            type: 'smoothstep',
            data: { order: i }
        });
        edges.push(...sub.edges);
    });

    return { nodes: allNodes, edges };
}

/**
 * Assign positions using a proper tree layout.
 * Leaves occupy consecutive horizontal slots, and each parent is centered
 * over its children — so branches stay visually grouped instead of having
 * all nodes at the same depth jumbled into one row.
 */
export function autoLayout(nodes: FabledGraphNode[], edges: FabledGraphEdge[]) {
    const childrenOf = new Map<string, string[]>();
    const parentOf  = new Map<string, string>();
    nodes.forEach(n => childrenOf.set(n.id, []));
    edges.forEach(e => {
        childrenOf.get(e.source)?.push(e.target);
        parentOf.set(e.target, e.source);
    });

    const roots = nodes.filter(n => !parentOf.has(n.id));
    const nodeMap = new Map(nodes.map(n => [n.id, n]));

    let slot = 0;
    const X_STEP = NODE_W + H_GAP;
    const Y_STEP = NODE_H + V_GAP;

    /** Post-order layout: leaves get consecutive slots, parents center over children. */
    function layoutSubtree(nodeId: string, depth: number): number {
        const children = childrenOf.get(nodeId) || [];
        const node = nodeMap.get(nodeId);

        if (children.length === 0) {
            const x = slot * X_STEP;
            if (node) node.position = { x, y: depth * Y_STEP };
            const cx = x + NODE_W / 2;
            slot++;
            return cx;
        }

        const childCXs = children.map(cid => layoutSubtree(cid, depth + 1));
        const left  = childCXs[0];
        const right = childCXs[childCXs.length - 1];
        const cx = (left + right) / 2;
        if (node) node.position = { x: cx - NODE_W / 2, y: depth * Y_STEP };
        return cx;
    }

    const rootCXs = roots.map(r => layoutSubtree(r.id, 0));

    // Center the whole forest at x = 0
    if (roots.length > 0) {
        const overallCenter = (rootCXs[0] + rootCXs[rootCXs.length - 1]) / 2;
        for (const nd of nodes) {
            nd.position = { x: nd.position.x - overallCenter, y: nd.position.y };
        }
    }
}

function getChildren(comp: FabledComponent): FabledComponent[] {
    const s = comp.components as any;
    if (s && typeof s.subscribe === 'function') {
        let val: FabledComponent[] = [];
        s.subscribe((v: FabledComponent[]) => { val = v; })();
        return val;
    }
    return [];
}
