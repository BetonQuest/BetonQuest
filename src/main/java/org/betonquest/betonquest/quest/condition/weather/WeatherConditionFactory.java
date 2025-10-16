package org.betonquest.betonquest.quest.condition.weather;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;
import org.betonquest.betonquest.quest.event.weather.Weather;
import org.bukkit.World;

/**
 * Factory to create weather conditions from {@link Instruction}s.
 */
public class WeatherConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * The variable processor used to process variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create the weather condition factory.
     *
     * @param data              the data used for checking the condition on the main thread
     * @param variableProcessor the variable processor used to process variables
     */
    public WeatherConditionFactory(final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Weather> weather = instruction.get(Weather::parseWeather);
        final Variable<World> world = instruction.get(instruction.getValue("world", "%location.world%"), Argument.WORLD);
        return new PrimaryServerThreadPlayerCondition(
                new NullableConditionAdapter(new WeatherCondition(weather, world)), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final String worldString = instruction.getValue("world");
        if (worldString == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        final Variable<Weather> weather = instruction.get(Weather::parseWeather);
        final Variable<World> world = new Variable<>(variableProcessor, instruction.getPackage(), worldString, Argument.WORLD);
        return new PrimaryServerThreadPlayerlessCondition(
                new NullableConditionAdapter(new WeatherCondition(weather, world)), data);
    }
}
