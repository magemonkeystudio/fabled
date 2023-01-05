export const squish = (node: Element, {
  delay = 0,
  duration = 500
}) => {
  const width = getComputedStyle(node).maxWidth;
  const minWidth = getComputedStyle(node).minWidth;
  const matcher = width.match(/([\d.]+)(.*)/);
  const minMatcher = minWidth.match(/([\d.]+)(.*)/);
  if (!matcher) {
    return {
      delay,
      duration,
      css: (t: number) => `max-width: ${t * 100}%`
    };
  }
  const w = Number.parseFloat(matcher[1]);
  const mw = minMatcher && minMatcher[1] ? Number.parseFloat(minMatcher[1]) : 100;
  const mwUnit = minMatcher && minMatcher[2] ? minMatcher[2] : "%";

  return {
    delay,
    duration,
    css: (t: number) => {
      return `max-width: ${t * w}${matcher[2]}; min-width: ${t * mw}${mwUnit};`;
    }
  };
};
