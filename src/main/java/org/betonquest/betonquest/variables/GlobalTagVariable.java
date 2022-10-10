package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Exposes the presence of global tags as a variable.
 * Originally implemented for use with the PAPI integration.
 */
public class GlobalTagVariable extends TagVariable {

    /**
     * Constructs a new GlobalTagVariable.
     *
     * @param instruction the instruction to parse
     * @throws InstructionParseException if the instruction is malformed
     */
    public GlobalTagVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        staticness = true;
    }

    @Override
    public String getValue(final Profile profile) {
        return getValue(BetonQuest.getInstance().getGlobalData().getTags());
    }
}

