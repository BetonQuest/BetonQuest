package org.betonquest.betonquest.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a function that takes two delta values and returns a particle data value.
 *
 * @param <T> the type of the particle data value
 */
@FunctionalInterface
public interface ParticleDataFunction<T> {

    /**
     * Evaluates the function with two delta values.
     *
     * @param firstDelta  1st delta value
     * @param secondDelta 2nd delta value
     * @return the result of the function
     * @throws QuestException if the function fails to evaluate
     */
    @Nullable
    T evaluate(Number firstDelta, Number secondDelta) throws QuestException;
}
