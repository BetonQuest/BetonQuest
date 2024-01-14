package org.betonquest.betonquest.quest.event.weather;

import org.bukkit.World;

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
}
