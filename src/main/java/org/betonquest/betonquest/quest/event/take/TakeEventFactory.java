package org.betonquest.betonquest.quest.event.take;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.quest.event.NotificationSender;

import java.util.List;

/**
 * Factory for {@link TakeEvent}.
 */
public class TakeEventFactory extends AbstractTakeEventFactory {

    /**
     * Create the take event factory.
     *
     * @param loggerFactory logger factory to use
     */
    public TakeEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        super(loggerFactory);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(TakeEvent.class);
        final List<CheckType> checkOrder = getCheckOrder(instruction);
        final Instruction.Item[] questItems = instruction.getItemList();
        final NotificationSender notificationSender = getNotificationSender(instruction, log);
        return new OnlineEventAdapter(new TakeEvent(questItems, checkOrder, notificationSender), log, instruction.getPackage());
    }

}
