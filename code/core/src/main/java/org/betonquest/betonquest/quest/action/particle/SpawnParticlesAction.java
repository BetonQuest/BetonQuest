package org.betonquest.betonquest.quest.action.particle;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ParticleIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.particle.ParticleAnimation;
import org.betonquest.betonquest.api.particle.ParticleAnimationProvider;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.betonquest.betonquest.api.service.particle.Particles;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * An action that spawns particles at a location or the player's location.
 */
public class SpawnParticlesAction implements NullableAction {

    /**
     * The particle service.
     */
    private final Particles particles;

    /**
     * The particle identifier.
     */
    private final Argument<ParticleIdentifier> identifier;

    /**
     * The location to spawn the particles at.
     */
    private final Argument<Location> location;

    /**
     * Create a new spawn particles action.
     *
     * @param particles  the particle service
     * @param identifier the particle identifier
     * @param location   the location to spawn the particles at
     */
    public SpawnParticlesAction(final Particles particles, final Argument<ParticleIdentifier> identifier, final Argument<Location> location) {
        this.particles = particles;
        this.identifier = identifier;
        this.location = location;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final ParticleAnimationProvider provider = particles.getParticleAnimation(identifier.getValue(profile));
        final ParticleAnimation animation = provider.getAnimation(profile);
        final Location loc = location.getValue(profile);
        animation.spawn(loc);
    }
}
