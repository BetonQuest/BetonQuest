package org.betonquest.betonquest.compatibility.denizen.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;

/**
 * Factory to create Denizen Task Script Events.
 */
public class DenizenTaskScriptEventFactory implements PlayerEventFactory {

    /**
     * Create a new Factory to create Denizen Task Script Events.
     */
    public DenizenTaskScriptEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> nameVar = instruction.string().get();
        return new DenizenTaskScriptEvent(nameVar);
    }
}
