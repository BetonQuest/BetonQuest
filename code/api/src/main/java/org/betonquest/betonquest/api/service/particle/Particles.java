package org.betonquest.betonquest.api.service.particle;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ParticleIdentifier;
import org.betonquest.betonquest.api.particle.ParticleAnimationProvider;

/**
 * Service interface for accessing and managing particle animations.
 * <p>
 * Particle animations can be used to create visual effects in the game world.
 *
 * @since 3.4.0
 */
@FunctionalInterface
public interface Particles {

    /**
     * Retrieves the particle animation provider associated with the specified identifier.
     * <p>
     * This method looks up and returns a {@link ParticleAnimationProvider} that can be used
     * to create particle animations for different profiles. The provider is identified by
     * the given {@link ParticleIdentifier}, which uniquely references a configured particle animation.
     *
     * @param particleIdentifier the unique identifier for the particle animation to retrieve
     * @return the animation provider associated with the given identifier
     * @throws QuestException if the animation provider could not be found, is not registered,
     *                        or if an error occurs during retrieval
     */
    ParticleAnimationProvider getParticleAnimation(ParticleIdentifier particleIdentifier) throws QuestException;
}
