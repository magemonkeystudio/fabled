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

export const getHaste = async (data: { id?: string, url?: string }) => {
  if (data.url && data.url.includes("astebin.com") && !data.url.includes("raw"))
    data.url = data.url.replace("astebin.com", "astebin.com/raw");
  const req = await fetch(`${awsUrl}?${data.id ? "id=" + data.id : "url=" + data.url}`);
  const text = await req.text();
  return text;
};