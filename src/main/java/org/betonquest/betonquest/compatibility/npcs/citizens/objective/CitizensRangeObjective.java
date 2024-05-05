package org.betonquest.betonquest.compatibility.npcs.citizens.objective;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.objective.NPCRangeObjective;
import org.betonquest.betonquest.compatibility.npcs.citizens.CitizensNPCSupplier;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Citizens implementation of {@link NPCRangeObjective}.
 */
public class CitizensRangeObjective extends NPCRangeObjective implements CitizensNPCSupplier {
    /**
     * Creates a new CitizensRangeObjective from the given instruction.
     *
     * @param instruction the user-provided instruction
     * @throws InstructionParseException if the instruction is invalid
     */
    public CitizensRangeObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }
}
