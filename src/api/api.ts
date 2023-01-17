export const toProperCase = (s: string) => {
  return s.replace("_", " ").toLowerCase()
    .replace(/^(.)|\s(.)/g, $1 => $1.toUpperCase());
};

export const toEditorCase = (s: string) => {
  s = s.replace("_", " ");
  return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
};