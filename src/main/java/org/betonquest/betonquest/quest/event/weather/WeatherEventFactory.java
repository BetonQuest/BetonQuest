package org.betonquest.betonquest.quest.event.weather;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.common.worldselector.ConstantWorldSelector;
import org.betonquest.betonquest.api.common.worldselector.PlayerWorldSelector;
import org.betonquest.betonquest.api.common.worldselector.WorldSelector;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.DoNothingStaticEvent;
import org.betonquest.betonquest.quest.event.NullStaticEventAdapter;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Factory to create weather events from {@link Instruction}s.
 */
public class WeatherEventFactory implements EventFactory, StaticEventFactory {
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
     * Create the weather event factory.
     *
     * @param server    server to use
     * @param scheduler scheduler to use
     * @param plugin    plugin to use
     */
    public WeatherEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final Weather weather = parseWeather(instruction.next());
        final WorldSelector worldSelector = parseWorld(instruction.getOptional("world"));
        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        new WeatherEvent(weather, worldSelector), instruction.getPackage()),
                server, scheduler, plugin);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        if (instruction.copy().getOptional("world") == null) {
            return new DoNothingStaticEvent();
        } else {
            return new NullStaticEventAdapter(parseEvent(instruction));
        }
    }

    @NotNull
    private Weather parseWeather(final String weatherName) throws InstructionParseException {
        return switch (weatherName.toLowerCase(Locale.ROOT)) {
            case "sun", "clear" -> Weather.SUN;
            case "rain" -> Weather.RAIN;
            case "storm" -> Weather.STORM;
            default ->
                    throw new InstructionParseException("Unknown weather state (valid options are: sun, clear, rain, storm): " + weatherName);
        };
    }

    @NotNull
    private WorldSelector parseWorld(final String worldName) {
        if (worldName == null) {
            return new PlayerWorldSelector();
        } else {
            final World world = server.getWorld(worldName);
            return new ConstantWorldSelector(world);
        }
    }
}
