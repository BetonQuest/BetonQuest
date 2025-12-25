package org.betonquest.betonquest.quest.event.weather;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.ConstantSelector;
import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.common.function.Selectors;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.quest.event.DoNothingPlayerlessEvent;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Factory to create weather events from {@link Instruction}s.
 */
public class WeatherEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The server to access worlds.
     */
    private final Server server;

    /**
     * Creates the weather event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param server        the server to access worlds
     */
    public WeatherEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server) {
        this.loggerFactory = loggerFactory;
        this.server = server;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final PlayerEvent weatherPlayerEvent = parseWeatherEvent(instruction);
        final PlayerEvent playerEvent;
        if (requiresPlayer(instruction)) {
            playerEvent = new OnlineEventAdapter(
                    weatherPlayerEvent::execute,
                    loggerFactory.create(WeatherEvent.class),
                    instruction.getPackage()
            );
        } else {
            playerEvent = weatherPlayerEvent;
        }
        return playerEvent;
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        if (requiresPlayer(instruction)) {
            return new DoNothingPlayerlessEvent();
        }
        return parseWeatherEvent(instruction);
    }

    private boolean requiresPlayer(final Instruction instruction) throws QuestException {
        return instruction.copy().string().get("world").isEmpty();
    }

    private NullableEventAdapter parseWeatherEvent(final Instruction instruction) throws QuestException {
        final Argument<Weather> weather = instruction.parse(Weather::parseWeather).get();
        final Argument<String> worldVar = instruction.string().get("world").orElse(null);
        final Selector<World> worldSelector = parseWorld(worldVar == null ? null : worldVar.getValue(null));
        final Argument<Number> duration = instruction.number().get("duration", 0);
        return new NullableEventAdapter(new WeatherEvent(weather, worldSelector, duration));
    }

    private Selector<World> parseWorld(@Nullable final String worldName) {
        if (worldName == null) {
            return Selectors.fromPlayer(Player::getWorld);
        }
        final World world = server.getWorld(worldName);
        return new ConstantSelector<>(world);
    }
}
