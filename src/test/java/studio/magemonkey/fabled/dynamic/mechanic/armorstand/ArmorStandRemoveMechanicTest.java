package studio.magemonkey.fabled.dynamic.mechanic.armorstand;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.armorstand.ArmorStandInstance;
import studio.magemonkey.fabled.api.armorstand.ArmorStandManager;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.listener.MechanicListener;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests for issue #1719: "armor stand remove" did nothing when it targeted
 * the armor stand itself, which is what the children of "armor stand" target.
 */
public class ArmorStandRemoveMechanicTest extends MockedTest {
    private static final String KEY = "stand";

    private Player caster;

    @BeforeEach
    void setup() {
        caster = genPlayer("Travja");
    }

    @AfterEach
    void cleanUp() {
        ArmorStandManager.cleanUp();
    }

    private ArmorStandRemoveMechanic getMechanic() {
        ArmorStandRemoveMechanic mechanic = new ArmorStandRemoveMechanic();
        DynamicSkill             skill    = new DynamicSkill("Test");
        DataSection              config   = new DataSection();
        DataSection              data     = new DataSection();
        data.set("key", KEY);
        config.set("data", data);
        mechanic.load(skill, config);
        return mechanic;
    }

    private ArmorStand spawnStand(boolean register) {
        ArmorStand stand = caster.getWorld().spawn(caster.getLocation(), ArmorStand.class);
        Fabled.setMeta(stand, MechanicListener.ARMOR_STAND, true);
        if (register) ArmorStandManager.register(new ArmorStandInstance(stand, caster, false), caster, KEY);
        return stand;
    }

    @Test
    void execute_targetingOwner_removesStandByKey() {
        ArmorStand stand = spawnStand(true);

        getMechanic().execute(caster, 1, List.of(caster), false);
        server.getScheduler().performOneTick();

        assertFalse(ArmorStandManager.getArmorStand(caster, KEY).isPresent());
        assertFalse(stand.isValid());
    }

    @Test
    void execute_targetingStandItself_removesStand() {
        ArmorStand stand = spawnStand(true);

        getMechanic().execute(caster, 1, List.of(stand), false);
        server.getScheduler().performOneTick();

        assertFalse(ArmorStandManager.getArmorStand(caster, KEY).isPresent());
        assertFalse(stand.isValid());
    }

    @Test
    void execute_targetingUnregisteredMechanicStand_removesStand() {
        ArmorStand stand = spawnStand(false);

        getMechanic().execute(caster, 1, List.of(stand), false);

        assertFalse(stand.isValid());
    }

    @Test
    void execute_targetingStandItself_leavesOtherStandsAlone() {
        ArmorStand stand = spawnStand(true);
        ArmorStand other = caster.getWorld().spawn(caster.getLocation(), ArmorStand.class);
        Fabled.setMeta(other, MechanicListener.ARMOR_STAND, true);
        ArmorStandManager.register(new ArmorStandInstance(other, caster, false), caster, "other");

        getMechanic().execute(caster, 1, List.of(stand), false);
        server.getScheduler().performOneTick();

        assertFalse(stand.isValid());
        assertTrue(other.isValid());
        assertTrue(ArmorStandManager.getArmorStand(caster, "other").isPresent());
    }

    @Test
    void execute_targetingPlainArmorStand_doesNotRemoveIt() {
        ArmorStand stand = caster.getWorld().spawn(caster.getLocation(), ArmorStand.class);

        getMechanic().execute(caster, 1, List.of(stand), false);
        server.getScheduler().performOneTick();

        assertTrue(stand.isValid());
    }
}
