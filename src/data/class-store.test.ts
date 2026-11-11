import 'fake-indexeddb/auto';

import { beforeEach, describe, expect, it, vi } from 'vitest';

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

const classBlock = (name: string, regen?: string) =>
  [`${name}:`, `  name: ${name}`, ...(regen === undefined ? [] : [`  mana-regen: ${regen}`])].join(
    '\n'
  );

describe('class import', () => {
  beforeEach(async () => {
    localStorage.clear();
    const persistence = await import('./editor-persistence');
    await persistence.resetEditorPersistenceForTests();
  });

  it.each([
    ['0', 0],
    ['0.5', 0.5],
    ["'2'", 2]
  ])('loads mana-regen %s from a single class import', async (raw, expected) => {
    const { classStore } = await import('./class-store.svelte');
    const name = `Single${expected}`;
    classStore.loadClassText(classBlock(name, raw));
    expect(classStore.getClass(name)!.manaRegen).toBe(expected);
  });

  it('loads mana-regen from a multi-class import', async () => {
    const { classStore } = await import('./class-store.svelte');
    classStore.loadClassText(
      [
        classBlock('MultiZero', '0'),
        classBlock('MultiHalf', '0.5'),
        classBlock('MultiStr', "'2'")
      ].join('\n')
    );
    expect(classStore.getClass('MultiZero')!.manaRegen).toBe(0);
    expect(classStore.getClass('MultiHalf')!.manaRegen).toBe(0.5);
    expect(classStore.getClass('MultiStr')!.manaRegen).toBe(2);
  });

  it('keeps the default of 1 when mana-regen is missing', async () => {
    const { classStore } = await import('./class-store.svelte');
    classStore.loadClassText(classBlock('NoRegen'));
    expect(classStore.getClass('NoRegen')!.manaRegen).toBe(1);
  });

  it('keeps the default exp-source when the key is missing', async () => {
    const { classStore } = await import('./class-store.svelte');
    classStore.loadClassText(classBlock('NoExpSource'));
    expect(classStore.getClass('NoExpSource')!.expSources).toBe(273);
  });

  it('loads an explicit exp-source', async () => {
    const { classStore } = await import('./class-store.svelte');
    classStore.loadClassText(`${classBlock('WithExpSource')}\n  exp-source: 1`);
    expect(classStore.getClass('WithExpSource')!.expSources).toBe(1);
  });

  it('serializes a string mana regen (from the text input) as a number', async () => {
    const { default: FabledClass } = await import('./class-store.svelte');
    const clazz = new FabledClass({ name: 'Edited' });
    (clazz as unknown as { manaRegen: string }).manaRegen = '1.5';
    expect(clazz.serializeYaml()['mana-regen']).toBe(1.5);
  });
});
