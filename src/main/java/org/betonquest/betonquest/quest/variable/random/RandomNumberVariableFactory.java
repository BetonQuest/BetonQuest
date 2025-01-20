package org.betonquest.betonquest.quest.variable.random;

import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

/**
 * Factory for creating a random number variable.
 */
public class RandomNumberVariableFactory implements PlayerVariableFactory, PlayerlessVariableFactory {

    /**
     * Used for check if fractional uses limited decimal places.
     */
    private static final int DECIMAL_LENGTH = "decimal".length();

    /**
     * The argument for whole numbers.
     */
    private static final String WHOLE_NUMBER = "whole";

    /**
     * The {@link VariableProcessor} to use for variable processing.
     */
    private final VariableProcessor processor;

    /**
     * Creates a new {@link RandomNumberVariableFactory}.
     *
     * @param processor the {@link VariableProcessor} to use for variable processing
     */
    public RandomNumberVariableFactory(final VariableProcessor processor) {
        this.processor = processor;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    private NullableVariableAdapter parseInstruction(final Instruction instruction) throws QuestException {
        final String type = instruction.next();
        final boolean fractional;
        final DecimalFormat format;
        if (WHOLE_NUMBER.equalsIgnoreCase(type)) {
            fractional = false;
            format = null;
        } else if (type.startsWith("decimal")) {
            fractional = true;
            format = getFormat(type);
        } else {
            throw new QuestException("Invalid type for random number variable");
        }
        final VariableNumber low = parseFirst(instruction);
        final VariableNumber high = parseSecond(instruction);
        return new NullableVariableAdapter(new RandomNumberVariable(low, high, fractional, format));
    }

    @Nullable
    private DecimalFormat getFormat(final String type) throws QuestException {
        if (type.length() > DECIMAL_LENGTH) {
            try {
                final int amount = Integer.parseInt(type.substring(DECIMAL_LENGTH + 1));
                if (amount > 0) {
                    return new DecimalFormat("#." + "#".repeat(amount));
                }
            } catch (final NumberFormatException e) {
                throw new QuestException("Could not parse round value", e);
            }
        }
        return null;
    }

    private VariableNumber parseFirst(final Instruction instruction) throws QuestException {
        final String start = instruction.next();
        if (start.startsWith("{")) {
            return parseToVariable(start, instruction);
        } else {
            if (start.contains("~")) {
                return new VariableNumber(processor, instruction.getPackage(), start.substring(0, start.indexOf('~')));
            } else {
                final String next = instruction.next();
                return new VariableNumber(processor, instruction.getPackage(), start + '.' + next.substring(0, next.indexOf('~')));
            }
        }
    }

    private VariableNumber parseSecond(final Instruction instruction) throws QuestException {
        final String start = instruction.current().substring(instruction.current().indexOf('~') + 1);
        if (start.startsWith("{")) {
            return parseToVariable(start, instruction);
        } else {
            return new VariableNumber(processor, instruction.getPackage(), instruction.hasNext()
                    ? start + '.' + instruction.next() : start);
        }
    }

    /**
     * Converts a variable part into the a {@link VariableNumber}.
     *
     * @param start       The String to start with, including '{'
     * @param instruction The {@link Instruction} to retrieve a Variable
     * @return a new {@link VariableNumber} from the variable
     * @throws QuestException if instruction ends before a variable is resolved
     */
    private VariableNumber parseToVariable(final String start, final Instruction instruction) throws QuestException {
        final StringBuilder builder = new StringBuilder("%");
        builder.append(start.substring(1)).append('.');
        while (true) {
            final String current = instruction.next();
            if (current.contains("}")) {
                builder.append(current, 0, current.indexOf('}')).append('%');
                return new VariableNumber(processor, instruction.getPackage(), builder.toString());
            } else {
                builder.append(current).append('.');
            }
        }
    }
}
