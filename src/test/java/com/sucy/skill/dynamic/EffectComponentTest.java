package com.sucy.skill.dynamic;

import com.sucy.skill.api.CastData;
import com.sucy.skill.dynamic.target.LinearTarget;
import com.sucy.skill.testutil.MockedTest;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EffectComponentTest extends MockedTest {
    private Player                     player;
    private Player                     target;
    private MockedStatic<DynamicSkill> dynamicSkill;
    private CastData                   data;
    private EffectComponent           component;

    @BeforeEach
    void setUp() {
        player = genPlayer("Travja");
        target = genPlayer("goflish");
        dynamicSkill = mockStatic(DynamicSkill.class);
        data = new CastData(player);
        dynamicSkill.when(() -> DynamicSkill.getCastData(any())).thenReturn(data);

        component = new LinearTarget();
    }

    @AfterEach
    void tearDown() {
        dynamicSkill.close();
        dynamicSkill = null;
    }

    @Test
    void filter_translatesPlayerName() {
        String          text      = "Hello, {player}!";
        String filtered = component.filter(player, target, text);
        assertEquals("Hello, Travja!", filtered);
    }

    @Test
    void filter_translatesTargetName() {
        String          text      = "Hello, {target}!";
        String filtered = component.filter(player, target, text);
        assertEquals("Hello, goflish!", filtered);
    }

    @Test
    void filter_translatesTargetUUID() {
        String          text      = "Hello, {targetUUID}!";
        String filtered = component.filter(player, target, text);
        assertEquals("Hello, " + target.getUniqueId() + "!", filtered);
    }

    @Test
    void filter_translatesCastData_string() {
        data.put("test", "test");
        String          text      = "Hello, {test}!";
        String filtered = component.filter(player, target, text);
        assertEquals("Hello, test!", filtered);
    }

    @Test
    void filter_translatesCastData_double() {
        data.put("test", 5.0);
        String          text      = "Hello, {test}!";
        String filtered = component.filter(player, target, text);
        assertEquals("Hello, 5!", filtered);
    }

    @Test
    void filter_translatesCastData_doubleWithDecimal() {
        data.put("test", 5.144);
        String          text      = "Hello, {test}!";
        String filtered = component.filter(player, target, text);
        assertEquals("Hello, 5.14!", filtered);
    }

    @Test
    void filter_translatesCastData_doubleWithDecimalRoundingUp() {
        data.put("test", 5.145);
        String          text      = "Hello, {test}!";
        String filtered = component.filter(player, target, text);
        assertEquals("Hello, 5.15!", filtered);
    }

    @Test
    void filter_nestedPlaceholders() {
        data.put("test1", "test");
        data.put("v1", 1);
        String          text      = "Hello, {test{v1}}!";
        String filtered = component.filter(player, target, text);
        assertEquals("Hello, test!", filtered);
    }

    @Test
    void filter_tripleNestedPlaceholders() {
        data.put("test-asdf", "test");
        data.put("v1", 1);
        data.put("v2", 2);
        data.put("v12", "asdf");
        String          text      = "Hello, {test-{v{v1}{v2}}}!";
        String filtered = component.filter(player, target, text);
        assertEquals("Hello, test!", filtered);
    }

    @Test
    void filter_quadrupleNestedPlaceholders() {
        data.put("test-asdf", "test");
        data.put("v1", 1);
        data.put("v2", 2);
        data.put("v12", "asdf");
        data.put("vasdf", "ffff");
        data.put("vasdf2", "abc");
        data.put("test-abc", 112233);
        String          text      = "Hello, {test-{v{v1{v2}}{v2}}}}!";
        String filtered = component.filter(player, target, text);
        assertEquals("Hello, 112233}!", filtered);
    }

    @Test
    void filter_quadrupleNestedPlaceholders_someMissing() {
        data.put("test-asdf", "test");
        data.put("v1", 1);
        data.put("v2", 2);
        data.put("v12", "asdf");
        data.put("vasdf", "ffff");
        String          text      = "Hello, {test-{v{v1{v2}}{v2}}}}!";
        String filtered = component.filter(player, target, text);
        assertEquals("Hello, {test-{vasdf2}}}!", filtered);
    }

    @Test
    void filter_noPlaceholders() {
        String          text      = "Hello, world!";
        String filtered = component.filter(player, target, text);
        assertEquals("Hello, world!", filtered);
    }
}