package org.betonquest.betonquest.mc_1_20_6.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.particle.datafunction.ParticleDataFunction;
import org.betonquest.betonquest.particle.datafunction.ParticleDataFunctionProvider;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunction;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunctionParser;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

/**
 * A float provider usable as an argument.
 */
public class FloatProvider implements ParticleDataFunctionProvider {

    /**
     * The delta function argument.
     */
    @Nullable
    private Argument<DeltaFunction> deltaFunctionValue;

    /**
     * Creates a new float provider.
     */
    public FloatProvider() {
    }

    @Override
    public boolean match(final Particle particle) {
        return particle.getDataType().equals(Float.class);
    }

    @Override
    public void read(final SectionInstruction instruction, final DeltaFunctionParser deltaFunctionParser) throws QuestException {
        this.deltaFunctionValue = instruction.read().value("value").parse(deltaFunctionParser).get();
    }

    @Override
    public ParticleDataFunction<?> getValue(@Nullable final Profile profile) throws QuestException {
        if (this.deltaFunctionValue == null) {
            throw new QuestException("Float particle requires 'value' argument");
        }
        final DeltaFunction deltaFunction = this.deltaFunctionValue.getValue(profile);
        return (firstDelta, secondDelta) -> deltaFunction.evaluate(firstDelta, secondDelta).floatValue();
    }
}
