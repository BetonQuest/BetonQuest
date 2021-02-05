package org.betonquest.betonquest.api;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Represents a variable in conversations.
 */
abstract public class Variable {

    /**
     * Stores instruction string for the condition.
     */
    protected Instruction instruction;

    /**
     * Creates new instance of the variable. The variable should parse the
     * instruction string at this point and extract all the data from it. If
     * anything goes wrong, throw {@link InstructionParseException} with an
     * error message describing the problem.
     *
     * @param instruction the Instruction object representing this variable; you need to
     *                    extract all required data from it and throw
     *                    {@link InstructionParseException} if there is anything wrong
     */
    public Variable(final Instruction instruction) {
        this.instruction = instruction;
    }

    /**
     * This method should return a resolved value of variable for given player.
     *
     * @param playerID ID of the player
     * @return the value of this variable
     */
    public abstract String getValue(String playerID);

    @Override
    public String toString() {
        return instruction.getInstruction();
    }
}
