package org.betonquest.betonquest.compatibility.skript;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;

/**
 * Factory to create {@link BQEventSkript}s from {@link Instruction}s.
 */
public class BQEventSkriptFactory implements PlayerEventFactory {

    /**
     * Create a new betonquest skript event factory.
     */
    public BQEventSkriptFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> identifier = instruction.string().get();
        return new BQEventSkript(identifier);
    }
}
