package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.quest.event.NullStaticEventAdapter;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for the notify all event.
 */
public class NotifyAllEventFactory extends NotifyEventFactory implements EventFactory, StaticEventFactory {

    /**
     * Creates a new factory for {@link NotifyAllEvent}.
     *
     * @param server    Server to use for syncing to the primary server thread.
     * @param scheduler Scheduler to use for syncing to the primary server thread.
     * @param plugin    Plugin to use for syncing to the primary server thread.
     */
    public NotifyAllEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        super(server, scheduler, plugin);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final Map<String, VariableString> translations = new HashMap<>();
        final NotifyIO notifyIO = processInstruction(instruction, translations);
        return new NotifyAllEvent(notifyIO, translations);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return new NullStaticEventAdapter(parseEvent(instruction));
    }
}
