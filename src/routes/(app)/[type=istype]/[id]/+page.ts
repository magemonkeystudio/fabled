import type { ProClass } from "../../../../api/proclass";
import type { ProSkill } from "../../../../api/proskill";
import { classes, setActive, skills } from "../../../../data/store";
import { get } from "svelte/store";
import { redirect } from "@sveltejs/kit";

/** @type {import("./$types").PageLoad} */
export async function load({ params }: any) {
  const name = params.id;
  console.log(params);
  let data: ProClass | ProSkill | undefined;
  let fallback: ProClass | ProSkill | undefined;
  if (params.type == "class") {
    for (const c of get(classes)) {
      if (!fallback) fallback = c;

      if (c.name == name) {
        data = c;
        break;
      }
    }
  } else if (params.type == "skill") {
    for (const c of get(skills)) {
      if (!fallback) fallback = c;

      if (c.name == name) {
        data = c;
        break;
      }
    }
  }
  //
  if (data) {
    setActive(data, params.type);
    return { data };
  } else {
    if (fallback) {
      throw redirect(302, `/${params.type}/${fallback.name}`);
    } else {
      throw redirect(302, "/");
    }
  }
}