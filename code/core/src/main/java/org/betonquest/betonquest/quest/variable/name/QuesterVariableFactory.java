package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory to create {@link QuesterVariable}s from {@link Instruction}s.
 */
public class QuesterVariableFactory implements PlayerVariableFactory {

    /**
     * Create a NpcName variable factory.
     */
    public QuesterVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) {
        return new QuesterVariable();
    }
}
