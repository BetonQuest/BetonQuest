package org.betonquest.betonquest.quest.variable.point;

import org.apache.commons.lang3.tuple.Triple;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.util.Locale;

/**
 * A factory for creating Point variables.
 *
 * @param <T> the data holder type
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractPointVariableFactory<T> {

    /**
     * The data holder.
     */
    protected final T dataHolder;

    /**
     * The logger instance for this factory.
     */
    protected final BetonQuestLogger logger;

    /**
     * Create a new Point variable factory.
     *
     * @param dataHolder the data holder
     * @param logger     the logger instance for this factory
     */
    public AbstractPointVariableFactory(final T dataHolder, final BetonQuestLogger logger) {
        this.dataHolder = dataHolder;
        this.logger = logger;
    }

    /**
     * Parse the instruction to get the category, type and amount of the point.
     *
     * @param instruction the instruction to parse
     * @return a triple containing the category, amount and type of the point
     * @throws QuestException if the instruction could not be parsed
     */
    protected Triple<String, Integer, PointCalculationType> parseInstruction(final Instruction instruction) throws QuestException {
        final String category = instruction.get(PackageArgument.IDENTIFIER).getValue(null);
        final PointCalculationType type = getType(instruction.nextElement());
        int amount = 0;
        if (type == PointCalculationType.LEFT) {
            amount = getAmount(instruction);
        }
        return Triple.of(category, amount, type);
    }

    private int getAmount(final Instruction instruction) throws QuestException {
        try {
            return Integer.parseInt(instruction.current().substring(5));
        } catch (final NumberFormatException e) {
            throw new QuestException("Could not parse point amount", e);
        }
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private PointCalculationType getType(final String type) throws QuestException {
        if ("amount".equalsIgnoreCase(type)) {
            return PointCalculationType.AMOUNT;
        } else if (type.toLowerCase(Locale.ROOT).startsWith("left:")) {
            return PointCalculationType.LEFT;
        } else {
            throw new QuestException(String.format("Unknown variable type: '%s'",
                    type));
        }
    }
}
