package com.sucy.skill.dynamic.mechanic;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

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
            HashMap<String, Integer> handle = new HashMap<>();
            skills.parallelStream().forEach(s->{
                String[] split = s.split(":", 2);
                handle.put(split[0], parseInt(split.length>1?split[1]:null));
            });

            //  Filter out skills that can't be cast
            String[] filtered = (String[]) handle.keySet().parallelStream().filter(s
                    -> SkillAPI.getSkill(s)!=null
                    && SkillAPI.getSkill(s) instanceof SkillShot
                    && (force_cast || (data.hasSkill(s) && data.getSkill(s).getLevel()>0))
            ).toArray();
            if (filtered.length==0) return;

            //  Cast
            switch (mode) {
                case "first" -> cast(player, filtered[0], handle.get(filtered[0]), force_cast);
                case "all" -> handle.forEach((sk, lv) -> cast(player, sk, lv, force_cast));
                case "random" -> {
                    int i = new Random().nextInt(filtered.length);
                    cast(player, filtered[i], handle.get(filtered[i]), force_cast);
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
