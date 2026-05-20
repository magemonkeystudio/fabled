package studio.magemonkey.fabled.dynamic.condition;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.metadata.FixedMetadataValue;
import studio.magemonkey.fabled.Fabled;

public class AttackIndicatorCondition extends ConditionComponent {
    private static final String MIN    = "min";
    private static final String MAX    = "max";
    private static final String WEAPON = "weapon";

    // Metadata key — set by ShootListener when player fires bow/crossbow.
    // Read by getIndicatorValue when normal polling fails (post-launch state).
    private static final String LAST_SHOOT_FORCE_META = "fabled_last_shoot_force";

    // Paper-only APIs accessed via reflection — runtime is Paper, but compile classpath is Spigot.
    private static java.lang.reflect.Method ACTIVE_ITEM_METHOD;
    private static java.lang.reflect.Method IS_CHARGED_METHOD;

    private static ItemStack getActiveItemSafe(Player player) {
        try {
            if (ACTIVE_ITEM_METHOD == null) {
                ACTIVE_ITEM_METHOD = Player.class.getMethod("getActiveItem");
            }
            return (ItemStack) ACTIVE_ITEM_METHOD.invoke(player);
        } catch (Throwable ignored) {
            return player.getInventory().getItemInMainHand();
        }
    }

    private static boolean isChargedSafe(CrossbowMeta meta) {
        try {
            if (IS_CHARGED_METHOD == null) {
                IS_CHARGED_METHOD = CrossbowMeta.class.getMethod("isCharged");
            }
            return (Boolean) IS_CHARGED_METHOD.invoke(meta);
        } catch (Throwable ignored) {
            return meta.hasChargedProjectiles();
        }
    }

    /**
     * Static listener that captures bow/crossbow shoot force as metadata on the shooter.
     * EntityShootBowEvent fires BEFORE ProjectileLaunchEvent (Launch trigger). Metadata is
     * read by AttackIndicator on the same tick, then cleared one tick later.
     */
    public static class ShootListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onShoot(EntityShootBowEvent e) {
            if (!(e.getEntity() instanceof Player)) return;
            final Player p = (Player) e.getEntity();
            p.setMetadata(LAST_SHOOT_FORCE_META,
                    new FixedMetadataValue(Fabled.inst(), (double) e.getForce()));
            Bukkit.getScheduler().runTaskLater(Fabled.inst(),
                    () -> p.removeMetadata(LAST_SHOOT_FORCE_META, Fabled.inst()), 1L);
        }
    }

    @Override
    public String getKey() {
        return "attack indicator";
    }

    @Override
    public boolean test(LivingEntity caster, int level, LivingEntity target) {
        double min = parseValues(target, MIN, level, settings.getFloat(MIN, 0));
        double max = parseValues(target, MAX, level, settings.getFloat(MAX, 1));

        if (!(caster instanceof Player))
            return false;
        Player player = (Player) caster;

        double value = getIndicatorValue(player);
        return value >= min && value <= max;
    }

    private double getIndicatorValue(Player player) {
        String weapon = settings.getString(WEAPON, "auto").toLowerCase();
        ItemStack main = player.getInventory().getItemInMainHand();
        Material type = main.getType();

        // Post-launch path — Launch trigger fires after release/fire, so polling player state
        // returns 0. Use force snapshot captured by ShootListener on EntityShootBowEvent.
        if (player.hasMetadata(LAST_SHOOT_FORCE_META)) {
            try {
                if ((weapon.equals("auto") && (type == Material.BOW || type == Material.CROSSBOW))
                        || weapon.equals("bow") || weapon.equals("crossbow")) {
                    return Math.min(1.0, player.getMetadata(LAST_SHOOT_FORCE_META).get(0).asDouble());
                }
            } catch (Exception ignored) { /* fall through to polling path */ }
        }

        if (weapon.equals("bow") || (weapon.equals("auto") && type == Material.BOW)) {
            if (!player.isHandRaised() || getActiveItemSafe(player).getType() != Material.BOW)
                return 0.0;
            return Math.min(1.0, player.getItemInUseTicks() / 20.0);
        }

        if (weapon.equals("crossbow") || (weapon.equals("auto") && type == Material.CROSSBOW)) {
            ItemStack crossbow = (type == Material.CROSSBOW) ? main : player.getInventory().getItemInOffHand();
            if (crossbow.getType() != Material.CROSSBOW)
                return 0.0;
            if (crossbow.getItemMeta() instanceof CrossbowMeta) {
                CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
                if (isChargedSafe(meta))
                    return 1.0;
            }
            // Partially loaded (player is drawing crossbow)
            if (player.isHandRaised() && getActiveItemSafe(player).getType() == Material.CROSSBOW) {
                Enchantment quickCharge = Enchantment.getByKey(NamespacedKey.minecraft("quick_charge"));
                int qcLevel = (quickCharge != null) ? crossbow.getEnchantmentLevel(quickCharge) : 0;
                int loadTicks = Math.max(5, 25 - qcLevel * 5);
                return Math.min(1.0, player.getItemInUseTicks() / (double) loadTicks);
            }
            return 0.0;
        }

        return player.getAttackCooldown();
    }
}
