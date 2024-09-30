package org.betonquest.betonquest.quest.condition.time;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.jetbrains.annotations.NotNull;

/**
 * A time frame with a start and end time.
 */
public record TimeFrame(Time startTime, Time endTime) {

    /**
     * Parse a time frame from a string.
     *
     * @param input the string to parse
     * @return the parsed time frame
     * @throws InstructionParseException if the input is invalid
     */
    @NotNull
    public static TimeFrame parse(final String input) throws InstructionParseException {
        final String[] parts = input.split("-");
        final int expectedLength = 2;
        if (parts.length != expectedLength) {
            throw new InstructionParseException("Wrong time format. Expected format: <time>-<time>");
        }
        return new TimeFrame(Time.parseTime(parts[0]), Time.parseTime(parts[1]));
    }

    /**
     * Check if a time is between or at the start and end time.
     *
     * @param now the time to check
     * @return true if the time is between the start and end time
     */
    public boolean isTimeBetween(final Time now) {
        if (startTime.isBeforeOrSame(endTime)) {
            return now.isAfterOrSame(startTime) && now.isBeforeOrSame(endTime);
        } else {
            return now.isAfterOrSame(startTime) || now.isBeforeOrSame(endTime);
        }
    }
}
