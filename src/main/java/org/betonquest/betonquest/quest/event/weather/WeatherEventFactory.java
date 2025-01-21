package org.betonquest.betonquest.quest.event.weather;

import org.betonquest.betonquest.api.common.function.ConstantSelector;
import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.common.function.Selectors;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.DoNothingStaticEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Factory to create weather events from {@link Instruction}s.
 */
public class WeatherEventFactory implements EventFactory, StaticEventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Creates the weather event factory.
     *
     * @param loggerFactory logger factory to use
     * @param data          the data for primary server thread access
     */
    public WeatherEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final Event weatherEvent = parseWeatherEvent(instruction);
        final Event event;
        if (requiresPlayer(instruction)) {
            event = new OnlineEventAdapter(
                    weatherEvent::execute,
                    loggerFactory.create(WeatherEvent.class),
                    instruction.getPackage()
            );
        } else {
            event = weatherEvent;
        }
        return new PrimaryServerThreadEvent(event, data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        if (requiresPlayer(instruction)) {
            return new DoNothingStaticEvent();
        } else {
            return new PrimaryServerThreadStaticEvent(parseWeatherEvent(instruction), data);
        }
    }

    private boolean requiresPlayer(final Instruction instruction) {
        return instruction.copy().getOptional("world") == null;
    }

    private NullableEventAdapter parseWeatherEvent(final Instruction instruction) throws QuestException {
        final Weather weather = Weather.parseWeather(instruction.next());
        final Selector<World> worldSelector = parseWorld(instruction.getOptional("world"));
        final VariableNumber duration = instruction.get(instruction.getOptional("duration", "0"), VariableNumber::new);
        return new NullableEventAdapter(new WeatherEvent(weather, worldSelector, duration));
    }

    private Selector<World> parseWorld(@Nullable final String worldName) {
        if (worldName == null) {
            return Selectors.fromPlayer(Player::getWorld);
        } else {
            final World world = data.server().getWorld(worldName);
            return new ConstantSelector<>(world);
        }
    }
}
