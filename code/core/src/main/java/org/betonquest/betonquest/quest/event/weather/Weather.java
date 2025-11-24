package org.betonquest.betonquest.quest.event.weather;

import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.World;

import java.util.Locale;

/**
 * Possible weather in a minecraft world.
 */
public enum Weather {
    /**
     * Clear weather. No rain or lightning and thunder.
     */
    SUN(false, false),

    /**
     * Rainy weather. This is just rain.
     */
    RAIN(true, false),

    /**
     * Stormy weather. This is rain with lighting and thunder.
     */
    STORM(true, true);

    /**
     * Whether it should rain.
     */
    private final boolean storm;

    /**
     * Whether it should thunder.
     */
    private final boolean thunder;

    Weather(final boolean storm, final boolean thunder) {
        this.storm = storm;
        this.thunder = thunder;
    }

    /**
     * Parses the weather string to the corresponding weather.
     *
     * @param weatherName the name of the weather
     * @return the weather corresponding to the name
     * @throws QuestException if the weather name is not recognized
     */
    public static Weather parseWeather(final String weatherName) throws QuestException {
        return switch (weatherName.toLowerCase(Locale.ROOT)) {
            case "sun", "clear" -> SUN;
            case "rain", "rainy" -> RAIN;
            case "storm", "thunder" -> STORM;
            default ->
                    throw new QuestException("Unknown weather state (valid options are: sun, clear, rain, rainy, storm, thunder): " + weatherName);
        };
    }

    /**
     * Apply the weather to the given world.
     *
     * @param world    world to change the weather in
     * @param duration how long the weather will remain - values &lt;= 0 won't set a duration
     */
    public void applyTo(final World world, final int duration) {
        world.setStorm(storm);
        world.setThundering(thunder);
        if (duration > 0) {
            world.setWeatherDuration(duration);
        }
    }

    /**
     * Checks whether the weather matches the current weather in the world.
     *
     * @param world the world to check for
     * @return if weather matching the world's weather
     */
    public boolean isInWorld(final World world) {
        return world.isThundering() == thunder && world.hasStorm() == storm;
    }
}
