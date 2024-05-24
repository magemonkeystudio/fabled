package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import studio.magemonkey.codex.mccore.util.VersionManager;

import java.util.List;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.mechanic.DurabilityMechanic
 */
public class DurabilityMechanic extends MechanicComponent {
    private static final String AMOUNT  = "amount";
    private static final String OFFHAND = "offhand";

    @Override
    public String getKey() {
        return "durability";
    }

    @Override
    public boolean execute(
            final LivingEntity caster, final int level, final List<LivingEntity> targets, boolean force) {
        if (!(caster instanceof Player)) {
            return false;
        }

        final Player  player  = (Player) caster;
        final boolean offhand = settings.getBool(OFFHAND, false);
        final short   amount  = (short) (parseValues(caster, AMOUNT, level, 1) * targets.size());

        final ItemStack item;
        if (offhand && VersionManager.isVersionAtLeast(VersionManager.V1_9_0)) {
            item = player.getInventory().getItemInOffHand();
        } else {
            //noinspection deprecation
            item = player.getInventory().getItemInHand();
        }

        if (item.getType().isAir() || item.getType().getMaxDurability() == 0) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (!(itemMeta instanceof Damageable)) {
            return false;
        }

        Damageable im         = (Damageable) itemMeta;
        int        durability = item.getType().getMaxDurability() - im.getDamage();
        if (amount > 0 && durability <= amount) {
            if (offhand && VersionManager.isVersionAtLeast(VersionManager.V1_9_0)) {
                player.getInventory().setItemInOffHand(null);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        }
        im.setDamage(im.getDamage() + amount);
        item.setItemMeta(im);
        return true;
    }
}
