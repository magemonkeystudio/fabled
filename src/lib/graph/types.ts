import type { Node, Edge } from '@xyflow/svelte';

export type FabledGraphNode = Node<{
    componentId: string;
    label: string;
    typeName: string;
    summary: string;
    isCompound: boolean;
    childCount: number;
    onAddChild?: () => void;
    onDisconnect?: (handleType: 'source' | 'target') => void;
}, 'fabled-node'>;

export type FabledGraphEdge = Edge<{
    order: number;
}, 'smoothstep'>;