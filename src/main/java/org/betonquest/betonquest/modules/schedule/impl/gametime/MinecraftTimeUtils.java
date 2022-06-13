package org.betonquest.betonquest.modules.schedule.impl.gametime;

import org.bukkit.World;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;

public interface MinecraftTimeUtils {

    /**
     * The zone offset of in-game time.
     * Luckily minecraft has no time zones so UTC is everywhere.
     */
    ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    /**
     * The day minecraft starts is January 1st, year 0ad.
     * When a world loads for the first time it will have this time.
     */
    Instant DAY_ZERO = LocalDateTime.of(0, 1, 1, 0, 0).toInstant(ZONE_OFFSET);

    /**
     * How long a tick takes in in-game time ({@code 5/18} second)
     */
    Duration ONE_TICK = durationFromTicks(1);

    /**
     * <p>
     * Convert a duration from Minecraft ticks to in-game time.
     * </p>
     * <code>
     * 24000 ticks = 1 minecraft day<br>
     * 1000 ticks = 1 minecraft hour<br>
     * 1 tick = 5/18 minecraft second<br>
     * </code>
     *
     * @param ticks length of the duration in game ticks
     * @return the duration in minecraft in-game time this takes
     * @throws ArithmeticException if an integer overflow occurs while converting
     */
    static Duration durationFromTicks(final long ticks) {
        final long seconds = Math.multiplyExact(ticks, 5) / 18;
        final long remainder = (ticks * 5) % 18;
        final long nanos = (remainder * 1_000_000_000) / 18;
        return Duration.ofSeconds(seconds, nanos);
    }

    /**
     * <p>
     * Calculate how many ticks the given in-game duration object is equal to.
     * </p>
     * <code>
     * 24000 ticks = 1 day<br>
     * 1000 ticks = 1 hour<br>
     * 1 tick = 5/18 second<br>
     * </code>
     *
     * @param duration an in-game duration
     * @return how many ticks the duration takes.
     * @throws ArithmeticException if an integer overflow occurs while converting
     */
    static long durationToTicks(final Duration duration) {
        return duration.dividedBy(ONE_TICK);
    }

    static Instant timeFromTicksLived(final long ticks) {
        return DAY_ZERO.plus(durationFromTicks(ticks));
    }

    static long timeToTicksLived(final Instant time) {
        return durationToTicks(Duration.between(DAY_ZERO, time));
    }

    /**
     * <p>
     * Get the world in-game time as Instant.
     * </p>
     * <p>
     * Minecraft time starts at {@code 0000-1-1T00:00:00Z} and passes at the rate of 1h per 1000 ticks.
     * </p>
     *
     * @param world the world to retrieve the time for
     * @return in-game time as instant
     * @see #durationFromTicks(long) convert ticks to in-game time
     */
    static Instant worldTime(final World world) {
        return timeFromTicksLived(world.getFullTime());
    }

    /**
     * Creates a comparator that compares two in-game durations by converting them to ticks.
     * While {@link Duration#compareTo(Duration)} has nanosecond precision,
     * this method rounds and treats two durations as equal if they took the
     * same amount of in-game ticks.
     *
     * @return
     */
    static Comparator<Duration> compareDuration() {
        return Comparator.comparingLong(MinecraftTimeUtils::durationToTicks);
    }

    static Comparator<Instant> compareTime() {
        return Comparator.comparingLong(time -> durationToTicks(Duration.between(DAY_ZERO, time)));
    }

    static boolean equals(Instant time1, Instant time2) {
        return compareTime().compare(time1, time2) == 0;
    }

    static boolean equals(Duration duration1, Duration duration2) {
        return compareDuration().compare(duration1, duration2) == 0;
    }
}
