package org.betonquest.betonquest.compatibility.denizen.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create Denizen Task Script Events.
 */
public class DenizenTaskScriptActionFactory implements PlayerActionFactory {

    /**
     * Create a new Factory to create Denizen Task Script Events.
     */
    public DenizenTaskScriptActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> name = instruction.string().get();
        return new DenizenTaskScriptAction(name);
    }
}
