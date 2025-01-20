package org.betonquest.betonquest.quest.condition.time.real;

import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The time interval used to evaluate the {@link PartialDateCondition}.
 */
public class TimeInterval {
    /**
     * Begin of the interval.
     */
    private final int start;

    /**
     * End of the interval.
     */
    private final int end;

    /**
     * Constructs a new TimeInterval with a start and end.
     *
     * @param start the first match of the interval
     * @param end   the last match of the interval
     * @param type  the type for validating range
     * @throws QuestException when the input is out of range for the type
     *                                   or {@code end} is less than {@code start}
     */
    public TimeInterval(final int start, final int end, final PartialDate type) throws QuestException {
        this.start = start;
        this.end = end;
        if (end < start) {
            throw new QuestException(type + " " + end + " is before " + start);
        }
        if (type.isInvalid(start)) {
            throw new QuestException(start + " is not a valid " + type);
        }
        if (type.isInvalid(end)) {
            throw new QuestException(end + " is not a valid " + type);
        }
    }

    /**
     * Constructs a new TimeInterval with a single matching value.
     *
     * @param value the value of the interval
     * @param type  the type for validating range
     * @throws QuestException when the input is out of range for the type
     */
    public TimeInterval(final int value, final PartialDate type) throws QuestException {
        this(value, value, type);
    }

    /**
     * Parses TimeIntervals from a single string. Intervals are defined by a dash between start and end,
     * multiple intervals are separated by a comma.
     * <p>
     * Example: {@code 1,3-4,7}
     *
     * @param string the string to parse
     * @param type   the type for validating range
     * @return list of parsed and validated time intervals
     * @throws QuestException when the input is miss-formated or exceeds type range
     */
    public static List<TimeInterval> parseFromString(final String string, final PartialDate type) throws QuestException {
        if (!string.matches("\\d+(-\\d+)?(,\\d+(-\\d+)?)*")) {
            throw new QuestException("could not parse " + type + " from '" + string + "'" + " (invalid format)");
        }
        final List<TimeInterval> intervals = new ArrayList<>();
        final String[] args = string.split(",");
        for (final String arg : args) {
            try {
                if (arg.contains("-")) {
                    final int index = arg.indexOf('-');
                    intervals.add(new TimeInterval(Integer.parseInt(arg.substring(0, index)),
                            Integer.parseInt(arg.substring(index + 1)),
                            type));
                } else {
                    intervals.add(new TimeInterval(Integer.parseInt(arg), type));
                }
            } catch (final QuestException e) {
                throw new QuestException("could not parse " + type + " from '" + string + "'"
                        + " (" + e.getMessage() + ")", e);
            }
        }
        return intervals;
    }

    /**
     * Gets the first match.
     *
     * @return start, less or equal {@link #getEnd()}
     */
    public int getStart() {
        return start;
    }

    /**
     * Gets the last match.
     *
     * @return end, greater or equals {@link #getStart()}
     */
    public int getEnd() {
        return end;
    }

    /**
     * Checks if this interval contains the value.
     *
     * @param value the value to check
     * @return if value is within {@code start} and {@code end}
     */
    public boolean isWithin(final int value) {
        return value >= start && value <= end;
    }

    @Override
    public boolean equals(@Nullable final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final TimeInterval interval = (TimeInterval) other;
        return start == interval.start && end == interval.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return start == end ? String.valueOf(start) : start + "-" + end;
    }
}
