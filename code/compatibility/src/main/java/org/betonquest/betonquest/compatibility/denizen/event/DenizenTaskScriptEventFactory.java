package org.betonquest.betonquest.compatibility.denizen.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;

/**
 * Factory to create Denizen Task Script Events.
 */
public class DenizenTaskScriptEventFactory implements PlayerEventFactory {

    /**
     * The data for the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Denizen Task Script Events.
     *
     * @param data the data for the primary server thread.
     */
    public DenizenTaskScriptEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> nameVar = instruction.get(DefaultArgumentParsers.STRING);
        return new PrimaryServerThreadEvent(new DenizenTaskScriptEvent(nameVar), data);
    }
}
