package studio.magemonkey.fabled.util;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class VectorUtil {
    private static final Vector UP = new Vector(0, 1, 0);

    public static Location getOffsetLocation(LivingEntity target,
                                             double forward,
                                             double right,
                                             double upward
    ) {
        Location location = target.getEyeLocation();
        Vector   offset   = location.getDirection().setY(0).normalize();
        Vector   ref      = offset.clone();

        offset.multiply(forward);
        if (right != 0) {
            offset.add(ref.crossProduct(UP).multiply(right));
        }

        location.add(offset).add(0, upward, 0);
        return location;
    }
}
