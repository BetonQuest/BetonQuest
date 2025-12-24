package org.betonquest.betonquest.quest.condition.weather;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.Variables;
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
     * The variable processor used to process variables.
     */
    private final Variables variables;

    /**
     * Create the weather condition factory.
     *
     * @param variables the variable processor to create and resolve variables
     */
    public WeatherConditionFactory(final Variables variables) {
        this.variables = variables;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Weather> weather = instruction.parse(Weather::parseWeather).get();
        final Variable<String> locationWorld = instruction.string().get("world", "%location.world%");
        final Variable<World> world = instruction.get(locationWorld.getValue(null), instruction.getParsers().world());
        return new NullableConditionAdapter(new WeatherCondition(weather, world));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final Variable<Weather> weather = instruction.parse(Weather::parseWeather).get();
        final Optional<Variable<World>> world = instruction.world().get("world");
        return world.map(worldVariable -> (PlayerlessCondition) new NullableConditionAdapter(new WeatherCondition(weather, world.orElse(null))))
                .orElse(new ThrowExceptionPlayerlessCondition());
    }
}
