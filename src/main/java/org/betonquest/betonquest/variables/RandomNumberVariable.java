package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This variable resolves into a random value (inclusive arguments)
 * Note that it will return a different value for each call.
 */
public class RandomNumberVariable extends Variable {

    /**
     * Used for check if fractional uses limited decimal places
     */
    private static final int DECIMAL_LENGTH = "decimal".length();
    /**
     * The lower bar of the random amount
     */
    private final VariableNumber low;
    /**
     * The higher bar of the random amount
     */
    private final VariableNumber high;
    /**
     * If the value should be returned as {@code double}
     */
    private final boolean fractional;
    /**
     * The digit amount to round to in fractional mode stored in pattern
     */
    private final DecimalFormat format;

    /**
     * Creates a new {@link RandomNumberVariable}
     *
     * @param instruction the {@link Instruction} used to create the Variable
     * @throws InstructionParseException When an error occurs
     */
    public RandomNumberVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        final String type = instruction.next();
        if ("whole".equalsIgnoreCase(type)) {
            this.fractional = false;
            this.format = null;
        } else if (type.startsWith("decimal")) {
            this.fractional = true;
            this.format = getFormat(type);
        } else {
            throw new InstructionParseException(String.format("Unknown argument type: '%s'", type));
        }

        this.low = parseFirst(instruction);
        this.high = parseSecond(instruction);
    }

    private DecimalFormat getFormat(final String type) throws InstructionParseException {
        if (type.length() > DECIMAL_LENGTH) {
            try {
                final int amount = Integer.parseInt(type.substring(DECIMAL_LENGTH + 1));
                if (amount > 0) {
                    return new DecimalFormat("#." + "#".repeat(amount));
                }
            } catch (final NumberFormatException e) {
                throw new InstructionParseException("Could not parse round value", e);
            }
        }
        return null;
    }

    private VariableNumber parseFirst(final Instruction instruction) throws InstructionParseException {
        final String start = instruction.next();
        if (start.startsWith("{")) {
            return parseToVariable(start, instruction);
        } else {
            if (start.contains("~")) {
                return new VariableNumber(instruction.getPackage(), start.substring(0, start.indexOf('~')));
            } else {
                final String next = instruction.next();
                return new VariableNumber(instruction.getPackage(), start + '.' + next.substring(0, next.indexOf('~')));
            }
        }
    }

    private VariableNumber parseSecond(final Instruction instruction) throws InstructionParseException {
        final String start = instruction.current().substring(instruction.current().indexOf('~') + 1);
        if (start.startsWith("{")) {
            return parseToVariable(start, instruction);
        } else {
            return new VariableNumber(instruction.getPackage(), instruction.hasNext() ?
                    start + '.' + instruction.next() : start);
        }
    }

    /**
     * Converts a variable part into the a {@link VariableNumber}
     *
     * @param start       The String to start with, including '{'
     * @param instruction The {@link Instruction} to retrieve a Variable
     * @return a new {@link VariableNumber} from the variable
     * @throws InstructionParseException if instruction ends before a variable is resolved
     */
    private VariableNumber parseToVariable(final String start, final Instruction instruction) throws InstructionParseException {
        final StringBuilder builder = new StringBuilder("%");
        builder.append(start.substring(1)).append('.');
        while (true) {
            final String current = instruction.next();
            if (current.contains("}")) {
                builder.append(current, 0, current.indexOf('}')).append('%');
                return new VariableNumber(instruction.getPackage(), builder.toString());
            } else {
                builder.append(current).append('.');
            }
        }
    }

    @Override
    public String getValue(final Profile profile) {
        try {
            if (fractional) {
                final double value = ThreadLocalRandom.current().nextDouble(low.getDouble(profile), high.getDouble(profile));
                if (format != null) {
                    return format.format(value);
                }
                return String.valueOf(value);
            } else {
                return String.valueOf(ThreadLocalRandom.current().nextInt(
                        low.getInt(profile), high.getInt(profile) + 1));
            }
        } catch (final IllegalArgumentException | QuestRuntimeException e) {
            return "";
        }
    }
}
