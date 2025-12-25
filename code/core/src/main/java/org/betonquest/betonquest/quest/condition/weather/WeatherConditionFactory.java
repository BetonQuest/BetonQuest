package org.betonquest.betonquest.quest.condition.weather;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;
import org.betonquest.betonquest.quest.event.weather.Weather;
import org.bukkit.World;

import java.util.Optional;

/**
 * Factory to create weather conditions from {@link Instruction}s.
 */
public class WeatherConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Create the weather condition factory.
     */
    public WeatherConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Weather> weather = instruction.parse(Weather::parseWeather).get();
        final Argument<String> locationWorld = instruction.string().get("world", "%location.world%");
        final Argument<World> world = instruction.chainForArgument(locationWorld.getValue(null)).world().get();
        return new NullableConditionAdapter(new WeatherCondition(weather, world));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final Argument<Weather> weather = instruction.parse(Weather::parseWeather).get();
        final Optional<Argument<World>> world = instruction.world().get("world");
        return world.map(worldVariable -> (PlayerlessCondition) new NullableConditionAdapter(new WeatherCondition(weather, world.orElse(null))))
                .orElse(new ThrowExceptionPlayerlessCondition());
    }
}
