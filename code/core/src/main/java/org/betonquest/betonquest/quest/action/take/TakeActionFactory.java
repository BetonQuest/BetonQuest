package org.betonquest.betonquest.quest.action.take;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.Translations;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.action.NotificationSender;

import java.util.List;

/**
 * Factory for {@link TakeAction}.
 */
public class TakeActionFactory extends AbstractTakeActionFactory {

    /**
     * The storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Create the take action factory.
     *
     * @param loggerFactory     the logger factory to create a logger for the actions
     * @param playerDataStorage the storage providing player data
     * @param translations      the {@link Translations} instance
     */
    public TakeActionFactory(final BetonQuestLoggerFactory loggerFactory, final PlayerDataStorage playerDataStorage,
                             final Translations translations) {
        super(loggerFactory, translations);
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(TakeAction.class);
        final Argument<List<CheckType>> checkOrder = getCheckOrder(instruction);
        final Argument<List<ItemWrapper>> questItems = instruction.item().list().get();
        final NotificationSender notificationSender = getNotificationSender(instruction, log);
        return new OnlineActionAdapter(new TakeAction(playerDataStorage, questItems, checkOrder, notificationSender));
    }
}
