# Open Issue Triage

A full pass over all **150 open issues** (as of 2026-08-10). Every open issue now carries a
priority, an area, and an effort estimate. This document is the narrative summary; the labels on
GitHub are the source of truth.

## Label scheme

| Group | Labels | Meaning |
|---|---|---|
| Priority | `priority: critical` / `high` / `medium` / `low` | Urgency and impact |
| Effort | `effort: small` / `medium` / `large` | Small ≈ hours, medium ≈ days, large ≈ architectural |
| Area | `area: mechanics`, `area: gui`, `area: commands`, `area: data/storage`, `area: combat/damage`, `area: attributes`, `area: integrations`, `area: particles`, `area: api`, `editor` | Where the work lands |
| Status | `confirmed`, `possibly fixed`, `needs info`, `stale` | Triage state |
| Contributor | `good first issue`, `help wanted` | Onboarding signals |

The legacy `low-priority` label was folded into `priority: low`.

## Distribution

| Priority | Count | | Effort | Count | | Area | Count |
|---|---|---|---|---|---|---|---|
| critical | 1 | | small | 57 | | mechanics | 71 |
| high | 14 | | medium | 75 | | gui | 22 |
| medium | 74 | | large | 18 | | attributes | 17 |
| low | 61 | | | | | integrations | 16 |
| | | | | | | combat/damage | 15 |
| | | | | | | commands | 12 |
| | | | | | | data/storage | 8 |
| | | | | | | particles | 6 |
| | | | | | | editor | 5 |
| | | | | | | api | 2 |

Also tagged: **42** `good first issue`, **11** `needs info`, **9** `stale`, **2** `possibly fixed`.

The shape of the backlog: it is overwhelmingly a *feature request* queue (145 enhancements vs 5
bugs), heavily concentrated in the dynamic-component system, and roughly 40% of it is small enough
for a first-time contributor to land.

Two issues (#478, #159) had no type label at all and were given `enhancement`.

---

## Critical

### #1795 — `PlayerLoader.cachedPlayers` unsynchronised `TreeMap` hangs the server

**Verified against `dev`.** This is the only issue that should block a release.

`cachedPlayers` is a plain `TreeMap` mutated from two threads: joins write it on the main thread via
`getPlayerAccounts`, and quits remove from it on an async thread because `Fabled.unloadPlayerData`
dispatches through `runTaskAsynchronously`. `unloadPlayer` writes a YAML file *between* its read and
its `remove`, so the race window spans disk I/O. A corrupted red-black tree sends
`TreeMap.getEntry` into an infinite loop **on the main thread** and the server stops ticking
permanently.

The reporter supplied stack traces, a reproduction (1364 rapid rejoins), and a patch.

An additional hazard found during triage, not in the original report: `getAllPlayerAccounts()`
returns the backing map directly, and four call sites iterate it while the async unloader can be
mutating it — `ConfigIO.java:114`, `FabledPlayersSQL.java:142`, `IOManager.java:117`, and
`Fabled.java:696`. The fix should return an unmodifiable copy as well as changing the map type.

---

## High priority

Broken core behaviour, or the most-requested features by comment/reaction volume.

**Bugs**

| # | Title | Note |
|---|---|---|
| #1791 | Divinity menus — options past page 1 don't respond | GUI pagination; `needs info` |
| #1719 | `Mechanic: Armor stand remove` doesn't remove | `needs info` — likely a target/key mismatch, see below |
| #1697 | Projectile children fire twice | **`possibly fixed`** by 5349f49 |
| #1582 | Mana Regen resets to 1 on editor import | Editor-side; small and self-contained |

**Features with the most demand**

| # | Title | Effort |
|---|---|---|
| #467 | Display Entity support (1.19+) — `in-demand`, draft PR #1647 | large |
| #1340 | Display mechanics: text/item/block + transforms — the detailed spec for #467 | large |
| #527 | Global skills applied to all classes | large |
| #642 | Command rework onto CommandAPI (tab completion) | large |
| #462 | `/class damage` command — 10 comments, the single most-discussed issue | medium |
| #961 | Accounts & class-selection GUI | large |
| #1592 | Additive skill lists on subclass progression — draft PR #1594 | medium |
| #485 | Environmental damage from mechanics should count as caster damage | medium |
| #553 | Persistent values | medium |
| #403 | Persistent flags | medium |

`#485` deserves attention out of proportion to its age: it is an interoperability correctness
problem (vanilla XP/loot drops, MythicMobs threat tables, friendly-fire cancellation all read the
damage source), not a cosmetic feature.

---

## Already resolved — verify and close

| # | Finding |
|---|---|
| #1697 | Fixed by 5349f49 (2026-07-24), *after* the issue was filed. The commit adds the `getHitEntity() != null` guard in `MechanicListener.onLand` that stops the callback firing from both `onDamageByEntity` and `ProjectileHitEvent`. |
| #405 | `ExperienceMechanic` already implements give/set/take × flat/percent/levels, with `group`, `level-down` and `vanilla` options. The request is fully covered. |

Both are labelled `possibly fixed` rather than closed, so a maintainer confirms before they go.

---

## Good first issues (42)

Small, well-scoped, and mostly additive — a new condition, a new trigger, or one extra setting on an
existing component. These are the ones to point new contributors at.

**New conditions / triggers** — the most mechanical pattern in the codebase; copy a sibling class in
`dynamic/condition/` or `dynamic/trigger/` and register it:
#288 (saturation), #1134 (is-bleeding, Divinity hook), #1364 (remember), #133 (onLogin),
#130 (onInteract), #131 (onPotionSplash), #458 (switch-slot), #1790 (feed animal / milk cow),
#309 (value potion), #306 (mob-target targeter), #99 (slot count).

**One extra setting on an existing component:**
#513 (move-trigger interval), #426 (projectile gravity), #472 (both-targeting on particle
projectile), #434 (max targets), #448 (enchantments on armor mechanic), #478 (durability on armor
slots), #164 (hollow sphere/cuboid), #326 (randomise rain placement), #1789 (enchantment filter in
item conditions), #477 (select DamageCauses), #450 (int/double value types), #290 (sqrt/log values).

**Small commands:** #487 (list loaded skills), #476 (remove unspent points), #392 (force exp/level),
#1318 (reset by group), #786 / #443 (jump straight to a group's skill tree).

**Notably tractable:** #381 (WorldGuard region condition) — `hook/WorldGuardHook.java` already
exists and is wired up for damage flags, so this is mostly a thin `ConditionComponent` on top of it.

**Editor:** #1582 (mana-regen import bug).

---

## Duplicate and overlapping clusters

Worth consolidating — several of these are the same feature requested twice, years apart.

| Cluster | Issues | Suggestion |
|---|---|---|
| Clearing a `Mechanic: Trigger` | **#1605**, #466 | Near-identical. Close #466 as duplicate. |
| Persistence of values/flags | **#553**, #403 | Same subsystem, ship together. |
| Display entities | **#467**, #1340 | #1340 is the implementation spec for #467. Make it a sub-issue. |
| Duration stacking / extension | **#1698**, #265 | Same "extend rather than stack" semantics. |
| Reading NBT | **#1274**, #549 | Values-from-NBT and attributes-from-NBT share a reader. |
| Attribute commands | **#383**, #364 | Fold #364 into #383. |
| Cooldown display | **#286**, #56 | Actionbar vs bossbar — one setting, two renderers. |
| Skill-tree group navigation | **#786**, #443 | Same command. |
| Region-gated skills/damage | **#381**, #460, #533 | All want region-aware skill behaviour. |
| Accounts | **#961**, #96 | Per-account inventories are a sub-feature of the accounts GUI. |
| Mob & spawner suite | #490, #491, #492, #493 | One epic, filed as four. All `priority: low`. |
| Damage attribution | **#462**, #840 | Both want Fabled-system damage from outside a skill. |

Bold = the one to keep.

---

## Needs info (11)

Cannot be actioned as filed. #1791, #1719, #1582 have had specific questions posted; the rest are
older and vague: #698, #414, #395, #368, #253, #173, #165, #79, #23.

The three recent bug reports all left the template's **Environment** and **Steps To Reproduce**
sections blank. Making those required in the bug template would prevent the recurrence.

On #1719 specifically: `ArmorStandRemoveMechanic` resolves the stand by `key` **on the current
target**, defaulting to the skill name, and `ArmorStandMechanic` registers with the same default.
The two are symmetric, so the most likely explanation is that the create and remove mechanics sit
under different targeters — which makes this a documentation or validation gap rather than a code
bug. The attached skill YAML will settle it.

---

## Stale (9)

2021-era, zero engagement, superseded or too vague to act on: #16, #23, #40, #79, #159, #173, #234,
#246, #253. Recommend a close-with-comment pass inviting a refile if still wanted.

---

## Open PRs mapped to issues

| PR | Issue | State |
|---|---|---|
| #1647 | #467 Display entities | draft (Copilot) |
| #1594 | #1592 Additive skill lists | draft (Copilot) |
| #1775 | relates #1134, #210 (Divinity stats) | open |
| #1774, #1781 | — | splits out of #1677 |
| #1677 | — | large fork PR, being split |
| #1622 | superseded by #1775 | open |
| #1680 | editor graph mode | open |
| #1762, #1792 | — | dependabot |

`#1622` looks superseded by `#1775` and is worth closing if so.

---

## Suggested order of work

1. **#1795** — ship the concurrency fix. It is a whole-server hang with a patch already offered.
2. **Close the two resolved issues** (#1697, #405) and the duplicate clusters above. That is ~14
   issues off the board for very little effort.
3. **#1582** — small, self-contained editor bug.
4. **Pick one high-demand epic.** #467/#1340 (display entities) has the most external pull and a
   draft PR already in flight.
5. **Publish the `good first issue` set.** 42 issues is a large, genuinely approachable on-ramp for
   a project with this much community feature demand.
