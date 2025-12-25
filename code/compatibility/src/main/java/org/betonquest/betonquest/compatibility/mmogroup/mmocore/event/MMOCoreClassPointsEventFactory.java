package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;

/**
 * Factory to create {@link MMOCoreClassPointsEvent}s from {@link Instruction}s.
 */
public class MMOCoreClassPointsEventFactory implements PlayerEventFactory {

    /**
     * Create a new MMO Core Event Factory.
     */
    public MMOCoreClassPointsEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> amount = instruction.number().get();
        return new MMOCoreClassPointsEvent(amount);
    }
}
