package org.betonquest.betonquest.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestBiFunction;
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
     * Handler for the initial color.
     */
    private final ColorSection initialColor;

    /**
     * Handler for the transition color.
     */
    private final ColorSection transitionColor;

    /**
     * Argument for the size of the dust particle.
     */
    @Nullable
    private Argument<DeltaFunction> size;

    /**
     * Creates a new transition options provider.
     */
    public TransitionOptionsProvider() {
        this.initialColor = new ColorSection("color");
        this.transitionColor = new ColorSection("transition");
    }

    @Override
    public ParticleDataFunction<Particle.DustTransition> getValue(@Nullable final Profile profile) throws QuestException {
        if (size == null) {
            throw new QuestException("DustTransition particle 'size' is invalid");
        }
        final QuestBiFunction<Number, Number, Color> initialColor = this.initialColor.getColor(profile);
        final QuestBiFunction<Number, Number, Color> transitionColor = this.transitionColor.getColor(profile);
        final DeltaFunction sizeDelta = size.getValue(profile);
        return (firstDelta, secondDelta) -> {
            final Color color = initialColor.apply(firstDelta, secondDelta);
            final Color transition = transitionColor.apply(firstDelta, secondDelta);
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
        this.initialColor.read(instruction, deltaFunctionParser);
        this.transitionColor.read(instruction, deltaFunctionParser);
        this.size = instruction.read().value("size").parse(deltaFunctionParser).getOptional(new ConstantDeltaFunction(1));
    }
}
