package org.betonquest.betonquest.quest.event.give;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory for {@link GiveEvent}.
 */
public class GiveEventFactory implements PlayerEventFactory {
    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

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
     * @param data          the data for primary server thread access
     * @param dataStorage   the storage providing player backpack
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public GiveEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data,
                            final PlayerDataStorage dataStorage, final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(GiveEvent.class);
        final NotificationSender itemsGivenSender;
        if (instruction.hasArgument("notify")) {
            itemsGivenSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(), instruction.getID().getFullID(), NotificationLevel.INFO, "items_given");
        } else {
            itemsGivenSender = new NoNotificationSender();
        }

        final NotificationSender itemsInBackpackSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(), instruction.getID().getFullID(), NotificationLevel.ERROR, "inventory_full_backpack", "inventory_full");
        final NotificationSender itemsDroppedSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(), instruction.getID().getFullID(), NotificationLevel.ERROR, "inventory_full_drop", "inventory_full");

        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new GiveEvent(
                        instruction.getList(PackageArgument.ITEM),
                        itemsGivenSender,
                        itemsInBackpackSender,
                        itemsDroppedSender,
                        instruction.hasArgument("backpack"),
                        dataStorage
                ),
                log, instruction.getPackage()
        ), data);
    }
}
