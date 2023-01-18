export function numberOnly(node: HTMLElement, opts: { intMode?: boolean, enabled?: boolean } = {
  intMode: false,
  enabled: true
}) {
  const intMode = opts.intMode;
  const enabled = opts.enabled;
  const checkNumber = (e: KeyboardEvent) => {
    if (!enabled) return;
    if (intMode && !e.key.match(/\d/)
      || !e.key.match(/[\d.]/)) {
      e.preventDefault();
    }
  };

  node.addEventListener("keypress", checkNumber, true);

  return {
    destroy() {
      node.removeEventListener("keypress", checkNumber, true);
    }
  };
}