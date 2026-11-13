package org.betonquest.betonquest.api.particle;

import org.betonquest.betonquest.api.QuestException;
import org.bukkit.Location;

/**
 * Represents an animation that spawns particles at a given location.
 * <p>
 * This interface encapsulates the logic for rendering particle effects in the game world.
 * Implementations may handle simple instant particle spawning or complex animations with
 * multiple particles spawned over time.
 * <p>
 * Implementations should be stateless and thread-safe.
 *
 * @since 3.4.0
 */
@FunctionalInterface
public interface ParticleAnimation {

    /**
     * Spawns the particle animation at the given location.
     * <p>
     * This method triggers the particle effect at the specified location. Depending on the
     * implementation, this may spawn particles instantly or initiate a complex animation
     * that spawns multiple particles over time.
     * <p>
     * Implementations should be non-blocking. For animations with duration,
     * the initial call should return immediately after scheduling the animation, with
     * particles being spawned asynchronously over time.
     *
     * @param location the location to spawn the particle animation at
     * @return a {@link Runnable} that can be used to cancel the animation prematurely if needed
     * @throws QuestException if the particle animation could not be spawned due to invalid
     *                        location, missing world context, scheduling failures, or other
     *                        implementation-specific errors
     * @since 3.4.0
     */
    Runnable spawn(Location location) throws QuestException;
}
