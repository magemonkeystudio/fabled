package studio.magemonkey.fabled.api.player;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerSkillCooldownTest {

    private static Object allocateInstance(Class<?> cls) throws Exception {
        // Obtain Unsafe
        Field f = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Object unsafe = f.get(null);
        return unsafe.getClass().getMethod("allocateInstance", Class.class).invoke(unsafe, cls);
    }

    @Test
    public void testCooldownLeftAndPreciseCooldown() throws Exception {
        // allocate PlayerSkill without running its constructor
        Object inst = allocateInstance(PlayerSkill.class);
        assertNotNull(inst);

        // set the private 'cooldown' field to now + 2500ms
        Field cdField = PlayerSkill.class.getDeclaredField("cooldown");
        cdField.setAccessible(true);
        long now = System.currentTimeMillis();
        cdField.setLong(inst, now + 2500L);

        // call getPreciseCooldownLeft and getCooldownLeft
        float precise = (float) PlayerSkill.class.getMethod("getPreciseCooldownLeft").invoke(inst);
        int left = (int) PlayerSkill.class.getMethod("getCooldownLeft").invoke(inst);

        // precise currently performs integer division, so 2500ms -> 2.0 seconds
        assertTrue(Math.abs(precise - 2.0f) < 0.1f, "Precise cooldown should be ~2.0s (truncated) but was " + precise);

        // integer cooldown left should be ceiling to 3 seconds
        assertEquals(3, left);
    }

    @Test
    public void testZeroWhenNotOnCooldown() throws Exception {
        Object inst = allocateInstance(PlayerSkill.class);
        Field cdField = PlayerSkill.class.getDeclaredField("cooldown");
        cdField.setAccessible(true);
        cdField.setLong(inst, 0L);

        float precise = (float) PlayerSkill.class.getMethod("getPreciseCooldownLeft").invoke(inst);
        int left = (int) PlayerSkill.class.getMethod("getCooldownLeft").invoke(inst);

        assertEquals(0f, precise);
        assertEquals(0, left);
    }
}
