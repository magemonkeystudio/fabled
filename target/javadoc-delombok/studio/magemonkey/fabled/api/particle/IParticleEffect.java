package studio.magemonkey.fabled.api.particle;

import org.bukkit.Location;

/**
 * A particle effect that can be played
 */
public interface IParticleEffect {
    /**
     * @return time between each frame in ticks
     */
    int getInterval();

    /**
     * @return name of the effect
     */
    String getName();

    void play(Location loc, int frame, int level);
}
