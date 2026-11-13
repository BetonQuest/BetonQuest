package org.betonquest.betonquest.particle;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.particle.ParticleAnimation;
import org.betonquest.betonquest.api.particle.ParticleAnimationProvider;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Provides particle animations for a profile.
 */
public class DefaultParticleAnimationProvider implements ParticleAnimationProvider {

    /**
     * The scheduler to use for particle batches.
     */
    private final Argument<BatchScheduler> batchScheduler;

    /**
     * The primary sample size.
     */
    private final Argument<Number> primarySampleSize;

    /**
     * The optional secondary sample size.
     */
    @Nullable
    private final Argument<Number> secondarySampleSize;

    /**
     * The batch size defines how many particles are spawned in a single batch.
     */
    private final Argument<Number> batchSize;

    /**
     * The duration of the animation.
     */
    private final Argument<Number> duration;

    /**
     * The unit of the duration.
     */
    private final Argument<TimeUnit> durationUnit;

    /**
     * The render mode.
     */
    private final Argument<ParticleRenderMode> renderMode;

    /**
     * The particle function.
     */
    private final Argument<ParticleFunction> particleFunction;

    /**
     * Creates a new particle animation provider.
     *
     * @param batchScheduler      batch scheduler for delayed particle spawns
     * @param primarySampleSize   1st sample size
     * @param secondarySampleSize 2nd sample size, or null if not used
     * @param batchSize           the batch size defines how many particles are spawned in a single batch
     * @param duration            total duration of the animation
     * @param durationUnit        the unit of the duration
     * @param renderMode          the render mode defines the order of how the particles are rendered
     * @param particleFunction    the particle function defines the particle type and properties dynamically
     */
    public DefaultParticleAnimationProvider(final Argument<BatchScheduler> batchScheduler, final Argument<Number> primarySampleSize,
                                            @Nullable final Argument<Number> secondarySampleSize, final Argument<Number> batchSize,
                                            final Argument<Number> duration, final Argument<TimeUnit> durationUnit,
                                            final Argument<ParticleRenderMode> renderMode, final Argument<ParticleFunction> particleFunction) {
        this.batchScheduler = batchScheduler;
        this.primarySampleSize = primarySampleSize;
        this.secondarySampleSize = secondarySampleSize;
        this.batchSize = batchSize;
        this.duration = duration;
        this.durationUnit = durationUnit;
        this.renderMode = renderMode;
        this.particleFunction = particleFunction;
    }

    @Override
    public ParticleAnimation getAnimation(@Nullable final Profile profile) throws QuestException {
        final SamplingSettings samplingSettings = secondarySampleSize == null
                ? new SamplingSettings(primarySampleSize.getValue(profile).intValue(), batchSize.getValue(profile).intValue(),
                duration.getValue(profile).longValue(), durationUnit.getValue(profile), renderMode.getValue(profile))
                : new SamplingSettings(primarySampleSize.getValue(profile).intValue(), secondarySampleSize.getValue(profile).intValue(),
                batchSize.getValue(profile).intValue(), duration.getValue(profile).longValue(), durationUnit.getValue(profile), renderMode.getValue(profile));
        final Player source = Optional.ofNullable(profile).flatMap(Profile::getOnlineProfile).map(OnlineProfile::getPlayer).orElse(null);
        return new DefaultParticleAnimation(source, batchScheduler.getValue(profile), samplingSettings, particleFunction.getValue(profile));
    }
}
