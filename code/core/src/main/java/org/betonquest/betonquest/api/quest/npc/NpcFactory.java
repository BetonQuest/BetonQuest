package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.kernel.TypeFactory;

/**
 * Factory to create specific {@link Npc}s from {@link Instruction}s.
 */
@FunctionalInterface
public interface NpcFactory extends TypeFactory<NpcWrapper<?>> {
    /**
     * Parses an instruction to create a {@link NpcWrapper} which resolves into a {@link Npc}.
     *
     * @param instruction instruction to parse
     * @return npc referenced by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    NpcWrapper<?> parseInstruction(Instruction instruction) throws QuestException;
}
