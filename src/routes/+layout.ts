import type { LayoutLoad }   from './$types';
import { socketService }     from '$api/socket/socket-connector';
import { initComponents }    from '$api/components/components.svelte';
import { synthesisEnabled }  from '../data/settings';
import { hydrateEditorData } from '../data/editor-session';

export const ssr = false;

export const load: LayoutLoad = async ({ url }) => {
	initComponents();
	await hydrateEditorData();

	if (synthesisEnabled && url.searchParams.has('session')) {
		// Attempt to connect to the socket.io server
		const sessionId = url.searchParams.get('session');
		if (sessionId) {
			socketService.connect(sessionId);
		}
	}
};
