package studio.magemonkey.fabled.dynamic.mechanic;

import studio.magemonkey.codex.mccore.util.VersionManager;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
            item = player.getInventory().getItemInHand();
        }

        if (item == null || item.getType().getMaxDurability() == 0) {
            return false;
        }

        int durability = item.getType().getMaxDurability() - item.getDurability();
        if (durability <= -amount) {
            if (offhand && VersionManager.isVersionAtLeast(VersionManager.V1_9_0)) {
                player.getInventory().setItemInOffHand(null);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        }
        item.setDurability((short) (item.getDurability() - amount));
        return true;
    }
}
