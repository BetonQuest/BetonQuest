package org.betonquest.betonquest.quest.event.take;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.NotificationSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Abstract factory for take events, to take items from the players inventory or backpack.
 */
public abstract class AbstractTakeEventFactory implements EventFactory {

    /**
     * Logger factory to create a logger for events.
     */
    protected final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the abstract take event factory.
     *
     * @param loggerFactory logger factory to use
     */
    public AbstractTakeEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    /**
     * Get the check order for the take event.
     *
     * @param instruction the instruction to get the check order from
     * @return the check order
     * @throws QuestException if the check order is invalid
     */
    protected List<CheckType> getCheckOrder(final Instruction instruction) throws QuestException {
        final String order = instruction.getOptional("invOrder");
        if (order == null) {
            return Arrays.asList(CheckType.INVENTORY, CheckType.OFFHAND, CheckType.ARMOR, CheckType.BACKPACK);
        } else {
            final String[] enumNames = order.split(",");
            final List<CheckType> checkOrder = new ArrayList<>();
            for (final String s : enumNames) {
                try {
                    final CheckType checkType = CheckType.valueOf(s.toUpperCase(Locale.ROOT));
                    checkOrder.add(checkType);
                } catch (final IllegalArgumentException e) {
                    throw new QuestException("There is no such check type: " + s, e);
                }
            }
            return checkOrder;
        }
    }

    /**
     * Get the notification sender for the take event.
     *
     * @param instruction the instruction to get the notification sender from
     * @param log         the logger to use
     * @return the notification sender
     */
    protected NotificationSender getNotificationSender(final Instruction instruction, final BetonQuestLogger log) {
        return instruction.hasArgument("notify")
                ? new IngameNotificationSender(log, instruction.getPackage(), instruction.getID().getFullID(), NotificationLevel.INFO, "items_taken")
                : new NoNotificationSender();
    }
}
