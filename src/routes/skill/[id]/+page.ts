import { setActive, skills } from "../../../data/store";
import { redirect } from "@sveltejs/kit";
import type { ProSkill } from "../../../api/proskill";
import { get } from "svelte/store";

/** @type {import("./$types").PageLoad} */
export async function load({ params }: any) {
  const name = params.id;
  let skill: ProSkill | undefined;
  let fallback: ProSkill | undefined;
  for (const c of get(skills)) {
    if (!fallback) fallback = c;

    if (c.name == name) {
      skill = c;
      break;
    }
  }

  if (skill) {
    setActive(skill, "skill");
    return { skill: skill };
  } else {
    if (fallback) {
      throw redirect(302, "/skill/" + fallback.name);
    } else {
      throw redirect(302, "/");
    }
  }
}