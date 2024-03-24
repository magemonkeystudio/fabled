package com.promcteam.fabled.dataload;

import com.promcteam.fabled.Fabled;
import com.promcteam.fabled.testutil.MockedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DataLoadTest extends MockedTest {

    @Override
    public void preInit() {
        loadClasses("Honor Guard");
        loadSkills("Brilliance Strike");
    }

    @Test
    void honorGuardLoads() {
        assertNotNull(Fabled.getClass("Honor Guard"));
    }

    @Test
    void brillianceStrikeLoads() {
        assertNotNull(Fabled.getSkill("Brilliance Strike"));
    }

    @Test
    void masterFilesLoad() {
        unloadClasses("Honor Guard");
        unloadSkills("Brilliance Strike");
        useClasses("full/classes.yml");
        useSkills("full/skills.yml");
        reload();

        assertEquals(8, Fabled.getClasses().size());
        assertEquals(41, Fabled.getSkills().size());

        Fabled.getClasses().forEach((name, clazz) -> {
            assert (!clazz.getSkills().isEmpty());
        });
    }
}