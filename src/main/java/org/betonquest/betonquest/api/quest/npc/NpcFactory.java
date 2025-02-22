package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.registry.type.TypeFactory;

import java.util.Set;

/**
 * Factory to create specific {@link Npc}s from {@link Instruction}s.
 *
 * @param <T> the original Npc type
 */
public interface NpcFactory<T> extends TypeFactory<NpcWrapper<T>> {
    /**
     * Parses an instruction to create a {@link NpcWrapper} which resolves into a {@link Npc}.
     *
     * @param instruction instruction to parse
     * @return npc referenced by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    NpcWrapper<T> parseInstruction(Instruction instruction) throws QuestException;

    /**
     * Gets the instruction strings which could be used to identify this Npc.
     *
     * @param npc the Npc to get its identifier from
     * @return all identifying string possible used inside {@link org.betonquest.betonquest.id.NpcID NpcId}s.
     */
    Set<String> npcInstructionStrings(Npc<T> npc);

    /**
     * Gets the class of the Npc.
     *
     * @return the class of {@link T}
     */
    Class<? extends Npc<T>> factoredClass();
}
