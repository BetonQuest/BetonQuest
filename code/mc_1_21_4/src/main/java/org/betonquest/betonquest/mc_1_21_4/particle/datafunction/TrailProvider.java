package org.betonquest.betonquest.mc_1_21_4.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;
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
     * Argument for the red component of the initial color.
     */
    @Nullable
    private Argument<DeltaFunction> colorR;

    /**
     * Argument for the green component of the initial color.
     */
    @Nullable
    private Argument<DeltaFunction> colorG;

    /**
     * Argument for the blue component of the initial color.
     */
    @Nullable
    private Argument<DeltaFunction> colorB;

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
    }

    @Override
    public ParticleDataFunction<Particle.Trail> getValue(@Nullable final Profile profile) throws QuestException {
        if (colorR == null || colorG == null || colorB == null || duration == null || timeUnit == null || location == null) {
            throw new QuestException("Trail particle requires 'color', 'duration' and 'location'");
        }
        final DeltaFunction redDelta = this.colorR.getValue(profile);
        final DeltaFunction greenDelta = this.colorG.getValue(profile);
        final DeltaFunction blueDelta = this.colorB.getValue(profile);
        final DeltaFunction durationDelta = this.duration.getValue(profile);
        final TimeUnit timeUnit = this.timeUnit.getValue(profile);
        final Location location = this.location.getValue(profile);
        return (firstDelta, secondDelta) -> {
            final Color color = color(redDelta.evaluate(firstDelta, secondDelta), greenDelta.evaluate(firstDelta, secondDelta), blueDelta.evaluate(firstDelta, secondDelta));
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
        this.colorR = instruction.read().value("color", "r").parse(deltaFunctionParser).get();
        this.colorG = instruction.read().value("color", "g").parse(deltaFunctionParser).get();
        this.colorB = instruction.read().value("color", "b").parse(deltaFunctionParser).get();
        this.duration = instruction.read().value("duration").parse(deltaFunctionParser).get();
        this.timeUnit = instruction.read().value("duration_unit").enumeration(TimeUnit.class).getOptional(TimeUnit.SECONDS);
        this.location = instruction.read().value("target").location().get();
    }

    private Color color(final Number red, final Number green, final Number blue) {
        return Color.fromRGB(clampColor(red.intValue()), clampColor(green.intValue()), clampColor(blue.intValue()));
    }

    private int clampColor(final int value) {
        return Math.max(0, Math.min(255, value));
    }
}
