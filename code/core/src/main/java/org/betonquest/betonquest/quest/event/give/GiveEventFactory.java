package org.betonquest.betonquest.quest.event.give;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.NotificationSender;

/**
 * Factory for {@link GiveEvent}.
 */
public class GiveEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
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
     * Create the give event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param dataStorage   the storage providing player backpack
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public GiveEventFactory(final BetonQuestLoggerFactory loggerFactory, final PlayerDataStorage dataStorage,
                            final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(GiveEvent.class);
        final NotificationSender itemsGivenSender;
        final boolean notify = instruction.bool().getFlag("notify", false).getValue(null).orElse(false);
        if (notify) {
            itemsGivenSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(), instruction.getID().getFull(), NotificationLevel.INFO, "items_given");
        } else {
            itemsGivenSender = new NoNotificationSender();
        }

        final NotificationSender itemsInBackpackSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(), instruction.getID().getFull(), NotificationLevel.ERROR, "inventory_full_backpack", "inventory_full");
        final NotificationSender itemsDroppedSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(), instruction.getID().getFull(), NotificationLevel.ERROR, "inventory_full_drop", "inventory_full");

        return new OnlineEventAdapter(new GiveEvent(
                instruction.item().list().get(),
                itemsGivenSender,
                itemsInBackpackSender,
                itemsDroppedSender,
                instruction.bool().getFlag("backpack", false),
                dataStorage
        ), log, instruction.getPackage());
    }
}
