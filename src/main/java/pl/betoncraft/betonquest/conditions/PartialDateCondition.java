package pl.betoncraft.betonquest.conditions;

import com.google.common.base.Joiner;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;


/**
 * Checks if the current date matches one of the given dates
 *
 * @author Jonas Blocher
 */
public class PartialDateCondition extends Condition {

    private final Set<Integer> dayOfMonth;
    private final Set<Integer> month;
    private final Set<Integer> year;

    public PartialDateCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        super.staticness = true;
        super.persistent = true;
        dayOfMonth = new HashSet<>();
        month = new HashSet<>();
        year = new HashSet<>();
        String dayOfMonthString = instruction.getOptional("day");
        if (dayOfMonthString != null) {
            if (dayOfMonthString.matches("[^-,]*")) {
                int i = instruction.getInt(dayOfMonthString, -1);
                if (i < 1 || i > 31)
                    throw new InstructionParseException(dayOfMonthString + " is not a valid day");
                dayOfMonth.add(i);
            } else {
                if (!dayOfMonthString.matches("[0-9]{1,2}(-[0-9]{1,2})?(,[0-9]{1,2}(-[0-9]{1,2})?)*"))
                    throw new InstructionParseException("could not parse days from " + dayOfMonthString);
                String[] args1 = dayOfMonthString.split(",");
                for (String arg : args1) {
                    if (arg.contains("-")) {
                        int index = arg.indexOf("-");
                        int beginning = instruction.getInt((arg.substring(0,index)), -1);
                        int end = instruction.getInt(arg.substring(index + 1), -1);
                        if (beginning >= end) throw new InstructionParseException("day " + beginning + " is before " + end);
                        for (int i = beginning; i <= end; i++) {
                            if (i < 1 || i > 31 ) throw new InstructionParseException(dayOfMonthString + " contains invalid days");
                            dayOfMonth.add(i);
                        }
                    } else {
                        int i = instruction.getInt(arg, -1);
                        if (i < 1 || i > 31 ) throw new InstructionParseException(dayOfMonthString + " contains invalid days");
                        dayOfMonth.add(i);
                    }
                }
            }
        }
        String monthString = instruction.getOptional("month");
        if (monthString != null) {
            if (monthString.matches("[^-,]*")) {
                int i = instruction.getInt(monthString, -1);
                if (i < 1 || i > 12)
                    throw new InstructionParseException(monthString + " is not a valid month");
                month.add(i);
            } else {
                if (!monthString.matches("[0-9]{1,2}(-[0-9]{1,2})?(,[0-9]{1,2}(-[0-9]{1,2})?)*"))
                    throw new InstructionParseException("could not parse months from " + monthString);
                String[] args1 = monthString.split(",");
                for (String arg : args1) {
                    if (arg.contains("-")) {
                        int index = arg.indexOf("-");
                        int beginning = instruction.getInt(arg.substring(0,index), -1);
                        int end = instruction.getInt(arg.substring(index + 1), -1);
                        if (beginning >= end) throw new InstructionParseException("month " + beginning + " is before " + end);
                        for (int i = beginning; i <= end; i++) {
                            if (i < 1 || i > 12 ) throw new InstructionParseException(monthString + " contains invalid months");
                            month.add(i);
                        }
                    } else {
                        int i = instruction.getInt(arg, -1);
                        if (i < 1 || i > 12 ) throw new InstructionParseException(monthString + " contains invalid months");
                        month.add(i);
                    }
                }
            }
        }
        String yearString = instruction.getOptional("year");
        if (yearString != null) {
            if (yearString.matches("[^-,]*")) {
                int i = instruction.getInt(yearString, -1);
                if (i < 1)
                    throw new InstructionParseException(yearString + " is not a valid year");
                year.add(i);
            } else {
                if (!yearString.matches("[0-9]+(-[0-9]+)?(,[0-9]+(-[0-9]+)?)*"))
                    throw new InstructionParseException("could not parse years from " + yearString);
                String[] args1 = yearString.split(",");
                for (String arg : args1) {
                    if (arg.contains("-")) {
                        int index = arg.indexOf("-");
                        int beginning = instruction.getInt(arg.substring(0,index), -1);
                        int end = instruction.getInt(arg.substring(index + 1), -1);
                        if (beginning >= end) throw new InstructionParseException("year " + beginning + " is before " + end);
                        for (int i = beginning; i <= end; i++) {
                            if (i < 1) throw new InstructionParseException(yearString + " contains invalid years");
                            year.add(i);
                        }
                    } else {
                        int i = instruction.getInt(arg, -1);
                        if (i < 1) throw new InstructionParseException(yearString + " contains invalid years");
                        year.add(i);
                    }
                }
            }
        }
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        Calendar current = Calendar.getInstance();
        if (!dayOfMonth.isEmpty()) {
            if (!dayOfMonth.contains(current.get(Calendar.DAY_OF_MONTH))) return false;
        }
        if (!month.isEmpty()) {
            if (!month.contains(current.get(Calendar.MONTH) + 1)) return false;
            //Dont ask why +1: java.util.times is a complete mess (january is 0, december is 11,...)
        }
        if (!year.isEmpty()) {
            if (!year.contains(current.get(Calendar.YEAR))) return false;
        }
        return true;
    }
}
