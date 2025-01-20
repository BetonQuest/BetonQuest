package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.registry.type.TypeFactory;

/**
 * Factory to create {@link T}s from {@link Instruction}s.
 *
 * @param <T> legacy quest type
 */
public interface LegacyTypeFactory<T> extends TypeFactory<T> {
    /**
     * Parse an instruction to create a {@link T}.
     *
     * @param instruction instruction to parse for the {@link T}
     * @return {@link T} represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    T parseInstruction(Instruction instruction) throws QuestException;
}
