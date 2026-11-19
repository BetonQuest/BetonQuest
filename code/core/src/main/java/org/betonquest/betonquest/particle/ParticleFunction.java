package org.betonquest.betonquest.particle;

import com.destroystokyo.paper.ParticleBuilder;
import org.betonquest.betonquest.api.QuestException;
import org.bukkit.Location;

import java.util.List;

/**
 * A function that returns a two-dimensional list of particles to spawn.
 */
@FunctionalInterface
public interface ParticleFunction {

    /**
     * Evaluates the function at the given location.
     *
     * @param origin           the location to evaluate the function at
     * @param samplingSettings the sampling settings to use
     * @return the list of particles to spawn
     * @throws QuestException if the function fails to evaluate
     */
    List<List<ParticleBuilder>> evaluate(Location origin, SamplingSettings samplingSettings) throws QuestException;
}
