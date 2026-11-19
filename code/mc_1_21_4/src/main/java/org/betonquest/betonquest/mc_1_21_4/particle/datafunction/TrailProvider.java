package org.betonquest.betonquest.mc_1_21_4.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestBiFunction;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;
import org.betonquest.betonquest.particle.datafunction.ColorSection;
import org.betonquest.betonquest.particle.datafunction.ParticleDataFunction;
import org.betonquest.betonquest.particle.datafunction.ParticleDataFunctionProvider;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunction;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunctionParser;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

/**
 * A trail provider usable as an argument.
 */
public class TrailProvider implements ParticleDataFunctionProvider {

    /**
     * Handler for the color.
     */
    private final ColorSection color;

    /**
     * Argument for the duration of the trail.
     */
    @Nullable
    private Argument<DeltaFunction> duration;

    /**
     * Argument for the time unit of the duration.
     */
    @Nullable
    private Argument<TimeUnit> timeUnit;

    /**
     * Argument for the location of the trail.
     */
    @Nullable
    private Argument<Location> location;

    /**
     * Creates a new trail provider.
     */
    public TrailProvider() {
        this.color = new ColorSection("color");
    }

    @Override
    public ParticleDataFunction<Particle.Trail> getValue(@Nullable final Profile profile) throws QuestException {
        if (duration == null || timeUnit == null || location == null) {
            throw new QuestException("Trail particle requires 'duration' and 'location'");
        }
        final QuestBiFunction<Number, Number, Color> colorFunction = this.color.getColor(profile);
        final DeltaFunction durationDelta = this.duration.getValue(profile);
        final TimeUnit timeUnit = this.timeUnit.getValue(profile);
        final Location location = this.location.getValue(profile);
        return (firstDelta, secondDelta) -> {
            final Color color = colorFunction.apply(firstDelta, secondDelta);
            final int durationTicks = (int) timeUnit.getTicks(durationDelta.evaluate(firstDelta, secondDelta).longValue());
            return new Particle.Trail(location, color, durationTicks);
        };
    }

    @Override
    public boolean match(final Particle particle) {
        return particle.getDataType().equals(Particle.Trail.class);
    }

    @Override
    public void read(final SectionInstruction instruction, final DeltaFunctionParser deltaFunctionParser) throws QuestException {
        this.color.read(instruction, deltaFunctionParser);
        this.duration = instruction.read().value("duration").parse(deltaFunctionParser).get();
        this.timeUnit = instruction.read().value("duration_unit").enumeration(TimeUnit.class).getOptional(TimeUnit.SECONDS);
        this.location = instruction.read().value("target").location().get();
    }
}
