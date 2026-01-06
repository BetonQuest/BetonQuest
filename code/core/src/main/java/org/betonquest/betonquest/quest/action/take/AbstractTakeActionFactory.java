package org.betonquest.betonquest.quest.action.take;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NoNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.betonquest.betonquest.quest.action.NotificationSender;

import java.util.List;

/**
 * Abstract factory for take events, to take items from the players inventory or backpack.
 */
public abstract class AbstractTakeActionFactory implements PlayerActionFactory {

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
    public AbstractTakeActionFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage) {
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
     * @throws QuestException if the notification sender could not be created
     */
    protected NotificationSender getNotificationSender(final Instruction instruction, final BetonQuestLogger log) throws QuestException {
        final boolean notify = instruction.bool().getFlag("notify", true)
                .getValue(null).orElse(false);
        return notify ? new IngameNotificationSender(log, pluginMessage, instruction.getPackage(),
                instruction.getID().getFull(), NotificationLevel.INFO, "items_taken")
                : new NoNotificationSender();
    }
}
