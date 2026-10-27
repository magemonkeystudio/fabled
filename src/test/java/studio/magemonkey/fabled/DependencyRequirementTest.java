package studio.magemonkey.fabled;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static studio.magemonkey.fabled.DependencyRequirement.compareVersions;
import static studio.magemonkey.fabled.DependencyRequirement.meetsVersion;

class DependencyRequirementTest {

    @Test
    void versionsAreOrderedLikeSemver() {
        String[] ascending = {
                "1.1.1-R0.17-SNAPSHOT", "1.1.1", "1.2.0-R0.7-SNAPSHOT", "1.2.0",
                "1.2.1-dev.1", "1.2.1-dev.2", "1.2.1-dev.10", "1.2.1-SNAPSHOT", "1.2.1",
                "1.2.2-dev.1", "1.3.0", "1.10.0", "2.0.0"
        };
        for (int i = 0; i < ascending.length; i++) {
            for (int j = 0; j < ascending.length; j++) {
                assertEquals(Integer.signum(Integer.compare(i, j)),
                        Integer.signum(compareVersions(ascending[i], ascending[j])),
                        ascending[i] + " vs " + ascending[j]);
            }
        }
    }

    @Test
    void releaseIsHigherThanItsDevBuilds() {
        assertTrue(meetsVersion("1.2.1-dev.9", "1.2.1"));
        assertFalse(meetsVersion("1.2.1", "1.2.1-dev.9"));
        assertFalse(meetsVersion("1.2.1", "1.2.1-SNAPSHOT"));
    }

    @Test
    void minimumCanBeADevBuild() {
        String min = "1.2.1-dev.3";
        assertTrue(meetsVersion(min, "1.2.1-dev.3"));
        assertTrue(meetsVersion(min, "1.2.1-dev.4"));
        assertTrue(meetsVersion(min, "1.2.1"));
        assertTrue(meetsVersion(min, "1.2.2-dev.1"));
        assertTrue(meetsVersion(min, "1.3.0"));
        assertFalse(meetsVersion(min, "1.2.1-dev.2"));
        assertFalse(meetsVersion(min, "1.2.0"));
    }

    @Test
    void releaseMinimum() {
        String min = "1.2.0";
        assertTrue(meetsVersion(min, "1.2.0"));
        assertTrue(meetsVersion(min, "1.2.1-dev.1"));
        assertTrue(meetsVersion(min, "2.0.0"));
        assertFalse(meetsVersion(min, "1.2.0-R0.7-SNAPSHOT"));
        assertFalse(meetsVersion(min, "1.1.1-R1"));
    }

    @Test
    void legacyAndUnusualVersions() {
        assertEquals(0, compareVersions("1.1.1-R1", "1.1.1"));
        assertEquals(0, compareVersions("1.2.0", "1.2"));
        assertEquals(0, compareVersions("1.2.1-dev.3+abc1234", "1.2.1-dev.3"));
        assertTrue(meetsVersion("", "anything"));
        assertTrue(meetsVersion(null, "1.0.0"));
        assertFalse(meetsVersion("1.0.0", ""));
        assertFalse(meetsVersion("1.0.0", null));
    }
}
