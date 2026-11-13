package org.betonquest.betonquest.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunctionParser;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

/**
 * A data provider that always returns null.
 */
public class EmptyDataProvider implements ParticleDataFunctionProvider {

    /**
     * Creates a new empty data provider.
     */
    public EmptyDataProvider() {
    }

    @Override
    public boolean match(final Particle particle) {
        return true;
    }

    @Override
    public void read(final SectionInstruction instruction, final DeltaFunctionParser deltaFunctionParser) throws QuestException {
        // Empty
    }

    @Override
    public ParticleDataFunction<?> getValue(@Nullable final Profile profile) throws QuestException {
        return (firstDelta, secondDelta) -> null;
    }
}
