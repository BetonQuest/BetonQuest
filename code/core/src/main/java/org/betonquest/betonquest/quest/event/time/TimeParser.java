package org.betonquest.betonquest.quest.event.time;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;

/**
 * Parses a time change from a string.
 */
public class TimeParser implements Argument<TimeChange> {

    /**
     * Parser for {@link TimeChange}.
     */
    public static final TimeParser TIME = new TimeParser();

    /**
     * Creates a new time parser.
     */
    public TimeParser() {
    }

    @Override
    public TimeChange apply(final String string) throws QuestException {
        if (string.isEmpty()) {
            throw new QuestException("Time cannot be empty");
        }
        final Time time = Time.getForPrefix(string.charAt(0));
        final boolean hasPrefix = time != Time.SET;
        final String rawTime = hasPrefix ? string.substring(1) : string;
        return new TimeChange(time, DefaultArgumentParsers.NUMBER.apply(rawTime));
    }
}
