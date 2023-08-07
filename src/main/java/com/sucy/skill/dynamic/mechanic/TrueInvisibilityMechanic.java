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

public class TrueInvisibilityMechanic extends MechanicComponent{

    @Override
    public String getKey() {
        return "true invisibility";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        int duration = settings.getInt("duration",20);
        PotionEffect potion = new PotionEffect(PotionEffectType.INVISIBILITY, duration,0,false);
        caster.addPotionEffect(potion);
        BuffManager.addBuff(caster,BuffType.TRUE_INVISIBILITY,new Buff(this.skill.getName(),0,false),duration);
        if (!(caster instanceof Player)) return true;
        if (PluginChecker.isProtocolLibActive())
            PacketListener.updateEquipment((Player) caster);
        return true;
    }
}
