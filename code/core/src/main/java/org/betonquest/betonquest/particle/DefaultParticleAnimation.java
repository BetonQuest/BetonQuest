package org.betonquest.betonquest.particle;

import com.destroystokyo.paper.ParticleBuilder;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.particle.ParticleAnimation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A default implementation of {@link ParticleAnimation}.
 */
public class DefaultParticleAnimation implements ParticleAnimation {

    /**
     * The source of the animation.
     */
    @Nullable
    private final Player source;

    /**
     * The scheduler instance.
     */
    private final BatchScheduler batchScheduler;

    /**
     * The sampling settings.
     */
    private final SamplingSettings samplingSettings;

    /**
     * The particle function.
     */
    private final ParticleFunction particleFunction;

    /**
     * Creates a new particle animation.
     *
     * @param source           the source of the animation, or null if it's not a player
     * @param batchScheduler   the batch acceptor to schedule delayed particle spawns
     * @param samplingSettings the sampling settings
     * @param particleFunction the particle function
     */
    public DefaultParticleAnimation(@Nullable final Player source, final BatchScheduler batchScheduler,
                                    final SamplingSettings samplingSettings, final ParticleFunction particleFunction) {
        this.source = source;
        this.batchScheduler = batchScheduler;
        this.samplingSettings = samplingSettings;
        this.particleFunction = particleFunction;
    }

    @Override
    public Runnable spawn(final Location location) throws QuestException {
        final List<List<ParticleBuilder>> particles = particleFunction.evaluate(location, samplingSettings);
        final List<ParticleBuilder> renderableList = samplingSettings.renderMode().apply(particles);
        if (source != null) {
            renderableList.forEach(particle -> particle.source(source));
        }
        if (samplingSettings.duration() <= 0) {
            renderableList.forEach(ParticleBuilder::spawn);
            return () -> {
            };
        }
        final int batchSize = samplingSettings.batchSize();
        final double batchSizeRatio = (double) batchSize / renderableList.size();
        final long delay = (long) (samplingSettings.durationUnit().getTicks(samplingSettings.duration()) * 50 * batchSizeRatio);
        final List<Runnable> batches = new ArrayList<>();
        for (int i = 0; i < renderableList.size(); i += batchSize) {
            final List<ParticleBuilder> particleBatch = renderableList.subList(i, Math.min(i + batchSize, renderableList.size()));
            batches.add(() -> particleBatch.forEach(ParticleBuilder::spawn));
        }
        return batchScheduler.scheduleBatch(batches.iterator(), delay);
    }
}
