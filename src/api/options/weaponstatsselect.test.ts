import { beforeAll, describe, expect, it, vi } from 'vitest';
import type FabledMechanic from '$api/components/mechanics.svelte';
import type { Unknown } from '$api/types';

vi.mock('$app/env', () => ({
  browser: true
}));

vi.mock('$app/navigation', () => ({
  goto: vi.fn(() => Promise.resolve())
}));

vi.mock('$app/paths', () => ({
  resolve: (path: string) => (path.startsWith('/') ? path : `/${path}`)
}));

vi.mock('$api/notification-service', () => ({
  notify: vi.fn()
}));

vi.mock('$api/socket/socket-connector', () => ({
  socketService: {
    onConnect: vi.fn(),
    offConnect: vi.fn(),
    onDisconnect: vi.fn(),
    connect: vi.fn(),
    getSkills: vi.fn(() => Promise.resolve([])),
    getClasses: vi.fn(() => Promise.resolve([])),
    getSkillYaml: vi.fn(() => Promise.resolve('')),
    getClassYaml: vi.fn(() => Promise.resolve(''))
  }
}));

type Registry = typeof import('$api/components/registry').default;
let registry: Registry;

beforeAll(async () => {
  const components = await import('$api/components/components.svelte');
  registry = (await import('$api/components/registry')).default;
  components.initComponents();
});

const MECHANICS = ['Damage', 'Damage Lore'];

/** Loads a mechanic the same way a skill is loaded from yaml. */
const load = (mechanic: string, data: Unknown): FabledMechanic => {
  const [comp] = registry.deserializeComponents({
    [mechanic]: { type: 'mechanic', comment: '', data, children: {} }
  });
  return comp as FabledMechanic;
};

const saved = (comp: FabledMechanic) => comp.toYamlObj().data as Unknown;

const field = (comp: FabledMechanic) => {
  const opt = comp.data.find((o) => o.key === 'weapon-stats');
  if (!opt) throw new Error('Weapon Stats option is missing');
  return opt as unknown as { data: { selected: string; value: string[] }; name: string };
};

describe.each(MECHANICS)('%s weapon stats', (mechanic) => {
  it('replaces the Ignore Divinity checkbox with a Weapon Stats dropdown', () => {
    const comp = load(mechanic, {});
    expect(comp.data.some((o) => o.key === 'ignore-divinity')).toBe(false);
    expect(field(comp).name).toBe('Weapon Stats');
    expect(field(comp).data.value).toEqual(['Add', 'Replace', 'Ignore', 'Raw']);
    expect(comp.data.find((o) => o.key === 'weapon-stats')!.tooltip).toContain('Divinity');
  });

  it('defaults a freshly added component to Add and saves it like the old default', () => {
    const comp = (
      registry.getMechanicByName(mechanic) as unknown as { new: () => FabledMechanic }
    ).new();
    expect(field(comp).data.selected).toBe('Add');
    const data = saved(comp);
    expect(data['ignore-divinity']).toBe(false);
    expect('weapon-stats' in data).toBe(false);
  });

  it('loads an old skill with ignore-divinity true as Ignore and saves it unchanged', () => {
    const comp = load(mechanic, { 'ignore-divinity': true });
    expect(field(comp).data.selected).toBe('Ignore');
    const data = saved(comp);
    expect(data['ignore-divinity']).toBe(true);
    expect('weapon-stats' in data).toBe(false);
  });

  it('loads a string "true" like the old checkbox did and saves a boolean', () => {
    const comp = load(mechanic, { 'ignore-divinity': 'true' });
    expect(field(comp).data.selected).toBe('Ignore');
    expect(saved(comp)['ignore-divinity']).toBe(true);
    expect('weapon-stats' in saved(comp)).toBe(false);
  });

  it('loads ignore-divinity false as Add and saves it unchanged', () => {
    const comp = load(mechanic, { 'ignore-divinity': false });
    expect(field(comp).data.selected).toBe('Add');
    const data = saved(comp);
    expect(data['ignore-divinity']).toBe(false);
    expect('weapon-stats' in data).toBe(false);
  });

  it('loads a skill with neither key as Add and saves it unchanged', () => {
    const comp = load(mechanic, {});
    expect(field(comp).data.selected).toBe('Add');
    const data = saved(comp);
    expect(data['ignore-divinity']).toBe(false);
    expect('weapon-stats' in data).toBe(false);
  });

  it('keeps the other saved keys and key order untouched for an old skill', () => {
    const before = saved(load(mechanic, { 'ignore-divinity': true, knockback: false }));
    const keys = Object.keys(before);
    expect(keys.indexOf('knockback')).toBeLessThan(keys.indexOf('ignore-divinity'));
    expect(keys).not.toContain('weapon-stats');
    expect(before.knockback).toBe(false);
  });

  it.each([
    ['Add', 'add', false],
    ['Replace', 'replace', false],
    ['Ignore', 'ignore', true],
    ['Raw', 'raw', false]
  ])('saves %s as weapon-stats %s with a compatible ignore-divinity', (label, value, ignore) => {
    // Start from an old skill whose value differs from the target so the change is real
    const comp = load(mechanic, { 'ignore-divinity': label === 'Ignore' ? false : true });
    field(comp).data.selected = label;
    const data = saved(comp);
    expect(data['weapon-stats']).toBe(value);
    expect(data['ignore-divinity']).toBe(ignore);
  });

  it('writes both keys when a new skill component is changed', () => {
    const comp = load(mechanic, {});
    field(comp).data.selected = 'Replace';
    const data = saved(comp);
    expect(data['weapon-stats']).toBe('replace');
    expect(data['ignore-divinity']).toBe(false);
  });

  it.each([
    ['add', false],
    ['replace', false],
    ['ignore', true],
    ['raw', false]
  ])('round-trips a skill with weapon-stats %s', (value, ignore) => {
    const comp = load(mechanic, { 'weapon-stats': value, 'ignore-divinity': ignore });
    expect(field(comp).data.selected.toLowerCase()).toBe(value);
    const data = saved(comp);
    expect(data['weapon-stats']).toBe(value);
    expect(data['ignore-divinity']).toBe(ignore);

    // Loading what was saved gives the same thing again
    const again = load(mechanic, data);
    expect(saved(again)['weapon-stats']).toBe(value);
    expect(saved(again)['ignore-divinity']).toBe(ignore);
  });

  it('fixes an inconsistent ignore-divinity when weapon-stats is present', () => {
    const comp = load(mechanic, { 'weapon-stats': 'replace', 'ignore-divinity': true });
    expect(field(comp).data.selected).toBe('Replace');
    expect(saved(comp)['ignore-divinity']).toBe(false);

    const ignored = load(mechanic, { 'weapon-stats': 'ignore' });
    expect(saved(ignored)['ignore-divinity']).toBe(true);
  });

  it('prefers weapon-stats over ignore-divinity when loading', () => {
    const comp = load(mechanic, { 'weapon-stats': 'raw', 'ignore-divinity': true });
    expect(field(comp).data.selected).toBe('Raw');
  });

  it('writes the compat key when a weapon-stats skill keeps weapon-stats but is changed', () => {
    const comp = load(mechanic, { 'weapon-stats': 'raw', 'ignore-divinity': false });
    field(comp).data.selected = 'Ignore';
    expect(saved(comp)['weapon-stats']).toBe('ignore');
    expect(saved(comp)['ignore-divinity']).toBe(true);
  });

  it('survives a clone with its loaded state', () => {
    const comp = load(mechanic, { 'ignore-divinity': true });
    const copy = comp.data.find((o) => o.key === 'weapon-stats')!.clone();
    expect(copy.getData()).toEqual({ 'ignore-divinity': true });
    (copy as unknown as { data: { selected: string } }).data.selected = 'Raw';
    expect(copy.getData()).toEqual({ 'weapon-stats': 'raw', 'ignore-divinity': false });
  });
});
