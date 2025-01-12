package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.QuestException;

import java.lang.reflect.InvocationTargetException;

/**
 * Factory to create {@link L}s with the old convention of the pre-defined constructor taking just one
 * {@link Instruction} argument.
 *
 * @param <T> concrete type of the {@link L}
 * @param <L> legacy quest type
 * @deprecated new events must use an {@link EventFactory} instead
 */
@Deprecated
public class FromClassLegacyTypeFactory<T extends L, L> implements LegacyTypeFactory<L> {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Class of the {@link L} to create.
     */
    private final Class<T> lClass;

    /**
     * Type name to use in log.
     */
    private final String typeName;

    /**
     * Create the factory for a specific {@link L} class.
     *
     * @param log      the logger that will be used for logging
     * @param lClass   {@link L} class to create with this factory
     * @param typeName the name of {@link L} to use in the log
     */
    public FromClassLegacyTypeFactory(final BetonQuestLogger log, final Class<T> lClass, final String typeName) {
        this.log = log;
        this.lClass = lClass;
        this.typeName = typeName;
    }

    @Override
    public L parseInstruction(final Instruction instruction) throws QuestException {
        final Throwable error;
        try {
            return lClass.getConstructor(Instruction.class).newInstance(instruction);
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof QuestException) {
                throw (QuestException) cause;
            }
            error = e;
        } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            error = e;
        }
        log.reportException(instruction.getPackage(), error);
        throw new QuestException("A broken " + typeName + " prevents the creation of " + instruction, error);
    }
}
