package org.betonquest.betonquest.quest.variable.random;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariableAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Factory for creating a random number variable.
 */
public class RandomNumberVariableFactory implements PlayerVariableFactory, PlayerlessVariableFactory {

    /**
     * The argument for whole numbers.
     */
    private static final String WHOLE_NUMBER = "whole";

    /**
     * Used for check if fractional uses limited decimal places.
     */
    private static final int DECIMAL_LENGTH = "decimal".length();

    /**
     * Creates a new {@link RandomNumberVariableFactory}.
     */
    public RandomNumberVariableFactory() {
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
        final Variable<Number> low = parseFirst(instruction);
        final Variable<Number> high = parseSecond(instruction);
        return new NullableVariableAdapter(new RandomNumberVariable(ThreadLocalRandom.current(), low, high, fractional, format));
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

    private Variable<Number> parseFirst(final Instruction instruction) throws QuestException {
        final String start = instruction.next();
        if (start.startsWith("{")) {
            return parseToVariable(start, instruction);
        } else {
            if (start.contains("~")) {
                return instruction.get(start.substring(0, start.indexOf('~')), Argument.NUMBER);
            } else {
                final String next = instruction.next();
                return instruction.get(start + '.' + next.substring(0, next.indexOf('~')), Argument.NUMBER);
            }
        }
    }

    private Variable<Number> parseSecond(final Instruction instruction) throws QuestException {
        final String start = instruction.current().substring(instruction.current().indexOf('~') + 1);
        if (start.startsWith("{")) {
            return parseToVariable(start, instruction);
        } else {
            return instruction.get(instruction.hasNext() ? start + '.' + instruction.next() : start, Argument.NUMBER);
        }
    }

    /**
     * Converts a variable part into a Variable Number.
     *
     * @param start       The String to start with, including '{'
     * @param instruction The {@link Instruction} to retrieve a Variable
     * @return a new Variable Number
     * @throws QuestException if the instruction ends before a variable is resolved
     */
    private Variable<Number> parseToVariable(final String start, final Instruction instruction) throws QuestException {
        final StringBuilder builder = new StringBuilder("%");
        builder.append(start.substring(1)).append('.');
        while (true) {
            final String current = instruction.next();
            if (current.contains("}")) {
                builder.append(current, 0, current.indexOf('}')).append('%');
                return instruction.get(builder.toString(), Argument.NUMBER);
            } else {
                builder.append(current).append('.');
            }
        }
    }
}
