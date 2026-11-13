package org.betonquest.betonquest.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.particle.deltafunction.ConstantDeltaFunction;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunction;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunctionParser;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

/**
 * A dust options provider usable as an argument.
 */
public class DustOptionsProvider implements ParticleDataFunctionProvider {

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
     * Argument for the size of the dust particle.
     */
    @Nullable
    private Argument<DeltaFunction> size;

    /**
     * Creates a new dust options provider.
     */
    public DustOptionsProvider() {
    }

    @Override
    public ParticleDataFunction<Particle.DustOptions> getValue(@Nullable final Profile profile) throws QuestException {
        if (colorR == null || colorG == null || colorB == null || size == null) {
            throw new QuestException("DustOptions particle requires 'color' and 'size'");
        }
        final DeltaFunction redDelta = colorR.getValue(profile);
        final DeltaFunction greenDelta = colorG.getValue(profile);
        final DeltaFunction blueDelta = colorB.getValue(profile);
        final DeltaFunction sizeDelta = size.getValue(profile);
        return (firstDelta, secondDelta) -> {
            final Color color = color(redDelta.evaluate(firstDelta, secondDelta), greenDelta.evaluate(firstDelta, secondDelta), blueDelta.evaluate(firstDelta, secondDelta));
            final Number sizeValue = sizeDelta.evaluate(firstDelta, secondDelta);
            return new Particle.DustOptions(color, sizeValue.floatValue());
        };
    }

    @Override
    public boolean match(final Particle particle) {
        return particle.getDataType().equals(Particle.DustOptions.class);
    }

    @Override
    public void read(final SectionInstruction instruction, final DeltaFunctionParser deltaFunctionParser) throws QuestException {
        this.colorR = instruction.read().value("color", "r").parse(deltaFunctionParser).get();
        this.colorB = instruction.read().value("color", "b").parse(deltaFunctionParser).get();
        this.colorG = instruction.read().value("color", "g").parse(deltaFunctionParser).get();
        this.size = instruction.read().value("size").parse(deltaFunctionParser).getOptional(new ConstantDeltaFunction(1));
    }

    private Color color(final Number red, final Number green, final Number blue) {
        return Color.fromRGB(clampColor(red.intValue()), clampColor(green.intValue()), clampColor(blue.intValue()));
    }

    private int clampColor(final int value) {
        return Math.max(0, Math.min(255, value));
    }
}
