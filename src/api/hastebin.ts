const awsUrl = "https://6jgex7frk0.execute-api.us-west-2.amazonaws.com/default/haste-uploader";
export const createPaste = (content: string) => {
  if (typeof content !== "string") {
    return Promise.reject(new Error("You cannot send that. Please include a \"content\" argument that is a valid string."));
  }

  if (content === "") {
    return Promise.reject(new Error("You cannot send nothing."));
  }

  const resolvedGotOptions: RequestInit = {
    method: "POST",
    body: content,
    headers: {
      "Content-Type": "text/plain"
    }
  };

  return fetch(awsUrl, resolvedGotOptions)
    .then((result) => result.json())
    .then(data => {
      if (!data.url) {
        if (data.error) {
          throw new Error(data.error);
        }
        throw new Error("Did not receive hastebin key.");
      }

      return data.url;
    });
};

export const getHaste = (id: string) => {
  fetch(`${awsUrl}?id=${id}`)
    .then((result) => result.text())
    .then(result => {
      console.log(result);
    });
};