package org.betonquest.betonquest.quest.event.weather;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.common.function.ConstantSelector;
import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.common.function.Selectors;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.DoNothingStaticEvent;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

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

    private ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        final Weather weather = parseWeather(instruction.next());
        final Selector<World> worldSelector = parseWorld(instruction.getOptional("world"));
        final VariableNumber duration = instruction.getVarNum(instruction.getOptional("duration", "0"));
        return new PrimaryServerThreadComposedEvent(new WeatherEvent(weather, worldSelector, duration), data);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new OnlineProfileRequiredEvent(loggerFactory.create(WeatherEvent.class), parseComposedEvent(instruction), instruction.getPackage());
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        if (instruction.copy().getOptional("world") == null) {
            return new DoNothingStaticEvent();
        } else {
            return parseComposedEvent(instruction);
        }
    }

    private Weather parseWeather(final String weatherName) throws InstructionParseException {
        return switch (weatherName.toLowerCase(Locale.ROOT)) {
            case "sun", "clear" -> Weather.SUN;
            case "rain" -> Weather.RAIN;
            case "storm" -> Weather.STORM;
            default ->
                    throw new InstructionParseException("Unknown weather state (valid options are: sun, clear, rain, storm): " + weatherName);
        };
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
