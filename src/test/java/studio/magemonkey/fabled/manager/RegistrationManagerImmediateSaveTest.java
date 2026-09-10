package studio.magemonkey.fabled.manager;

import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Covers RegistrationManager's switch from a deferred/scheduled batch save to saving each
 * dynamic class/skill file immediately during initialization (soft-save -> load -> save).
 */
public class RegistrationManagerImmediateSaveTest extends MockedTest {
    @Override
    public void preInit() {
        loadClasses("Honor Guard");
    }

    @Test
    void dynamicClassRegistration_rewritesFileOnDiskDuringInit() throws Exception {
        assertNotNull(Fabled.getClass("Honor Guard"));

        String originalFixture;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("classes/Honor Guard.yml")) {
            originalFixture = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        File classFile = new File(
                getPluginFolder() + File.separator + "dynamic" + File.separator + "class",
                "Honor Guard.yml");
        assertNotNull(classFile);
        String diskContent = Files.readString(classFile.toPath());

        // The file was rewritten via FabledClass#save (soft-save -> load -> save) during
        // registration, not left as the pristine hand-authored fixture pending some later
        // scheduled flush that this test never ticks toward.
        assertNotEquals(originalFixture, diskContent);
    }

    @Test
    void repeatedReloads_doNotCorruptRegisteredClass() {
        assertNotNull(Fabled.getClass("Honor Guard"));
        assertEquals(50, Fabled.getClass("Honor Guard").getMaxLevel());

        // Previously, save() was deferred to a scheduled task per initialize() call;
        // reloading again before that task ran risked queuing overlapping/duplicate
        // flushes. The immediate-save approach has no such queue to build up.
        reload();
        assertNotNull(Fabled.getClass("Honor Guard"));
        assertEquals(50, Fabled.getClass("Honor Guard").getMaxLevel());

        reload();
        assertNotNull(Fabled.getClass("Honor Guard"));
        assertEquals(50, Fabled.getClass("Honor Guard").getMaxLevel());
    }
}
