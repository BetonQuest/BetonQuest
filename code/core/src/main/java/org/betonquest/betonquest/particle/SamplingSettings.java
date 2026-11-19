package org.betonquest.betonquest.particle;

import org.betonquest.betonquest.lib.argument.type.TimeUnit;

/**
 * Represents the settings for particle sampling.
 *
 * @param primarySamples   the number of samples
 * @param secondarySamples the number of samples for the second dimension
 * @param batchSize        the batch size
 * @param duration         the duration of the sampling
 * @param durationUnit     the unit of the duration
 * @param renderMode       the render mode
 */
public record SamplingSettings(int primarySamples, int secondarySamples, int batchSize,
                               long duration, TimeUnit durationUnit, ParticleRenderMode renderMode) {

    /**
     * Creates a new SamplingSettings instance.
     *
     * @param samples      the number of samples split evenly across the primary and secondary dimensions;
     *                     might be rounded down to the nearest square number
     * @param batchSize    the batch size
     * @param duration     the duration of the sampling
     * @param durationUnit the unit of the duration
     * @param renderMode   the render mode
     */
    public SamplingSettings(final int samples, final int batchSize, final long duration, final TimeUnit durationUnit,
                            final ParticleRenderMode renderMode) {
        this(samples, 0, batchSize, duration, durationUnit, renderMode);
    }
}
