package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.api.util.Buff;
import com.sucy.skill.api.util.BuffManager;
import com.sucy.skill.api.util.BuffType;
import com.sucy.skill.hook.PluginChecker;
import com.sucy.skill.listener.PacketListener;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Applies invisibility, optionally hiding target's equipment
 */
public class InvisibilityMechanic extends MechanicComponent{

    @Override
    public String getKey() {
        return "invisibility";
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        int             duration        = settings.getInt("duration",20);
        boolean         hideEquipment   = settings.getBool("hideEquipment",false);
        PotionEffect    potion          = new PotionEffect(PotionEffectType.INVISIBILITY, duration,0,false);
        targets.forEach(c->c.addPotionEffect(potion));
        if (!hideEquipment || !PluginChecker.isProtocolLibActive()) return true;
        targets.forEach(target->{
            if(!(target instanceof Player)) return;
            BuffManager.addBuff(target,BuffType.INVISIBILITY,new Buff(this.skill.getName(),0,false),duration);
            PacketListener.updateEquipment((Player) target);
        });
        return true;
    }
}
