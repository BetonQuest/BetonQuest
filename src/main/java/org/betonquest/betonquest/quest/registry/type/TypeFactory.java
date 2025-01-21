package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * A factory to create a Quest Type from an {@link Instruction}.
 *
 * @param <T> the type to create
 */
public interface TypeFactory<T> {
    /**
     * Create a new {@link T} from an {@link Instruction}.
     *
     * @param instruction the instruction to parse
     * @return the newly created {@link T}
     * @throws QuestException if the instruction cannot be parsed
     */
    T parseInstruction(Instruction instruction) throws QuestException;
}
