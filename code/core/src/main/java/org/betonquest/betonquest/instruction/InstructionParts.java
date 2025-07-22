package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.api.quest.QuestException;

import java.util.List;

/**
 * Represents the parts of an instruction.
 */
public interface InstructionParts {
    /**
     * Gets the next part of the instruction.
     *
     * @return The next part of the instruction.
     * @throws QuestException If there are no parts left.
     */
    String next() throws QuestException;

    /**
     * Gets the current part of the instruction.
     *
     * @return The current part of the instruction.
     */
    String current();

    /**
     * Checks if there are more parts left.
     *
     * @return True if there are more parts left, false otherwise.
     */
    boolean hasNext();

    /**
     * Gets the number of parts.
     *
     * @return The number of parts.
     */
    int size();

    /**
     * Gets the part at the given index.
     *
     * @param index The index of the part to get.
     * @return The part at the given index.
     * @throws QuestException If the index is out of bounds.
     */
    String getPart(int index) throws QuestException;

    /**
     * Gets all parts of the instruction as a list.
     *
     * @return The parts of the instruction as a list.
     */
    List<String> getParts();

    /**
     * Gets the parts of the instruction excluding the first part.
     *
     * @return The parts of the instruction.
     */
    default List<String> getValueParts() {
        return getParts().subList(1, size());
    }
}
