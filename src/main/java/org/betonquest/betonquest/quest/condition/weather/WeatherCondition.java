package org.betonquest.betonquest.quest.condition.weather;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.event.weather.Weather;
import org.bukkit.World;

/**
 * A condition that checks the weather in the player's world.
 */
public class WeatherCondition implements OnlineCondition {

    /**
     * The weather to check for.
     */
    private final Weather weather;

    /**
     * Creates a new weather condition.
     *
     * @param weather the weather to check for
     */
    public WeatherCondition(final Weather weather) {
        this.weather = weather;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        final World world = profile.getPlayer().getWorld();
        return weather.isInWorld(world);
    }
}
