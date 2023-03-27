package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
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
    private static final int DOUBLE_LENGTH = "double".length();
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
        if ("int".equalsIgnoreCase(type)) {
            this.fractional = false;
            this.format = null;
        } else if (type.startsWith("double")) {
            this.fractional = true;
            this.format = getFormat(type);
        } else {
            throw new InstructionParseException(String.format("Unknown argument type: '%s'", type));
        }

        final QuestPackage pack = instruction.getPackage();

        final String low = instruction.next();
        this.low = new VariableNumber(pack, low.startsWith("{") ?
                parseToVariableString(instruction) : low.replace(',', '.'));

        final String high = instruction.next();
        this.high = new VariableNumber(pack, high.startsWith("{") ?
                parseToVariableString(instruction) : high.replace(',', '.'));
    }

    private DecimalFormat getFormat(final String type) throws InstructionParseException {
        if (type.length() > DOUBLE_LENGTH) {
            try {
                final int amount = Integer.parseInt(type.substring(DOUBLE_LENGTH + 1));
                if (amount > 0) {
                    return new DecimalFormat("#." + "#".repeat(amount));
                }
            } catch (final NumberFormatException e) {
                throw new InstructionParseException("Could not parse round value", e);
            }
        }
        return null;
    }

    /**
     * Converts a variable into the format used by {@link VariableNumber}
     *
     * @param instruction The {@link Instruction} to retrieve a Variable String
     * @return The variable with '%' at start and end
     * @throws InstructionParseException if instruction ends before a variable is resolved
     */
    private String parseToVariableString(final Instruction instruction) throws InstructionParseException {
        final StringBuilder builder = new StringBuilder("%");
        builder.append(instruction.current().substring(1)).append('.');
        while (true) {
            final String current = instruction.next();
            if (current.endsWith("}")) {
                builder.append(current, 0, current.length() - 1).append('%');
                return builder.toString();
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
