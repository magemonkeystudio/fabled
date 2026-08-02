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
 * where a Particle Projectile with {@code collision-radius: 0.5} never triggered
 * its child mechanics (e.g. Damage) on hit, with or without homing enabled.
 *
 * <p>Root cause was two-fold in {@link CustomProjectile}:</p>
 * <ol>
 *   <li>{@code getColliding()} used to rely on raw NMS reflection for an
 *       accurate AABB entity query. On servers where that reflection setup
 *       failed to resolve (as happened on the reporter's
 *       {@code paper-26.1.2-74}, once Paper stopped shipping obfuscated jars
 *       and the guessed NMS class/method names stopped existing), it fell
 *       back to a hand-rolled chunk scan.</li>
 *   <li>That chunk-scan fallback had an off-by-one bug on the Z axis
 *       (exclusive {@code j < maxZ} instead of inclusive, unlike the X loop),
 *       which meant it silently found zero entities whenever the collision
 *       radius didn't straddle a chunk seam -- effectively always, for small
 *       radii like the reported 0.5.</li>
 * </ol>
 *
 * <p>Both issues are gone now: {@code getColliding()} uses Bukkit's public
 * {@code World#getNearbyEntities(BoundingBox)}, which has been available
 * since well before this plugin's minimum supported version (1.16.5) and
 * doesn't depend on any Minecraft-version-specific internals. These tests
 * now serve as a plain regression check that collision detection works at
 * small radii and doesn't false-positive at long range.</p>
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
     * Mirrors the reported skill: a 0.5-block collision radius with a target
     * well within range.
     */
    @Test
    void targetWithinSmallRadiusShouldBeDetected() {
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
                "Zombie 0.3 blocks away should register as a hit within the 0.5 collision radius");
    }

    /**
     * Same scenario at the mechanic's own default collision radius (1.5),
     * proving this isn't specific to the reporter's 0.5 config.
     */
    @Test
    void targetWithinDefaultRadiusShouldBeDetected() {
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
