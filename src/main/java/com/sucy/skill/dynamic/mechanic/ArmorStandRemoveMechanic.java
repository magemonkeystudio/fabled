package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.armorstand.ArmorStandData;
import com.sucy.skill.api.armorstand.ArmorStandInstance;
import com.sucy.skill.api.armorstand.ArmorStandManager;
import com.sucy.skill.dynamic.TempEntity;
import com.sucy.skill.listener.MechanicListener;
import com.sucy.skill.task.RemoveTask;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Removes an armor stand
 */
public class ArmorStandRemoveMechanic extends MechanicComponent {
    private static final String KEY          = "key";

    @Override
    public String getKey() {
        return "armor stand remove";
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        String  key         = settings.getString(KEY, skill.getName());

        for (LivingEntity target : targets) {
            ArmorStandData data = ArmorStandManager.getArmorStandData(target);
            if (data == null) continue;
            data.remove(key);
        }
        return !targets.isEmpty();
    }
}
