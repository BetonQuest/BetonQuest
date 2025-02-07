package org.betonquest.betonquest.compatibility.brewery.event;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link GiveBrewEvent}s from {@link Instruction}s.
 */
public class TakeBrewEventFactory implements EventFactory {
    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new Factory to create Give Brew Events.
     *
     * @param loggerFactory the logger factory.
     * @param data          the data used for primary server access.
     */
    public TakeBrewEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final VariableNumber countVar = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        final VariableString brewVar = instruction.get(VariableString::new);
        final BetonQuestLogger logger = loggerFactory.create(TakeBrewEvent.class);
        return new PrimaryServerThreadEvent(
                new OnlineEventAdapter(new TakeBrewEvent(countVar, brewVar), logger, instruction.getPackage()), data);
    }
}
