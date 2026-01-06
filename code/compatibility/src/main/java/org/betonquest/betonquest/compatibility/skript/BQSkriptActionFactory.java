package org.betonquest.betonquest.compatibility.skript;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create {@link BQSkriptAction}s from {@link Instruction}s.
 */
public class BQSkriptActionFactory implements PlayerActionFactory {

    /**
     * Create a new betonquest skript event factory.
     */
    public BQSkriptActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> identifier = instruction.string().get();
        return new BQSkriptAction(identifier);
    }
}
