package org.betonquest.betonquest.quest.action.particle;

import com.destroystokyo.paper.ParticleBuilder;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/**
 * Action that spawns particles.
 */
public class ParticleAction implements NullableAction {

    /**
     * Particle to spawn.
     */
    private final Argument<Particle> particle;

    /**
     * Location to spawn particles at.
     */
    private final Argument<Location> location;

    /**
     * Number of particles to spawn.
     */
    private final Argument<Number> count;

    /**
     * Offset of particles.
     */
    private final Argument<Vector> offset;

    /**
     * Range of showing particles to players.
     */
    private final Argument<Number> range;

    /**
     * Create a new particle action.
     *
     * @param particle particle to spawn
     * @param location location to spawn particles at
     * @param count    number of particles to spawn
     * @param offset   offset of particles
     * @param range    range of particles to show to players
     */
    public ParticleAction(final Argument<Particle> particle, final Argument<Location> location, final Argument<Number> count,
                          final Argument<Vector> offset, final Argument<Number> range) {
        this.particle = particle;
        this.location = location;
        this.count = count;
        this.offset = offset;
        this.range = range;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Vector offsetValue = offset.getValue(profile);
        final ParticleBuilder particleBuilder = new ParticleBuilder(particle.getValue(profile))
                .location(location.getValue(profile))
                .count(count.getValue(profile).intValue())
                .offset(offsetValue.getX(), offsetValue.getY(), offsetValue.getZ())
                .receivers(range.getValue(profile).intValue());
        if (profile != null && profile.getOnlineProfile().isPresent()) {
            particleBuilder.source(profile.getPlayer().getPlayer());
        }
        particleBuilder.spawn();
    }
}
