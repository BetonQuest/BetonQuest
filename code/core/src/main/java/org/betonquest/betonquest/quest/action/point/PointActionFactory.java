package org.betonquest.betonquest.quest.action.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NoNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.betonquest.betonquest.quest.action.NotificationSender;

/**
 * Factory to create points actions from {@link Instruction}s.
 */
public class PointActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create the points action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     * @param dataStorage   the storage providing player data
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public PointActionFactory(final BetonQuestLoggerFactory loggerFactory, final PlayerDataStorage dataStorage,
                              final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> category = instruction.packageIdentifier().get();
        final Argument<Number> amount = instruction.number().get();
        final PointType type = instruction.enumeration(PointType.class).get("action", PointType.ADD).getValue(null);

        final NotificationSender pointSender;
        final boolean notify = instruction.bool().getFlag("notify", true)
                .getValue(null).orElse(false);
        if (notify) {
            pointSender = new IngameNotificationSender(loggerFactory.create(PointAction.class), pluginMessage,
                    instruction.getPackage(), instruction.getID().getFull(), NotificationLevel.INFO, type.getNotifyCategory());
        } else {
            pointSender = new NoNotificationSender();
        }

        return new PointAction(pointSender, category, amount, type, dataStorage);
    }
}
