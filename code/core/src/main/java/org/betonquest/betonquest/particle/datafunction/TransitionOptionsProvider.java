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
 * A transition options provider usable as an argument.
 */
public class TransitionOptionsProvider implements ParticleDataFunctionProvider {

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
     * Argument for the red component of the transition color.
     */
    @Nullable
    private Argument<DeltaFunction> transitionR;

    /**
     * Argument for the green component of the transition color.
     */
    @Nullable
    private Argument<DeltaFunction> transitionG;

    /**
     * Argument for the blue component of the transition color.
     */
    @Nullable
    private Argument<DeltaFunction> transitionB;

    /**
     * Argument for the size of the dust particle.
     */
    @Nullable
    private Argument<DeltaFunction> size;

    /**
     * Creates a new transition options provider.
     */
    public TransitionOptionsProvider() {
    }

    @Override
    public ParticleDataFunction<Particle.DustTransition> getValue(@Nullable final Profile profile) throws QuestException {
        if (colorR == null || colorG == null || colorB == null || transitionR == null || transitionG == null || transitionB == null || size == null) {
            throw new QuestException("DustTransition particle requires 'color', 'transition' and optional 'size'");
        }
        final DeltaFunction redDelta = colorR.getValue(profile);
        final DeltaFunction greenDelta = colorG.getValue(profile);
        final DeltaFunction blueDelta = colorB.getValue(profile);
        final DeltaFunction transitionRedDelta = transitionR.getValue(profile);
        final DeltaFunction transitionGreenDelta = transitionG.getValue(profile);
        final DeltaFunction transitionBlueDelta = transitionB.getValue(profile);
        final DeltaFunction sizeDelta = size.getValue(profile);
        return (firstDelta, secondDelta) -> {
            final Color color = color(redDelta.evaluate(firstDelta, secondDelta), greenDelta.evaluate(firstDelta, secondDelta), blueDelta.evaluate(firstDelta, secondDelta));
            final Color transition = color(transitionRedDelta.evaluate(firstDelta, secondDelta), transitionGreenDelta.evaluate(firstDelta, secondDelta), transitionBlueDelta.evaluate(firstDelta, secondDelta));
            final Number sizeValue = sizeDelta.evaluate(firstDelta, secondDelta);
            return new Particle.DustTransition(color, transition, sizeValue.floatValue());
        };
    }

    @Override
    public boolean match(final Particle particle) {
        return particle.getDataType().equals(Particle.DustTransition.class);
    }

    @Override
    public void read(final SectionInstruction instruction, final DeltaFunctionParser deltaFunctionParser) throws QuestException {
        this.colorR = instruction.read().value("color", "r").parse(deltaFunctionParser).get();
        this.colorG = instruction.read().value("color", "g").parse(deltaFunctionParser).get();
        this.colorB = instruction.read().value("color", "b").parse(deltaFunctionParser).get();
        this.size = instruction.read().value("size").parse(deltaFunctionParser).getOptional(new ConstantDeltaFunction(1));
        this.transitionR = instruction.read().value("transition", "r").parse(deltaFunctionParser).get();
        this.transitionG = instruction.read().value("transition", "g").parse(deltaFunctionParser).get();
        this.transitionB = instruction.read().value("transition", "b").parse(deltaFunctionParser).get();
    }

    private Color color(final Number red, final Number green, final Number blue) {
        return Color.fromRGB(clampColor(red.intValue()), clampColor(green.intValue()), clampColor(blue.intValue()));
    }

    private int clampColor(final int value) {
        return Math.max(0, Math.min(255, value));
    }
}
