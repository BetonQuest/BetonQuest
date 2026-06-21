package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainParser;

import java.util.List;

/**
 * Represents the parts of an instruction.
 *
 * @since 3.0.0
 * @deprecated Use {@link Instruction} and {@link InstructionChainParser} instead.
 */
@Deprecated
public interface InstructionParts {

    /**
     * Gets the next part of the instruction.
     *
     * @return The next part of the instruction.
     * @throws QuestException If there are no parts left.
     * @since 3.0.0
     * @deprecated Use {@link Instruction} and {@link InstructionChainParser} instead.
     */
    @Deprecated
    String nextElement() throws QuestException;

    /**
     * Gets the current part of the instruction.
     *
     * @return The current part of the instruction.
     * @since 3.0.0
     * @deprecated Use {@link Instruction} and {@link InstructionChainParser} instead.
     */
    @Deprecated
    String current();

    /**
     * Checks if there are more parts left.
     *
     * @return True if there are more parts left, false otherwise.
     * @since 3.0.0
     * @deprecated Use {@link Instruction} and {@link InstructionChainParser} instead.
     */
    @Deprecated
    boolean hasNext();

    /**
     * Gets the number of parts.
     *
     * @return The number of parts.
     * @since 3.0.0
     * @deprecated Use {@link Instruction} and {@link InstructionChainParser} instead.
     */
    @Deprecated
    int size();

    /**
     * Gets the part at the given index.
     *
     * @param index The index of the part to get.
     * @return The part at the given index.
     * @throws QuestException If the index is out of bounds.
     * @since 3.0.0
     * @deprecated Use {@link Instruction} and {@link InstructionChainParser} instead.
     */
    @Deprecated
    String getPart(int index) throws QuestException;

    /**
     * Gets all parts of the instruction as a list.
     *
     * @return The parts of the instruction as a list.
     * @since 3.0.0
     * @deprecated Use {@link Instruction} and {@link InstructionChainParser} instead.
     */
    @Deprecated
    List<String> getParts();

    /**
     * Gets the parts of the instruction excluding the first part.
     *
     * @return The parts of the instruction.
     * @since 3.0.0
     * @deprecated Use {@link Instruction} and {@link InstructionChainParser} instead.
     */
    @Deprecated
    default List<String> getValueParts() {
        return getParts().subList(1, size());
    }
}
