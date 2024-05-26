// const awsUrl = "https://6jgex7frk0.execute-api.us-west-2.amazonaws.com/default/haste-uploader";
const hastebinUrl = 'https://paste.travja.dev';

export const createPaste = (content: string) => {
	if (typeof content !== 'string') {
		return Promise.reject(new Error('You cannot send that. Please include a "content" argument that is a valid string.'));
	}

	if (content === '') {
		return Promise.reject(new Error('You cannot send nothing.'));
	}

	const resolvedGotOptions: RequestInit = {
		method:  'POST',
		body:    content,
		headers: {
			'Content-Type': 'text/plain'
		}
	};

	return fetch(`${hastebinUrl}/documents`, resolvedGotOptions)
		.then((result) => result.json())
		.then(data => {
			if (!data.key) {
				throw new Error('Did not receive hastebin key.');
			}

			return `${hastebinUrl}/${data.key}`;
		});
};

export const getHaste = async (data: { id?: string, url?: string }): Promise<string> => {
	if (data.url && (data.url.includes('astebin.com') || data.url.includes('paste.travja.dev')) && !data.url.includes('raw'))
		data.url = data.url.replace(/(astebin\.com|paste\.travja\.dev)/, '$1/raw');
	// const req = await fetch(`${awsUrl}?${data.id ? "id=" + data.id : "url=" + data.url}`);
	if (!data.url && data.id)
		data.url = `${hastebinUrl}/raw/${data.id}`;

	if (!data.url) return '';

	const req  = await fetch(data.url);
	const text = await req.text();
	return text;
};