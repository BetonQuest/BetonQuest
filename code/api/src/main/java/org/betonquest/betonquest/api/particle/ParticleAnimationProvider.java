package org.betonquest.betonquest.api.particle;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * Provides particle animations for a profile.
 * <p>
 * This is a factory interface that creates {@link ParticleAnimation} instances based on
 * a given profile context. Implementations can use profile-specific data to customize
 * the animation behavior or return profile-independent animations.
 * <p>
 * Implementations should be stateless and thread-safe to allow reuse across multiple profiles.
 *
 * @since 3.4.0
 */
@FunctionalInterface
public interface ParticleAnimationProvider {

    /**
     * Creates and returns a {@link ParticleAnimation} instance for the given profile.
     * <p>
     * This method is called to obtain an animation that is specific to the given profile.
     * The returned animation can be used to spawn particles at any location.
     * <p>
     * The profile parameter may be null for context-independent animations. Implementations
     * should handle null profiles gracefully if they support profile-independent behavior.
     *
     * @param profile the profile to create the animation for, or {@code null} for profile-independent animations
     * @return a new {@link ParticleAnimation} instance for the given profile
     * @throws QuestException if the animation could not be created due to invalid configuration,
     *                        missing dependencies, or profile-specific data retrieval failures
     * @since 3.4.0
     */
    ParticleAnimation getAnimation(@Nullable Profile profile) throws QuestException;
}
