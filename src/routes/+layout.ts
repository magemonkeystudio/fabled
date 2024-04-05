import type { LayoutLoad }                  from './$types';
import { createPaste, getHaste }            from '$api/hastebin';
import { base }                             from '$app/paths';
import { loadSkillText, skills }            from '../data/skill-store';
import { getAllClassYaml, getAllSkillYaml } from '../data/store';
import { get }                              from 'svelte/store';
import { loadClassText }                    from '../data/class-store';
import YAML                                 from 'yaml';
import { initComponents }                   from '$api/components/components';

export const ssr = false;

const expectedHost = 'fabled.magemonkey.studio';
const separator    = '\n\n\n~~~~~\n\n\n';

export const load: LayoutLoad = async ({ url }) => {
	initComponents();
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

				loadSkillText(skillData).then(() => {
				});
				loadClassText(classData);
				localStorage.setItem('skillFolders', skillFolders);
				localStorage.setItem('classFolders', classFolders);

				window.location.href = `https://${expectedHost}${base}`;
			})
			.catch(console.error);

		return;
	}

	if (url.host === expectedHost || get(skills).length == 0) return;

	alert('We\'re migrating to a new URL. You\'re now going to be redirected. Your skills/classes should remain in tact.');

	const skillYaml    = YAML.stringify(await getAllSkillYaml(), { lineWidth: 0 });
	const classYaml    = YAML.stringify(getAllClassYaml(), { lineWidth: 0 });
	const skillFolders = localStorage.getItem('skillFolders');
	const classFolders = localStorage.getItem('classFolders');

	const qualifiedData = skillYaml + separator
		+ classYaml + separator
		+ skillFolders + separator
		+ classFolders;

	createPaste(qualifiedData)
		.then((url: string) => window.location.href = `https://${expectedHost}?migrationData=${url}`);
};