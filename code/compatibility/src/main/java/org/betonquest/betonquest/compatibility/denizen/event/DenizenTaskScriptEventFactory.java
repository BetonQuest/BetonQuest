package org.betonquest.betonquest.compatibility.denizen.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create Denizen Task Script Events.
 */
public class DenizenTaskScriptEventFactory implements PlayerActionFactory {

    /**
     * Create a new Factory to create Denizen Task Script Events.
     */
    public DenizenTaskScriptEventFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> name = instruction.string().get();
        return new DenizenTaskScriptEvent(name);
    }
}
