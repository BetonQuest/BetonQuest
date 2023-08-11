package org.betonquest.betonquest.quest.event.give;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Factory for {@link GiveEvent}.
 */
public class GiveEventFactory implements EventFactory {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Server to use for syncing to the primary server thread.
     */
    private final Server server;

    /**
     * Scheduler to use for syncing to the primary server thread.
     */
    private final BukkitScheduler scheduler;

    /**
     * Plugin to use for syncing to the primary server thread.
     */
    private final Plugin plugin;

    /**
     * Create the give event factory.
     *
     * @param log       the logger to use
     * @param server    the server to use
     * @param scheduler the scheduler to use
     * @param plugin    the plugin to use
     */
    public GiveEventFactory(final BetonQuestLogger log, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.log = log;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final NotificationSender itemsGivenSender;
        if (instruction.hasArgument("notify")) {
            itemsGivenSender = new IngameNotificationSender(log, instruction.getPackage(), instruction.getID().getFullID(), NotificationLevel.INFO, "items_given");
        } else {
            itemsGivenSender = new NoNotificationSender();
        }

        final NotificationSender itemsInBackpackSender = new IngameNotificationSender(log, instruction.getPackage(), instruction.getID().getFullID(), NotificationLevel.ERROR, "inventory_full_backpack", "inventory_full");
        final NotificationSender itemsDroppedSender = new IngameNotificationSender(log, instruction.getPackage(), instruction.getID().getFullID(), NotificationLevel.ERROR, "inventory_full_drop", "inventory_full");

        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(log, new GiveEvent(
                        instruction.getItemList(),
                        itemsGivenSender,
                        itemsInBackpackSender,
                        itemsDroppedSender,
                        instruction.hasArgument("backpack")
                ), instruction.getPackage()),
                server, scheduler, plugin
        );
    }
}
