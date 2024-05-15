import YAML       from 'yaml';
import { notify } from '$api/notification-service';

export const parseYaml = (data: string) => {
	try {
		return YAML.parse(data);
	} catch (e: any) {
		notify('Error parsing YAML: ' + (e.error || e.message) + '\n' + data);
		return {};
	}
};