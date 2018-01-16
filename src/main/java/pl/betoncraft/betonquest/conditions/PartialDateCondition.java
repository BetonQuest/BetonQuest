package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


/**
 * Checks if the current date matches one of the given dates
 *
 * @author Jonas Blocher
 */
public class PartialDateCondition extends Condition {

    private final List<TimeInterval> dayOfMonth;
    private final List<TimeInterval> month;
    private final List<TimeInterval> year;

    public PartialDateCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        super.staticness = true;
        super.persistent = true;
        String dayOfMonthString = instruction.getOptional("day");
        if (dayOfMonthString != null) {
            this.dayOfMonth = TimeInterval.parseFromString(dayOfMonthString, PartialDate.DAY);
        } else {
            this.dayOfMonth = null;
        }
        String monthString = instruction.getOptional("month");
        if (monthString != null) {
            this.month = TimeInterval.parseFromString(monthString, PartialDate.MONTH);
        } else {
            this.month = null;
        }
        String yearString = instruction.getOptional("year");
        if (yearString != null) {
            this.year = TimeInterval.parseFromString(yearString, PartialDate.YEAR);
        } else {
            this.year = null;
        }
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        Calendar current = Calendar.getInstance();
        if (dayOfMonth != null) {
            int day = current.get(Calendar.DAY_OF_MONTH);
            return this.dayOfMonth.stream().anyMatch(interval -> interval.isWithin(day));
        }
        if (month != null) {
            int month = current.get(Calendar.MONTH) + 1;
            //Dont ask why +1: java.util.Calendar is a complete mess (january is 0, december is 11,...)
            return this.month.stream().anyMatch(interval -> interval.isWithin(month));
        }
        if (year != null) {
            int year = current.get(Calendar.YEAR);
            return this.year.stream().anyMatch(interval -> interval.isWithin(year));
        }
        return true;
    }

    public enum PartialDate {

        DAY(31),
        MONTH(12),
        YEAR(-1);

        private final int maxValue;

        PartialDate(int maxValue) {
            this.maxValue = maxValue;
        }

        public boolean isValid(int i) {
            return i > 0 && (maxValue == -1 || i <= maxValue);
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static class TimeInterval {

        private final int start;
        private final int end;

        public TimeInterval(int start, int end, PartialDate type) throws InstructionParseException {
            this.start = start;
            this.end = end;
            if (end < start) throw new InstructionParseException(type + " " + end + " is before " + start);
            if (!type.isValid(start)) throw new InstructionParseException(start + " is not a valid " + type);
            if (!type.isValid(end)) throw new InstructionParseException(end + " is not a valid " + type);
        }

        public TimeInterval(int value, PartialDate type) throws InstructionParseException {
            this(value, value, type);
        }

        public static List<TimeInterval> parseFromString(String string, PartialDate type) throws InstructionParseException {
            List<TimeInterval> intervals = new ArrayList<>();
            if (!string.matches("\\d+(-\\d+)?(,\\d+(-\\d+)?)*"))
                throw new InstructionParseException("could not parse " + type + " from '" + string + "'" + " (invalid format)");
            String[] args = string.split(",");
            for (String arg : args) {
                try {
                    if (arg.contains("-")) {
                        int i = arg.indexOf("-");
                            intervals.add(new TimeInterval(Integer.parseInt(arg.substring(0, i)),
                                                           Integer.parseInt(arg.substring(i + 1)),
                                                           type));
                    } else {
                        intervals.add(new TimeInterval(Integer.parseInt(arg), type));
                    }
                } catch (InstructionParseException e) {
                    throw new InstructionParseException("could not parse " + type + " from '" + string + "'"
                                                            + " (" + e.getMessage() + ")");
                }
            }
            return intervals;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public boolean isWithin(int value) {
            return value >= start && value <= end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TimeInterval interval = (TimeInterval) o;
            return start == interval.start &&
                    end == interval.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

        @Override
        public String toString() {
            return start == end ? String.valueOf(start) : (start + "-" + end);
        }

    }
}
