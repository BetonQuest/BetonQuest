package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.CallStaticEventAdapter;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupStaticEventAdapter;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.utils.PlayerConverter;

/**
 * Factory for the notify all event.
 */
public class NotifyAllEventFactory extends NotifyEventFactory implements EventFactory, StaticEventFactory {

    /**
     * Creates the notify all event factory.
     *
     * @param loggerFactory     the logger factory to use for creating the event logger
     * @param data              the data for primary server thread access
     * @param variableProcessor the variable processor for creating variables
     */
    public NotifyAllEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        super(loggerFactory, data, variableProcessor);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new CallStaticEventAdapter(parseStaticEvent(instruction));
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return new OnlineProfileGroupStaticEventAdapter(PlayerConverter::getOnlineProfiles, super.parseEvent(instruction));
    }
}
