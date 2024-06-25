package org.betonquest.betonquest.quest.event.drop;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.common.function.Selectors;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupStaticEventAdapter;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Factory to create {@link Event}s that drop items from instructions.
 */
public class DropEventFactory implements EventFactory, StaticEventFactory {
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
     * Creates the drop event factory.
     *
     * @param server    server to use
     * @param scheduler scheduler to use
     * @param plugin    plugin to use
     */
    public DropEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    private static Item[] parseItemList(final Instruction instruction) throws InstructionParseException {
        final Item[] items = instruction.getItemListArgument("items");
        if (items.length == 0) {
            throw new InstructionParseException("No items to drop defined");
        }
        return items;
    }

    private static Selector<Location> parseLocationSelector(final Instruction instruction) throws InstructionParseException {
        return instruction.getLocationArgument("location")
                .map(loc -> (Selector<Location>) loc::getValue)
                .orElse(Selectors.fromPlayer(Player::getLocation));
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        if (instruction.hasArgument("location")) {
            return parseEvent(instruction);
        } else {
            return new OnlineProfileGroupStaticEventAdapter(PlayerConverter::getOnlineProfiles, parseEvent(instruction));
        }
    }

    @Override
    public ComposedEvent parseEvent(final Instruction instruction) throws InstructionParseException {
        final Item[] items = parseItemList(instruction);
        final Selector<Location> location = parseLocationSelector(instruction);

        return new PrimaryServerThreadComposedEvent(new DropEvent(items, location), server, scheduler, plugin);
    }
}
