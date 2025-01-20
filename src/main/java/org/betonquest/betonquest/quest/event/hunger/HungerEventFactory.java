package org.betonquest.betonquest.quest.event.hunger;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory for the hunger event.
 */
public class HungerEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the hunger event factory.
     *
     * @param loggerFactory logger factory to use
     * @param data          the data for primary server thread access
     */
    public HungerEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final Hunger hunger = instruction.getEnum(Hunger.class);
        final VariableNumber amount = instruction.get(VariableNumber::new);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new HungerEvent(hunger, amount),
                loggerFactory.create(HungerEvent.class),
                instruction.getPackage()
        ), data);
    }
}
