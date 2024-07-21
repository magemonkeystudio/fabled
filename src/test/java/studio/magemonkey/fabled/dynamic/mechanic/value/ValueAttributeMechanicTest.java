package studio.magemonkey.fabled.dynamic.mechanic.value;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import studio.magemonkey.codex.mccore.config.parse.DataSection;
import studio.magemonkey.fabled.dynamic.DynamicSkill;
import studio.magemonkey.fabled.testutil.MockedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValueAttributeMechanicTest extends MockedTest {
    private ValueAttributeMechanic mechanic;
    private DynamicSkill           skill;
    private DataSection            config;
    private DataSection            data;

    @BeforeEach
    void setUp() {
        mechanic = new ValueAttributeMechanic();
        skill = new DynamicSkill("Test");

        config = new DataSection();
        data = new DataSection();
        data.set("key", "test");
        data.set("save", "false");
        config.set("data", data);

        DataSection preview = new DataSection();
        preview.set("enabled", false);
        config.set("preview", preview);
    }

    @Test
    void getAttribute_parsesSingleString() {
        data.set("attribute", "vitality");
        mechanic.load(skill, config);

        String key = mechanic.getAttribute();
        assertEquals("vitality", key);
    }

    @Test
    void getAttribute_parsesStringList() {
        data.set("attribute", List.of("vitality", "strength"));
        mechanic.load(skill, config);

        String key = mechanic.getAttribute();
        assertEquals("vitality", key);
    }
}