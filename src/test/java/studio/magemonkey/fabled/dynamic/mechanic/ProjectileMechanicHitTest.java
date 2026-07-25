package studio.magemonkey.fabled.dynamic.mechanic;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.listener.MechanicListener;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

/**
 * Regression test for the projectile mechanic firing its callback (and thus
 * its child components) twice for a single entity hit: once via
 * {@link MechanicListener#onDamageByEntity(EntityDamageByEntityEvent)} and
 * once via {@link MechanicListener#onLand(ProjectileHitEvent)}, since Bukkit
 * fires {@link ProjectileHitEvent} for entity hits as well as block hits.
 */
public class ProjectileMechanicHitTest extends MockedTest {
    private Player caster;

    @BeforeEach
    public void setup() {
        caster = genPlayer("Travja");
    }

    /**
     * A no-op child mechanic that just counts how many times it was executed,
     * standing in for whatever the skill's actual child components would do
     * (damage, effects, etc.) when the projectile mechanic calls back.
     */
    private static class CountingMechanic extends MechanicComponent {
        int count = 0;

        @Override
        public String getKey() {
            return "counting-test";
        }

        @Override
        public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
            count++;
            return true;
        }
    }

    private ProjectileMechanic getMechanic(CountingMechanic child) {
        ProjectileMechanic mechanic = new ProjectileMechanic();

        DynamicSkill skill  = new DynamicSkill("Projectile");
        DataSection  config = new DataSection();
        DataSection  data   = new DataSection();
        data.set("projectile", "arrow");
        data.set("amount", 1);
        data.set("velocity", 2.0);
        data.set("cost", "none");
        data.set("distance", 50);
        data.set("lifespan", 9999);
        // Avoid the default "Villager happy" particle key, which isn't a valid
        // Particle enum constant on the Bukkit test API version used here.
        data.set("particle", "flame");
        // Explicitly false, matching the reported bug conditions
        data.set("on-expire", false);
        config.set("data", data);

        mechanic.load(skill, config);
        mechanic.children.add(child);
        return mechanic;
    }

    /**
     * Fires the mechanic through a spy'd caster so the actual Projectile
     * instance that gets launched (via LivingEntity#launchProjectile) can be
     * captured directly, since MockBukkit doesn't register launched
     * projectiles in World#getEntities().
     */
    private Projectile fireProjectile(ProjectileMechanic mechanic) {
        Player spyCaster = spy(caster);
        List<Projectile> captured = new ArrayList<>();
        doAnswer(inv -> {
            Projectile p = (Projectile) inv.callRealMethod();
            captured.add(p);
            return p;
        }).when(spyCaster).launchProjectile(any());

        boolean fired = mechanic.execute(spyCaster, 1, List.of(spyCaster), false);
        assertTrue(fired, "Expected the projectile mechanic to fire");
        assertEquals(1, captured.size(), "Expected exactly one projectile to be launched");

        Projectile projectile = captured.get(0);
        // MockBukkit's launchProjectile() doesn't wire up the shooter the way real Bukkit
        // does, but ProjectileMechanic#callback only executes children when a shooter is
        // present, so it has to be set explicitly here to exercise that code path.
        projectile.setShooter(spyCaster);
        return projectile;
    }

    @Test
    void hittingAnEntityOnlyTriggersCallbackOnce() {
        CountingMechanic   child      = new CountingMechanic();
        ProjectileMechanic mechanic   = getMechanic(child);
        Projectile         projectile = fireProjectile(mechanic);

        assertTrue(projectile.hasMetadata(MechanicListener.P_CALL),
                "Projectile should be tagged for callback handling");

        Zombie target = world.spawn(new Location(world, 5, 0, 0), Zombie.class);

        // Mirrors real Bukkit/Paper behavior: the projectile's hit detection fires
        // ProjectileHitEvent first, then damage is applied via EntityDamageByEntityEvent.
        server.getPluginManager().callEvent(new ProjectileHitEvent(projectile, target));
        server.getPluginManager()
                .callEvent(new EntityDamageByEntityEvent(projectile,
                        target,
                        EntityDamageEvent.DamageCause.PROJECTILE,
                        5D));

        // Both handlers schedule their callback a tick (or more) later
        server.getScheduler().performTicks(60);

        assertEquals(1,
                child.count,
                "Child components should only execute once per entity hit, even though Bukkit "
                        + "fires both ProjectileHitEvent and EntityDamageByEntityEvent for the same hit");
    }

    @Test
    void hittingABlockStillTriggersCallbackOnce() {
        CountingMechanic   child      = new CountingMechanic();
        ProjectileMechanic mechanic   = getMechanic(child);
        Projectile         projectile = fireProjectile(mechanic);

        server.getPluginManager().callEvent(new ProjectileHitEvent(projectile));

        server.getScheduler().performTicks(60);

        assertEquals(1, child.count, "Landing on a block should still trigger the callback once");
    }
}
