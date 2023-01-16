export function clickOutside(node: HTMLElement) {
  const handleClick = (event: MouseEvent) => {
    if (!node.contains(<HTMLElement>event.target)) {
      node.dispatchEvent(new CustomEvent("outclick", { detail: event }));
    }
  };

  document.addEventListener("click", handleClick, true);

  return {
    destroy() {
      document.removeEventListener("click", handleClick, true);
    }
  };
}