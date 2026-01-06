package org.betonquest.betonquest.quest.action.take;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.quest.action.NotificationSender;

import java.util.List;

/**
 * Factory for {@link TakeAction}.
 */
public class TakeActionFactory extends AbstractTakeActionFactory {

    /**
     * Create the take event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public TakeActionFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage) {
        super(loggerFactory, pluginMessage);
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(TakeAction.class);
        final List<CheckType> checkOrder = getCheckOrder(instruction);
        final Argument<List<ItemWrapper>> questItems = instruction.item().list().get();
        final NotificationSender notificationSender = getNotificationSender(instruction, log);
        return new OnlineActionAdapter(new TakeAction(questItems, checkOrder, notificationSender), log, instruction.getPackage());
    }
}
