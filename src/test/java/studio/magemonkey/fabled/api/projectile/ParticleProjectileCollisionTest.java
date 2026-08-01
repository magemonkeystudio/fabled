package studio.magemonkey.fabled.api.projectile;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Regression test for a Discord report ("에테르 쇼크" skill, thread 1530101010129748020)
 * where a Particle Projectile with {@code collision-radius: 0.5} never triggers
 * its child mechanics (e.g. Damage) on hit, with or without homing enabled.
 *
 * <p>Root cause: {@link CustomProjectile#getColliding()} normally relies on NMS
 * reflection for an accurate AABB entity query, set up once in a static
 * initializer. {@link MockedTest} mocks {@code Reflex} with no stubbing, so
 * that reflection setup always fails here -- the same way it fails on real
 * servers where the expected NMS class/method names don't resolve (as in the
 * reported {@code paper-26.1.2-74} case) -- which means every test in this
 * class exercises the same fallback path real users hit: {@link
 * CustomProjectile#getNearbyEntities()}.</p>
 *
 * <p>That fallback has an off-by-one bug on the Z axis:</p>
 * <pre>
 *   for (int i = minX; i <= maxX; i++)
 *       for (int j = minZ; j <  maxZ; j++)   // should be &lt;=, like the X loop
 * </pre>
 * <p>Since {@code minZ == maxZ} whenever the collision radius doesn't straddle
 * a chunk boundary on the Z axis -- guaranteed for small radii like 0.5, and
 * true most of the time even at the mechanic's own default of 1.5 -- the inner
 * loop body never runs, so {@code getNearbyEntities()} returns an empty list
 * and the projectile silently passes through targets it should have hit.</p>
 */
public class ParticleProjectileCollisionTest extends MockedTest {
    private Player caster;

    @BeforeEach
    public void setup() {
        caster = genPlayer("Travja");
    }

    private ParticleProjectile launch(Location loc, double collisionRadius) {
        Settings settings = new Settings();
        settings.set(ParticleProjectile.RADIUS, collisionRadius, 0);
        return new ParticleProjectile(caster, 1, loc, settings, 100, 50);
    }

    /**
     * Mirrors the reported skill: a 0.5-block collision radius, target well
     * inside the same chunk as the projectile (chunks are 16 blocks wide, so
     * minZ == maxZ here for any radius under ~16 blocks) -- the overwhelmingly
     * common case in practice.
     */
    @Test
    void sameChunkTargetWithinRadiusShouldBeDetected() {
        // Each test uses a source location far from the others: MockedTest shares one
        // world for the whole class and never despawns entities between tests, so
        // overlapping coordinates would let a leftover zombie from another test get
        // picked up here instead.
        Location source = new Location(world, 8, 0, 8);
        source.setDirection(new Vector(1, 0, 0));
        Zombie target = world.spawn(new Location(world, 8.3, 0, 8), Zombie.class);

        ParticleProjectile projectile = launch(source, 0.5);
        AtomicReference<LivingEntity> hitEntity = new AtomicReference<>();
        projectile.setCallback((p, hit) -> hitEntity.set(hit));

        projectile.checkCollision(false);

        assertSame(target, hitEntity.get(),
                "Zombie 0.3 blocks away (within the 0.5 collision radius) should register as a hit, "
                        + "but getNearbyEntities()'s Z-axis loop (`j < maxZ`) never runs when the source "
                        + "and target share a chunk, so the hit is silently missed");
    }

    /**
     * Same scenario at the mechanic's own default collision radius (1.5),
     * proving this isn't specific to the reporter's 0.5 config -- it affects
     * any projectile whose radius doesn't happen to straddle a chunk seam.
     */
    @Test
    void sameChunkTargetWithinDefaultRadiusShouldBeDetected() {
        Location source = new Location(world, 108, 0, 8);
        source.setDirection(new Vector(1, 0, 0));
        Zombie target = world.spawn(new Location(world, 109, 0, 8), Zombie.class);

        ParticleProjectile projectile = launch(source, 1.5);
        AtomicReference<LivingEntity> hitEntity = new AtomicReference<>();
        projectile.setCallback((p, hit) -> hitEntity.set(hit));

        projectile.checkCollision(false);

        assertSame(target, hitEntity.get(),
                "Zombie 1 block away (within the default 1.5 collision radius) should register as a hit");
    }

    /**
     * Control case: a target genuinely outside the collision radius should
     * never be detected, confirming the test setup isn't just permissive.
     */
    @Test
    void distantTargetOutsideRadiusShouldNotBeDetected() {
        Location source = new Location(world, 208, 0, 8);
        source.setDirection(new Vector(1, 0, 0));
        world.spawn(new Location(world, 220, 0, 8), Zombie.class);

        ParticleProjectile projectile = launch(source, 0.5);
        AtomicReference<LivingEntity> hitEntity = new AtomicReference<>();
        projectile.setCallback((p, hit) -> hitEntity.set(hit));

        projectile.checkCollision(false);

        assertNull(hitEntity.get(), "A zombie 12 blocks away should not register as a hit");
    }
}
