package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.utils.Utils;

/**
 * Factory for the delete global point event.
 */
public class DeleteGlobalPointEventFactory implements StaticEventFactory {

    /**
     * Creates a new DeleteGlobalPointEventFactory.
     */
    public DeleteGlobalPointEventFactory() {
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final String category = Utils.addPackage(instruction.getPackage(), instruction.next());
        return new DeleteGlobalPointEvent(category);
    }
}
