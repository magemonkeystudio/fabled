package studio.magemonkey.fabled.shield;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.codex.api.items.PrefixHelper;
import studio.magemonkey.codex.registry.provider.BuffProvider;
import studio.magemonkey.fabled.Fabled;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class ShieldManager implements BuffProvider {
    private final Fabled                   plugin;
    private final Map<UUID, ShieldDetails> shieldDetails = new HashMap<>();

    @Nullable
    public ShieldDetails getShieldDetails(@NotNull LivingEntity entity) {
        return shieldDetails.get(entity.getUniqueId());
    }

    public void setShieldDetails(@NotNull LivingEntity entity, @NotNull ShieldDetails details) {
        shieldDetails.put(entity.getUniqueId(), details);
    }

    public void removeShieldDetails(LivingEntity entity) {
        ShieldDetails details = shieldDetails.remove(entity.getUniqueId());
        if (details != null) {
            details.clearEffects();
        }
    }

    public void clearShields() {
        shieldDetails.values().forEach(ShieldDetails::clearEffects);
        shieldDetails.clear();
    }

    public boolean hasShield(@NotNull LivingEntity entity) {
        return shieldDetails.containsKey(entity.getUniqueId());
    }

    public void addEffect(@NotNull LivingEntity entity, @NotNull ShieldEffect effect, int ticks) {
        UUID          uuid    = entity.getUniqueId();
        ShieldDetails details = shieldDetails.computeIfAbsent(uuid, ShieldDetails::new);
        details.addEffect(effect, ticks);

        displayShieldDetails(entity);
    }

    public void displayShieldDetails(@NotNull LivingEntity entity) {
        if (!(entity instanceof Player)) {
            return;
        }

        ShieldDetails details = getShieldDetails(entity);
        if (details == null) {
            return;
        }

        if (details.getDisplayTask() != null && !details.getDisplayTask().isCancelled()) {
            // Task is actively running, no need to display the shield effects
            return;
        }

        details.setDisplayTask(plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!entity.isValid()) {
                details.getDisplayTask().cancel();
                return;
            }

            if (!details.hasEffects()) {
                details.getDisplayTask().cancel();
            }

            for (ShieldEffect effect : details.getActiveEffects()) {
                // Display the shield effect to the entity
                effect.display((Player) entity);
            }
        }, 0, 20));
    }

    @Override
    public double scaleValue(String identifier, @NotNull LivingEntity entity, double value) {
        identifier = PrefixHelper.stripPrefix("FABLED", identifier);

        ShieldDetails details = getShieldDetails(entity);
        // If they don't have any shield effects, return the unmodified value
        if (details == null) {
            return value;
        }

        double scaled = value;
        for (ShieldEffect effect : details.getActiveEffects()) {
            // If the effect's classifier matches the identifier, scale the value
            if (!effect.getClassifier().equals(identifier)) continue;

            scaled = effect.damageAndDisplay(scaled, entity);
        }

        return scaled;
    }

    @Override
    public double scaleDamageForDefense(String identifier, @NotNull LivingEntity entity, double damage) {
        identifier = PrefixHelper.stripPrefix("FABLED", identifier);

        ShieldDetails details = getShieldDetails(entity);
        // If they don't have any shield effects, return the unmodified value
        if (details == null) {
            return damage;
        }

        double actualDamage = damage;
        for (ShieldEffect effect : details.getActiveEffects()) {
            // If the effect's classifier matches the identifier, scale the value
            if (!effect.getClassifier().equals(identifier)) continue;

            actualDamage = effect.damageAndDisplay(actualDamage, entity);
        }

        return actualDamage;
    }
}
