package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.api.skills.SkillShot;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillCastMechanic extends MechanicComponent {
    @Override
    public String getKey() {
        return "skill cast";
    }

    private static void cast(LivingEntity caster, String sk, int lv, boolean force) {
        if (sk == null || sk.isBlank()) return;

        if (caster instanceof Player) {
            PlayerData data = Fabled.getData((Player) caster);
            if (lv <= 0)
                lv = (data.hasSkill(sk) && data.getSkill(sk).getLevel() > 0) ? data.getSkill(sk).getLevel() : 1;
        }
        Skill skill = Fabled.getSkill(sk);
        if (skill == null) {
            Fabled.inst().getLogger().warning("Attempted to cast skill " + sk + " but it does not exist.");
            return;
        }
        ((SkillShot) skill).cast(caster, lv, force);
    }

    private static int parseInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }

    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        if (targets.isEmpty()) return false;

        //  First (cast first available)/ All (cast all available)/ Random (cast random available)
        final String       mode       = settings.getString("mode", "first").toLowerCase();
        final boolean      force_cast = settings.getBool("force", false);
        final List<String> skills     = settings.getStringList("skills");

        targets.forEach(target -> {
            //  Split skills input into skill name and level
            List<Map.Entry<String, Integer>> handle = new ArrayList<>();
            skills.forEach(s -> {
                String[] split = s.split(":", 2);
                handle.add(new AbstractMap.SimpleEntry<>(split[0], parseInt(split.length > 1 ? split[1] : null)));
            });

            //  Filter out skills that can't be cast
            List<Map.Entry<String, Integer>> filtered = handle.stream().filter(e
                    -> Fabled.getSkill(e.getKey()) != null
                    && Fabled.getSkill(e.getKey()) instanceof SkillShot
                    && (force_cast || (!(target instanceof Player) || ( // Bypass checks if the target is not a player
                    Fabled.getData((Player) target).hasSkill(e.getKey())
                            && Fabled.getData((Player) target).getSkill(e.getKey()).getLevel() > 0)))
            ).collect(Collectors.toList());
            if (filtered.isEmpty()) return;

            //  Cast
            switch (mode) {
                case "first" -> cast(target, filtered.get(0).getKey(), filtered.get(0).getValue(), force_cast);
                case "all" -> handle.forEach(entry -> cast(target, entry.getKey(), entry.getValue(), force_cast));
                case "random" -> {
                    int i = Fabled.RANDOM.nextInt(filtered.size());
                    cast(target, filtered.get(i).getKey(), filtered.get(i).getValue(), force_cast);
                }
            }
        });
        return true;
    }
}
