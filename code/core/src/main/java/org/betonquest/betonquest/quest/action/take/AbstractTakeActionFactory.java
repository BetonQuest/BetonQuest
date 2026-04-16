package org.betonquest.betonquest.quest.action.take;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NoNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.betonquest.betonquest.quest.action.NotificationSender;

import java.util.List;

/**
 * Abstract factory for take actions, to take items from the players inventory or backpack.
 */
public abstract class AbstractTakeActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    protected final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link Localizations} instance.
     */
    private final Localizations localizations;

    /**
     * Create the abstract take action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     * @param localizations the {@link Localizations} instance
     */
    public AbstractTakeActionFactory(final BetonQuestLoggerFactory loggerFactory, final Localizations localizations) {
        this.loggerFactory = loggerFactory;
        this.localizations = localizations;
    }

    /**
     * Get the check order for the take action.
     *
     * @param instruction the instruction to get the check order from
     * @return the check order
     * @throws QuestException if the check order is invalid
     */
    protected Argument<List<CheckType>> getCheckOrder(final Instruction instruction) throws QuestException {
        return instruction.enumeration(CheckType.class).list().get("invOrder",
                List.of(CheckType.INVENTORY, CheckType.OFFHAND, CheckType.ARMOR, CheckType.BACKPACK));
    }

    /**
     * Get the notification sender for the take action.
     *
     * @param instruction the instruction to get the notification sender from
     * @param log         the logger to use
     * @return the notification sender
     * @throws QuestException if the notification sender could not be created
     */
    protected NotificationSender getNotificationSender(final Instruction instruction, final BetonQuestLogger log) throws QuestException {
        final boolean notify = instruction.bool().getFlag("notify", true)
                .getValue(null).orElse(false);
        return notify ? new IngameNotificationSender(log, localizations, instruction.getPackage(),
                instruction.getID().getFull(), NotificationLevel.INFO, "items_taken")
                : new NoNotificationSender();
    }
}
