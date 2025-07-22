package org.betonquest.betonquest.compatibility.protocollib;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link FreezeEvent}s from {@link Instruction}s.
 */
public class FreezeEventFactory implements PlayerEventFactory {

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
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> ticks = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        final BetonQuestLogger log = loggerFactory.create(FreezeEvent.class);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new FreezeEvent(ticks),
                log, instruction.getPackage()), data);
    }
}
