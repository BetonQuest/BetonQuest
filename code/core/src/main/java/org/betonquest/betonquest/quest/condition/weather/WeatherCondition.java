package org.betonquest.betonquest.quest.condition.weather;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
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
    private final Argument<Weather> weather;

    /**
     * The world to check the weather in.
     */
    private final Argument<World> world;

    /**
     * Checks if the weather in the given world matches the weather of this condition.
     *
     * @param weather the weather to check for
     * @param world   the world to check the weather in
     */
    public WeatherCondition(final Argument<Weather> weather, final Argument<World> world) {
        this.weather = weather;
        this.world = world;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final World world = this.world.getValue(profile);
        return weather.getValue(profile).isInWorld(world);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
