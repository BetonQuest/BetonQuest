package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;

/**
 * Factory to create {@link MMOCoreAttributeReallocationPointsEvent}s from {@link Instruction}s.
 */
public class MMOCoreAttributeReallocationPointsEventFactory implements PlayerEventFactory {

    /**
     * Create a new MMO Core Event Factory.
     */
    public MMOCoreAttributeReallocationPointsEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> amount = instruction.number().get();
        return new MMOCoreAttributeReallocationPointsEvent(amount);
    }
}
