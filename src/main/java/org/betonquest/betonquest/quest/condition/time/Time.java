package org.betonquest.betonquest.quest.condition.time;

import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * A time with an hour and minute.
 */
public record Time(int hour, int minute) {

    /**
     * Parse a time from a string. Takes hh or hh:mm format as input.
     *
     * @param time the time string to parse
     * @return the parsed time
     * @throws InstructionParseException if the time is invalid
     */
    public static Time parseTime(final String time) throws InstructionParseException {
        final String[] parts = time.split(":");
        final int hour;
        int minute = 0;
        final int lengthForMinutes = 2;
        try {
            hour = Integer.parseInt(parts[0]);
            if (parts.length == lengthForMinutes) {
                minute = Integer.parseInt(parts[1]);
            }
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Invalid time format. Expected hh or hh:mm", e);
        }
        if (!isValidHour(hour)) {
            throw new InstructionParseException("Hour must be between 0 and 23");
        }
        if (!isValidMinute(minute)) {
            throw new InstructionParseException("Minute must be between 0 and 59");
        }
        return new Time(hour, minute);
    }

    private static boolean isValidHour(final int time) {
        return time >= 0 && time <= 23;
    }

    private static boolean isValidMinute(final int time) {
        return time >= 0 && time <= 59;
    }

    /**
     * Check if this time is before or the same given time.
     *
     * @param time the time to check against
     * @return true if this time is before or the same given time
     */
    public boolean isBeforeOrSame(final Time time) {
        return hour < time.hour || (hour == time.hour && minute <= time.minute);
    }

    /**
     * Check if this time is after or the same given time.
     *
     * @param time the time to check against
     * @return true if this time is after or the same given time
     */
    public boolean isAfterOrSame(final Time time) {
        return hour > time.hour || (hour == time.hour && minute >= time.minute);
    }
}
