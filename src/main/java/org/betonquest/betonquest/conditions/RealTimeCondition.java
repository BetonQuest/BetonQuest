package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.Calendar;
import java.util.Date;

/**
 * A condition which checks if the current time is in the specified period
 * <p>
 * Created by Jonas Blocher on 27.11.2017.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidDuplicateLiterals"})
public class RealTimeCondition extends Condition {

    private final int hoursMin;
    private final int minutesMin;
    private final int hoursMax;
    private final int minutesMax;

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.AvoidLiteralsInIfCondition"})
    public RealTimeCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        super.staticness = true;
        super.persistent = true;
        final String[] theTime = instruction.next().split("-");
        if (theTime.length != 2) {
            throw new InstructionParseException("Wrong time format");
        }
        try {
            final String[] timeMin = theTime[0].split(":");
            final String[] timeMax = theTime[1].split(":");
            if (timeMin.length != 2 || timeMax.length != 2) {
                throw new InstructionParseException("Could not parse time");
            }
            hoursMin = Integer.parseInt(timeMin[0]);
            minutesMin = Integer.parseInt(timeMin[1]);
            hoursMax = Integer.parseInt(timeMax[0]);
            minutesMax = Integer.parseInt(timeMax[1]);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse time", e);
        }
        if (hoursMax < 0 || hoursMax > 23) {
            throw new InstructionParseException("Could not parse time");
        }
        if (hoursMin < 0 || hoursMin > 23) {
            throw new InstructionParseException("Could not parse time");
        }
        if (minutesMax < 0 || minutesMax > 59) {
            throw new InstructionParseException("Could not parse time");
        }
        if (minutesMin < 0 || minutesMin > 59) {
            throw new InstructionParseException("Could not parse time");
        }
        if (hoursMin == hoursMax && minutesMin == minutesMax) {
            throw new InstructionParseException("min and max time must be different");
        }
    }


    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Calendar cal = Calendar.getInstance();
        final Date now = cal.getTime();
        final Date startTime = atTime(cal, hoursMin, minutesMin);
        final Date endTime = atTime(cal, hoursMax, minutesMax);
        if (startTime.before(endTime)) {
            return now.after(startTime) && now.before(endTime);
        } else {
            return now.after(startTime) || now.before(endTime);
        }
    }

    private Date atTime(final Calendar day, final int hours, final int minutes) {
        day.setLenient(false);
        day.set(Calendar.HOUR_OF_DAY, hours);
        day.set(Calendar.HOUR, hours < 12 ? hours : hours - 12);
        day.set(Calendar.AM_PM, hours < 12 ? Calendar.AM : Calendar.PM);
        day.set(Calendar.MINUTE, minutes);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        return day.getTime();
    }
}
