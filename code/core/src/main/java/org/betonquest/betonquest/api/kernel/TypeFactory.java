package org.betonquest.betonquest.api.kernel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;

/**
 * A factory to create a Quest Type from an {@link DefaultInstruction}.
 *
 * @param <T> the type to create
 */
@FunctionalInterface
public interface TypeFactory<T> {

    /**
     * Create a new {@link T} from an {@link DefaultInstruction}.
     *
     * @param instruction the instruction to parse
     * @return the newly created {@link T}
     * @throws QuestException if the instruction cannot be parsed
     */
    T parseInstruction(DefaultInstruction instruction) throws QuestException;
}
