package org.betonquest.betonquest.quest.action.take;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NoNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.betonquest.betonquest.quest.action.NotificationSender;

import java.util.List;

/**
 * Factory for {@link TakeAction}.
 */
public class TakeActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    protected final BetonQuestLoggerFactory loggerFactory;

    /**
     * The storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The {@link Localizations} instance.
     */
    private final Localizations localizations;

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * Create the take action factory.
     *
     * @param loggerFactory     the logger factory to create a logger for the actions
     * @param playerDataStorage the storage providing player data
     * @param localizations     the {@link Localizations} instance
     * @param actionManager     the action manager
     */
    public TakeActionFactory(final BetonQuestLoggerFactory loggerFactory, final PlayerDataStorage playerDataStorage,
                             final Localizations localizations, final ActionManager actionManager) {
        this.loggerFactory = loggerFactory;
        this.playerDataStorage = playerDataStorage;
        this.localizations = localizations;
        this.actionManager = actionManager;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(TakeAction.class);
        final Argument<List<CheckType>> checkOrder = getCheckOrder(instruction);
        final Argument<List<ItemWrapper>> questItems = instruction.item().list().get();
        final NotificationSender notificationSender = getNotificationSender(instruction, log);
        final FlagArgument<Boolean> abort = instruction.bool().getFlag("abort", true);
        final Argument<List<ActionIdentifier>> failActions = instruction.identifier(ActionIdentifier.class)
                .list().get("fail").orElse(null);
        return new OnlineActionAdapter(new TakeAction(playerDataStorage, notificationSender, actionManager,
                questItems, checkOrder, abort, failActions));
    }

    /**
     * Get the check order for the take action.
     *
     * @param instruction the instruction to get the check order from
     * @return the check order
     * @throws QuestException if the check order is invalid
     */
    protected Argument<List<CheckType>> getCheckOrder(final Instruction instruction) throws QuestException {
        return instruction.enumeration(CheckType.class).list().distinct().get("invOrder",
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
