export const notify = (message: any) => {
	// Post to ntfy.sh/trav-alerts with the message data

	if (typeof (message) !== 'string') message = JSON.stringify(message, null, 2);

	fetch('https://ntfy.sh/trav-alerts', {
		method: 'POST',
		body:   message
	}).then(() => {
	});
};