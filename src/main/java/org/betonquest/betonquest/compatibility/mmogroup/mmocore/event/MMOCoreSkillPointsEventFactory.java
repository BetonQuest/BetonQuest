package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link MMOCoreSkillPointsEvent}s from {@link Instruction}s.
 */
public class MMOCoreSkillPointsEventFactory implements EventFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new MMO Core Event Factory.
     *
     * @param data the data for primary server thread access
     */
    public MMOCoreSkillPointsEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final VariableNumber amount = instruction.get(VariableNumber::new);
        return new PrimaryServerThreadEvent(new MMOCoreSkillPointsEvent(amount), data);
    }
}
