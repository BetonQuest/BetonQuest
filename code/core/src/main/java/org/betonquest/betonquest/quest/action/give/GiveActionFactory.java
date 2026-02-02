package org.betonquest.betonquest.quest.action.give;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NoNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.betonquest.betonquest.quest.action.NotificationSender;

/**
 * Factory for {@link GiveAction}.
 */
public class GiveActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Storage for player backpack.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create the give action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     * @param dataStorage   the storage providing player backpack
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public GiveActionFactory(final BetonQuestLoggerFactory loggerFactory, final PlayerDataStorage dataStorage,
                             final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(GiveAction.class);
        final NotificationSender itemsGivenSender;
        final boolean notify = instruction.bool().getFlag("notify", true).getValue(null).orElse(false);
        if (notify) {
            itemsGivenSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(), instruction.getID().getFull(), NotificationLevel.INFO, "items_given");
        } else {
            itemsGivenSender = new NoNotificationSender();
        }

        final NotificationSender itemsInBackpackSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(), instruction.getID().getFull(), NotificationLevel.ERROR, "inventory_full_backpack", "inventory_full");
        final NotificationSender itemsDroppedSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(), instruction.getID().getFull(), NotificationLevel.ERROR, "inventory_full_drop", "inventory_full");

        return new OnlineActionAdapter(new GiveAction(
                instruction.item().list().get(),
                itemsGivenSender,
                itemsInBackpackSender,
                itemsDroppedSender,
                instruction.bool().getFlag("backpack", true),
                dataStorage
        ), log, instruction.getPackage());
    }
}
