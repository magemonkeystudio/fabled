import { classes, closeSidebar, setActive } from "../../../data/store";
import { redirect } from "@sveltejs/kit";
import { get } from "svelte/store";
import type { ProClass } from "../../../api/proclass";

/** @type {import("./$types").PageLoad} */
export async function load({ params }: any) {
  const name = params.id;
  let clazz: ProClass | undefined;
  let fallback: ProClass | undefined;
  for (const c of get(classes)) {
    if (!fallback) fallback = c;

    if (c.name == name) {
      clazz = c;
      break;
    }
  }

  if (clazz) {
    setActive(clazz, "class");
    closeSidebar();
    return { class: clazz };
  } else {
    if (fallback) {
      throw redirect(302, "/class/" + fallback.name);
    } else {
      throw redirect(302, "/");
    }
  }
}