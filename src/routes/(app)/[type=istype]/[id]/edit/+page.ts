import { active, isShowClasses } from "../../../../../data/store";
import { get } from "svelte/store";
import { redirect } from "@sveltejs/kit";
import { skills } from "../../../../../data/skill-store";
import type ProClass from "$api/proclass";
import type ProSkill from "$api/proskill";

export const ssr = false;

// noinspection JSUnusedGlobalSymbols
/** @type {import("./$types").PageLoad} */
export async function load({ params }: any) {
  const name = params.id;
  let data: ProClass | ProSkill | undefined;
  let fallback: ProClass | ProSkill | undefined;
  if (params.type == "class") {
    throw redirect(302, `/${params.type}/${params.id}`);
  } else if (params.type == "skill") {
    for (const c of get(skills)) {
      if (!fallback) fallback = c;

      if (c.name == name) {
        data = c;
        break;
      }
    }
  }

  if (data) {
    active.set(data);
    isShowClasses.set(params.type == "class");
    return { data };
  } else {
    if (fallback) {
      throw redirect(302, `/${params.type}/${fallback.name}`);
    } else {
      throw redirect(302, "/");
    }
  }
}