package org.betonquest.betonquest.quest.condition.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.condition.ConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.lang.reflect.InvocationTargetException;

/**
 * Factory to create {@link Condition Legacy Condition} with the old convention of the pre-defined constructor taking
 * just one {@link Instruction} argument.
 *
 * @param <T> type of the condition
 * @deprecated new conditions must use an {@link ConditionFactory} instead
 */
@Deprecated
public class FromClassLegacyConditionFactory<T extends Condition> implements LegacyConditionFactory {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Class of the event to condition.
     */
    private final Class<T> conditionClass;

    /**
     * Create the factory for a specific condition class.
     *
     * @param log            the logger that will be used for logging
     * @param conditionClass condition class to create with this factory
     */
    public FromClassLegacyConditionFactory(final BetonQuestLogger log, final Class<T> conditionClass) {
        this.log = log;
        this.conditionClass = conditionClass;
    }

    @Override
    public Condition parseConditionInstruction(final Instruction instruction) throws InstructionParseException {
        final Throwable error;
        try {
            return conditionClass.getConstructor(Instruction.class).newInstance(instruction);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof InstructionParseException) {
                throw (InstructionParseException) cause;
            }
            error = e;
        } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            error = e;
        }
        log.reportException(instruction.getPackage(), error);
        throw new InstructionParseException("A broken condition prevents the creation of " + instruction, error);
    }
}
