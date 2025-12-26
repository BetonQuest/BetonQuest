package org.betonquest.betonquest.quest.event.take;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.NotificationSender;

import java.util.List;

/**
 * Abstract factory for take events, to take items from the players inventory or backpack.
 */
public abstract class AbstractTakeEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    protected final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create the abstract take event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public AbstractTakeEventFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
    }

    /**
     * Get the check order for the take event.
     *
     * @param instruction the instruction to get the check order from
     * @return the check order
     * @throws QuestException if the check order is invalid
     */
    protected List<CheckType> getCheckOrder(final Instruction instruction) throws QuestException {
        return instruction.enumeration(CheckType.class).list().get("invOrder",
                List.of(CheckType.INVENTORY, CheckType.OFFHAND, CheckType.ARMOR, CheckType.BACKPACK)).getValue(null);
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
                ? new IngameNotificationSender(log, pluginMessage, instruction.getPackage(),
                instruction.getID().getFull(), NotificationLevel.INFO, "items_taken")
                : new NoNotificationSender();
    }
}
