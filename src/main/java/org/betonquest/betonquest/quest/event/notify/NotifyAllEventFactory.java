package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.CallStaticEventAdapter;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupStaticEventAdapter;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Factory for the notify all event.
 */
public class NotifyAllEventFactory extends NotifyEventFactory implements EventFactory, StaticEventFactory {

    /**
     * Creates the notify all event factory.
     *
     * @param loggerFactory Logger factory to use for creating the event logger.
     * @param server        Server to use for syncing to the primary server thread.
     * @param scheduler     Scheduler to use for syncing to the primary server thread.
     * @param plugin        Plugin to use for syncing to the primary server thread.
     */
    public NotifyAllEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        super(loggerFactory, server, scheduler, plugin);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new CallStaticEventAdapter(parseStaticEvent(instruction));
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return new OnlineProfileGroupStaticEventAdapter(PlayerConverter::getOnlineProfiles, super.parseEvent(instruction));
    }
}
