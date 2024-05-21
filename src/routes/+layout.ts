import type { LayoutLoad }         from './$types';
import { getHaste }                from '$api/hastebin';
import { base }                    from '$app/paths';
import { socketService }           from '$api/socket/socket-connector';
import { initComponents }          from '$api/components/components';
import YAML                        from 'yaml';
import { parseYaml }               from '$api/yaml';
import type { MultiSkillYamlData } from '$api/types';
import { synthesisEnabled }        from '../data/settings';

export const ssr = false;

const expectedHost = ['fabled.magemonkey.studio', 'synthesis.travja.dev'];
const separator    = '\n\n\n~~~~~\n\n\n';

export const load: LayoutLoad = async ({ url }) => {
	initComponents();
	if (synthesisEnabled && url.searchParams.has('session')) {
		// Attempt to connect to the socket.io server
		const sessionId = url.searchParams.get('session');
		if (sessionId) {
			socketService.connect(sessionId);
		}
	}

	if (url.host.includes('localhost')) return;

	if (url.searchParams.has('migrationData')) {
		// Load the skills into the editor.
		// This should be from migrations.

		getHaste({ url: url.searchParams.get('migrationData') || undefined })
			.then(data => {
				const skillData    = data.split(separator)[0];
				const classData    = data.split(separator)[1];
				const skillFolders = data.split(separator)[2];
				const classFolders = data.split(separator)[3];
				const attributes   = data.split(separator)[4];

				parseYaml(skillData).forEach((skill: MultiSkillYamlData) => {
					localStorage.setItem('sapi.skill.' + skill.name, YAML.stringify(skill));
				});
				parseYaml(classData).forEach((cls: MultiSkillYamlData) => {
					localStorage.setItem('sapi.class.' + cls.name, YAML.stringify(cls));
				});
				localStorage.setItem('skillFolders', skillFolders);
				localStorage.setItem('classFolders', classFolders);
				localStorage.setItem('attribs', attributes);

				window.location.href = `https://${expectedHost}${base}`;
			})
			.catch(console.error);

		return;
	}

	// if (expectedHost.includes(url.host) || get(skills).length == 0) return;
	//
	// alert('We\'re migrating to a new URL. You\'re now going to be redirected. Your skills/classes should remain in tact.');
	//
	// const skillYaml    = YAML.stringify(await getAllSkillYaml(), { lineWidth: 0 });
	// const classYaml    = YAML.stringify(getAllClassYaml(), { lineWidth: 0 });
	// const skillFolders = localStorage.getItem('skillFolders');
	// const classFolders = localStorage.getItem('classFolders');
	// const attributes   = localStorage.getItem('attribs');
	//
	// const qualifiedData = skillYaml + separator
	// 	+ classYaml + separator
	// 	+ skillFolders + separator
	// 	+ classFolders + separator
	// 	+ attributes;
	//
	// createPaste(qualifiedData)
	// 	.then((url: string) => window.location.href = `https://${expectedHost}?migrationData=${url}`);
};