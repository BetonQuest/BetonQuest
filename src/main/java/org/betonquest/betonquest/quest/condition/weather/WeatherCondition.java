package org.betonquest.betonquest.quest.condition.weather;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.location.VariableWorld;
import org.betonquest.betonquest.quest.event.weather.Weather;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * A condition that checks the weather in the given world.
 */
public class WeatherCondition implements NullableCondition {

    /**
     * The weather to check for.
     */
    private final Weather weather;

    /**
     * The world to check the weather in.
     */
    private final VariableWorld variableWorld;

    /**
     * Checks if the weather in the given world matches the weather of this condition.
     *
     * @param weather the weather to check for
     * @param world   the world to check the weather in
     */
    public WeatherCondition(final Weather weather, final VariableWorld world) {
        this.weather = weather;
        this.variableWorld = world;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final World world = variableWorld.getValue(profile);
        return weather.isInWorld(world);
    }
}
