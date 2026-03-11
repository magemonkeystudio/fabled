package studio.magemonkey.fabled.api.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.classes.FabledClass;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for bulk skill upgrading via {@link PlayerData#upgradeSkill(studio.magemonkey.fabled.api.skills.Skill, int)}
 * and {@link PlayerData#upgradeSkillToMax(studio.magemonkey.fabled.api.skills.Skill)}.
 *
 * <p>The "NonCastSkill" used here has max-level=5 and costs 1 skill point per level
 * with no class-level requirement beyond 1.</p>
 */
public class SkillUpgradeTest extends MockedTest {

    private PlayerData  playerData;
    private PlayerSkill skill;

    @Override
    public void preInit() {
        loadClasses("Honor Guard");
        loadSkills("NonCastSkill");
    }

    @BeforeEach
    public void setup() {
        PlayerMock  player = genPlayer("Tester");
        playerData = Fabled.getData(player);

        FabledClass pClass = Fabled.getClass("Honor Guard");
        playerData.setClass(null, pClass, true);

        skill = playerData.getSkill("NonCastSkill");
        assertNotNull(skill, "NonCastSkill should be available to Honor Guard");
    }

    // -----------------------------------------------------------------------
    // upgradeSkill(Skill, int) tests
    // -----------------------------------------------------------------------

    @Test
    void upgradeSkill_multiLevel_upgradesExactAmount() {
        playerData.setPoints(3);

        boolean result = playerData.upgradeSkill(skill.getData(), 3);

        assertTrue(result, "upgradeSkill should return true when at least one upgrade succeeds");
        assertEquals(3, skill.getLevel(), "Skill should be at level 3 after upgrading 3 levels");
        assertEquals(0, playerData.getPoints(), "All 3 points should have been spent");
    }

    @Test
    void upgradeSkill_multiLevel_stopsWhenPointsRunOut() {
        playerData.setPoints(2);

        boolean result = playerData.upgradeSkill(skill.getData(), 5);

        assertTrue(result, "upgradeSkill should return true when at least one upgrade succeeds");
        assertEquals(2, skill.getLevel(), "Skill should stop at level 2 when only 2 points are available");
        assertEquals(0, playerData.getPoints(), "All available points should have been spent");
    }

    @Test
    void upgradeSkill_multiLevel_stopsAtMaxLevel() {
        // NonCastSkill max level is 5; request more than max
        playerData.setPoints(10);

        boolean result = playerData.upgradeSkill(skill.getData(), 10);

        assertTrue(result, "upgradeSkill should return true when at least one upgrade succeeds");
        assertEquals(5, skill.getLevel(), "Skill should be capped at max level 5");
        assertEquals(5, playerData.getPoints(), "Exactly 5 points should have been spent to reach max level");
    }

    @Test
    void upgradeSkill_multiLevel_returnsFalseWhenNoPointsAvailable() {
        playerData.setPoints(0);

        boolean result = playerData.upgradeSkill(skill.getData(), 5);

        assertFalse(result, "upgradeSkill should return false when no upgrade is possible");
        assertEquals(0, skill.getLevel(), "Skill level should remain 0");
    }

    @Test
    void upgradeSkill_multiLevel_returnsFalseWhenAlreadyMaxed() {
        playerData.setPoints(10);
        playerData.upgradeSkill(skill.getData(), 5); // max out first
        assertEquals(5, skill.getLevel());

        playerData.setPoints(10);
        boolean result = playerData.upgradeSkill(skill.getData(), 1);

        assertFalse(result, "upgradeSkill should return false when skill is already at max level");
        assertEquals(5, skill.getLevel(), "Skill level should remain at max");
    }

    // -----------------------------------------------------------------------
    // upgradeSkillToMax(Skill) tests
    // -----------------------------------------------------------------------

    @Test
    void upgradeSkillToMax_upgradesAllLevelsAtOnce() {
        playerData.setPoints(10);

        boolean result = playerData.upgradeSkillToMax(skill.getData());

        assertTrue(result, "upgradeSkillToMax should return true when at least one upgrade succeeds");
        assertEquals(5, skill.getLevel(), "Skill should be at max level 5");
        assertEquals(5, playerData.getPoints(), "Exactly 5 points should have been spent");
    }

    @Test
    void upgradeSkillToMax_stopsWhenPointsRunOut() {
        playerData.setPoints(3);

        boolean result = playerData.upgradeSkillToMax(skill.getData());

        assertTrue(result, "upgradeSkillToMax should return true when at least one upgrade succeeds");
        assertEquals(3, skill.getLevel(), "Skill should stop at level 3 when only 3 points are available");
        assertEquals(0, playerData.getPoints(), "All available points should have been spent");
    }

    @Test
    void upgradeSkillToMax_returnsFalseWhenAlreadyMaxed() {
        playerData.setPoints(10);
        playerData.upgradeSkillToMax(skill.getData()); // max out first
        assertEquals(5, skill.getLevel());

        playerData.setPoints(10);
        boolean result = playerData.upgradeSkillToMax(skill.getData());

        assertFalse(result, "upgradeSkillToMax should return false when skill is already at max level");
        assertEquals(5, skill.getLevel(), "Skill level should remain at max");
    }

    @Test
    void upgradeSkillToMax_returnsFalseWhenNoPointsAvailable() {
        playerData.setPoints(0);

        boolean result = playerData.upgradeSkillToMax(skill.getData());

        assertFalse(result, "upgradeSkillToMax should return false when no upgrade is possible");
        assertEquals(0, skill.getLevel(), "Skill level should remain 0");
    }
}
