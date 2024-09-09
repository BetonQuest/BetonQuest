package org.betonquest.betonquest.quest.condition.weather;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.event.weather.Weather;

import java.util.Locale;

/**
 * Factory to create weather conditions from {@link Instruction}s.
 */
public class WeatherConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the weather condition factory.
     *
     * @param loggerFactory the logger factory used for creating loggers
     * @param data          the data used for checking the condition on the main thread
     */
    public WeatherConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final Weather weather = Weather.parseWeather(instruction.next().toLowerCase(Locale.ROOT).trim());
        final BetonQuestLogger logger = loggerFactory.create(WeatherCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new WeatherCondition(weather), logger, instruction.getPackage()), data);
    }

}
