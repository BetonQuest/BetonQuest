package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Exposes the presence of global tags as a variable.
 * Originally implemented for use with the PAPI integration.
 */
public class GlobalTagVariable extends TagVariable {

    /**
     * Constructs a new GlobalTagVariable.
     *
     * @param instruction the instruction to parse
     * @throws QuestException if the instruction is malformed
     */
    public GlobalTagVariable(final Instruction instruction) throws QuestException {
        super(instruction);
        staticness = true;
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        return getValue(BetonQuest.getInstance().getGlobalData().getTags());
    }
}

