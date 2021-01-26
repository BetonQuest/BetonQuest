package org.betonquest.betonquest.conditions;

import lombok.CustomLog;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Jonas Blocher on 27.11.2017.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class DayOfWeekCondition extends Condition {

    private final DayOfWeek day;

    @SuppressWarnings("PMD.PreserveStackTrace")
    public DayOfWeekCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        super.staticness = true;
        super.persistent = true;
        final String dayString = instruction.next();
        DayOfWeek dayOfWeek;
        try {
            dayOfWeek = DayOfWeek.of(Integer.parseInt(dayString));
        } catch (final DateTimeException e) {
            throw new InstructionParseException(dayString + " is not a valid day of a week", e);
        } catch (final NumberFormatException e) {
            LOG.debug("Could not parse number!", e);
            try {
                dayOfWeek = DayOfWeek.valueOf(dayString.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException iae) {
                throw new InstructionParseException(dayString + " is not a valid day of a week", iae);
            }
        }
        this.day = dayOfWeek;
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day = day == 1 ? 7 : day - 1;
        //As calendar.get(Calendar.DAY_OF_WEEK) returns 1 on sunday, 2 oin monday, and so on this has to be fixed
        return day == this.day.getValue();
    }
}
