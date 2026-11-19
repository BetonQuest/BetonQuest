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
 * A dust options provider usable as an argument.
 */
public class DustOptionsProvider implements ParticleDataFunctionProvider {

    /**
     * Handler for the color.
     */
    private final ColorSection color;

    /**
     * Argument for the size of the dust particle.
     */
    @Nullable
    private Argument<DeltaFunction> size;

    /**
     * Creates a new dust options provider.
     */
    public DustOptionsProvider() {
        this.color = new ColorSection("color");
    }

    @Override
    public ParticleDataFunction<Particle.DustOptions> getValue(@Nullable final Profile profile) throws QuestException {
        if (size == null) {
            throw new QuestException("DustOptions particle requires 'size'");
        }
        final QuestBiFunction<Number, Number, Color> colorFunction = color.getColor(profile);
        final DeltaFunction sizeDelta = size.getValue(profile);
        return (firstDelta, secondDelta) -> {
            final Color color = colorFunction.apply(firstDelta, secondDelta);
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
        this.color.read(instruction, deltaFunctionParser);
        this.size = instruction.read().value("size").parse(deltaFunctionParser).getOptional(new ConstantDeltaFunction(1));
    }
}
