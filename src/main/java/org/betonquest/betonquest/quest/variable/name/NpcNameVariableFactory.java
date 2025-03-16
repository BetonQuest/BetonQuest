package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory to create {@link NpcNameVariable}s from {@link Instruction}s.
 */
public class NpcNameVariableFactory implements PlayerVariableFactory {

    /**
     * Create a NpcName variable factory.
     */
    public NpcNameVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) {
        return new NpcNameVariable();
    }
}
