package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Factory for the delete global point event.
 */
public class DeleteGlobalPointEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Creates a new DeleteGlobalPointEventFactory.
     *
     * @param globalData the global data
     */
    public DeleteGlobalPointEventFactory(final GlobalData globalData) {
        this.globalData = globalData;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createDeleteGlobalPointEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createDeleteGlobalPointEvent(instruction);
    }

    private NullableEventAdapter createDeleteGlobalPointEvent(final Instruction instruction) throws QuestException {
        final Variable<String> category = instruction.packageIdentifier().get();
        return new NullableEventAdapter(new DeleteGlobalPointEvent(globalData, category));
    }
}
