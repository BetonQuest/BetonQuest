package org.betonquest.betonquest.particle;

import com.destroystokyo.paper.ParticleBuilder;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.particle.datafunction.ParticleDataFunction;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunction;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * The default implementation of {@link ParticleFunction}.
 *
 * @param particle             the particle to use
 * @param positionX            the function to use for the x coordinate
 * @param positionY            the function to use for the y coordinate
 * @param positionZ            the function to use for the z coordinate
 * @param offsetX              the function to use for the x offset
 * @param offsetY              the function to use for the y offset
 * @param offsetZ              the function to use for the z offset
 * @param extra                the function to use for the extra value
 * @param range                the range of the function to use for the visibility range
 * @param number               the function to use for the number of particles
 * @param particleDataFunction the function to use for the function to get the number of particles
 */
public record DefaultParticleFunction(
        Particle particle, DeltaFunction positionX, DeltaFunction positionY, DeltaFunction positionZ,
        DeltaFunction offsetX, DeltaFunction offsetY, DeltaFunction offsetZ,
        DeltaFunction extra, DeltaFunction range, DeltaFunction number,
        ParticleDataFunction<?> particleDataFunction) implements ParticleFunction {

    @Override
    public List<List<ParticleBuilder>> evaluate(final Location origin, final SamplingSettings samplingSettings) throws QuestException {
        final World world = origin.getWorld();
        final double originX = origin.getX();
        final double originY = origin.getY();
        final double originZ = origin.getZ();
        final int primarySamples = Math.max(2, samplingSettings.primarySamples());
        final int secondarySamples = Math.max(2, samplingSettings.secondarySamples());
        final double primarySampleRate = 1.0 / (primarySamples - 1);
        final double secondarySampleRate = 1.0 / (secondarySamples - 1);
        final List<List<ParticleBuilder>> particles = new ArrayList<>();
        for (int i = 0; i < primarySamples; i++) {
            final double firstDelta = primarySampleRate * i;
            final List<ParticleBuilder> particleList = new ArrayList<>();
            if (samplingSettings.secondarySamples() == 0) {
                particleList.add(calculateBuilder(firstDelta, 0, world, originX, originY, originZ));
            } else {
                for (int j = 0; j < secondarySamples; j++) {
                    final double secondDelta = secondarySampleRate * j;
                    particleList.add(calculateBuilder(firstDelta, secondDelta, world, originX, originY, originZ));
                }
            }
            particles.add(particleList);
        }
        return particles;
    }

    private ParticleBuilder calculateBuilder(final Number firstDelta, final Number secondDelta, final World world, final double originX, final double originY, final double originZ) throws QuestException {
        final int count = this.number.evaluate(firstDelta, secondDelta).intValue();
        final double posX = this.positionX.evaluate(firstDelta, secondDelta).doubleValue();
        final double posY = this.positionY.evaluate(firstDelta, secondDelta).doubleValue();
        final double posZ = this.positionZ.evaluate(firstDelta, secondDelta).doubleValue();
        final double offsetX = this.offsetX.evaluate(firstDelta, secondDelta).doubleValue();
        final double offsetY = this.offsetY.evaluate(firstDelta, secondDelta).doubleValue();
        final double offsetZ = this.offsetZ.evaluate(firstDelta, secondDelta).doubleValue();
        final double extraValue = this.extra.evaluate(firstDelta, secondDelta).doubleValue();
        final int rangeValue = this.range.evaluate(firstDelta, secondDelta).intValue();
        final Object particleData = this.particleDataFunction.evaluate(firstDelta, secondDelta);
        return new ParticleBuilder(particle)
                .location(world, originX + posX, originY + posY, originZ + posZ)
                .receivers(rangeValue)
                .count(count)
                .offset(offsetX, offsetY, offsetZ)
                .extra(extraValue)
                .data(particleData);
    }
}
