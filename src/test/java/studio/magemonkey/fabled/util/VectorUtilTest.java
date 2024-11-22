package studio.magemonkey.fabled.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VectorUtilTest extends MockedTest {
    private Player player    = null;
    private double eyeHeight = 0;

    @BeforeEach
    public void setup() {
        player = this.genPlayer("Travja");
        player.teleport(new Location(player.getWorld(), 0, 0, 0));
        eyeHeight = player.getEyeHeight();
    }

    @Test
    void getOffsetLocation_noOffset() {
        Location result = VectorUtil.getOffsetLocation(player, 0, 0, 0);

        assertEquals(0, result.getZ());
        assertEquals(eyeHeight, result.getY());
        assertEquals(0, result.getX());
    }

    @Test
    void getOffsetLocation_rightOnly() {
        Location result = VectorUtil.getOffsetLocation(player, 0, 1, 0);

        assertEquals(0, result.getZ());
        assertEquals(eyeHeight, result.getY());
        assertEquals(-1, result.getX());
    }

    @Test
    void getOffsetLocation_bigRight() {
        Location result = VectorUtil.getOffsetLocation(player, 0, 5, 0);
        assertEquals(0, result.getZ());
        assertEquals(eyeHeight, result.getY());
        assertEquals(-5, result.getX());
    }

    @Test
    void getOffsetLocation_upward() {
        Location result = VectorUtil.getOffsetLocation(player, 0, 0, 4);
        assertEquals(0, result.getZ());
        assertEquals(4 + eyeHeight, result.getY());
        assertEquals(0, result.getX());
    }

    @Test
    void getOffsetLocation_forward() {
        Location result = VectorUtil.getOffsetLocation(player, 6, 0, 0);
        assertEquals(6, result.getZ());
        assertEquals(eyeHeight, result.getY());
        assertEquals(0, result.getX());
    }

    @Test
    void getOffsetLocation_forwardAndRight() {
        Location result = VectorUtil.getOffsetLocation(player, 2, 4, 0);
        assertEquals(2, result.getZ());
        assertEquals(eyeHeight, result.getY());
        assertEquals(-4, result.getX());
    }

    @Test
    void getOffsetLocation_forwardAndUp() {
        Location result = VectorUtil.getOffsetLocation(player, 4, 0, 3);
        assertEquals(4, result.getZ());
        assertEquals(3 + eyeHeight, result.getY());
        assertEquals(0, result.getX());
    }

    @Test
    void getOffsetLocation_upAndRight() {
        Location result = VectorUtil.getOffsetLocation(player, 0, 2, 5);
        assertEquals(0, result.getZ());
        assertEquals(5 + eyeHeight, result.getY());
        assertEquals(-2, result.getX());
    }

    @Test
    void getOffsetLocation_allThree() {
        Location result = VectorUtil.getOffsetLocation(player, 1, 4, 3);
        assertEquals(1, result.getZ());
        assertEquals(3 + eyeHeight, result.getY());
        assertEquals(-4, result.getX());
    }


}