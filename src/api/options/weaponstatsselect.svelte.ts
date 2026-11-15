import DropdownSelect from '$api/options/dropdownselect.svelte';
import type { Unknown } from '$api/types';
import { parseBool } from '$api/api';

const LEGACY_KEY = 'ignore-divinity';
const CHOICES = ['Add', 'Replace', 'Ignore', 'Raw'];

/**
 * Dropdown for what Divinity does with the caster's weapon stats on a skill hit.
 * It is saved as `weapon-stats` (add, replace, ignore, raw) together with the legacy
 * `ignore-divinity` boolean so older plugin builds keep working.
 *
 * A skill that was loaded without `weapon-stats` is written back exactly as before
 * (only `ignore-divinity`) until the user changes the choice.
 */
export default class WeaponStatsSelect extends DropdownSelect {
  private hasWeaponStats = false;
  private legacyIgnore = false;
  private baseline = 'Add';

  constructor(name: string, key = 'weapon-stats') {
    super(name, key, [...CHOICES], 'Add');
  }

  clone = (): WeaponStatsSelect => {
    const copy = new WeaponStatsSelect(this.name, this.key);
    copy.data.selected = this.data.selected;
    copy.hasWeaponStats = this.hasWeaponStats;
    copy.legacyIgnore = this.legacyIgnore;
    copy.baseline = this.baseline;
    copy.tooltip = this.tooltip;
    return copy;
  };

  getData = (): { [key: string]: unknown } => {
    const choice = <string>this.data.selected;
    if (!this.hasWeaponStats && choice === this.baseline)
      return { [LEGACY_KEY]: this.legacyIgnore };

    const value = choice.toLowerCase();
    return { [this.key]: value, [LEGACY_KEY]: value === 'ignore' };
  };

  deserialize = (yaml: Unknown) => {
    const raw = yaml[this.key];
    const saved =
      typeof raw === 'string'
        ? CHOICES.find((choice) => choice.toLowerCase() === raw.trim().toLowerCase())
        : undefined;
    const legacy = <boolean | string | undefined>yaml[LEGACY_KEY];

    this.legacyIgnore = legacy === undefined ? false : parseBool(legacy);
    this.hasWeaponStats = saved !== undefined;
    this.data.selected = saved ?? (this.legacyIgnore ? 'Ignore' : 'Add');
    this.baseline = <string>this.data.selected;
  };
}
