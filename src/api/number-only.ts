export function numberOnly(node: HTMLElement, intMode = false) {
  const checkNumber = (e: KeyboardEvent) => {
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