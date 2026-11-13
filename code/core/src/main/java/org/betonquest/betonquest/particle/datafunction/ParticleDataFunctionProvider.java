package org.betonquest.betonquest.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunctionParser;
import org.bukkit.Particle;

/**
 * An argument that can be used to provide a {@link ParticleDataFunction}.
 */
public interface ParticleDataFunctionProvider extends Argument<ParticleDataFunction<?>> {

    /**
     * Checks if the given particle matches this argument.
     *
     * @param particle the particle to check
     * @return true if the particle matches, false otherwise
     */
    boolean match(Particle particle);

    /**
     * Reads the data from the given instruction.
     *
     * @param instruction         the instruction to read parameters from
     * @param deltaFunctionParser the parser to use for delta functions
     * @throws QuestException if the instruction is invalid
     */
    void read(SectionInstruction instruction, DeltaFunctionParser deltaFunctionParser) throws QuestException;
}
