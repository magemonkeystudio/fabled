/** @type {import('@sveltejs/kit').ParamMatcher} */
export function match(param: string) {
  return /^(skill|class)$/.test(param);
}