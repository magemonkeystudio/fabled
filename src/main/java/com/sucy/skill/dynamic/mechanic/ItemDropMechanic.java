package com.sucy.skill.dynamic.mechanic;

import mc.promcteam.engine.mccore.util.TextFormatter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

/**
 * Spawns a dropped item at the specified location.
 */
public class ItemDropMechanic extends MechanicComponent {
    private static final Vector UP = new Vector(0, 1, 0);

    private static final String FORWARD = "forward";
    private static final String UPWARD = "upward";
    private static final String RIGHT = "right";

    private static final String PICKUP_DELAY = "pickup_delay";
    private static final String DURATION = "duration";

    private static final String MATERIAL = "material";
    private static final String AMOUNT = "amount";
    private static final String UNBREAKABLE = "unbreakable";
    private static final String DURABILITY = "data";
    private static final String HIDE_FLAGS = "hide-flags";
    private static final String CMD = "byte";
    private static final String CUSTOM = "custom";
    private static final String NAME = "name";
    private static final String LORE = "lore";

    private static final String POTION_COLOR = "potion_color";
    private static final String POTION_TYPE = "potion_type";
    private static final String POTION_LEVEL = "potion_level";
    private static final String POTION_DURATION = "potion_duration";

    @Override
    public String getKey() {
        return "item drop";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String mat = settings.getString(MATERIAL, "arrow").toUpperCase().replace(" ", "_");
        Material material;
        try {
            material = Material.valueOf(mat);
        } catch (Exception ex) {
            return false;
        }
        int amount = settings.getInt(AMOUNT, 1);
        int durability = settings.getInt(DURABILITY, 0);
        int data = settings.getInt(CMD, 0);

        ItemStack item = new ItemStack(material, amount);

        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        if (settings.getString(CUSTOM, "false").equalsIgnoreCase("true")) {
            String name = TextFormatter.colorString(settings.getString(NAME, ""));
            if (name.length() > 0) {
                meta.setDisplayName(name);
            }
            List<String> lore = TextFormatter.colorStringList(settings.getStringList(LORE));
            meta.setLore(lore);
        }
        meta.setCustomModelData(data);
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            damageable.setDamage(durability);
            damageable.setUnbreakable(settings.getBool(UNBREAKABLE, false));
        }

        for (String hideFlag : settings.getStringList(HIDE_FLAGS)) {
            try {
                meta.addItemFlags(ItemFlag.valueOf("HIDE_"+hideFlag.toUpperCase().replace(' ', '_')));
            } catch (IllegalArgumentException ignored) { }
        }

        item.setItemMeta(meta);

        if (item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION) {
            PotionMeta pm = (PotionMeta) meta;
            pm.clearCustomEffects();
            PotionEffect pe = new PotionEffect(
                    PotionEffectType.getByName(settings.getString(POTION_TYPE).replace(" ", "_")),
                    settings.getInt(POTION_DURATION)*20,
                    settings.getInt(POTION_LEVEL)
            );
            int col = Integer.parseInt(settings.getString(POTION_COLOR).substring(1), 16);
            pm.setColor(Color.fromRGB(col));
            pm.addCustomEffect(pe, true);
            item.setItemMeta(pm);
        }

        double forward = parseValues(caster, FORWARD, level, 0);
        double upward = parseValues(caster, UPWARD, level, 0);
        double right = parseValues(caster, RIGHT, level, 0);
        int delay = (int) parseValues(caster, PICKUP_DELAY, level, 0);
        int duration = (int) parseValues(caster, DURATION, level, 0);

        for (LivingEntity target : targets) {
            Location loc = target.getLocation();
            Vector dir = loc.getDirection().setY(0).normalize();
            Vector side = dir.clone().crossProduct(UP);
            loc.add(dir.multiply(forward)).add(0, upward, 0).add(side.multiply(right));
            target.getWorld().dropItem(loc, item, drop -> {
                drop.setPickupDelay(delay);
                drop.setTicksLived(Math.max(6000-duration, 1));
            });
        }
        return targets.size() > 0;
    }
}