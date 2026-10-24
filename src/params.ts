import { defineParams } from '@sveltejs/kit/params';

export const params = defineParams({
	istype: (param: string) => (/^(skill|class|attribute)$/.test(param) ? param : undefined)
});
