package org.betonquest.betonquest.mc_1_20_6.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.particle.datafunction.ParticleDataFunction;
import org.betonquest.betonquest.particle.datafunction.ParticleDataFunctionProvider;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunction;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunctionParser;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

/**
 * A color provider usable as an argument.
 */
public class ColorProvider implements ParticleDataFunctionProvider {

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
     * Creates a new color provider.
     */
    public ColorProvider() {
    }

    @Override
    public ParticleDataFunction<Color> getValue(@Nullable final Profile profile) throws QuestException {
        if (colorR == null || colorG == null || colorB == null) {
            throw new QuestException("Color particle requires 'color'");
        }
        final DeltaFunction redDelta = colorR.getValue(profile);
        final DeltaFunction greenDelta = colorG.getValue(profile);
        final DeltaFunction blueDelta = colorB.getValue(profile);
        return (firstDelta, secondDelta) ->
                color(redDelta.evaluate(firstDelta, secondDelta), greenDelta.evaluate(firstDelta, secondDelta), blueDelta.evaluate(firstDelta, secondDelta));
    }

    @Override
    public boolean match(final Particle particle) {
        return particle.getDataType().equals(Color.class);
    }

    @Override
    public void read(final SectionInstruction instruction, final DeltaFunctionParser deltaFunctionParser) throws QuestException {
        this.colorR = instruction.read().value("color", "r").parse(deltaFunctionParser).get();
        this.colorG = instruction.read().value("color", "g").parse(deltaFunctionParser).get();
        this.colorB = instruction.read().value("color", "b").parse(deltaFunctionParser).get();
    }

    private Color color(final Number red, final Number green, final Number blue) {
        return Color.fromRGB(clampColor(red.intValue()), clampColor(green.intValue()), clampColor(blue.intValue()));
    }

    private int clampColor(final int value) {
        return Math.max(0, Math.min(255, value));
    }
}
