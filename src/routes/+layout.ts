import type { LayoutLoad }       from './$types';
import { createPaste, getHaste } from '$api/hastebin';
import { base }                  from '$app/paths';

export const ssr = false;

const expectedHost = 'promcteam.github.io';
const separator    = '\n\n\n~~~~~\n\n\n';

export const load: LayoutLoad = ({ url }) => {
	if (url.searchParams.has('migrationData')) {
		// Load the skills into the editor.
		// This should be from migrations.

		getHaste({ url: url.searchParams.get('migrationData') || undefined })
			.then(data => {
				const skillData    = data.split(separator)[0];
				const classData    = data.split(separator)[1];
				const skillFolders = data.split(separator)[2];
				const classFolders = data.split(separator)[3];

				localStorage.setItem('skillData', skillData);
				localStorage.setItem('classData', classData);
				localStorage.setItem('skillFolders', skillFolders);
				localStorage.setItem('classFolders', classFolders);

				window.location.href = `http://${expectedHost}${base}`;
			})
			.catch(console.error);

		return;
	}

	if (url.host === expectedHost || !localStorage.getItem('skillData')) return;

	alert('We\'re migrating the new editor to the old URL. You\'re now going to be redirected. Your skills/classes should remain in tact.');

	const skillYaml    = localStorage.getItem('skillData');
	const classYaml    = localStorage.getItem('classData');
	const skillFolders = localStorage.getItem('skillFolders');
	const classFolders = localStorage.getItem('classFolders');

	const qualifiedData = skillYaml + separator
		+ classYaml + separator
		+ skillFolders + separator
		+ classFolders;

	createPaste(qualifiedData)
		.then((data) => {
			console.log(data);
			window.location.href = `http://${expectedHost}/proskillapi?migrationData=${data}`;
		});

	return;
};