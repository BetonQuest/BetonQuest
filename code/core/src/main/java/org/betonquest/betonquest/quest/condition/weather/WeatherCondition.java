package org.betonquest.betonquest.quest.condition.weather;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
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
    private final Variable<Weather> weather;

    /**
     * The world to check the weather in.
     */
    private final Variable<World> variableWorld;

    /**
     * Checks if the weather in the given world matches the weather of this condition.
     *
     * @param weather the weather to check for
     * @param world   the world to check the weather in
     */
    public WeatherCondition(final Variable<Weather> weather, final Variable<World> world) {
        this.weather = weather;
        this.variableWorld = world;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final World world = variableWorld.getValue(profile);
        return weather.getValue(profile).isInWorld(world);
    }
}
