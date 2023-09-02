package com.sucy.skill.dataload;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.testutil.MockedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

public class DataLoadTest extends MockedTest {

    @Override
    public void preInit() {
        loadClasses("Honor Guard");
        loadSkills("Brilliance Strike");
    }

    @Test
    void honorGuardLoads() {
        assertNotNull(SkillAPI.getClass("Honor Guard"));
    }

    @Test
    void brillianceStrikeLoads() {
        assertNotNull(SkillAPI.getSkill("Brilliance Strike"));
    }
}