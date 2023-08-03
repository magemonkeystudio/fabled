package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class SkillCastMechanic extends MechanicComponent {
    @Override
    public String getKey() {
        return "skill cast";
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
        if (targets.isEmpty()) return false;

        //  First (cast first available)/ All (cast all available)/ Random (cast random available)
        final String mode = settings.getString("mode", "First").toLowerCase();
        final boolean force_cast = settings.getBool("force", false);
        final List<String> skills = settings.getStringList("skills");

        targets.forEach(target -> {
            if (!(target instanceof Player)) return;
            Player player = (Player) target;
            PlayerData data = SkillAPI.getPlayerData(player);

            //  Split skills input into skill name and level
            List<Map.Entry<String, Integer>> handle = new ArrayList<>();
            skills.forEach(s->{
                String[] split = s.split(":", 2);
                handle.add(new AbstractMap.SimpleEntry<>(split[0], parseInt(split.length>1?split[1]:null)));
            });

            //  Filter out skills that can't be cast
            List<Map.Entry<String, Integer>> filtered = handle.stream().filter(e
                    -> SkillAPI.getSkill(e.getKey())!=null
                    && SkillAPI.getSkill(e.getKey()) instanceof SkillShot
                    && (force_cast || (data.hasSkill(e.getKey()) && data.getSkill(e.getKey()).getLevel()>0))
            ).collect(Collectors.toList());
            if (filtered.isEmpty()) return;

            //  Cast
            switch (mode) {
                case "first" -> cast(player, filtered.get(0).getKey(), filtered.get(0).getValue(), force_cast);
                case "all" -> handle.forEach(entry -> cast(player, entry.getKey(), entry.getValue(), force_cast));
                case "random" -> {
                    int i = new Random().nextInt(filtered.size());
                    cast(player, filtered.get(i).getKey(), filtered.get(i).getValue(), force_cast);
                }
            }
        });
        return true;
    }

    private static int parseInt(String input){
        try {return Integer.parseInt(input);}
        catch (NumberFormatException | NullPointerException e){
            return -1;
        }
    }
    private static void cast(Player player, String sk, int lv, boolean force){
        PlayerData data = SkillAPI.getPlayerData(player);
        if (lv<=0) lv = (data.hasSkill(sk) && data.getSkill(sk).getLevel()>0) ? data.getSkill(sk).getLevel():1;
        Skill skill = SkillAPI.getSkill(sk);
        ((SkillShot) skill).cast(player, lv, force);
    }
}
