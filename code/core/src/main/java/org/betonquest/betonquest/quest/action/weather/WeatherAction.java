package org.betonquest.betonquest.quest.action.weather;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * The weather action, changing the weather on the server.
 */
public class WeatherAction implements NullableAction {

    /**
     * The weather that will be set when the action is executed.
     */
    private final Argument<Weather> weather;

    /**
     * The selector to get the world for that the weather should be set.
     */
    private final Argument<World> world;

    /**
     * The time weather will not change naturally.
     */
    private final Argument<Number> duration;

    /**
     * Creates the weather action to set the given state.
     *
     * @param weather  the weather to set
     * @param world    to get the world that should be affected
     * @param duration how long the weather will not change - values &lt;= 0 won't set a duration
     */
    public WeatherAction(final Argument<Weather> weather, final Argument<World> world, final Argument<Number> duration) {
        this.weather = weather;
        this.world = world;
        this.duration = duration;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final World world = this.world.getValue(profile);
        weather.getValue(profile).applyTo(world, duration.getValue(profile).intValue());
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
