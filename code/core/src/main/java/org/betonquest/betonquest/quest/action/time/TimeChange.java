package org.betonquest.betonquest.quest.action.time;

import org.bukkit.World;

/**
 * Represents an applicable time change.
 *
 * @param time  the type of time change
 * @param value the value for the time change
 */
public record TimeChange(Time time, Number value) {

    /**
     * Applies the time to the world.
     * Forwarding to {@link Time#applyTo(World, long)} after applying hourFormat if necessary.
     *
     * @param world      the world to apply the time to
     * @param hourFormat if the time is given in hours and needs to be multiplied with 1000
     * @return the calculated time
     */
    public long applyTo(final World world, final boolean hourFormat) {
        final long timeValue = Math.abs(hourFormat ? value().longValue() * 1000 : value.longValue());
        return time().applyTo(world, timeValue);
    }
}
