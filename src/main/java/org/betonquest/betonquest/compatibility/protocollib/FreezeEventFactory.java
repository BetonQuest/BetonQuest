package org.betonquest.betonquest.compatibility.protocollib;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link FreezeEvent}s from {@link Instruction}s.
 */
public class FreezeEventFactory implements EventFactory {

    /**
     * Logger factory to create class specific logger for quest type factories.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new freeze event factory.
     *
     * @param loggerFactory the logger factory to create new class specific logger
     * @param data          the data for primary server thread access
     */
    public FreezeEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final VariableNumber ticks = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        final BetonQuestLogger log = loggerFactory.create(FreezeEvent.class);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new FreezeEvent(ticks),
                log, instruction.getPackage()), data);
    }
}
