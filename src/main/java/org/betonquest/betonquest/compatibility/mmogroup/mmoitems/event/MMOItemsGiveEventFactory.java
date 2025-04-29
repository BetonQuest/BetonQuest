package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.event;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsUtils;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link MMOItemsGiveEvent}s from {@link Instruction}s.
 */
public class MMOItemsGiveEventFactory implements PlayerEventFactory {
    /**
     * {@link MMOItems} plugin instance.
     */
    private static final MMOItems MMO_PLUGIN = MMOItems.plugin;

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for MMO Item Hand Conditions.
     *
     * @param loggerFactory the logger factory to create class specific logger
     * @param pluginMessage the {@link PluginMessage} instance
     * @param data          the data for primary server thread access
     */
    public MMOItemsGiveEventFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Type itemType = MMOItemsUtils.getMMOItemType(instruction.next());
        final String itemID = instruction.next();

        final BetonQuestLogger log = loggerFactory.create(MMOItemsGiveEvent.class);

        Variable<Number> amount = new Variable<>(1);
        boolean scale = false;
        boolean singleStack = false;
        NotificationSender notificationSender = new NoNotificationSender();
        while (instruction.hasNext()) {
            final String next = instruction.next();
            if (next.matches("conditions?:")) {
                continue;
            }
            switch (next) {
                case "scale" -> scale = true;
                case "singleStack" -> singleStack = true;
                case "notify" ->
                        notificationSender = new IngameNotificationSender(log, pluginMessage, instruction.getPackage(),
                                instruction.getID().getFullID(), NotificationLevel.INFO, "items_given");
                default -> amount = instruction.get(next, Argument.NUMBER);
            }
        }

        MMOItemsUtils.getMMOItemStack(itemType, itemID);

        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new MMOItemsGiveEvent(MMO_PLUGIN, itemType, itemID, scale, notificationSender, singleStack, amount),
                log, instruction.getPackage()), data);
    }
}
