package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;

/**
 * Factory for the delete global point event.
 */
public class DeleteGlobalPointEventFactory implements EventFactory, StaticEventFactory {

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
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return createDeleteGlobalPointEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return createDeleteGlobalPointEvent(instruction);
    }

    private NullableEventAdapter createDeleteGlobalPointEvent(final Instruction instruction) throws QuestException {
        final VariableIdentifier category = instruction.get(VariableIdentifier::new);
        return new NullableEventAdapter(new DeleteGlobalPointEvent(globalData, category));
    }
}
