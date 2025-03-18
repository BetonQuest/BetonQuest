package org.betonquest.betonquest.compatibility.jobsreborn.event;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.VariableJob;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link EventAddExp}s from {@link Instruction}s.
 */
public class FactoryEventAddExp implements EventFactory {
    /**
     * The data for the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Can Level Conditions.
     *
     * @param data the data for the primary server thread.
     */
    public FactoryEventAddExp(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final VariableJob job = instruction.get(VariableJob::new);
        final VariableNumber amount = instruction.get(VariableNumber::new);
        return new PrimaryServerThreadEvent(new EventAddExp(job, amount), data);
    }
}
