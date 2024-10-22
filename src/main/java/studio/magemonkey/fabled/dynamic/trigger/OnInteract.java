package studio.magemonkey.fabled.dynamic.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import studio.magemonkey.fabled.api.CastData;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.event.SkillInteractEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

/**
 * Fabled © 2024
 * studio.magemonkey.fabled.dynamic.trigger.InteractTrigger
 */
public class InteractTrigger extends SkillTrigger {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "ON_INTERACT";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getCaster(SkillInteractEvent event) {
        return event.getPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LivingEntity getTarget(SkillInteractEvent event, Settings settings) {
        // Definindo o alvo baseado na configuração
        if (isUsingTarget(settings)) {
            return event.getTarget();
        } else {
            return event.getPlayer();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(SkillInteractEvent event, CastData data) {
        data.put("target-type", event.getTarget().getType().toString());
        data.put("target-name", event.getTarget().getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldTrigger(SkillInteractEvent event, int level, Settings settings) {
        String mobType = settings.getString("MobType", "ALL");
        if (!mobType.equalsIgnoreCase("ALL") &&
                !event.getTarget().getType().toString().equalsIgnoreCase(mobType)) {
            return false;
        }

        // Hook para Citizens NPC se necessário
        if (settings.has("CitizensNPCID")) {
            int npcId = settings.getInt("CitizensNPCID");
            if (event.getTarget().hasMetadata("NPC")) {
                int targetNpcId = getNpcId(event.getTarget());
                return targetNpcId == npcId;
            }
            return false;
        }

        return true; // Se passou em todas as verificações, o trigger deve ocorrer
    }

    /**
     * Helper para pegar o ID do NPC Citizens
     */
    private int getNpcId(LivingEntity entity) {
        // Verifica se o NPC é do Citizens e retorna o ID
        if (CitizensAPI.getNPCRegistry().isNPC(entity)) {
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
            return npc.getId(); // Retorna o ID do NPC
        }
        return -1; // Retorna -1 se não for um NPC válido
    }

    /**
     * Definir se está usando o alvo de interação
     */
    private boolean isUsingTarget(Settings settings) {
        return settings.getString("use-target", "true").equalsIgnoreCase("true");
    }
}
