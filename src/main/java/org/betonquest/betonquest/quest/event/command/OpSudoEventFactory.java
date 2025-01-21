package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.OpPlayerEventAdapter;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Creates a new OpSudoEvent from an {@link Instruction}.
 */
public class OpSudoEventFactory extends BaseCommandEventFactory {

    /**
     * Create the OpSudoEvent factory.
     *
     * @param loggerFactory logger factory to use
     * @param data          the data for primary server thread access
     */
    public OpSudoEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        super(loggerFactory, data);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new OpPlayerEventAdapter(new SudoEvent(parseCommands(instruction))),
                loggerFactory.create(SudoEvent.class),
                instruction.getPackage()
        ), data);
    }
}
